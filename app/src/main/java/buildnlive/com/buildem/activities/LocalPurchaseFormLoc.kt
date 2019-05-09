package buildnlive.com.buildem.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.Locale

import buildnlive.com.buildem.adapters.SingleImageAdapter
import buildnlive.com.buildem.elements.Item
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.google.android.gms.location.*

class LocalPurchaseFormLoc : AppCompatActivity() {

    private var submit: Button? = null
    private val upload: Button? = null
    private var progress: ProgressBar? = null
    private var `val` = true
    private var hider: TextView? = null
    private val checkout_text: TextView? = null
    private var item: TextView? = null
    private var quantity_edit: EditText? = null
    private var total_edit: EditText? = null
    private var overheads_edit: EditText? = null
    private var vendor_details_edit: EditText? = null
    private var ship_no_edit: EditText? = null
    private var details_edit: EditText? = null
    private var rate_edit: EditText? = null
    private var tax_edit:EditText? = null
    private val LOADING: Boolean = false
    private var unitspinner: Spinner? = null
    private var builder: AlertDialog.Builder? = null
    private var imagePath: String? = null
    private var images: ArrayList<Packet>? = null
    private var imagesAdapter: SingleImageAdapter? = null
    private var context: Context? = null
    private var selectedItem: Item? = null
    private var rate: String? = null
    private var tax:String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location?=null
    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var handler: Handler? = null


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
    }


    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            // Display the address string
            // or an error message sent from the intent service.
            outputLocation = resultData?.getString(Constants.RESULT_DATA_KEY) ?: ""


            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
//                utilityofActivity!!.toast(getString(R.string.address_found))
//                utilityofActivity!!.toast(outputLocation!!)
                utilityofActivity!!.dismissProgressDialog()
//                dialogView!!.findViewById<TextView>(R.id.address).text = outputLocation
//                next()
                console.log("Location: "+ outputLocation)

            } else {
                utilityofActivity!!.toast("Error")
                utilityofActivity!!.dismissProgressDialog()
            }

        }
    }


    private fun startIntentService() {

        val intent = Intent(context, FetchAddressIntentService::class.java).apply {
            putExtra(Constants.RECEIVER, resultReceiver)
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }
        context!!.startService(intent)
    }


    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val requestingLocationUpdates: Boolean = true

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_purchase_form)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = "Local Purchase"
        val bundle = intent.extras

        if (bundle != null) {
            selectedItem = bundle.getSerializable("Items") as Item
        }


        item = findViewById(R.id.item)
        progress = findViewById(R.id.progress)
        submit = findViewById(R.id.submit)
        context = this
        //        name_edit =  findViewById(R.id.name);
        quantity_edit = findViewById(R.id.quantity)
        total_edit = findViewById(R.id.total)
        overheads_edit = findViewById(R.id.overheads)
        vendor_details_edit = findViewById(R.id.vendor_details)
        ship_no_edit = findViewById(R.id.ship_no)
        details_edit = findViewById(R.id.details)
        rate_edit = findViewById(R.id.rate)
        tax_edit = findViewById(R.id.tax)

        builder = AlertDialog.Builder(context)

        unitspinner = findViewById(R.id.unit)


        item!!.text = selectedItem!!.name




        unitspinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                unit = unitspinner!!.selectedItem.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }


        progress = findViewById(R.id.progress)
        hider = findViewById(R.id.hider)

        if (LOADING) {
            progress!!.visibility = View.VISIBLE
            hider!!.visibility = View.VISIBLE
        } else {
            progress!!.visibility = View.GONE
            hider!!.visibility = View.GONE
        }


        val list = findViewById<AdvancedRecyclerView>(R.id.images)
        images = ArrayList()
        images!!.add(Packet())
        imagesAdapter = SingleImageAdapter(context, images, SingleImageAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.image_chooser, null)
                val dialogBuilder = android.support.v7.app.AlertDialog.Builder(context!!, R.style.PinDialog)
                val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
                alertDialog.show()
                val gallery = dialogView.findViewById<TextView>(R.id.gallery)
                val camera = dialogView.findViewById<TextView>(R.id.camera)

                gallery.setOnClickListener {
                    alertDialog.dismiss()
                    val pictureIntent = Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pictureIntent, REQUEST_GALLERY_IMAGE)
                }

                camera.setOnClickListener {
                    alertDialog.dismiss()
                    val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (pictureIntent.resolveActivity(packageManager) != null) {

                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                        } catch (ex: IOException) {
                        }

                        if (photoFile != null) {
                            val photoURI = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", photoFile)
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            imagePath = photoFile.absolutePath
                            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
                        }
                    }
                }

                val negative = dialogView.findViewById<Button>(R.id.negative)
                negative.setOnClickListener { alertDialog.dismiss() }

            } else {
                images!!.removeAt(pos)
                imagesAdapter!!.notifyItemRemoved(pos)
                imagesAdapter!!.notifyDataSetChanged()
            }
        })
        list.adapter = imagesAdapter
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        list.setmMaxHeight(350)



        submit!!.setOnClickListener {
            //                name=name_edit.getText().toString();
            quantity = quantity_edit!!.text.toString()
            total = total_edit!!.text.toString()
            overheads = overheads_edit!!.text.toString()
            vendor_details = vendor_details_edit!!.text.toString()
            ship_no = ship_no_edit!!.text.toString()
            details = details_edit!!.text.toString()
            rate = rate_edit!!.getText().toString()
            tax = tax_edit!!.getText().toString()

            builder!!.setMessage("Are you sure?").setTitle("Payment")

            //Setting message manually and performing action on button click
            builder!!.setMessage("Do you want to Submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        if (validate(quantity, total, vendor_details, unit)) {
                            console.log("From Validate")
                            try {
                                sendRequest(selectedItem!!.id, quantity, unit, total, overheads,rate,tax,vendor_details, ship_no, details, images!!)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        //  Action for 'NO' Button
                        dialog.cancel()
                    }
            //Creating dialog box
            val alert = builder!!.create()
            //Setting the title manually
            alert.setTitle("Local Purchase")
            alert.show()
        }

        if (PrefernceFile.getInstance(context!!).getString("Lat") != null) {
            console.log("Fragment: " + PrefernceFile.getInstance(context!!).getString("Lat")!!)
        }

        resultReceiver = AddressResultReceiver(Handler())

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appCompatActivity!!)


        locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

        builder.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null)
                        lastLocation = location
                    if(lastLocation!=null){
                        PrefernceFile.getInstance(context!!).setString("Lat",lastLocation!!.latitude.toString())
                        PrefernceFile.getInstance(context!!).setString("Long",lastLocation!!.longitude.toString())
//                        fetchAddressButtonHander()
                    }
                    console.log("Location LatLang: $lastLocation")
                }
            }
        }



        if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                        appCompatActivity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(appCompatActivity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 102)
        } else {
            //TODO add not dismissable dialog to enable permissions

//            startLocationUpdates()

            val lm = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gps_enabled = false
            var network_enabled = false

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (ex: Exception) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (ex: Exception) {
            }

            if (!gps_enabled && !network_enabled) {
                // notify user

                val builder = android.support.v7.app.AlertDialog.Builder(context!!)

                // Set the alert dialog title
                builder.setTitle("Location Settings")

                builder.setMessage("Location services are required for posting please switch them on to continue.")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Open Settings") { dialog, which ->
                    // Do something when user press the positive button
                    context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }


                // Display a negative button on alert dialog
                builder.setNegativeButton("Dismiss") { dialog, which ->
                    Toast.makeText(
                            context,
                            "Location Services are necessary for posting",
                            Toast.LENGTH_SHORT
                    ).show()
                }


                // Finally, make the alert dialog using builder
                val dialog: android.support.v7.app.AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
            }


        }




    }


    private fun validate(quantity: String?, total: String?, vendor_details: String?, unit: String?): Boolean {
        `val` = true

        if (TextUtils.equals(unit, "Unit")) {
            Toast.makeText(context, "Select Unit", Toast.LENGTH_LONG).show()
            `val` = false

        }

        if (TextUtils.isEmpty(quantity)) {
            quantity_edit!!.error = "Enter Quantity"
            `val` = false
        }

        if (TextUtils.isEmpty(total)) {
            total_edit!!.error = "Enter Total"
            `val` = false
        }
        if (TextUtils.isEmpty(vendor_details)) {
            vendor_details_edit!!.error = "Enter Vendor Details"
            `val` = false
        }

        return `val`
    }

    @Throws(JSONException::class)
    private fun sendRequest(stockid: String, quantity: String?, units: String?, total: String?,
                            overheads: String?,rate:String? ,tax:String? , vendor_details: String?, ship_no: String?, details: String?, images: ArrayList<Packet>) {
        val app = application as App
        val params = HashMap<String, String>()
//        params["local_purchase"] = App.userId
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        //        JSONArray array = new JSONArray();
        val jsonObject = JSONObject()
        jsonObject.put("stock_id", stockid).put("project_id", App.projectId).put("user_id", App.userId)
                .put("quantity", quantity).put("units", units).put("total_amount", total)
                .put("overheads", overheads).put("rate",rate).put("tax",tax).put("vendor_details", vendor_details).put("slip_no", ship_no)
                .put("details", details)
        params["local_purchase"] = jsonObject.toString()
        console.log("Local Purchase$params")
        val array = JSONArray()
        for (p in images) {
            if (p.name != null) {
                val bm = BitmapFactory.decodeFile(p.name)
                val baos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos)
                val b = baos.toByteArray()
                val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                array.put(encodedImage)
            }
        }
        params["images"] = array.toString()
        console.log("Image$params")
        app.sendNetworkRequest(Config.SEND_LOCAL_PURCHASE, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                Toast.makeText(context, "Error$error", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                if (response == "1") {
                    Toast.makeText(context, "Request Generated", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(context, "Check Your Network", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                val packet = images!!.removeAt(0)
                packet.name = imagePath
                //                Uri uri=data.getData();
                //                packet.setName(getRealPathFromURI(uri));
                console.log("Image Path " + packet.name + "EXTRAS " + packet.extra)
                images!!.add(0, Packet())
                images!!.add(packet)
                imagesAdapter!!.notifyDataSetChanged()
            } else if (resultCode == android.app.Activity.RESULT_CANCELED) {
                console.log("Canceled")
            }
        } else if (requestCode == REQUEST_GALLERY_IMAGE) {
            val packet = images!!.removeAt(0)
            //            packet.setName(imagePath);
            val uri = data!!.data
            packet.name = getRealPathFromURI(uri)
            console.log("Image Path " + packet.name + "EXTRAS " + packet.extra)
            images!!.add(0, Packet())
            images!!.add(packet)
            imagesAdapter!!.notifyDataSetChanged()
        }
    }

    // And to convert the image URI to the direct file system path of the image file
    fun getRealPathFromURI(contentUri: Uri?): String? {

        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context!!.contentResolver.query(contentUri!!,
                proj, null, null, null)// Which columns to return
        // WHERE clause; which rows to return (all rows)
        // WHERE clause selection arguments (none)
        // Order-by clause (ascending by name)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            results = cursor.getString(column_index)
        }
        //                managedQuery( );
        cursor.moveToFirst()
        cursor.close()
        return results
    }

    companion object {
        //    name_edit name,
        private var quantity: String? = null
        private var total: String? = null
        private var overheads: String? = null
        private var unit: String? = null
        private var vendor_details: String? = null
        private var ship_no: String? = null
        private var details: String? = null
        private var results: String? = null
        val QUALITY = 10
        val REQUEST_CAPTURE_IMAGE = 7190
        val REQUEST_GALLERY_IMAGE = 7191
    }


}
