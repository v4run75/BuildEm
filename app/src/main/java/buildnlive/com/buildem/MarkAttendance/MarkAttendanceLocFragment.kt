package buildnlive.com.buildem.MarkAttendance

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.AttendanceAdapter
import buildnlive.com.buildem.adapters.ListAdapter
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.Worker
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import buildnlive.com.buildem.utils.Utils
import com.android.volley.Request
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Realm
import io.realm.RealmResults
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MarkAttendanceLocFragment : Fragment(), Interfaces.SyncListener {
    private var submit: Button? = null
    private var attendees: RecyclerView? = null
    private var progress: ProgressBar? = null
    private var hider: TextView? = null
    private var LOADING: Boolean = false
    private var adapter: AttendanceAdapter? = null
    private var fab: FloatingActionButton? = null

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
        return inflater.inflate(R.layout.fragment_mark_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

                val builder = AlertDialog.Builder(mConetxt!!)

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
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
            }


        }



        attendees = view.findViewById(R.id.attendees)
        submit = view.findViewById(R.id.submit)
        progress = view.findViewById(R.id.progress)
        hider = view.findViewById(R.id.hider)
        fab = view.findViewById(R.id.fab)
        fab!!.setOnClickListener { startActivity(Intent(activity, buildnlive.com.buildem.LabourReport.CreateLabour::class.java)) }


        adapter = AttendanceAdapter(context, workers, AttendanceAdapter.OnItemClickListener { worker, pos, view -> showUser(worker) })
        attendees!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        attendees!!.adapter = adapter

        submit!!.setOnClickListener {
            val checkingOutWorkers = ArrayList<Worker>()
            val checkIn = JSONArray()
            val checkOut = JSONArray()
            val realm = Realm.getDefaultInstance()
            for (s in AttendanceAdapter.ViewHolder.changedUsers.keys) {
                val u = realm.where(Worker::class.java).equalTo("id", s).findFirst()
                try {
                    if (AttendanceAdapter.ViewHolder.changedUsers[s]!!) {
                        checkingOutWorkers.add(u!!)
                        checkOut.put(JSONObject().put("starttime", Utils.fromTimeStampToDate(u.checkInTime)).put("finishtime", Utils.fromTimeStampToDate(System.currentTimeMillis())).put("attendence_id", u.attendanceId).put("latitude",lastLocation!!.latitude).put("longitude",lastLocation!!.longitude))
                    } else {
                        checkIn.put(JSONObject().put("starttime", Utils.fromTimeStampToDate(System.currentTimeMillis())).put("labour_id", u!!.workerId).put("latitude",lastLocation!!.latitude).put("longitude",lastLocation!!.longitude))
                    }
                } catch (e: JSONException) {

                }

            }
            realm.close()
            if (checkIn.length() > 0) {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
                val params = HashMap<String, String>()
                params["user_id"] = App.userId
                params["project_id"] = App.projectId
                params["latitude"] = lastLocation!!.latitude.toString()
                params["longitude"] = lastLocation!!.longitude.toString()
                params["project_id"] = App.projectId
                params["attendence"] = checkIn.toString() // TODO PLEASE CHECK SPELLING MISTAKES
                app!!.sendNetworkRequest(Config.REQ_POST_CHECK_IN, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
                    override fun onNetworkRequestStart() {

                    }

                    override fun onNetworkRequestError(error: String) {
                        progress!!.visibility = View.GONE
                        hider!!.visibility = View.GONE
                    }

                    override fun onNetworkRequestComplete(response: String) {
                        progress!!.visibility = View.GONE
                        hider!!.visibility = View.GONE
                        AttendanceAdapter.ViewHolder.changedUsers.clear()
                        console.log("CheckIn Response:$response")
                        try {
                            val array = JSONArray(response)
                            val realm = Realm.getDefaultInstance()
                            for (i in 0 until array.length()) {
                                val obj = array.getJSONObject(i)
                                realm.executeTransaction { realm ->
                                    try {
                                        val u = realm.where(Worker::class.java).equalTo("id", obj.getString("labour_id") + App.belongsTo).findFirst()
                                        u!!.isCheckedIn = true
                                        u.checkInTime = System.currentTimeMillis()
                                        u.attendanceId = obj.getString("attendence_id")
                                        realm.copyToRealmOrUpdate(u)
                                    } catch (e: JSONException) {

                                    }
                                }
                            }
                            realm.close()
                        } catch (e: JSONException) {

                        }

                        Toast.makeText(context, "Attendance Updated", Toast.LENGTH_LONG).show()
                    }
                })
            }

            if (checkOut.length() > 0) {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
                val params = HashMap<String, String>()
                params["user_id"] = App.userId
                params["project_id"] = App.projectId
                params["latitude"] = lastLocation!!.latitude.toString()
                params["longitude"] = lastLocation!!.longitude.toString()
                params["attendence"] = checkOut.toString() // TODO PLEASE CHECK SPELLING MISTAKES
                app!!.sendNetworkRequest(Config.REQ_POST_CHECK_OUT, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
                    override fun onNetworkRequestStart() {
                        console.log(checkOut.toString())
                    }

                    override fun onNetworkRequestError(error: String) {
                        progress!!.visibility = View.GONE
                        hider!!.visibility = View.GONE
                    }

                    override fun onNetworkRequestComplete(response: String) {
                        AttendanceAdapter.ViewHolder.changedUsers.clear()
                        progress!!.visibility = View.GONE
                        hider!!.visibility = View.GONE
                        console.log("CheckOut Response:$response")
                        if (response == "1") {
                            val realm = Realm.getDefaultInstance()
                            realm.executeTransaction { realm ->
                                for (u in checkingOutWorkers) {
                                    u.checkOutTime = System.currentTimeMillis()
                                    u.isCheckedOut = true
                                    realm.copyToRealmOrUpdate(u)
                                }
                            }
                            realm.close()
                            Toast.makeText(context, "Attendance Updated", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
        }

        if (LOADING) {
            progress!!.visibility = View.VISIBLE
            hider!!.visibility = View.VISIBLE
        } else {
            progress!!.visibility = View.GONE
            hider!!.visibility = View.GONE
        }
    }

    fun fetchAddressButtonHander() {


        startIntentService()


    }


    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            when (requestCode) {
                102 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        startLocationUpdates()


                    }

                }
            }
        }
    }


    private fun showUser(worker: Worker) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog, null)
        val dialogBuilder = AlertDialog.Builder(activity!!, R.style.PinDialog)
        val alertDialog = dialogBuilder.setCancelable(true).setView(dialogView).create()
        alertDialog.show()
        val disable = dialogView.findViewById<TextView>(R.id.disableView)
        val progress = dialogView.findViewById<ProgressBar>(R.id.progress)
        val list = dialogView.findViewById<RecyclerView>(R.id.list)
        //        list.setmMaxHeight(400);
        val title = dialogView.findViewById<TextView>(R.id.alert_title)
        title.text = "Worker Details"
        val message = dialogView.findViewById<TextView>(R.id.alert_message)
        message.text = worker.name + " (" + worker.roleAssigned + ")"
        val positive = dialogView.findViewById<Button>(R.id.positive)
        positive.text = "Close"
        positive.setOnClickListener { alertDialog.dismiss() }
        val negative = dialogView.findViewById<Button>(R.id.negative)
        negative.visibility = View.GONE
        var requestUrl = Config.REQ_GET_USER_ATTENDANCE
        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", worker.workerId)
        requestUrl = requestUrl.replace("[2]", App.projectId)
        app!!.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {

            }

            override fun onNetworkRequestError(error: String) {

            }

            override fun onNetworkRequestComplete(response: String) {
                console.log("Request :$response")
                try {
                    val packets = parseRequest(response)
                    val adapter = ListAdapter(context, packets, ListAdapter.OnItemClickListener { packet, pos, view -> })
                    disable.visibility = View.GONE
                    progress.visibility = View.GONE
                    val manager = LinearLayoutManager(context)
                    list.layoutManager = manager
                    list.visibility = View.VISIBLE
                    list.adapter = adapter
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    @Throws(JSONException::class)
    private fun parseRequest(response: String): List<Packet> {
        val array = JSONArray(response)
        val packets = ArrayList<Packet>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            packets.add(Packet(obj.getString("start_date_time"), obj.getString("end_date_time"), 7190))
        }
        return packets
    }

    override fun onSyncError(error: String) {

    }

    override fun onSync(`object`: Any) {
        workers = `object` as RealmResults<Worker>
        adapter!!.notifyDataSetChanged()
    }

    override fun onSyncFinish() {
        LOADING = false
        if (progress != null) {
            progress!!.visibility = View.GONE
            hider!!.visibility = View.GONE
        }
    }

    override fun onSyncStart() {
        LOADING = true
        if (progress != null) {
            progress!!.visibility = View.VISIBLE
            hider!!.visibility = View.VISIBLE
        }
    }

    companion object {
        private var workers: RealmResults<Worker>? = null
        private var app: App? = null


        fun newInstance(a: App, u: RealmResults<Worker>): MarkAttendanceLocFragment {
            workers = u
            app = a
            return MarkAttendanceLocFragment()
        }
    }
}