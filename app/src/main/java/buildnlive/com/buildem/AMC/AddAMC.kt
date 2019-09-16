package buildnlive.com.buildem.AMC


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
import buildnlive.com.buildem.adapters.AddAMCAdapter
import buildnlive.com.buildem.elements.AMCItemDetails
import buildnlive.com.buildem.utils.UtilityofActivity
import kotlinx.android.synthetic.main.content_add_amc.*

class AddAMC : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var listAdapter: AddAMCAdapter? = null
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null

    private var listener = object : AddAMCAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: AMCItemDetails.Details, pos: Int, view: View) {

        }

        override fun onItemCheck(serviceItem: AMCItemDetails.Details, pos: Int, view: View, qty: TextView, checked: Boolean, check: CheckBox) {
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
        var amcId: String? = ""
        var workArray: ArrayList<AMCItemDetails.Details>? = ArrayList()
        var resultList: ArrayList<AMCItemDetails.Details> = ArrayList()
    }
    override fun onStart() {
        super.onStart()
        resultList.clear()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_amc)

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


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}