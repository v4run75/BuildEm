package buildnlive.com.buildem.Services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import buildnlive.com.buildem.App
import buildnlive.com.buildem.Interfaces
import buildnlive.com.buildem.R
import buildnlive.com.buildem.activities.HomeActivity
import buildnlive.com.buildem.console
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.WorkListItem
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import com.williamww.silkysignature.views.SignaturePad
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class ServiceSignatureActivity : AppCompatActivity() {

    private var mSignaturePad: SignaturePad? = null
    private var mClearButton: Button? = null
    private var mSaveButton: Button? = null
    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null
    private var imagePath: String? = null

    companion object {
        var serviceId: String? = ""
        val QUALITY = 10
    }

    var message: String? = ""
    var status: String? = ""
    var amount: String? = ""
    var tax: String? = ""
    var workArray: ArrayList<WorkListItem>? = null
    private var images: java.util.ArrayList<Packet>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature)

        context = this
        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        app = application as App

        if (intent != null) {
            serviceId = intent.getStringExtra("serviceId")
            status = intent.getStringExtra("status")
            workArray = intent.getParcelableArrayListExtra<WorkListItem>("workArray")
            message = intent.getStringExtra("message")
            amount = intent.getStringExtra("amount")
            tax = intent.getStringExtra("tax")
            images = intent.getParcelableArrayListExtra<Packet>("images")
        }

        mSignaturePad = findViewById(R.id.signature_pad)
        mSignaturePad!!.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {}

            override fun onSigned() {
                mSaveButton!!.isEnabled = true
                mClearButton!!.isEnabled = true
            }

            override fun onClear() {
                mSaveButton!!.isEnabled = false
                mClearButton!!.isEnabled = false
            }
        })

        mClearButton = findViewById<View>(R.id.clear_button) as Button
        mSaveButton = findViewById<View>(R.id.save_button) as Button

        mClearButton!!.setOnClickListener { mSignaturePad!!.clear() }

        mSaveButton!!.setOnClickListener {
            val signatureBitmap = mSignaturePad!!.signatureBitmap
            saveService(signatureBitmap)
//            addJpgSignatureToGallery(signatureBitmap)

        }
    }


    private fun saveService(signatureBitmap: Bitmap) {
        val requestUrl = Config.SaveServiceUpdate

        val params = HashMap<String, String>()

        params["service_id"] = serviceId!!
        val json = Gson()
        params["array"] = json.toJson(workArray)
        params["user_id"] = App.userId
        params["comments"] = message!!
        params["status"] = status!!
        params["amount"] = amount!!
        params["reason"] = ""
        params["tax"] = tax!!

//        val bm1= BitmapFactory.decodeFile(photo!!.absolutePath)
        val baos1 = ByteArrayOutputStream()
        signatureBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos1)
        val sign = baos1.toByteArray()
        val signEncodedImage = Base64.encodeToString(sign, Base64.DEFAULT)

        params["signature"] = signEncodedImage


        val array = JSONArray()
        for (p in images!!) {
            if (p.name != null) {
                val bm = BitmapFactory.decodeFile(p.name)
                val baos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos)
                val b = baos.toByteArray()
                val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                array.put(encodedImage)
            }
        }


        params["images"] = array.toString()
        console.log("Service URL:  $requestUrl")
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
                        val imm = appCompatActivity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)

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

    fun getAlbumStorageDir(albumName: String): File {
        // Get the directory for the user's public pictures directory.
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(storageDir, albumName)
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }


    fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpg", System.currentTimeMillis()))
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            imagePath=photo.absolutePath
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return result
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File) {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        this@ServiceSignatureActivity.sendBroadcast(mediaScanIntent)
    }


}
