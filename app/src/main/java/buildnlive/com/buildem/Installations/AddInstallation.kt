package buildnlive.com.buildem.Installations


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.activities.HomeActivity
import buildnlive.com.buildem.adapters.AddInstallationAdapter
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.WorkListItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.content_add_amc.*
import org.json.JSONException

class AddInstallation : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: AddInstallationAdapter? = null
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null
    var workArray: ArrayList<WorkListItem>? = ArrayList()
    var resultList: ArrayList<WorkListItem> = ArrayList()

    private var listener = object : AddInstallationAdapter.OnItemClickListener {
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
        var installationId: String? = ""
    }

    override fun onStart() {
        super.onStart()
        resultList.clear()
        getWorkList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_installation)

        context = this

        if (intent != null) {
            installationId = intent.getStringExtra("installationId")
        }

        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.installations)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        listAdapter = AddInstallationAdapter(context!!, workArray!!, listener)
        items!!.adapter = listAdapter



        next.setOnClickListener {
            if (!resultList.isNullOrEmpty()) {

                val builder = Builder(context!!)
                builder.setTitle("Submit?")

                builder.setMessage("Are you sure you want to submit?")
                builder.setPositiveButton("Yes") { dialog, which ->


                    val inflaterChooser1 = layoutInflater
                    val dialogViewChooser1 = inflaterChooser1.inflate(R.layout.signature_chooser, null)
                    val dialogBuilderChooser1 = AlertDialog.Builder(context!!, R.style.PinDialog)
                    val alertDialogChooser1 = dialogBuilderChooser1.setCancelable(false).setView(dialogViewChooser1).create()
                    alertDialogChooser1.show()
                    val close = dialogViewChooser1.findViewById<Button>(R.id.negative)
                    val submit = dialogViewChooser1.findViewById<Button>(R.id.positive)
                    val approval = dialogViewChooser1.findViewById<Spinner>(R.id.approval)
                    val reason = dialogViewChooser1.findViewById<EditText>(R.id.reason)

                    reason.movementMethod = ScrollingMovementMethod()

                    var approvalString: String? = ""

                    approval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            if (p2 > 0) {
                                if (p2 == 2) {
                                    reason.visibility = View.VISIBLE
                                } else {
                                    reason.visibility = View.GONE
                                }
                                approvalString = p0!!.selectedItem.toString()
                            } else {
                                reason.visibility = View.GONE
                                utilityofActivity!!.toast("Please Select Approval Option")
                            }
                        }
                    }


                    close.setOnClickListener {
                        alertDialogChooser1.dismiss()
                    }

                    submit.setOnClickListener {
                        if (approvalString.equals("Without Signature")) {
                            if (!reason.text.toString().isBlank())
                                saveInstallation(reason.text.toString())
                            else
                                reason.error="Enter Reason"
                        } else {

                            val intent = Intent(context, InstallationSignatureActivity::class.java)
                            intent.putExtra("workArray", resultList)
                            intent.putExtra("installationId", installationId)
                            startActivity(intent)

                            alertDialogChooser1.dismiss()
                            startActivity(intent)
                        }
                    }




                }

                builder.setNegativeButton("Dismiss") { dialog, which ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                utilityofActivity!!.toast("Please Select Work")
            }
        }


    }


    private fun saveInstallation(reason: String) {
        val requestUrl = Config.SaveInstallationUpdate

        val params = HashMap<String, String>()

        params["installation_id"] = installationId!!
        val json = Gson()
        params["array"] = json.toJson(resultList)
        params["user_id"] = App.userId
        params["reason"] = reason
        params["signature"] = ""

        console.log("Installation URL:  $requestUrl")
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

                        finishAffinity()
                        startActivity(Intent(context!!, HomeActivity::class.java))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
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

                    listAdapter = AddInstallationAdapter(context!!, workArray!!, listener)
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