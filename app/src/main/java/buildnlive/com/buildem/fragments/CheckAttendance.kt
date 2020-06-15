/*
package buildnlive.com.buildem.fragments


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.console
import buildnlive.com.buildem.utils.Config
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.android.volley.Request
import kotlinx.android.synthetic.main.content_check_attendance.*
import org.json.JSONException
import org.json.JSONObject

class CheckAttendance : Fragment() {

    var attendenceId: String? = null
    private var pref: SharedPreferences? = null
    private val generator = ColorGenerator.MATERIAL
    private var mContext: Context? = null
    private var appCompatActivity: AppCompatActivity? = null

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
        checkAttendanceFun()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_check_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = app!!.pref
        val n = pref!!.getString(buildnlive.com.buildem.LoginAndReset.LoginActivity.PREF_KEY_NAME, "Dummy")
        val e = pref!!.getString(buildnlive.com.buildem.LoginAndReset.LoginActivity.PREF_KEY_EMAIL, "abc@xyz.com")

        cover!!.setImageDrawable(TextDrawable.builder().buildRound("" + n!![0], generator.getColor(e!!)))

        name.text = n
        email.text = e

        checkin.setOnClickListener {
            Checkin()
        }

        checkout.setOnClickListener {
            CheckOut()
        }


    }

    private fun Checkin() {
        var requestUrl = Config.CHECK_IN
        requestUrl = requestUrl.replace("[0]", App.userId)


        console.log("CheckIn: " + requestUrl)
        app!!.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress.visibility=View.VISIBLE
                hider.visibility=View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress.visibility=View.GONE
                hider.visibility=View.GONE
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress.visibility=View.GONE
                hider.visibility=View.GONE
                try {

                    if (response == "0") {
                        Toast.makeText(context!!, "Checkout Failure Please try again", Toast.LENGTH_LONG).show()
                    } else if (response == "-1") {
                        Toast.makeText(context!!, "Invalid User, Please contact admin", Toast.LENGTH_LONG).show()
                    } else {
                        val jsonObject = JSONObject(response)
                        status.text = "You checked in at : " + jsonObject.get("start_time")
                        attendenceId = jsonObject.getString("attendence_id")


                        checkout.visibility = View.VISIBLE
                        checkin.visibility = View.GONE

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    private fun CheckOut() {
        var requestUrl = Config.CHECK_OUT
        requestUrl = requestUrl.replace("[0]", App.userId)
        requestUrl = requestUrl.replace("[1]", attendenceId!!)

        console.log("CheckOut: " + requestUrl)
        app!!.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress.visibility = View.VISIBLE
                hider.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress.visibility = View.GONE
                hider.visibility = View.GONE
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress.visibility = View.GONE
                hider.visibility = View.GONE
                try {
//                    if(response == "1")
//                    {

//                    }
//                    else if(response == "0")
                    if (response == "0") {
                        Toast.makeText(context!!, "Checkout Failure Please try again", Toast.LENGTH_LONG).show()
                    } else if (response == "-1") {
                        Toast.makeText(context!!, "Invalid User, Please contact admin", Toast.LENGTH_LONG).show()
                    } else {
                        val jsonObject = JSONObject(response)

                        Toast.makeText(context!!, "Checkout Successful", Toast.LENGTH_LONG).show()
                        status.text = "You checked in at : " + jsonObject.get("start_time")
                        checkout.background = ContextCompat.getDrawable(context!!, R.drawable.inactive_home_button)
                        checkout.isEnabled = false
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }


    private fun checkAttendanceFun() {
        var requestUrl = Config.GET_ATTENDANCE
        requestUrl = requestUrl.replace("[0]", App.userId)

        console.log("CheckOut: " + requestUrl)
        app!!.sendNetworkRequest(requestUrl, Request.Method.GET, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {
                progress.visibility = View.VISIBLE
                hider.visibility = View.VISIBLE
            }

            override fun onNetworkRequestError(error: String) {
                progress.visibility = View.GONE
                hider.visibility = View.GONE
                console.error("Network request failed with error :$error")
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                progress.visibility = View.GONE
                hider.visibility = View.GONE
                try {
//                    if(response == "1")
//                    {

//                    }
//                    else if(response == "0")
                    if (response == "-1") {
                        Toast.makeText(context!!, "Invalid User, Please contact admin", Toast.LENGTH_LONG).show()
                    } else {
                        val jsonObject = JSONObject(response)
                        if ((jsonObject.getString("start_time") == "0" && jsonObject.getString("end_time") == "0")) {
                            checkin.visibility = View.VISIBLE
                            checkin.isEnabled = true
                            checkin.background = ContextCompat.getDrawable(context!!, R.drawable.home_button)
                            checkout.visibility = View.GONE
                        } else if ((jsonObject.getString("start_time").isNotEmpty() && jsonObject.getString("end_time") == "null")) {
                            checkin.visibility = View.GONE
                            checkout.isEnabled = true
                            checkout.background = ContextCompat.getDrawable(context!!, R.drawable.home_button)
                            checkout.visibility = View.VISIBLE
                        } else {
                            checkin.visibility = View.GONE
                            checkout.visibility = View.VISIBLE
                            checkout.background = ContextCompat.getDrawable(context!!, R.drawable.inactive_home_button)
                            checkout.isEnabled = false
                        }

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        })
    }

    companion object {
        private var app: App? = null
        fun newInstance(a: App): CheckAttendance {
            app = a
            return CheckAttendance()
        }
    }
}
*/
