package buildnlive.com.buildem.LocalPurchaseAndPayment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.SingleImageAdapter
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.google.android.gms.location.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragmentLoc : Fragment() {
    private var submit: Button? = null
    private var payPrivate: RadioButton? = null
    private var payPublic: RadioButton? = null
    private var progress: ProgressBar? = null
    private var `val` = true
    private var hider: TextView? = null
    private var amount_edit: EditText? = null
    private var overheads_edit: EditText? = null
    private var to_edit: EditText? = null
    private var reason_edit: EditText? = null
    private var details_edit: EditText? = null
    private var amount: String? = null
    private var reason: String? = null
    private var to: String? = null
    private var details: String? = null
    private var payment_type: String? = null
    private var payment_mode: String? = null
    private var type_of_payment = "Public"
    private var purpose: String? = null
    private val LOADING: Boolean = false
    private var purposeSpinner: Spinner? = null
    private var paymentTypeSpinner: Spinner? = null
    private var paymentModeSpinner: Spinner? = null
    private var alertBuilder: AlertDialog.Builder? = null

    private var radioGroup: RadioGroup? = null
    private var imagePath: String? = null
    private var images: ArrayList<Packet>? = null
    private var imagesAdapter: SingleImageAdapter? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null



    private var lastLocation: Location?=null
    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = null
    var mConetxt: Context? = null
    var handler: Handler? = null


    override fun onAttach(context: Context) {
        mConetxt=context
        super.onAttach(context)
    }

    override fun onAttach(activity: Activity) {
        appCompatActivity=activity as AppCompatActivity
        super.onAttach(activity)
    }

    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"
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

        val intent = Intent(mConetxt, FetchAddressIntentService::class.java).apply {
            putExtra(Constants.RECEIVER, resultReceiver)
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }
        mConetxt!!.startService(intent)
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



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submit = view.findViewById(R.id.submit)

        details_edit = view.findViewById(R.id.payment_details)
        to_edit = view.findViewById(R.id.receiver);
        reason_edit = view.findViewById(R.id.reason);
//                overheads_edit = view.findViewById(R.id.overheads);
        amount_edit = view.findViewById(R.id.amount)

        payPrivate = view.findViewById(R.id.payPrivate)
        payPublic = view.findViewById(R.id.paypublic)

        progress = view.findViewById(R.id.progress)

        paymentModeSpinner = view.findViewById(R.id.paymentMode)
        paymentTypeSpinner = view.findViewById(R.id.paymentType)
        purposeSpinner = view.findViewById(R.id.purpose)
        payPublic!!.isChecked = true
        payPrivate!!.isChecked = false

        radioGroup = view.findViewById<View>(R.id.PaymentRadio) as RadioGroup
        radioGroup!!.setOnCheckedChangeListener { radioGroup, i ->
            val rb = radioGroup.findViewById<View>(i) as RadioButton
            if (null != rb) {
                when (i) {
                    R.id.payPrivate -> type_of_payment = "Private"
                    R.id.paypublic -> type_of_payment = "Public"
                }
            }
        }

        purposeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                purpose = purposeSpinner!!.selectedItem.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        paymentTypeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                payment_type = paymentTypeSpinner!!.selectedItem.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        paymentModeSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                payment_mode = paymentModeSpinner!!.selectedItem.toString()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
        hider = view.findViewById(R.id.hider)
        alertBuilder = AlertDialog.Builder(mConetxt!!)


        if (LOADING) {
            progress!!.visibility = View.VISIBLE
            hider!!.visibility = View.VISIBLE
        } else {
            progress!!.visibility = View.GONE
            hider!!.visibility = View.GONE
        }

        val list = view.findViewById<AdvancedRecyclerView>(R.id.images)
        images = ArrayList()
        images!!.add(Packet())
        imagesAdapter = SingleImageAdapter(mConetxt, images, SingleImageAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.image_chooser, null)
                val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(mConetxt!!, R.style.PinDialog)
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
                    if (pictureIntent.resolveActivity(activity!!.packageManager) != null) {

                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                        } catch (ex: IOException) {
                        }

                        if (photoFile != null) {
                            val photoURI = FileProvider.getUriForFile(getContext()!!, BuildConfig.APPLICATION_ID + ".provider", photoFile)
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
        list.layoutManager = LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        list.setmMaxHeight(350)


        submit!!.setOnClickListener {
                            to=to_edit!!.getText().toString();
                            reason=reason_edit!!.getText().toString();
            amount = amount_edit!!.text.toString()
            //                overheads=overheads_edit.getText().toString();
            details = details_edit!!.text.toString()
            alertBuilder!!.setMessage("Are you sure?").setTitle("Payment")

            //Setting message manually and performing action on button click
            alertBuilder!!.setMessage("Do you want to Submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        if (validate(purpose, details,reason,amount,to, payment_mode, payment_type, type_of_payment)) {
                            try {
                                sendRequest(purpose, details, amount,reason,to, payment_mode, payment_type, type_of_payment, images!!)
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
            val alert = alertBuilder!!.create()
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
                        PrefernceFile.getInstance(mConetxt!!).setString("Lat",lastLocation!!.latitude.toString())
                        PrefernceFile.getInstance(mConetxt!!).setString("Long",lastLocation!!.longitude.toString())
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

            val lm = mConetxt!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

                val builder = androidx.appcompat.app.AlertDialog.Builder(mConetxt!!)

                // Set the alert dialog title
                builder.setTitle("Location Settings")

                builder.setMessage("Location services are required for posting please switch them on to continue.")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Open Settings") { dialog, which ->
                    // Do something when user press the positive button
                    mConetxt!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }


                // Display a negative button on alert dialog
                builder.setNegativeButton("Dismiss") { dialog, which ->
                    Toast.makeText(
                            mConetxt,
                            "Location Services are necessary for posting",
                            Toast.LENGTH_SHORT
                    ).show()
                }


                // Finally, make the alert dialog using builder
                val dialog: androidx.appcompat.app.AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
            }


        }



    }


    private fun validate(purpose: String?, details: String?, reason:String?,amount: String?, to:String?, payment_mode: String?, payment_type: String?, type_of_payment: String): Boolean {

        if (TextUtils.equals(payment_mode, "Select Payment Mode")) {
            Toast.makeText(getContext(), "Please Payment Mode", Toast.LENGTH_LONG).show()
            `val` = false
        }

        if (TextUtils.equals(payment_type, "Select Payment Type")) {
            Toast.makeText(getContext(), "Please Payment Type", Toast.LENGTH_LONG).show()
            `val` = false
        }


        if (TextUtils.isEmpty(details)) {
            details_edit!!.error = "Enter Details"
            `val` = false
        }
        if(TextUtils.isEmpty(reason)){
            reason_edit!!.error = "Enter Reason";
            `val`=false;
        }
        if (TextUtils.isEmpty(amount)) {
            amount_edit!!.error = "Enter Amount"
            `val` = false
        }
                if(TextUtils.isEmpty(to)){
                    to_edit!!.error = "Enter Payee";
                    `val`=false;
                }
        return `val`
    }

    @Throws(JSONException::class)
    private fun sendRequest(purpose: String?, details: String?, amount: String?,payee:String?,reason:String?, payment_mode: String?, payment_type: String?, type_of_payment: String, images: ArrayList<Packet>) {
        val app = activity!!.application as App
        val params = HashMap<String, String>()
        val jsonObject = JSONObject()
        jsonObject.put("project_id", App.projectId).put("user_id", App.userId).put("purpose", purpose).put("details", details)
                .put("amount", amount).put("payee",payee).put("reason",reason).put("payment_mode", payment_mode).put("payment_type", payment_type)
                .put("type_of_payment", type_of_payment)
        params["site_payments"] = jsonObject.toString()
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        console.log("Res:$params")
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

        app.sendNetworkRequest(Config.SEND_SITE_PAYMENTS, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                Toast.makeText(getContext(), "Something went wrong, Try again later", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                console.log(response)
                if (response == "1") {
                    Toast.makeText(getContext(), "Request Generated", Toast.LENGTH_SHORT).show()
                    activity!!.finish()
                }
            }
        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        val cursor = mConetxt!!.contentResolver.query(contentUri!!,
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
        //    to, reason, overheads,
        private var results: String? = null
        val QUALITY = 10
        val REQUEST_CAPTURE_IMAGE = 7190
        val REQUEST_GALLERY_IMAGE = 7191


        fun newInstance(): PaymentFragmentLoc {
            return PaymentFragmentLoc()
        }
    }


}