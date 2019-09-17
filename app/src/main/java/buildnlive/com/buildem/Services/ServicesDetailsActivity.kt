package buildnlive.com.buildem.Services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.activities.ViewCustomerData
import buildnlive.com.buildem.adapters.ServiceDetailsItemAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.ServiceDetailsItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.GlideApp
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_services_details.*
import kotlinx.android.synthetic.main.content_service_details.*
import kotlinx.android.synthetic.main.content_service_details.address
import kotlinx.android.synthetic.main.content_service_details.items
import kotlinx.android.synthetic.main.content_service_details.mobileNo
import kotlinx.android.synthetic.main.content_service_details.name
import kotlinx.android.synthetic.main.content_services_details.*
import org.json.JSONException
import java.util.*

class ServicesDetailsActivity : AppCompatActivity() {
    var context: Context? = null
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var serviceId: String? = ""
    private var listAdapter: ServiceDetailsItemAdapter? = null
    private var itemList: ServiceDetailsItem? = null
    private var resultList: ArrayList<ServiceDetailsItem.Details> = ArrayList()
    var app: App? = null
    private var results: String? = null
    var quantity: String? = null


    private var listener = object : ServiceDetailsItemAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: ServiceDetailsItem.Details, pos: Int, view: View) {
        }

        override fun onItemCheck(serviceItem: ServiceDetailsItem.Details, pos: Int, view: View, qty: TextView) {
            serviceItem.qty = qty.text.toString()
            resultList.add(serviceItem)
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_details)

        context = this

        app = application as App

        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = getString(R.string.services)
        val bundle = intent.extras


        if (bundle != null) {
            serviceId = bundle.getString("serviceId")
        }

        utilityofActivity = UtilityofActivity(appCompatActivity!!)
        utilityofActivity!!.configureToolbar(appCompatActivity!!)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = ServiceDetailsItemAdapter(context!!, ArrayList<ServiceDetailsItem.Details>(), listener)
        items!!.adapter = listAdapter

        seeMore.setOnClickListener {
            val intent = Intent(context, ViewCustomerData::class.java)
            intent.putExtra("customerId", itemList!!.customerDetails.customerId)
            intent.putExtra("id", serviceId)
            intent.putExtra("type", "Service")
            startActivity(intent)
        }



        add.setOnClickListener {

            val intent = Intent(context, AddService::class.java)
            intent.putExtra("serviceId", serviceId)
            startActivity(intent)

        }

        getServiceItemDetails()
    }


    private fun getServiceItemDetails() {
        var requestUrl = Config.ShowServiceDetails

        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", serviceId!!)


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

                    val vendorType = object : TypeToken<ServiceDetailsItem>() {

                    }.type
                    itemList = Gson().fromJson<ServiceDetailsItem>(response, vendorType)


                    name.text = String.format(getString(R.string.nameHolder), itemList!!.customerDetails.customerName)
                    address.text = String.format(getString(R.string.addressHolder), itemList!!.customerDetails.address)
                    mobileNo.text = String.format(getString(R.string.mobileholder), itemList!!.customerDetails.mobileNo)


                    if (itemList!!.customerDetails.status == "Completed") {
                        GlideApp.with(context!!).load(R.drawable.active_circle).centerCrop().into(statusIndicator)
                    } else {
                        GlideApp.with(context!!).load(R.drawable.inactive_circle).centerCrop().into(statusIndicator)
                    }

                    listAdapter = ServiceDetailsItemAdapter(context!!, itemList!!.details, listener)
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