package buildnlive.com.buildem.AMC


import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.AMCItemDetailsAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.AMCItemDetails
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_review.*
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class ReviewAMC : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null
    private var listAdapter: AMCItemDetailsAdapter? = null
    private var resultList: java.util.ArrayList<AMCItemDetails.Details> = java.util.ArrayList()


    companion object {
        var amcId: String? = ""
        var workArray: ArrayList<AMCItemDetails.Details>? = ArrayList()
    }


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
        setContentView(R.layout.activity_review)

        context = this


        if (intent != null) {
            amcId = intent.getStringExtra("amcId")
            workArray = intent.getParcelableArrayListExtra<AMCItemDetails.Details>("workArray")
        }

        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.reviewAndSave)

        comments.movementMethod = ScrollingMovementMethod()


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = AMCItemDetailsAdapter(context!!, workArray!!, listener)
        items!!.adapter = listAdapter


        save.setOnClickListener {
            saveComplaint()
        }

    }


    private fun saveComplaint() {
        val requestUrl = Config.SaveAMCUpdate

        val params = HashMap<String, String>()

        params["amc_id"] = amcId!!
        val json = Gson()
        params["array"] = json.toJson(workArray)
        params["user_id"] = App.userId

        console.log("Complaint URL:  $requestUrl")
        console.log("Params:  $params")

        app!!.sendNetworkRequest(requestUrl, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
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
                    if (response == "1") {
                        utilityofActivity!!.toast("Request Generated")
                        finish()
                    }
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