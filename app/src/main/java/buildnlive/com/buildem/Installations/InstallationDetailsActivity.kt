package buildnlive.com.buildem.Installations

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.ActivityImagesAdapter
import buildnlive.com.buildem.adapters.InstallationAdapter
import buildnlive.com.buildem.elements.InstallationActivityItem
import buildnlive.com.buildem.elements.InstallationItem
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_installation_details.*
import kotlinx.android.synthetic.main.content_installation_details.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class InstallationDetailsActivity : AppCompatActivity() {
    var context: Context? = null
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var installationItem: InstallationItem? = null
    private var listAdapter: InstallationAdapter? = null
    private var itemList: ArrayList<InstallationActivityItem> = ArrayList()
    private var resultList: ArrayList<InstallationActivityItem> = ArrayList()
    var app: App? = null

    private var images: java.util.ArrayList<Packet>? = null
    private var imagesAdapter: ActivityImagesAdapter? = null
    private var imagePath: String? = null
    private var results: String? = null
    var quantity: String? = null

    companion object {
        val QUALITY = 10
        val REQUEST_GALLERY_IMAGE = 7191
        val REQUEST_CAPTURE_IMAGE = 7190
    }

    private var listener = object : InstallationAdapter.OnItemClickListener {
        override fun onItemClick(installationItem: InstallationActivityItem, pos: Int, view: View) {
        }

        override fun onItemCheck(installationItem: InstallationActivityItem, pos: Int, view: View, qty: TextView) {
            installationItem.quantity = qty.text.toString()
            resultList.add(installationItem)
        }

    }


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location? = null
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback

    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
        setContentView(R.layout.activity_installation_details)

        context = this

        app = application as App

        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = getString(R.string.installations)
        val bundle = intent.extras


        if (bundle != null) {
            installationItem = bundle.getParcelable("installationItem")
        }


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
                utilityofActivity!!.dismissFetchLocationDialog()
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null)
                        lastLocation = location
                    if (lastLocation != null) {
                        PrefernceFile.getInstance(context!!).setString("Lat", lastLocation!!.latitude.toString())
                        PrefernceFile.getInstance(context!!).setString("Long", lastLocation!!.longitude.toString())
                        startJob()
                    } else {
                        utilityofActivity!!.toast("Some error occurred, Could not fetch location")
                    }
                    console.log("Location LatLang: $lastLocation")
                }
            }
        }


        name.text = installationItem!!.name
        address.text = installationItem!!.address
        email.text = installationItem!!.email
//        email.text = "Email: "+installationItem!!.email
//        mobileNo.text = "Mobile No: "+installationItem!!.mobileNo
        mobileNo.text = String.format(getString(R.string.mobileholder), installationItem!!.mobileNo)
        date.text = installationItem!!.date
        time.text = installationItem!!.time

        comment.movementMethod = ScrollingMovementMethod()
        comment.text = String.format(getString(R.string.commentholder), installationItem!!.comment)


        utilityofActivity = UtilityofActivity(appCompatActivity!!)
        utilityofActivity!!.configureToolbar(appCompatActivity!!)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = InstallationAdapter(context!!, ArrayList<InstallationActivityItem>(), listener)
//        itemList.add()
//        listAdapter = InstallationAdapter(context,itemList, listener)
        items!!.adapter = listAdapter

        next.setOnClickListener {
            menuUpdate()
        }

        add.setOnClickListener {

            val intent = Intent(context, AddInstallation::class.java)
            intent.putExtra("installationId", installationItem!!.serviceId)
            startActivity(intent)

        }

        onJob.setOnClickListener {
            val builder = Builder(context!!)

            builder.setTitle("Start Job")

            builder.setPositiveButton("Start", DialogInterface.OnClickListener { dialogInterface, i ->

                if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                                appCompatActivity!!,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )) != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(appCompatActivity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 102)
                } else {

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

                        val builder = AlertDialog.Builder(context!!)

                        builder.setTitle("Location Settings")

                        builder.setMessage("Location services are required for posting please switch them on to continue.")
                        builder.setPositiveButton("Open Settings") { dialog, which ->
                            context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        }
                        builder.setNegativeButton("Dismiss") { dialog, which ->
                            Toast.makeText(
                                    context,
                                    "Location Services are necessary for posting",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    } else {
                        utilityofActivity!!.showFetchLocationDialog()
                        startLocationUpdates()
                    }
                }
            })

            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })

            val dialog = builder.create()
            dialog.show()
        }

        getServiceActivities()

    }

    private fun startJob() {
        val requestUrl = Config.UpdateUserSite

        val params = HashMap<String, String>()

        params["id"] = installationItem!!.serviceId
        params["type"] = "Installation"
        params["user_id"] = App.userId
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()

        console.log("Installation URL:  $requestUrl")
        console.log("Params:  $params")

        app!!.sendNetworkRequest(requestUrl, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                utilityofActivity!!.showProgressDialog()
            }

            override fun onNetworkRequestError(error: String) {
                stopLocationUpdates()
                utilityofActivity!!.dismissProgressDialog()
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                stopLocationUpdates()
                utilityofActivity!!.dismissProgressDialog()

                try {
                    if (response == "1") {
                        utilityofActivity!!.toast("Job Successfully Started")
                    } else
                        utilityofActivity!!.toast("Some error occurred, Please try again")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun getServiceActivities() {
        val requestUrl = Config.SERVICES_ACTIVITIES
        val params = HashMap<String, String>()
        params["service_id"] = installationItem!!.serviceId

//        requestUrl = requestUrl.replace("[0]", App.userId)

        itemList.clear()

        console.log("Services:  $requestUrl")

        app!!.sendNetworkRequest(requestUrl, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                utilityofActivity!!.showProgressDialog()
            }

            override fun onNetworkRequestError(error: String) {

                utilityofActivity!!.dismissProgressDialog()
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                utilityofActivity!!.dismissProgressDialog()
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        itemList.add(InstallationActivityItem().parseFromJSON(array.getJSONObject(i)))
                    }
                    console.log("data set changed")

                    if (itemList.isEmpty()) {
//                        Toast.makeText(mContext, "No Results", Toast.LENGTH_LONG).show()
                        no_content.visibility = View.VISIBLE
                    } else {
                        no_content.visibility = View.GONE
                        listAdapter!!.addItems(itemList)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun menuUpdate() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog_image_upload, null)

        val alert_title = dialogView.findViewById<TextView>(R.id.alert_title)
        alert_title.text = getString(R.string.installations)

        val dialogBuilder = AlertDialog.Builder(context!!, R.style.PinDialog)
        val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
        alertDialog.show()

        val message = dialogView.findViewById<EditText>(R.id.message)

        val list = dialogView.findViewById<AdvancedRecyclerView>(R.id.images)
        images = java.util.ArrayList()
        images!!.add(Packet())
        imagesAdapter = ActivityImagesAdapter(context, images, ActivityImagesAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflaterChooser = layoutInflater
                val dialogViewChooser = inflaterChooser.inflate(R.layout.image_chooser, null)
                val dialogBuilderChooser = AlertDialog.Builder(context!!, R.style.PinDialog)
                val alertDialogChooser = dialogBuilderChooser.setCancelable(false).setView(dialogViewChooser).create()
                alertDialogChooser.show()
                val gallery = dialogViewChooser.findViewById<TextView>(R.id.gallery)
                val camera = dialogViewChooser.findViewById<TextView>(R.id.camera)

                gallery.setOnClickListener {
                    if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )) != PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                104
                        )
                    } else {
                        alertDialogChooser.dismiss()
                        val pictureIntent = Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pictureIntent, REQUEST_GALLERY_IMAGE)
                    }
                }

                camera.setOnClickListener {

                    if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.CAMERA
                            )) != PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                                101
                        )
                    } else {
                        alertDialogChooser.dismiss()
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

                }

                val negative = dialogViewChooser.findViewById<Button>(R.id.negative)
                negative.setOnClickListener { alertDialogChooser.dismiss() }

            } else {
                images!!.removeAt(pos)
                imagesAdapter!!.notifyItemRemoved(pos)
                imagesAdapter!!.notifyDataSetChanged()
            }
        })
        list.adapter = imagesAdapter
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        list.setmMaxHeight(400)

        val positive = dialogView.findViewById<Button>(R.id.positive)
        positive.setOnClickListener {
            try {
                submit(installationItem!!.serviceId, message.text.toString(), images, resultList, alertDialog)
            } catch (e: Exception) {
                Toast.makeText(context, "Fill data properly!", Toast.LENGTH_SHORT).show()
            }
        }
        val negative = dialogView.findViewById<Button>(R.id.negative)
        negative.setOnClickListener { alertDialog.dismiss() }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            when (requestCode) {
                101 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    } else {
                        utilityofActivity!!.toast("Some functions may not work unless you allow permissions")
                    }
                }
                104 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        val pictureIntent = Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pictureIntent, REQUEST_GALLERY_IMAGE)
                    } else {
                        utilityofActivity!!.toast("Some functions may not work unless you allow permissions")
                    }

                }

            }
        }
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
            if (data != null) {
                val uri = data.data
                packet.name = getRealPathFromURI(uri)
                console.log("Image Path " + packet.name + "EXTRAS " + packet.extra)
                images!!.add(0, Packet())
                images!!.add(packet)
                imagesAdapter!!.notifyDataSetChanged()
            }

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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    @Throws(JSONException::class)
    private fun submit(service_id: String, comment: String, images: ArrayList<Packet>?, list: ArrayList<InstallationActivityItem>, alertDialog: AlertDialog) {

        utilityofActivity!!.showProgressDialog()

        val params = HashMap<String, String>()
        params["comment"] = comment
        params["service_id"] = service_id

        val jsonArray = JSONArray()

        for (i in list) {
            jsonArray.put(JSONObject().put("id", i.id)
                    .put("name", i.name)
                    .put("quantity", i.quantity)
                    .put("type", i.type))
        }

        params["list"] = jsonArray.toString()

        console.log("Service  $params")

        val array = JSONArray()
        for (p in images!!) {
            if (p.name != null) {
                val bm = BitmapFactory.decodeFile(p.name)
                val baos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos)
                val b = baos.toByteArray()
                val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                array.put(encodedImage)
            }

            params["images"] = array.toString()
            console.log("Service Images  $params")
        }

        app!!.sendNetworkRequest(Config.GET_SERVICE_UPDATES, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {

            }

            override fun onNetworkRequestError(error: String) {
                utilityofActivity!!.dismissProgressDialog()
                utilityofActivity!!.toast("Something went wrong, Try again later")

            }

            override fun onNetworkRequestComplete(response: String) {
                utilityofActivity!!.dismissProgressDialog()
                if (response == "1") {
                    Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    val imm = appCompatActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)

                    finish()
                } else {
                    utilityofActivity!!.toast("Invalid Response from server")
                }
            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
