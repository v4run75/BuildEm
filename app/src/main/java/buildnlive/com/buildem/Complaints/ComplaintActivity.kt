package buildnlive.com.buildem.Complaints

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
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.ComplaintListAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.Complaint
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_service.*
import org.json.JSONException

class ComplaintActivity : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: ComplaintListAdapter? = null
    private var itemList: ArrayList<Complaint> = ArrayList()
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null

    private var listener = ComplaintListAdapter.OnItemClickListener { item, pos, view ->
        val intent = Intent(context, ComplaintDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString("complaintId", item.complaintId)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
        getItems()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint)

        context = this


        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.complaints)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = ComplaintListAdapter(context, ArrayList<Complaint>(), listener)
        items!!.adapter = listAdapter

    }


    private fun getItems() {
        var requestUrl = Config.ShowComplaints

        requestUrl = requestUrl.replace("[0]", App.userId)

        itemList.clear()

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
                    val vendorType = object : TypeToken<ArrayList<Complaint>>() {

                    }.type
                    itemList = Gson().fromJson<ArrayList<Complaint>>(response, vendorType)

                    listAdapter = ComplaintListAdapter(context!!, itemList, listener)
                    items!!.adapter = listAdapter
/*

                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        itemList.add(ServiceItem().parseFromJSON(array.getJSONObject(i)))
                    }
                    console.log("data set changed")

                    if (itemList.isEmpty()) {
//                        Toast.makeText(mContext, "No Results", Toast.LENGTH_LONG).show()
                        no_content.visibility = View.VISIBLE
                    } else {
                        no_content.visibility = View.GONE
                        listAdapter!!.addItems(itemList)
                    }*/
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
