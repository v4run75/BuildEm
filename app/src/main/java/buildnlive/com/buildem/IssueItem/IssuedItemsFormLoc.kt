package buildnlive.com.buildem.IssueItem

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.AssetsSpinAdapter
import buildnlive.com.buildem.adapters.IssueVendorSpinAdapter
import buildnlive.com.buildem.elements.*
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.android.gms.location.*
import io.realm.Realm
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class IssuedItemsFormLoc : AppCompatActivity() {
    private var progress: ProgressBar? = null
    private var hider: TextView? = null
    private var max: TextView? = null
    private var unit: TextView? = null
    private var unit2: TextView? = null
    private var item: TextView? = null
    private var submit: Button? = null
    private var quantity: EditText? = null
    private var slip_no: EditText? = null
    private var comments: EditText? = null
    private var receiver: Spinner? = null
    private var inventory: ArrayList<Item>? = null
    private var items: ArrayList<String>? = null
    private var receivers: ArrayList<String>? = null
    private var realm: Realm? = null
    private val item_adapter: ArrayAdapter<*>? = null
    private var receiver_adapter: ArrayAdapter<*>? = null
    private var selectedItem: String? = null
    private var selectedReceiver: String? = null
    private var itemName: String? = null
    private var receiverName: String? = null
    private var item_rent_id: String? = null
    private var vendor_id: String? = null
    private var user_type: String? = null
    private var item_type: String? = null
    private var asset_id: String? = ""
    private var builder: AlertDialog.Builder? = null
    private var IssueVendorAdapter: IssueVendorSpinAdapter? = null
    private var vendorSpinner: Spinner? = null
    private var assetsAdapter: AssetsSpinAdapter? = null
    private var assetsSpinner: Spinner? = null
    private var context: Context? = null
    private var itemList: Item? = null
    private var receiver_person: EditText? = null


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_item_list)
        setContractorSpinner()
        setAssetsSpinner()
        context = this
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = "Issue Item"
        val bundle = intent.extras
        if (bundle != null) {
            itemList = bundle.getSerializable("Items") as Item
        }

        app = application as App

        slip_no = findViewById(R.id.slip_no)
        progress = findViewById(R.id.progress)
        hider = findViewById(R.id.hider)
        builder = AlertDialog.Builder(context!!)
        submit = findViewById(R.id.submit)
        quantity = findViewById(R.id.quantity)
        comments = findViewById(R.id.comment)
        max = findViewById(R.id.max_quantity)
        item = findViewById(R.id.item)
        unit = findViewById(R.id.unit)
        unit2 = findViewById(R.id.unit2)
        receiver = findViewById(R.id.receiver)
        receiver_person = findViewById(R.id.receiver_edittext)
        realm = Realm.getDefaultInstance()
        items = ArrayList()
        receivers = ArrayList()
        inventory = ArrayList()
        items!!.add(0, "Select Item")

        item!!.text = itemList!!.name
        itemName = itemList!!.name
        item_type = itemList!!.item_type

        console.log("Item Type" + item_type!!)
        selectedItem = itemList!!.id

        max!!.text = itemList!!.quantity
        unit!!.text = itemList!!.unit
        unit2!!.text = itemList!!.unit

        val receivers1 = realm!!.where(ProjectMember::class.java).equalTo("belongsTo", App.belongsTo).findAll()
        for (w in receivers1) {
            receivers!!.add(w.name)
        }
        receivers!!.add(0, "Select Member")

        receiver_adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item, receivers!!)
        receiver_adapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        receiver!!.adapter = receiver_adapter
        submit!!.setOnClickListener(View.OnClickListener {
            builder!!.setMessage("Do you want to Submit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                        if (quantity!!.text.toString().length > 0 && selectedItem!!.length > 0) {
                            try {
                                if (java.lang.Float.parseFloat(quantity!!.text.toString()) > java.lang.Float.parseFloat(max!!.text.toString())) {
                                    Toast.makeText(context, "Check Quantity!", Toast.LENGTH_SHORT).show()
                                    return@OnClickListener
                                }
//                                if (vendor_id != "" && selectedReceiver != "" || vendor_id == "" && selectedReceiver == "") {
//                                    Toast.makeText(context, "Select Either Member Or Vendor", Toast.LENGTH_SHORT).show()
//                                } else
                                    sendIssue()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            Toast.makeText(context, "Fill Data Properly!", Toast.LENGTH_LONG).show()
                        }
                    })
                    .setNegativeButton("No") { dialog, id ->
                        //  Action for 'NO' Button
                        dialog.cancel()
                    }
            //Creating dialog box
            val alert = builder!!.create()
            //Setting the title manually
            alert.setTitle("Issue Item")
            alert.show()
        })



        receiver!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    selectedReceiver = receivers1[position - 1]!!.userId
                    receiverName = receivers1[position - 1]!!.name
                    user_type = "user"
                } else {
                    selectedReceiver = ""
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        vendorSpinner = findViewById(R.id.contractor)
        vendorSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                vendor_id = IssueVendorAdapter!!.getID(i)

                if (i > 0) {
                    vendor_id = IssueVendorAdapter!!.getID(i)
                    user_type = "vendor"
                } else {
                    vendor_id = ""
                }

            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        IssueVendorAdapter = IssueVendorSpinAdapter(context!!, R.layout.custom_spinner, IssueVendorList)
        IssueVendorAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vendorSpinner!!.adapter = IssueVendorAdapter

        assetsSpinner = findViewById(R.id.asset)
        assetsSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                if (i > 0) {
                    item_rent_id = assetsAdapter!!.getRentId(i)
                    asset_id = assetsAdapter!!.getAssetId(i)
                } else {
                    item_rent_id = ""
                }


            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        assetsAdapter = AssetsSpinAdapter(context!!, R.layout.custom_spinner, assetsList)
        assetsAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        assetsSpinner!!.adapter = assetsAdapter

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

                val builder = AlertDialog.Builder(context!!)

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
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
            }


        }



    }


    @Throws(JSONException::class)
    private fun sendIssue() {
        progress!!.visibility = View.VISIBLE
        hider!!.visibility = View.VISIBLE
        val params = HashMap<String, String>()
        params["user_id"] = App.userId
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        val obj = JSONObject()
        console.log("user type: " + user_type!!)
//        if (user_type == "user") {
            obj.put("stock_id", selectedItem).put("quantity", quantity!!.text.toString())
                    .put("receiver_id", selectedReceiver).put("comments", comments!!.text.toString())
                    .put("item_rent_id", item_rent_id).put("slip_no", slip_no!!.text.toString())
                    .put("user_type", user_type).put("item_type", item_type).put("asset_id", asset_id)
                    .put("reciver", receiver_person!!.text.toString())
            params["issue_list"] = obj.toString()

/*
            console.log("Item User$obj")
        } else if (user_type == "vendor") {
            obj.put("stock_id", selectedItem).put("quantity", quantity!!.text.toString())
                    .put("receiver_id", vendor_id).put("comments", comments!!.text.toString())
                    .put("item_rent_id", item_rent_id).put("slip_no", slip_no!!.text.toString())
                    .put("user_type", user_type).put("item_type", item_type).put("asset_id", asset_id)
            params["issue_list"] = obj.toString()
            console.log("Item Vendor$obj")

        }
*/
        console.log("ISSUE DATA:$params")
        app!!.sendNetworkRequest(Config.SEND_ISSUED_ITEM, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log("Response:$response")
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                console.log(response)
                if (response == "1") {
                    try {
                        val issue = Issue().parseFromJSON(obj, itemName, receiverName)
                        realm!!.executeTransaction { realm -> realm.copyToRealmOrUpdate(issue) }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(context, "Something went wrong :(", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setAssetsSpinner() {
        val app = application as App
        assetsList.clear()
        var requestURl = Config.SEND_ASSETS
        requestURl = requestURl.replace("[0]", App.userId)
        requestURl = requestURl.replace("[1]", App.projectId)

        console.log(requestURl)
        app.sendNetworkRequest(requestURl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {

            }

            override fun onNetworkRequestError(error: String) {

                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()

            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)

                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        assetsList.add(Assets().parseFromJSON(array.getJSONObject(i)))
                    }
                    assetsAdapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun setContractorSpinner() {
        val app = application as App
        IssueVendorList.clear()
        var requestURl = Config.SEND_ISSUE_VENDORS
        requestURl = requestURl.replace("[0]", App.userId)
        requestURl = requestURl.replace("[1]", App.projectId)
        app.sendNetworkRequest(requestURl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {

            }

            override fun onNetworkRequestError(error: String) {

                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()

            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)

                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        IssueVendorList.add(IssueVendor().parseFromJSON(array.getJSONObject(i)))
                    }
                    IssueVendorAdapter!!.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    companion object {
        private var app: App? = null
        private val IssueVendorList = ArrayList<IssueVendor>()
        private val assetsList = ArrayList<Assets>()
    }
}