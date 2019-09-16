package buildnlive.com.buildem.Installations


import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import buildnlive.com.buildem.adapters.ServiceListAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.ServiceItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import kotlinx.android.synthetic.main.fragment_service.*
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class InstallationActivity : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: ServiceListAdapter? = null
    private var itemList: ArrayList<ServiceItem> = ArrayList()
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null

    private var listener = ServiceListAdapter.OnItemClickListener { item, pos, view ->
        val intent = Intent(context, InstallationDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("serviceItem", item)
        intent.putExtras(bundle)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
        getServiceItems()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        context = this


        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.services)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = ServiceListAdapter(context, ArrayList<ServiceItem>(), listener)
        items!!.adapter = listAdapter

    }


    private fun getServiceItems() {
        val requestUrl = Config.SERVICES_DETAILS
        val params = HashMap<String, String>()
        params["user_id"] = App.userId

        itemList.clear()

        console.log("Services:  $requestUrl")

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
