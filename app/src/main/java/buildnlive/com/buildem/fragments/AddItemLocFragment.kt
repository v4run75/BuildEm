package buildnlive.com.buildem.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R

import com.android.volley.Request

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

import buildnlive.com.buildem.adapters.AddItemAdapter
import buildnlive.com.buildem.elements.Item
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.google.android.gms.location.*

class AddItemLocFragment : Fragment() {
    private var items: RecyclerView? = null
    private var next: Button? = null
    private var submit: Button? = null
    private var progress: ProgressBar? = null
    private var hider: TextView? = null
    private var checkout_text: TextView? = null
    private val LOADING: Boolean = false
    private var close: ImageButton? = null
    private var search_close: ImageButton? = null
    private var search: ImageButton? = null
    private var adapter: AddItemAdapter? = null
    //    private SearchView searchView;
    private var search_view: LinearLayout? = null
    private var search_text: EditText? = null
    var builder: AlertDialog.Builder?=null
    internal val newItems: MutableList<Item> = ArrayList()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location?=null
    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = null
    var mConetxt: Context? = null
    var handler: Handler? = null



    var listener: AddItemAdapter.OnItemSelectedListener = object : AddItemAdapter.OnItemSelectedListener {
        override fun onItemCheck(checked: Boolean) {
            if (checked) {
                next!!.visibility = View.VISIBLE
            } else {
                next!!.visibility = View.GONE
            }
        }

        override fun onItemInteract(pos: Int, flag: Int) {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        newItems.clear()
        itemsList.clear()
        AddItemAdapter.ViewHolder.CHECKOUT = false
    }

    override fun onAttach(context: Context?) {
        mConetxt=context
        super.onAttach(context)
    }

    override fun onAttach(activity: Activity?) {
        appCompatActivity=activity as AppCompatActivity
        super.onAttach(activity)
    }
    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "${PACKAGE_NAME}.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "${PACKAGE_NAME}.LOCATION_DATA_EXTRA"
    }


    internal inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {

            // Display the address string
            // or an error message sent from the intent service.
            outputLocation = resultData?.getString(Constants.RESULT_DATA_KEY) ?: ""


            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
//                utilityofActivity!!.toast(getString(R.string.address_found))
//                utilityofActivity!!.toast(outputLocation!!)
                utilityofActivity!!.dismissProgressDialog()
//                dialogView!!.findViewById<TextView>(R.id.address).text = outputLocation
//                next()
                console.log("Location: "+ outputLocation)

            } else {
                utilityofActivity!!.toast("Error")
                utilityofActivity!!.dismissProgressDialog()
            }

        }
    }


    private fun startIntentService() {

        val intent = Intent(mConetxt, FetchAddressIntentService::class.java).apply {
            putExtra(Constants.RECEIVER, resultReceiver)
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }
        mConetxt!!.startService(intent)
    }


    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private val requestingLocationUpdates: Boolean = true

    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null /* Looper */
        )
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_inventory_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        builder = AlertDialog.Builder(context)
        items = view.findViewById(R.id.items)
        submit = view.findViewById(R.id.submit)
        next = view.findViewById(R.id.next)
        close = view.findViewById(R.id.close_checkout)
        search_view = view.findViewById(R.id.search_view)

        close!!.setOnClickListener {
            search_view!!.visibility = View.VISIBLE
            AddItemAdapter.ViewHolder.CHECKOUT = false
            checkout_text!!.visibility = View.GONE
            close!!.visibility = View.GONE
            AddItemAdapter.ViewHolder.CHECKOUT = false
            newItems.clear()
            adapter = AddItemAdapter(context, itemsList, listener)
            items!!.adapter = adapter
            submit!!.visibility = View.GONE
            next!!.visibility = View.GONE
        }
        checkout_text = view.findViewById(R.id.checkout_text)
        next!!.setOnClickListener {
            search_view!!.visibility = View.GONE
            checkout_text!!.visibility = View.VISIBLE
            close!!.visibility = View.VISIBLE
            //                search_textview.setVisibility(View.GONE);
            AddItemAdapter.ViewHolder.CHECKOUT = true
            for (i in itemsList.indices) {
                if (itemsList[i].isUpdated) {
                    newItems.add(itemsList[i])
                }
            }

            adapter = AddItemAdapter(context, newItems, object : AddItemAdapter.OnItemSelectedListener {
                override fun onItemCheck(checked: Boolean) {

                }

                override fun onItemInteract(pos: Int, flag: Int) {
                    if (flag == 100) {
                        newItems.removeAt(pos)
                        adapter!!.notifyItemRemoved(pos)
                    }
                }
            })

            items!!.adapter = adapter
            submit!!.visibility = View.VISIBLE
            next!!.visibility = View.GONE
            submit!!.setOnClickListener {
                builder!!.setMessage("Are you sure?").setTitle("Indent Item")

                //Setting message manually and performing action on button click
                builder!!.setMessage("Do you want to Submit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            try {
                                sendRequest(newItems)
                            } catch (e: JSONException) {

                            }
                        }
                        .setNegativeButton("No") { dialog, id ->
                            //  Action for 'NO' Button
                            dialog.cancel()
                        }
                //Creating dialog box
                val alert = builder!!.create()
                //Setting the title manually
                alert.setTitle("Indent Item")
                alert.show()
            }
        }


        //        search_textview=view.findViewById(R.id.search_textview);
        search_text = view.findViewById(R.id.search_text)
        search_close = view.findViewById(R.id.search_close)
        search = view.findViewById(R.id.search)

        progress = view.findViewById(R.id.progress)
        hider = view.findViewById(R.id.hider)
        //        adapter = new AddItemAdapter(getContext(), itemsList, listener);
        //        items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        //        items.setAdapter(adapter);
        if (LOADING) {
            progress!!.visibility = View.VISIBLE
            hider!!.visibility = View.VISIBLE
        } else {
            progress!!.visibility = View.GONE
            hider!!.visibility = View.GONE
        }

        //        searchView = view.findViewById(R.id.search_view);
        //
        //        // Associate searchable configuration with the SearchView
        ////        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        ////        searchView.setSearchableInfo(searchManager
        ////                .getSearchableInfo(getActivity().getComponentName()));
        //        searchView.setMaxWidth(Integer.MAX_VALUE);
        //        searchView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                search_textview.setVisibility(View.GONE);
        //            }
        //        });
        //        // listening to search query text change
        //        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
        //            @Override
        //            public boolean onQueryTextSubmit(String query) {
        //                // filter recycler view when query submitted
        //                adapter.getFilter().filter(query);
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onQueryTextChange(String query) {
        //                // filter recycler view when text is changed
        //                adapter.getFilter().filter(query);
        //                return false;
        //            }
        //        });


        search!!.setOnClickListener {
            search_text!!.visibility = View.VISIBLE
            search_close!!.visibility = View.VISIBLE
            search_text!!.requestFocus()
            if (search_text!!.hasFocus()) {
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                //                imm.hideSoftInputFromWindow(search_text.windowToken,InputMethodManager.SHOW_IMPLICIT )
                imm.showSoftInput(search_text, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        search_text!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(search_text!!.text.toString())
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(search_text!!.windowToken, 0)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }

        }

        search_close!!.setOnClickListener {
            search_text!!.setText("")
            search_text!!.visibility = View.INVISIBLE
            search_close!!.visibility = View.INVISIBLE
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            refresh()
        }

        refresh()



        // ***********************************LOCATION***************************************

        if (PrefernceFile.getInstance(context!!).getString("Lat") != null) {
            console.log("Fragment: " + PrefernceFile.getInstance(context!!).getString("Lat")!!)
        }

        resultReceiver = AddressResultReceiver(Handler())

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(appCompatActivity!!)


        locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)

        builder.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    if (location != null)
                        lastLocation = location
                    if(lastLocation!=null){
                        PrefernceFile.getInstance(mConetxt!!).setString("Lat",lastLocation!!.latitude.toString())
                        PrefernceFile.getInstance(mConetxt!!).setString("Long",lastLocation!!.longitude.toString())
                        fetchAddressButtonHander()
                    }
                    console.log("Location LatLang: $lastLocation")
                }
            }
        }



        if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                        appCompatActivity!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(appCompatActivity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 102)
        } else {
            //TODO add not dismissable dialog to enable permissions

//            startLocationUpdates()

            val lm = mConetxt!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gps_enabled = false
            var network_enabled = false

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (ex: Exception) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (ex: Exception) {
            }

            if (!gps_enabled && !network_enabled) {
                // notify user

                val builder = android.support.v7.app.AlertDialog.Builder(mConetxt!!)

                // Set the alert dialog title
                builder.setTitle("Location Settings")

                builder.setMessage("Location services are required for posting please switch them on to continue.")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Open Settings") { dialog, which ->
                    // Do something when user press the positive button
                    mConetxt!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }


                // Display a negative button on alert dialog
                builder.setNegativeButton("Dismiss") { dialog, which ->
                    Toast.makeText(
                            mConetxt,
                            "Location Services are necessary for posting",
                            Toast.LENGTH_SHORT
                    ).show()
                }


                // Finally, make the alert dialog using builder
                val dialog: android.support.v7.app.AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
            }


        }

    }

    fun fetchAddressButtonHander() {
        startIntentService()
    }



    @Throws(JSONException::class)
    private fun sendRequest(items: List<Item>) {
        val app = activity!!.application as App
        val params = HashMap<String, String>()
        params["user_id"] = App.userId
        params["detail_id"] = App.projectId
        params["latitude"] = lastLocation!!.longitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()

        val array = JSONArray()
        for (i in items) {
            array.put(JSONObject().put("stock_id", i.id).put("qty", i.quantity))
        }
        params["item_list"] = array.toString()
        console.log("Res:$params")
        app.sendNetworkRequest(Config.INVENTORY_ITEM_REQUEST, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                Toast.makeText(context, "Error:$error", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                if (response == "1") {
                    Toast.makeText(context, "Request Generated", Toast.LENGTH_SHORT).show()
                    AddItemAdapter.ViewHolder.CHECKOUT = false
                    activity!!.finish()
                }
            }
        })
    }

    private fun refresh() {
        val app = activity!!.application as App
        itemsList.clear()
        var requestUrl = Config.REQ_GET_ITEM_INVENTORY
        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", App.projectId)
        console.log(requestUrl)
        app.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                //                console.log(response);
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        itemsList.add(Item().parseFromJSON(array.getJSONObject(i)))
                    }
                    console.log("data set changed")
                    adapter = AddItemAdapter(context, itemsList, listener)
                    items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    items!!.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                    console.log("" + adapter!!.itemCount)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun search(keyword: String) {
        val app = activity!!.application as App
        itemsList.clear()
        var requestUrl = Config.INVENTORY_SEARCH
        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", App.projectId)
        requestUrl = requestUrl.replace("[2]", keyword)
        console.log(requestUrl)
        app.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress!!.visibility = View.VISIBLE
                hider!!.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                //                console.log(response);
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        itemsList.add(Item().parseFromJSON(array.getJSONObject(i)))
                    }
                    console.log("data set changed")
                    adapter = AddItemAdapter(context, itemsList, listener)
                    items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    items!!.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                    console.log("" + adapter!!.itemCount)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    companion object {
        private val itemsList = ArrayList<Item>()

        fun newInstance(): AddItemLocFragment {
            //        itemsList = u;
            return AddItemLocFragment()
        }
    }


}