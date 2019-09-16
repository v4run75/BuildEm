package buildnlive.com.buildem.LabourReport

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.LabourAdapter
import buildnlive.com.buildem.elements.LabourModel
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ManageLabourReportLoc : AppCompatActivity() {
    private var name: TextView? = null
    //    private TextView name1,name2,name3,name4,name5,name6,name7,name8,name9,name10,name11;
    //    private EditText quantity1,quantity2,quantity3,quantity4,quantity5,quantity6,quantity7,quantity8,quantity9,quantity10,quantity11;

    private var items: RecyclerView? = null
    private var labourAdapter: LabourAdapter? = null
    private var submit: Button? = null
    private var alertBuilder: AlertDialog.Builder? = null
    private val user: String? = null
    private val quantity: String? = null
    private val mYear: Int = 0
    private val mMonth: Int = 0
    private val mDay: Int = 0
    private val c: Calendar? = null
    private var context: Context? = null
    private var progress: ProgressBar? = null
    private var hider: TextView? = null
    private var no_content: TextView? = null
    private var coordinatorLayout: CoordinatorLayout? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location?=null
    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var handler: Handler? = null



    internal var listener: LabourAdapter.OnItemClickListener = object : LabourAdapter.OnItemClickListener {
        override fun onItemClick(items: LabourModel, pos: Int, view: View) {

        }

        override fun onItemCheck(checked: Boolean) {
            if (checked) {
                submit!!.visibility = View.VISIBLE
            } else {
                submit!!.visibility = View.GONE
            }
        }

        override fun onItemInteract(pos: Int, flag: Int) {

        }
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




    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        data.clear()
        newItems.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_labour)
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


        progress = findViewById(R.id.progress)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        hider = findViewById(R.id.hider)

        name = findViewById(R.id.name)
        alertBuilder = AlertDialog.Builder(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        no_content = findViewById(R.id.no_content)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = "Request Labour"
        //        date_button=findViewById(R.id.date_button);
        //        date=findViewById(R.id.date);
        //        date.setEnabled(false);
        //        date_button.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                c = Calendar.getInstance();
        //                mYear = c.get(Calendar.YEAR);
        //                mMonth = c.get(Calendar.MONTH);
        //                mDay = c.get(Calendar.DAY_OF_MONTH);
        //                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
        //                        new DatePickerDialog.OnDateSetListener() {
        //
        //                            @Override
        //                            public void onDateSet(DatePicker view, final int year,
        //                                                  final int monthOfYear, final int dayOfMonth) {
        //                                date_button.setText(year+"/"+monthOfYear+"/"+dayOfMonth);
        //                            }
        //                        }, mYear, mMonth, mDay);
        //                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        //                datePickerDialog.show();
        //
        //            }
        //        });
        //        name1=findViewById(R.id.name1);
        //        name2=findViewById(R.id.name2);
        //        name3=findViewById(R.id.name3);
        //        name4=findViewById(R.id.name4);
        //        name5=findViewById(R.id.name5);
        //        name6=findViewById(R.id.name6);
        //        name7=findViewById(R.id.name7);
        //        name8=findViewById(R.id.name8);
        //        name9=findViewById(R.id.name9);
        //        name10=findViewById(R.id.name10);
        //        name11=findViewById(R.id.name11);
        //
        //
        //        quantity1=findViewById(R.id.quantity);
        //        quantity2=findViewById(R.id.quantity1);
        //        quantity3=findViewById(R.id.quantity2);
        //        quantity4=findViewById(R.id.quantity3);
        //        quantity5=findViewById(R.id.quantity4);
        //        quantity6=findViewById(R.id.quantity5);
        //        quantity7=findViewById(R.id.quantity6);
        //        quantity8=findViewById(R.id.quantity7);
        //        quantity9=findViewById(R.id.quantity8);
        //        quantity10=findViewById(R.id.quantity9);
        //        quantity11=findViewById(R.id.quantity10);

        items = findViewById(R.id.item)
        submit = findViewById(R.id.submit)
        //        data.clear();
        //        data.add(new LabourModel("Male Helper",""));
        //        data.add(new LabourModel("Female Helper",""));
        //        data.add(new LabourModel("Mason",""));
        //        data.add(new LabourModel("Fitter",""));
        //        data.add(new LabourModel("Carpenter",""));
        //        data.add(new LabourModel("Welder",""));
        //        data.add(new LabourModel("Plumber",""));
        //        data.add(new LabourModel("Electrician",""));
        //        data.add(new LabourModel("Operator",""));
        //        data.add(new LabourModel("Help",""));
        //        data.add(new LabourModel("Others",""));

        name!!.text = buildnlive.com.buildem.LabourReport.ManageLabourFragment.name_s
        name!!.isEnabled = false

        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(items!!.context, LinearLayoutManager.VERTICAL)
        items!!.addItemDecoration(dividerItemDecoration)

        labourAdapter = LabourAdapter(context, data, listener)
        items!!.adapter = labourAdapter


        submit!!.setOnClickListener {
            newItems.clear()
            for (i in data.indices) {
                if (data[i].isUpdated) {
                    newItems.add(data[i])
                }
            }
            alertBuilder!!.setMessage("Are you sure?").setTitle("Submit")

            //Setting message manually and performing action on button click
            alertBuilder!!.setMessage("Do you want to Submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        try {
                            sendRequest(newItems)
                        } catch (e: JSONException) {

                        }
                    }
                    .setNegativeButton("No") { dialog, id ->
                        //  Action for 'NO' Button
                        dialog.cancel()
                    }
            //Creating dialog box
            val alert = alertBuilder!!.create()
            //Setting the title manually
            alert.setTitle("Submit")
            alert.show()
        }
    }

    //    private void prepareData(){
    //        data.add(new LabourModel(name1.getText().toString(),quantity1.getText().toString()));
    //
    //        data.add(new LabourModel(name2.getText().toString(),quantity2.getText().toString()));
    //
    //        data.add(new LabourModel(name3.getText().toString(),quantity3.getText().toString()));
    //
    //        data.add(new LabourModel(name4.getText().toString(),quantity4.getText().toString()));
    //
    //        data.add(new LabourModel(name5.getText().toString(),quantity5.getText().toString()));
    //
    //        data.add(new LabourModel(name6.getText().toString(),quantity6.getText().toString()));
    //
    //        data.add(new LabourModel(name7.getText().toString(),quantity7.getText().toString()));
    //
    //        data.add(new LabourModel(name8.getText().toString(),quantity8.getText().toString()));
    //
    //        data.add(new LabourModel(name9.getText().toString(),quantity9.getText().toString()));
    //
    //        data.add(new LabourModel(name10.getText().toString(),quantity10.getText().toString()));
    //
    //        data.add(new LabourModel(name11.getText().toString(),quantity11.getText().toString()));
    //    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    @Throws(JSONException::class)
    private fun sendRequest(items: List<LabourModel>) {
        val app = application as App
        val params = HashMap<String, String>()
        params["user_id"] = App.userId
        params["project_id"] = App.projectId
        params["vendor_id"] = buildnlive.com.buildem.LabourReport.ManageLabourFragment.user_id_s
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        //        params.put("date",date);
        val array = JSONArray()
        for (i in items) {
            array.put(JSONObject().put("labour_type", i.name).put("labour_count", i.quantity))
        }
        params["labour"] = array.toString()
        console.log("Res:$params")
        app.sendNetworkRequest(Config.GET_LABOUR_PROGRESS, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                //                Toast.makeText(getApplicationContext(),"Error:"+error,Toast.LENGTH_LONG).show();
                val snackbar = Snackbar.make(coordinatorLayout!!, "Something went wrong, Try again later", Snackbar.LENGTH_LONG)
                snackbar.show()
            }


            override fun onNetworkRequestComplete(response: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                console.log(response)
                if (response == "1") {
                    Toast.makeText(applicationContext, "Request Generated", Toast.LENGTH_SHORT).show()
                    //                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Request Generated", Snackbar.LENGTH_LONG);
                    //                    snackbar.show();
                    //                    AddItemAdapter.ViewHolder.CHECKOUT=false;
                    finish()
                }
            }
        })
    }


    private fun refresh() {
        val app = application as App
        data.clear()
        var requestUrl = Config.LABOUR_TYPE_LIST
        requestUrl = requestUrl.replace("[0]", App.userId)
        console.log(requestUrl)
        app.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {

                console.error("Network request failed with error :$error")
                //                Toast.makeText(getContext(), "Check Network, Something went wrong", Toast.LENGTH_LONG).show();
                val snackbar = Snackbar.make(coordinatorLayout!!, "Check Network, Something went wrong", Snackbar.LENGTH_LONG)
                snackbar.show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        data.add(LabourModel().parseFromJSON(array.getJSONObject(i)))
                    }
                    console.log("data set changed")
                    if (data.isEmpty()) {
                        no_content!!.visibility = View.VISIBLE
                        no_content!!.text = "No Labour"
                    } else {
                        no_content!!.visibility = View.GONE
                    }

                    items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    val dividerItemDecoration = DividerItemDecoration(items!!.context, LinearLayoutManager.VERTICAL)
                    items!!.addItemDecoration(dividerItemDecoration)

                    labourAdapter = LabourAdapter(context, data, listener)
                    items!!.adapter = labourAdapter

                    //                    adapter = new EmployeeAdapter(getContext(), employeeList, listner);
                    //                    recyclerView.setAdapter(adapter);
                    //                    adapter.notifyDataSetChanged();

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    companion object {
        private val data = ArrayList<LabourModel>()
        private val newItems = ArrayList<LabourModel>()
    }


}
