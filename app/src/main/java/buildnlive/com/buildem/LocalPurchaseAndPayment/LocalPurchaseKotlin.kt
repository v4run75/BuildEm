package buildnlive.com.buildem.LocalPurchaseAndPayment

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.console
import io.realm.Realm

class LocalPurchaseKotlin : AppCompatActivity() {
    private var app: App? = null
    private var realm: Realm? = null
    private var fragment: Fragment? = null
    private var edit: TextView? = null
    private var view: TextView? = null
    internal var listener: Interfaces.SyncListener? = null
    private val back: ImageButton? = null
    override fun onDestroy() {
        super.onDestroy()
        console.log("Destroyed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_purchase)
        app = application as App
        realm = Realm.getDefaultInstance()
        fragment = buildnlive.com.buildem.LocalPurchaseAndPayment.LocalPurchaseListFragment.newInstance(app)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = "Puchase & Payments"
        changeScreen()
        edit = findViewById(R.id.edit)
        edit!!.setOnClickListener {
            enableEdit()
            disableView()
            fragment = buildnlive.com.buildem.LocalPurchaseAndPayment.LocalPurchaseListFragment.newInstance(app)
            changeScreen()
        }
        view = findViewById(R.id.view)
        view!!.setOnClickListener {
            enableView()
            disableEdit()
            fragment = PaymentFragmentLoc.newInstance()
            changeScreen()
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

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        getMenuInflater().inflate(R.menu.attendance_menu, menu);
    //        return super.onCreateOptionsMenu(menu);
    //    }
    //
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        int id = item.getItemId();
    //        switch (id){
    //            case R.id.nav_home:
    //                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
    //                return true;
    //            case R.id.nav_profile:
    //                Toast.makeText(getApplicationContext(),"Item 2 Selected",Toast.LENGTH_LONG).show();
    //                return true;
    //            default:
    //                return super.onOptionsItemSelected(item);
    //        }
    //    }

    private fun changeScreen() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.site_content, fragment!!)
                .commit()
    }

    private fun disableView() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_left, null))
        } else {
            view!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_left)
        }
        view!!.setTextColor(resources.getColor(R.color.color2))
    }

    private fun enableView() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_left_selected, null))
        } else {
            view!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_left_selected)
        }
        view!!.setTextColor(resources.getColor(R.color.white))
    }

    private fun disableEdit() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            edit!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_right, null))
        } else {
            edit!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_right)
        }
        edit!!.setTextColor(resources.getColor(R.color.color2))
    }

    private fun enableEdit() {
        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            edit!!.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.round_right_selected, null))
        } else {
            edit!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.round_right_selected)
        }
        edit!!.setTextColor(resources.getColor(R.color.white))
    }


}
