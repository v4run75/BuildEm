package buildnlive.com.buildem.Complaints

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.App
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.AddComplaintAdapter
import buildnlive.com.buildem.elements.ComplaintDetails
import buildnlive.com.buildem.utils.UtilityofActivity
import kotlinx.android.synthetic.main.content_add_complaint.*

class AddComplaint : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: AddComplaintAdapter? = null
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null

    private var listener = object : AddComplaintAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: ComplaintDetails.Details, pos: Int, view: View) {

        }

        override fun onItemCheck(serviceItem: ComplaintDetails.Details, pos: Int, view: View, qty: TextView, checked: Boolean, check: CheckBox) {
            serviceItem.qty = qty.text.toString()
            resultList.add(serviceItem)
            /* if (checked) {
                if (!qty.text.isNullOrBlank()) {
                    serviceItem.qty = qty.text.toString()
                    resultList.add(serviceItem)
                } else {
                    Toast.makeText(context, "Enter Quantity", Toast.LENGTH_SHORT).show()
                    check.isChecked = false
                }

            }*/
        }

    }

    companion object {
        var complaintId: String? = ""
        var workArray: ArrayList<ComplaintDetails.Details>? = ArrayList()
        var resultList: ArrayList<ComplaintDetails.Details> = ArrayList()
    }
/*
    override fun onStart() {
        super.onStart()
        workArray!!.clear()
        resultList.clear()
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_complaint)

        context = this

        if (intent != null) {
            complaintId = intent.getStringExtra("complaintId")
            workArray = intent.getParcelableArrayListExtra<ComplaintDetails.Details>("workArray")
        }

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


        listAdapter = AddComplaintAdapter(context!!, workArray!!, listener)
        items!!.adapter = listAdapter



        next.setOnClickListener {
            if (!resultList.isNullOrEmpty()) {
                val intent = Intent(context, ReviewComplaint::class.java)
                intent.putExtra("workArray", resultList)
                intent.putExtra("complaintId", complaintId)
                startActivity(intent)
            } else {
                utilityofActivity!!.toast("Please Select Work")
            }
        }


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