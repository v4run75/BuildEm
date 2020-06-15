package buildnlive.com.buildem.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.SearchAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.SearchModel
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import kotlinx.android.synthetic.main.fragment_search.*
import org.json.JSONArray
import org.json.JSONException

class SearchFragment : Fragment() {

    private var mContext: Context? = null
    private var appCompatActivity: AppCompatActivity? = null
    private var adapter: SearchAdapter? = null
    private var itemList: ArrayList<SearchModel> = ArrayList()
    private var utilityofActivity: UtilityofActivity? = null
    private var listener = SearchAdapter.OnItemClickListener { project, pos, view ->

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.appCompatActivity = activity as AppCompatActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        adapter = SearchAdapter(context, ArrayList<SearchModel>(), listener)
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        items!!.adapter = adapter


        search_bar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText == "") {
                    itemList.clear()
                    adapter!!.notifyDataSetChanged()
                }
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // task HERE
                if (discount.text.toString() == "")
                    Search(query, "0")
                else
                    Search(query, discount.text.toString())
                return true
            }

        })


    }


    private fun Search(text: String, discount: String) {
        var requestUrl = Config.GET_MODEL
        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", text)
        requestUrl = requestUrl.replace("[2]", discount)

        itemList.clear()

        console.log("Search: " + requestUrl)
        app!!.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
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
                        itemList.add(SearchModel().parseFromJSON(array.getJSONObject(i)))
                    }
                    console.log("data set changed")

                    if (itemList.isEmpty()) {
                        Toast.makeText(mContext, "No Results", Toast.LENGTH_LONG).show()
                    } else {
                        adapter!!.addItems(itemList)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    companion object {
        private var app: App? = null
        fun newInstance(a: App): SearchFragment {
            app = a
            return SearchFragment()
        }
    }
}
