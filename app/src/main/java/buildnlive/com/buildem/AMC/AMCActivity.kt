package buildnlive.com.buildem.AMC


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
import buildnlive.com.buildem.adapters.AMCListAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.AMCItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_amc.*
import org.json.JSONException

class AMCActivity : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: AMCListAdapter? = null
    private var itemList: ArrayList<AMCItem> = ArrayList()
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null

    private var listener = AMCListAdapter.OnItemClickListener { item, pos, view ->
        val intent = Intent(context, AMCDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString("amcId", item.amcId)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
        getItems()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amc)

        context = this


        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.amc)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = AMCListAdapter(context, ArrayList<AMCItem>(), listener)
        items!!.adapter = listAdapter

    }


    private fun getItems() {
        var requestUrl = Config.ShowAMC

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
                    val vendorType = object : TypeToken<ArrayList<AMCItem>>() {

                    }.type
                    itemList = Gson().fromJson<ArrayList<AMCItem>>(response, vendorType)

                    listAdapter = AMCListAdapter(context!!, itemList, listener)
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
