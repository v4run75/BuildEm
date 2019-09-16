package buildnlive.com.buildem.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Complaints.ComplaintDetailsActivity
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.CustomerDataAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.CustomerData
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.GlideApp
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_customer_data.*
import org.json.JSONException

class ViewCustomerData : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: CustomerDataAdapter? = null
    private var itemList: CustomerData? = null
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null

    companion object {
        private var customerId: String? = null
        private var complaintId: String? = null
        private var type: String? = null

    }


    override fun onStart() {
        super.onStart()
        getCustomerData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_data)

        context = this


        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.customer_details)




        if (intent != null) {
            customerId = intent.getStringExtra("customerId")
            complaintId = intent.getStringExtra("complaintId")
            type = intent.getStringExtra("type")
        }


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = CustomerDataAdapter(context, ArrayList<CustomerData.Details>())
        items!!.adapter = listAdapter

    }


    private fun getCustomerData() {
        var requestUrl = Config.GetCustomerData

        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", customerId!!)
        requestUrl = requestUrl.replace("[2]", complaintId!!)
        requestUrl = requestUrl.replace("[3]", type!!)


        console.log("Services:  $requestUrl")

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

                    val vendorType = object : TypeToken<CustomerData>() {

                    }.type
                    itemList = Gson().fromJson<CustomerData>(response, vendorType)


                    name.text = String.format(getString(R.string.nameHolder), itemList!!.customerDetails.customerName)
                    address.text = String.format(getString(R.string.addressHolder), itemList!!.customerDetails.address)
                    mobileNo.text = String.format(getString(R.string.mobileholder), itemList!!.customerDetails.mobileNo)


                    if (itemList!!.customerDetails.status == "Completed") {
                        GlideApp.with(context!!).load(R.drawable.active_circle).centerCrop().into(statusIndicator)
                    } else {
                        GlideApp.with(context!!).load(R.drawable.inactive_circle).centerCrop().into(statusIndicator)
                    }

                    listAdapter = CustomerDataAdapter(context!!, itemList!!.details)
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