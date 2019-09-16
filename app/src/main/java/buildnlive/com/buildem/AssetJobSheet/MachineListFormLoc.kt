package buildnlive.com.buildem.AssetJobSheet

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
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
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.elements.Machine
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.google.android.gms.location.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MachineListFormLoc : AppCompatActivity() {

    private var submit: Button? = null
    private var progress: ProgressBar? = null
    private var `val` = true
    private var hider: TextView? = null
    private var name: TextView? = null
    private var loginTime: TextView? = null
    private var logoutTime: TextView? = null
    private var description: EditText? = null
    private var service_time: EditText? = null

    private val LOADING: Boolean = false
    private var builder: AlertDialog.Builder? = null
    private var selectedItem: Machine? = null
    private var context: Context? = null
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private val mYear: String? = null
    private var mMonth: String? = null
    private var mDay: String? = null
    private var sHour: String? = null
    private var sMinute: String? = null
    private val c = Calendar.getInstance()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location?=null
    private lateinit var resultReceiver: buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var handler: Handler? = null


    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
        const val RECEIVER = "${buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.PACKAGE_NAME}.RECEIVER"
        const val RESULT_DATA_KEY = "${buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.PACKAGE_NAME}.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "${buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.PACKAGE_NAME}.LOCATION_DATA_EXTRA"
    }


    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            // Display the address string
            // or an error message sent from the intent service.
            outputLocation = resultData?.getString(buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.RESULT_DATA_KEY) ?: ""


            // Show a toast message if an address was found.
            if (resultCode == buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.SUCCESS_RESULT) {
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
            putExtra(buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.RECEIVER, resultReceiver)
            putExtra(buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Constants.LOCATION_DATA_EXTRA, lastLocation)
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
        setContentView(R.layout.activity_machine_list_form)

        context = this

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = "Job Sheet"
        val bundle = intent.extras


        if (bundle != null) {
            selectedItem = bundle.getSerializable("Items") as Machine
        }




        builder = AlertDialog.Builder(context)


        name = findViewById(R.id.name)
        description = findViewById(R.id.description)
        service_time = findViewById(R.id.service_time)
        loginTime = findViewById(R.id.login_time)
        logoutTime = findViewById(R.id.logout_time)


        name!!.text = selectedItem!!.name

        loginTime!!.setOnClickListener {
            // Get Current Time

            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        loginTime!!.text = "$hourOfDay:$minute"

                        if (c.get(Calendar.MONTH) < 10) {
                            mMonth = "0" + c.get(Calendar.MONTH)
                        } else
                            mMonth = "" + c.get(Calendar.MONTH)

                        if (c.get(Calendar.DATE) < 10) {
                            mDay = "0" + c.get(Calendar.DATE)
                        } else
                            mDay = "" + c.get(Calendar.DATE)

                        if (hourOfDay < 10) {
                            sHour = "0$hourOfDay"
                        } else
                            sHour = "" + hourOfDay

                        if (minute < 10) {
                            sMinute = "0$minute"
                        } else
                            sMinute = "" + minute

                        buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Companion.loginTimeText = c.get(Calendar.YEAR).toString() + "-" + mMonth + "-" + mDay + " " + sHour + ":" + sMinute + ":" + "00"
                    }, mHour, mMinute, true)
            timePickerDialog.show()
        }

        logoutTime!!.setOnClickListener {
            // Get Current Time

            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)
            //                     c.get(Calendar.SECOND);

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(context,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        logoutTime!!.text = "$hourOfDay:$minute"

                        if (c.get(Calendar.MONTH) < 10) {
                            mMonth = "0" + (c.get(Calendar.MONTH) + 1)
                        } else
                            mMonth = "" + (c.get(Calendar.MONTH) + 1)

                        if (c.get(Calendar.DATE) < 10) {
                            mDay = "0" + c.get(Calendar.DATE)
                        } else
                            mDay = "" + c.get(Calendar.DATE)

                        if (hourOfDay < 10) {
                            sHour = "0$hourOfDay"
                        } else
                            sHour = "" + hourOfDay

                        if (minute < 10) {
                            sMinute = "0$minute"
                        } else
                            sMinute = "" + minute

                        buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Companion.logoutTimeText = c.get(Calendar.YEAR).toString() + "-" + mMonth + "-" + mDay + " " + sHour + ":" + sMinute + ":" + "00"
                    }, mHour, mMinute, true)
            timePickerDialog.show()
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



        submit = findViewById(R.id.submit)


        submit!!.setOnClickListener {
            //                name=name_edit.getText().toString();

            //Setting message manually and performing action on button click
            builder!!.setMessage("Do you want to Submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        if (validate(loginTime!!.text.toString(), logoutTime!!.text.toString(), description!!.text.toString())) {
                            console.log("From Validate")
                            try {
                                sendRequest(selectedItem!!.asset_id, selectedItem!!.inventory_item_rent_id, buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Companion.loginTimeText, buildnlive.com.buildem.AssetJobSheet.MachineListFormLoc.Companion.logoutTimeText, service_time!!.text.toString(), description!!.text.toString())
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
            alert.setTitle("Job Sheet")
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

                val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)

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
                val dialog: androidx.appcompat.app.AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
            }


        }




    }


    private fun validate(loginTimeText: String, logoutTimeText: String, descriptionText: String): Boolean {
        `val` = true


        if (TextUtils.equals(loginTimeText, "Login Time")) {
            Toast.makeText(context, "Select Login Time", Toast.LENGTH_LONG).show()
            `val` = false
        }

        if (TextUtils.equals(logoutTimeText, "Logout Time")) {
            Toast.makeText(context, "Select Logout Time", Toast.LENGTH_LONG).show()
            `val` = false
        }
        if (TextUtils.isEmpty(descriptionText)) {
            description!!.error = "Enter Description"
            `val` = false
        }

        return `val`
    }

    @Throws(JSONException::class)
    private fun sendRequest(asset_id: String, inventory_item_rent_id: String, log_in_time: String?, log_out_time: String?,
                            service_time: String, work_description: String) {
        val app = application as App
        val params = HashMap<String, String>()
        params["asset_jobsheet"] = App.userId
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        //        JSONArray array = new JSONArray();
        val jsonObject = JSONObject()
        jsonObject.put("asset_id", asset_id).put("project_id", App.projectId).put("user_id", App.userId)
                .put("inventory_item_rent_id", inventory_item_rent_id).put("log_in_time", log_in_time)
                .put("log_out_time", log_out_time).put("service_time", service_time)
                .put("work_description", work_description)

        params["asset_jobsheet"] = jsonObject.toString()
        console.log("JobSheet $params")


        app.sendNetworkRequest(Config.SEND_MACHINE_LIST, 1, params, object : Interfaces.NetworkInterfaceListener {
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

    companion object {

        private var loginTimeText: String? = null
        private var logoutTimeText: String? = null
        private val serviceTimeText: String? = null
        private val descriptionText: String? = null
    }


}
