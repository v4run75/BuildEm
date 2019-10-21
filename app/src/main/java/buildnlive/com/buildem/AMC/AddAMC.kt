package buildnlive.com.buildem.AMC


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.AddAMCAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.WorkListItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_add_amc.*
import org.json.JSONException

class AddAMC : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: AddAMCAdapter? = null
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null
    var workArray: ArrayList<WorkListItem>? = ArrayList()
    var resultList: ArrayList<WorkListItem> = ArrayList()

    private var listener = object : AddAMCAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: WorkListItem, pos: Int, view: View) {

        }

        override fun onItemCheck(serviceItem: WorkListItem, pos: Int, view: View, qty: TextView, checked: Boolean, check: CheckBox, rate: String) {
            serviceItem.qty = qty.text.toString()
            serviceItem.rate = rate
            resultList.add(serviceItem)
            /* if (checked) {
                if (!qty.text.isNullOrBlank()) {
                    installationItem.qty = qty.text.toString()
                    resultList.add(installationItem)
                } else {
                    Toast.makeText(context, "Enter Quantity", Toast.LENGTH_SHORT).show()
                    check.isChecked = false
                }

            }*/
        }

    }

    companion object {
        var amcId: String? = ""
    }

    override fun onStart() {
        super.onStart()
        resultList.clear()
        getWorkList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_amc)

        context = this

        if (intent != null) {
            amcId = intent.getStringExtra("amcId")
        }

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


        listAdapter = AddAMCAdapter(context!!, workArray!!, listener)
        items!!.adapter = listAdapter



        next.setOnClickListener {
            if (!resultList.isNullOrEmpty()) {
                val intent = Intent(context, ReviewAMC::class.java)
                intent.putExtra("workArray", resultList)
                intent.putExtra("amcId", amcId)
                startActivity(intent)
            } else {
                utilityofActivity!!.toast("Please Select Work")
            }
        }


    }


    private fun getWorkList() {
        var requestUrl = Config.ShowWork

        requestUrl = requestUrl.replace("[0]", App.userId)

        workArray!!.clear()

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
                    val vendorType = object : TypeToken<ArrayList<WorkListItem>>() {

                    }.type
                    workArray = Gson().fromJson<ArrayList<WorkListItem>>(response, vendorType)

                    listAdapter = AddAMCAdapter(context!!, workArray!!, listener)
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