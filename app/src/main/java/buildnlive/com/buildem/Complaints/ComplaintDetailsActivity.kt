package buildnlive.com.buildem.Complaints


import android.content.Context
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
import buildnlive.com.buildem.adapters.ComplaintDetailsAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.ComplaintDetails
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.ServiceActivityItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_complaint_details.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ComplaintDetailsActivity : AppCompatActivity() {
    var context: Context? = null
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var complaintId: String? = ""
    private var listAdapter: ComplaintDetailsAdapter? = null
    private var itemList: ComplaintDetails? = null
    private var resultList: ArrayList<ComplaintDetails.Details> = ArrayList()
    var app: App? = null
    private var results: String? = null
    var quantity: String? = null


    private var listener = object : ComplaintDetailsAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: ComplaintDetails.Details, pos: Int, view: View) {
        }

        override fun onItemCheck(serviceItem: ComplaintDetails.Details, pos: Int, view: View, qty: TextView) {
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
        textView.text = getString(R.string.complaints)
        val bundle = intent.extras


        if (bundle != null) {
            complaintId = bundle.getString("complaintId")
        }

        utilityofActivity = UtilityofActivity(appCompatActivity!!)
        utilityofActivity!!.configureToolbar(appCompatActivity!!)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = ComplaintDetailsAdapter(context!!, ArrayList<ComplaintDetails.Details>(), listener)
        items!!.adapter = listAdapter

        next.setOnClickListener {

<<<<<<< HEAD:app/src/main/java/buildnlive/com/buildem/Complaints/ComplaintDetailsActivity.kt
            val intent = Intent(context, ReviewComplaint::class.java)
            intent.putExtra("customerId", itemList!!.customerDetails.customerId)
            intent.putExtra("type", "Complaint")
            startActivity(intent)
=======
>>>>>>> parent of d31975d... New Complaint section:app/src/main/java/buildnlive/com/buildem/activities/ComplaintDetailsActivity.kt

        }


        getComplaintDetails()

    }


    private fun getComplaintDetails() {
        var requestUrl = Config.ShowComplaintsDetails

        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", complaintId!!)


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

                    val vendorType = object : TypeToken<ComplaintDetails>() {

                    }.type
                    itemList = Gson().fromJson<ComplaintDetails>(response, vendorType)


                    name.text = itemList!!.customerDetails.customerName
                    address.text = itemList!!.customerDetails.address
                    mobileNo.text = "Mobile No: " + itemList!!.customerDetails.mobileNo
                    status.text = "Status: " + itemList!!.customerDetails.status

                    listAdapter = ComplaintDetailsAdapter(context!!, itemList!!.details, listener)
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
