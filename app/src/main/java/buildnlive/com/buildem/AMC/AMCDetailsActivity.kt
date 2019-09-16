package buildnlive.com.buildem.AMC

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import buildnlive.com.buildem.elements.ServiceActivityItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.GlideApp
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_complaint_details.*
import kotlinx.android.synthetic.main.content_complaint_details.*
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_details)

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



        add.setOnClickListener {

            val intent = Intent(context, AddAMC::class.java)
            intent.putExtra("workArray", itemList!!.details)
            intent.putExtra("amcId", amcId)
            startActivity(intent)

        }

        getAMCItemDetails()
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


    @Throws(JSONException::class)
    private fun submit(service_id: String, comment: String, images: ArrayList<Packet>?, list: ArrayList<ServiceActivityItem>, alertDialog: AlertDialog) {

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