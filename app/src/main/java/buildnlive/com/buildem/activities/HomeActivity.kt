package buildnlive.com.buildem.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.LoginAndReset.LoginActivity.PREF_KEY_EMAIL
import buildnlive.com.buildem.LoginAndReset.LoginActivity.PREF_KEY_NAME
import buildnlive.com.buildem.Notifications.FirebaseMessagingService
import buildnlive.com.buildem.R
import buildnlive.com.buildem.console
import buildnlive.com.buildem.fragments.*
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.Helper
import buildnlive.com.buildem.utils.PrefernceFile
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.android.volley.Request
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var imageButton: ImageButton? = null
    private var imageView: ImageView? = null
    private var badge: TextView? = null
    private val generator = ColorGenerator.MATERIAL
    private var fragment: Fragment? = null
    private var pref: SharedPreferences? = null
    private var app: App? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var helper: Helper? = null
    private var context: Context? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

                if (currentFragment is HomeFragment) {

                } else {
                    helper = Helper.instance
                    helper?.moveFragment(HomeFragment.newInstance(app), null, R.id.container, appCompatActivity!!)
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.search -> {

                val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

                if (currentFragment is SearchFragment) {

                } else {
                    helper = Helper.instance
                    helper?.moveFragment(SearchFragment.newInstance(app!!), null, R.id.container, appCompatActivity!!)
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.services -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.container)

                if (currentFragment is ServiceMenuFragment) {

                } else {
                    helper = Helper.instance
                    helper?.moveFragment(ServiceMenuFragment.newInstance(app!!), null, R.id.container, appCompatActivity!!)
                    return@OnNavigationItemSelectedListener true
                }
            }

        }

        return@OnNavigationItemSelectedListener false
    }

    private val listener = object : DrawerLayout.DrawerListener {
        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        }

        override fun onDrawerOpened(drawerView: View) {

        }

        override fun onDrawerClosed(drawerView: View) {
            if (fragment != null) {
                changeFragment()
                fragment = null
            }
        }

        override fun onDrawerStateChanged(newState: Int) {

        }
    }

    override fun onStart() {
        super.onStart()
        //
        try {
            sendRequest()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        context = this

        val realm = Realm.getDefaultInstance()

        val fire = FirebaseMessagingService()

        console.log("Token Fcm " + fire.getToken(this)!!)

        try {
            sendFcmToken(fire.getToken(this))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        app = application as App
        pref = app!!.pref
        fragment = HomeFragment.newInstance(app)
        if (!pref!!.getBoolean(PREF_KEY_LOGGED_IN, false)) {
            startActivity(Intent(applicationContext, buildnlive.com.buildem.LoginAndReset.LoginActivity::class.java))
            finish()
        }
        badge = findViewById(R.id.badge_notification)

        imageButton = findViewById(R.id.notification)
        imageButton!!.setOnClickListener {
            badge!!.visibility = View.GONE
            startActivity(Intent(applicationContext, buildnlive.com.buildem.Notifications.NotificationActivity::class.java))
        }

        helper = Helper.instance
        helper?.moveFragment(HomeFragment(), null, R.id.container, appCompatActivity!!)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.addDrawerListener(listener)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val navView = navigationView.inflateHeaderView(R.layout.nav_header_home)
        imageView = navView.findViewById(R.id.imageView)
        val name = navView.findViewById<TextView>(R.id.name)
        val email = navView.findViewById<TextView>(R.id.email)
        val n = pref!!.getString(PREF_KEY_NAME, "Dummy")
        val e = pref!!.getString(PREF_KEY_EMAIL, "abc@xyz.com")
        name.text = n
        email.text = e
        imageView!!.setImageDrawable(TextDrawable.builder().buildRound("" + n!![0], generator.getColor(e!!)))
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        changeFragment()
        getStoragePermission()
    }

    private fun getStoragePermission() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 7190)
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navigation.visibility = View.VISIBLE
                fragment = HomeFragment.newInstance(app)
            }
            R.id.nav_attendance -> {
                navigation.visibility = View.GONE
                fragment = CheckAttendanceLoc.newInstance(app!!)
            }
            R.id.nav_plans -> {
                navigation.visibility = View.GONE
                fragment = buildnlive.com.buildem.WorkPlanning.PlansFragment.newInstance(application as App)
            }
            R.id.nav_about -> {
                navigation.visibility = View.GONE
                fragment = AboutUsFragment.newInstance()
            }
            R.id.nav_logout -> logout()
            R.id.nav_profile -> {
                fragment = ProfileFragment.newInstance(app)
                navigation.visibility = View.GONE
            }
        }


    val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
    drawer.closeDrawer(GravityCompat.START)
    return true
}

private fun logout() {
    val app = application as App
    var requestUrl = Config.LOGOOUT
    requestUrl = requestUrl.replace("[0]", App.userId)
    console.log(requestUrl)
    app.sendNetworkRequest(requestUrl, Request.Method.POST, null, object : Interfaces.NetworkInterfaceListener {
        override fun onNetworkRequestStart() {

        }

        override fun onNetworkRequestError(error: String) {

            console.error("Network request failed with error :$error")
            Toast.makeText(applicationContext, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()

        }

        override fun onNetworkRequestComplete(response: String) {

            pref!!.edit().clear().commit()
            PrefernceFile.getInstance(context!!).clearData()
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction { realm -> realm.deleteAll() }

            startActivity(Intent(applicationContext, buildnlive.com.buildem.LoginAndReset.LoginActivity::class.java))
            finish()

        }
    })

}

private fun changeFragment() {
    supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment!!)
            .commit()
}


@Throws(JSONException::class)
private fun sendRequest() {
    val app = application as App
    val params = HashMap<String, String>()
    val jsonObject = JSONObject()
    jsonObject.put("project_id", App.projectId).put("user_id", App.userId)
    params["notification_count"] = jsonObject.toString()
    console.log("Res:$params")
    app.sendNetworkRequest(Config.GET_NOTIFICATIONS_COUNT, 1, params, object : Interfaces.NetworkInterfaceListener {
        override fun onNetworkRequestStart() {

        }

        override fun onNetworkRequestError(error: String) {

        }

        override fun onNetworkRequestComplete(response: String) {
            console.log(response)
            if (response == "0") {
                badge!!.visibility = View.GONE
            } else {
                badge!!.visibility = View.VISIBLE
                badge!!.text = response
            }
        }
    })
}

@Throws(JSONException::class)
private fun sendFcmToken(fcmToken: String?) {
    val app = application as App
    val params = HashMap<String, String>()
    params["fcm_token"] = fcmToken!!
    params["user_id"] = App.userId

    console.log("Res:$params")

    app.sendNetworkRequest(Config.UPDATE_FCM_KEY, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
        override fun onNetworkRequestStart() {

        }

        override fun onNetworkRequestError(error: String) {
            console.log("$error Fail")
        }

        override fun onNetworkRequestComplete(response: String) {
            console.log("$response Success")
        }
    })
}

companion object {
    val PREF_KEY_LOGGED_IN = "is_logged_in"
}
}
