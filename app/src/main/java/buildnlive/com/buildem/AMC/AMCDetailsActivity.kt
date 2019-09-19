package buildnlive.com.buildem.AMC

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
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.activities.ViewCustomerData
import buildnlive.com.buildem.adapters.AMCItemDetailsAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.AMCItemDetails
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.InstallationActivityItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.GlideApp
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_amc_details.*
import kotlinx.android.synthetic.main.content_amc_details.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AMCDetailsActivity : AppCompatActivity() {
    var context: Context? = null
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var amcId: String? = ""
    private var listAdapter: AMCItemDetailsAdapter? = null
    private var itemList: AMCItemDetails? = null
    private var resultList: ArrayList<AMCItemDetails.Details> = ArrayList()
    var app: App? = null
    private var results: String? = null
    var quantity: String? = null

    private var listener = object : AMCItemDetailsAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: AMCItemDetails.Details, pos: Int, view: View) {
        }

        override fun onItemCheck(serviceItem: AMCItemDetails.Details, pos: Int, view: View, qty: TextView) {
            serviceItem.qty = qty.text.toString()
            resultList.add(serviceItem)
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
        setContentView(R.layout.activity_amc_details)

        context = this

        app = application as App

        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = getString(R.string.amc)
        val bundle = intent.extras


        if (bundle != null) {
            amcId = bundle.getString("amcId")
        }

        utilityofActivity = UtilityofActivity(appCompatActivity!!)
        utilityofActivity!!.configureToolbar(appCompatActivity!!)

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



        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = AMCItemDetailsAdapter(context!!, ArrayList<AMCItemDetails.Details>(), listener)
        items!!.adapter = listAdapter

        seeMore.setOnClickListener {
            val intent = Intent(context, ViewCustomerData::class.java)
            intent.putExtra("customerId", itemList!!.customerDetails.customerId)
            intent.putExtra("id", amcId)
            intent.putExtra("type", "AMC")
            startActivity(intent)
        }

        comment.movementMethod = ScrollingMovementMethod()

        add.setOnClickListener {

            val intent = Intent(context, AddAMC::class.java)
            intent.putExtra("amcId", amcId)
            startActivity(intent)

        }

        onJob.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(context!!)

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

        getAMCItemDetails()
    }

    private fun startJob() {
        val requestUrl = Config.UpdateUserSite

        val params = HashMap<String, String>()

        params["id"] = amcId!!
        params["type"] = "AMC"
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


    private fun getAMCItemDetails() {
        var requestUrl = Config.ShowAMCDetails

        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", amcId!!)


        console.log("Complaint Details:  $requestUrl")

        app!!.sendNetworkRequest(requestUrl, Request.Method.POST, null, object : Interfaces.NetworkInterfaceListener {
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

                    val vendorType = object : TypeToken<AMCItemDetails>() {

                    }.type
                    itemList = Gson().fromJson<AMCItemDetails>(response, vendorType)


                    name.text = String.format(getString(R.string.nameHolder), itemList!!.customerDetails.customerName)
                    address.text = String.format(getString(R.string.addressHolder), itemList!!.customerDetails.address)
                    mobileNo.text = String.format(getString(R.string.mobileholder), itemList!!.customerDetails.mobileNo)
                    comment.text = String.format(getString(R.string.commentholder), itemList!!.customerDetails.comment)


                    if (itemList!!.customerDetails.status == "Completed") {
                        GlideApp.with(context!!).load(R.drawable.active_circle).centerCrop().into(statusIndicator)
                    } else {
                        GlideApp.with(context!!).load(R.drawable.inactive_circle).centerCrop().into(statusIndicator)
                    }

                    listAdapter = AMCItemDetailsAdapter(context!!, itemList!!.details, listener)
                    items!!.adapter = listAdapter


                } catch (e: JSONException) {
                    e.printStackTrace()
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