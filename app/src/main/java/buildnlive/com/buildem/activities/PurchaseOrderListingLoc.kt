package buildnlive.com.buildem.activities

import android.Manifest
import android.annotation.SuppressLint
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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
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

import buildnlive.com.buildem.adapters.PurchaseOrderListingAdapter
import buildnlive.com.buildem.adapters.SingleImageAdapter
import buildnlive.com.buildem.elements.OrderItem
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.google.android.gms.location.*

class PurchaseOrderListingLoc : AppCompatActivity() {
    private var app: App? = null
    private var id: String? = null
    private var list: RecyclerView? = null
    private var adapter: PurchaseOrderListingAdapter? = null
    private var itemList: MutableList<OrderItem>? = null
    private var submit: Button? = null
    private var alertBuilder: AlertDialog.Builder? = null
    private var imagePath: String? = null
    private var images: ArrayList<Packet>? = null
    private var imagesAdapter: SingleImageAdapter? = null
    private var challan: EditText? = null
    private var invoice: EditText? = null
    private var progress: ProgressBar? = null
    private var hider: TextView? = null
    private var context: Context? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location?=null
    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var handler: Handler? = null


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
        context = this


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



        setContentView(R.layout.activity_purchase_order_listing)
        list = findViewById(R.id.items)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = "Purchase Order"

        challan = findViewById(R.id.challan)
        invoice = findViewById(R.id.invoice)
        progress = findViewById(R.id.progress)
        hider = findViewById(R.id.hider)
        itemList = ArrayList()
        submit = findViewById(R.id.submit)
        adapter = PurchaseOrderListingAdapter(applicationContext, itemList, PurchaseOrderListingAdapter.OnItemClickListener { pos, view -> })
        list!!.layoutManager = LinearLayoutManager(applicationContext)
        list!!.adapter = adapter
        alertBuilder = AlertDialog.Builder(this)
        app = application as App
        val bundle = intent.extras
        id = bundle!!.getString("id")
        fetchOrders()

        val list = findViewById<AdvancedRecyclerView>(R.id.images)
        images = ArrayList()
        images!!.add(Packet())
        imagesAdapter = SingleImageAdapter(applicationContext, images, SingleImageAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.image_chooser, null)
                val dialogBuilder = AlertDialog.Builder(context!!, R.style.PinDialog)
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
                            val photoURI = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".provider", photoFile)
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
        list.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        list.setmMaxHeight(350)

        submit!!.setOnClickListener {
            //Setting message manually and performing action on button click
            alertBuilder!!.setMessage("Do you want to Submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        try {
                            pushOrders(images)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        //  Action for 'NO' Button
                        dialog.cancel()
                    }
            //Creating dialog box
            val alert = alertBuilder!!.create()
            //Setting the title manually
            alert.setTitle("Purchase Order")
            alert.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    @Throws(JSONException::class)
    private fun pushOrders(images: ArrayList<Packet>?) {
        val url = Config.REQ_PURCHASE_ORDER_UPDATE
        val params = HashMap<String, String>()
        params["user_id"] = App.userId
        val array = JSONArray()
        for (i in itemList!!.indices) {
            if (itemList!![i].isIncluded) {
                val obj = JSONObject()
                obj.put("qty_received", itemList!![i].quantity)
                obj.put("comments", "Done")
                obj.put("purchase_order_list_id", itemList!![i].orderId)
                array.put(obj)
            }
        }
        params["purchase_order_list"] = array.toString()
        params["purchase_order_id"] = id!!
        params["challan"] = challan!!.text.toString()
        params["invoice"] = invoice!!.text.toString()
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        val imageArray = JSONArray()
        for (p in images!!) {
            if (p.name != null) {
                val bm = BitmapFactory.decodeFile(p.name)
                val baos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos)
                val b = baos.toByteArray()
                val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                imageArray.put(encodedImage)
            }
        }
        console.log("Params$params")
        params["images"] = imageArray.toString()
        console.log("Image$params")
        app!!.sendNetworkRequest(url, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                if (response == "1") {
                    Toast.makeText(applicationContext, "Request generated", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        })
    }

    private fun fetchOrders() {
        var url = Config.REQ_PURCHASE_ORDER_LISTING
        url = url.replace("[0]", App.userId).replace("[1]", id!!)
        app!!.sendNetworkRequest(url, 0, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                try {
                    itemList!!.clear()
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        itemList!!.add(OrderItem().parseFromJSON(array.getJSONObject(i)))
                    }
                    adapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {

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
        //        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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
        val QUALITY = 10
        val REQUEST_CAPTURE_IMAGE = 7190
        val REQUEST_GALLERY_IMAGE = 7191
        private var results: String? = null
    }

}