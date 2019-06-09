package buildnlive.com.buildem.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.activities.ServiceDetailsActivity
import buildnlive.com.buildem.adapters.ServiceListAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.ServiceItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import kotlinx.android.synthetic.main.fragment_service.*
import org.json.JSONArray
import org.json.JSONException
import java.util.HashMap

class ServiceFragment : Fragment() {

    private var mContext: Context? = null
    private var appCompatActivity: AppCompatActivity? = null
    private var listAdapter: ServiceListAdapter? = null
    private var itemList: ArrayList<ServiceItem> = ArrayList()
    private var utilityofActivity: UtilityofActivity? = null


    private var listener = ServiceListAdapter.OnItemClickListener { item, pos, view ->
        val intent = Intent(mContext, ServiceDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable("serviceItem", item)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        this.appCompatActivity = activity as AppCompatActivity?
    }

    override fun onStart() {
        super.onStart()
        getServiceItems()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        utilityofActivity = UtilityofActivity(appCompatActivity!!)


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

//        requestUrl = requestUrl.replace("[0]", App.userId)

        itemList.clear()

        console.log("Services:  $requestUrl")

        app!!.sendNetworkRequest(requestUrl, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                utilityofActivity!!.showProgressDialog()
            }

            override fun onNetworkRequestError(error: String) {

                utilityofActivity!!.dismissProgressDialog()
                console.error("Network request failed with error :$error")
                Toast.makeText(mContext, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
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

    companion object {
        private var app: App? = null
        fun newInstance(a: App): ServiceFragment {
            app = a
            return ServiceFragment()
        }
    }
}
