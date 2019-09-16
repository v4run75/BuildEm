package buildnlive.com.buildem.MarkAttendance

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.Worker
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import io.realm.Realm
import io.realm.RealmResults
import org.json.JSONArray
import org.json.JSONException

class MarkAttendanceKotlin : AppCompatActivity() {
    private var app: App? = null
    private var realm: Realm? = null
    private var edit: TextView? = null
    private var view: TextView? = null
    private var fragment: Fragment? = null
    private var listener: Interfaces.SyncListener? = null
    private var workers: RealmResults<Worker>? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation:Location?=null
//    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var mConetxt: Context? = null
    var handler: Handler? = null

//
//    object Constants {
//        const val SUCCESS_RESULT = 0
//        const val FAILURE_RESULT = 1
//        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
//        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
//        const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
//        const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
//    }
//
//
//    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
//
//        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
//
//            // Display the address string
//            // or an error message sent from the intent service.
//            outputLocation = resultData?.getString(Constants.RESULT_DATA_KEY) ?: ""
//
//
//            // Show a toast message if an address was found.
//            if (resultCode == Constants.SUCCESS_RESULT) {
////                utilityofActivity!!.toast(getString(R.string.address_found))
////                utilityofActivity!!.toast(outputLocation!!)
//                utilityofActivity!!.dismissProgressDialog()
////                dialogView!!.findViewById<TextView>(R.id.address).text = outputLocation
////                next()
//                console.log("Location: "+ outputLocation)
//
//            } else {
//                utilityofActivity!!.toast("Error")
//                utilityofActivity!!.dismissProgressDialog()
//            }
//
//        }
//    }
//
//
//    private fun startIntentService() {
//
//        val intent = Intent(this, FetchAddressIntentService::class.java).apply {
//            putExtra(Constants.RECEIVER, resultReceiver)
//            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
//        }
//        startService(intent)
//    }
//
//
//    override fun onPause() {
//        super.onPause()
//        stopLocationUpdates()
//    }
//
//    private fun stopLocationUpdates() {
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//    private val requestingLocationUpdates: Boolean = true
//
//    override fun onResume() {
//        super.onResume()
//        if (requestingLocationUpdates) startLocationUpdates()
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun startLocationUpdates() {
//        fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                null /* Looper */
//        )
//    }
//
//
//
    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mConetxt = this


//        resultReceiver = AddressResultReceiver(Handler())
//
//        utilityofActivity = UtilityofActivity(appCompatActivity!!)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//
//        locationRequest = LocationRequest.create()?.apply {
//            interval = 500
//            fastestInterval = 500
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest!!)
//
//        builder.build()
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                locationResult ?: return
//                for (location in locationResult.locations) {
//                    if (location != null)
//                        lastLocation = location
//                    if(lastLocation!=null){
//                        PrefernceFile.getInstance(mConetxt!!).setString("Lat",lastLocation!!.latitude.toString())
//                        PrefernceFile.getInstance(mConetxt!!).setString("Long",lastLocation!!.longitude.toString())
//                        fetchAddressButtonHander()
//                    }
//                    console.log("Location LatLang: $lastLocation")
//                }
//            }
//        }
//
//
//
//        if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                )) != PackageManager.PERMISSION_GRANTED)
//        ) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 102)
//        } else {
//            //TODO add not dismissable dialog to enable permissions
//
////            startLocationUpdates()
//
//            val lm = mConetxt!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            var gps_enabled = false
//            var network_enabled = false
//
//            try {
//                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            } catch (ex: Exception) {
//            }
//
//            try {
//                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            } catch (ex: Exception) {
//            }
//
//            if (!gps_enabled && !network_enabled) {
//                // notify user
//
//                val builder = AlertDialog.Builder(mConetxt!!)
//
//                // Set the alert dialog title
//                builder.setTitle("Location Settings")
//
//                builder.setMessage("Location services are required for posting please switch them on to continue.")
//                // Set a positive button and its click listener on alert dialog
//                builder.setPositiveButton("Open Settings") { dialog, which ->
//                    // Do something when user press the positive button
//                    mConetxt!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//
//                }
//
//
//                // Display a negative button on alert dialog
//                builder.setNegativeButton("Dismiss") { dialog, which ->
//                    Toast.makeText(
//                            applicationContext,
//                            "Location Services are necessary for posting",
//                            Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//
//                // Finally, make the alert dialog using builder
//                val dialog: AlertDialog = builder.create()
//
//                // Display the alert dialog on app interface
//                dialog.show()
//            } else {
//
////            utilityofActivity!!.showProgressDialog()
//                startLocationUpdates()
//            }
//
//
//        }
//
//




        utilityofActivity = UtilityofActivity(appCompatActivity!!)


        setContentView(R.layout.activity_mark_attendance)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = "Attendance"
        app = application as App
        realm = Realm.getDefaultInstance()
        workers = realm!!.where(Worker::class.java).equalTo("belongsTo", App.belongsTo).findAllAsync()
        fragment = MarkAttendanceLocFragment.newInstance(app!!, workers!!)
        listener = fragment as Interfaces.SyncListener?
        changeScreen()
        edit = findViewById(R.id.edit)
        edit!!.setOnClickListener {
            enableEdit()
            disableView()
            fragment = MarkAttendanceLocFragment.newInstance(app!!, workers!!)
            listener = fragment as Interfaces.SyncListener?
            changeScreen()
        }
        view = findViewById(R.id.view)
        view!!.setOnClickListener {
            enableView()
            disableEdit()
            fragment = buildnlive.com.buildem.MarkAttendance.ViewAttendanceFragment.newInstance(workers)
            listener = null
            changeScreen()
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

//    fun fetchAddressButtonHander() {
//
//
//        startIntentService()
//
//
//    }


    private fun disableView() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_left, null))
        } else {
            view!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_left)
        }
        view!!.setTextColor(resources.getColor(R.color.color2))
    }

    private fun enableView() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_left_selected, null))
        } else {
            view!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_left_selected)
        }
        view!!.setTextColor(resources.getColor(R.color.white))
    }

    private fun disableEdit() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            edit!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_right, null))
        } else {
            edit!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_right)
        }
        edit!!.setTextColor(resources.getColor(R.color.color2))
    }

    private fun enableEdit() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            edit!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_right_selected, null))
        } else {
            edit!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_right_selected)
        }
        edit!!.setTextColor(resources.getColor(R.color.white))
    }

    private fun refresh() {
        var requestUrl = Config.REQ_GET_LABOUR
        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", App.projectId)
        app!!.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                if (listener != null) {
                    listener!!.onSyncStart()
                }
            }

            override fun onNetworkRequestError(error: String) {
                if (listener != null) {
                    listener!!.onSyncError(error)
                }
                console.error("Network request failed with error :$error")
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                if (listener != null) {
                    listener!!.onSyncFinish()
                }
                console.log("Response:$response")
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        val worker = Worker().parseFromJSON(array.getJSONObject(i))
                        realm!!.executeTransaction { realm ->
                            val u = realm.where(Worker::class.java).equalTo("id", worker.id).findFirst()
                            if (u == null) {
                                realm.copyToRealm(worker)
                            }
                        }
                    }
                    if (listener != null) {
                        listener!!.onSync(workers)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun changeScreen() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.attendance_content, fragment!!)
                .commit()
    }

//    override fun onRequestPermissionsResult(
//            requestCode: Int, permissions: Array<String>,
//            grantResults: IntArray
//    ) {
//        if (grantResults.isNotEmpty()) {
//            when (requestCode) {
//                102 -> {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                        startLocationUpdates()
//
//
//                    }
//
//                }
//            }
//        }
//    }

}
