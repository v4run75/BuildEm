package buildnlive.com.buildem.Installations

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.*
import buildnlive.com.buildem.adapters.ActivityImagesAdapter
import buildnlive.com.buildem.adapters.ServicesAdapter
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.ServiceActivityItem
import buildnlive.com.buildem.elements.ServiceItem
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import kotlinx.android.synthetic.main.content_service_details.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class InstallationDetailsActivity : AppCompatActivity() {
    var context: Context? = null
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var serviceItem: ServiceItem? = null
    private var listAdapter: ServicesAdapter? = null
    private var itemList: ArrayList<ServiceActivityItem> = ArrayList()
    private var resultList: ArrayList<ServiceActivityItem> = ArrayList()
    var app: App? = null
    private var images: java.util.ArrayList<Packet>? = null
    private var imagesAdapter: ActivityImagesAdapter? = null
    private var imagePath: String? = null
    private var results: String? = null
    var quantity:String?=null

    companion object {
        val QUALITY = 10

        val REQUEST_GALLERY_IMAGE = 7191

        val REQUEST_CAPTURE_IMAGE = 7190
    }

    private var listener = object : ServicesAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: ServiceActivityItem, pos: Int, view: View) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemCheck(serviceItem: ServiceActivityItem, pos: Int, view: View, qty: TextView) {
            serviceItem.quantity=qty.text.toString()
            resultList.add(serviceItem)
        }

    }


//    override fun onStart() {
//        super.onStart()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_details)

        context = this

        app = application as App

        val textView = findViewById<TextView>(R.id.toolbar_title)
        textView.text = "Services"
        val bundle = intent.extras


        if (bundle != null) {
            serviceItem = bundle.getParcelable("serviceItem")
        }

        name.text = serviceItem!!.name
        address.text = serviceItem!!.address
        email.text = serviceItem!!.email
//        email.text = "Email: "+serviceItem!!.email
//        mobileNo.text = "Mobile No: "+serviceItem!!.mobileNo
        mobileNo.text = "Mobile No: " + serviceItem!!.mobileNo
        date.text = serviceItem!!.date
        time.text = serviceItem!!.time


        utilityofActivity = UtilityofActivity(appCompatActivity!!)
        utilityofActivity!!.configureToolbar(appCompatActivity!!)


        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = ServicesAdapter(context!!, ArrayList<ServiceActivityItem>(), listener)
//        itemList.add()
//        listAdapter = ServicesAdapter(context,itemList, listener)
        items!!.adapter = listAdapter

        next.setOnClickListener {
            menuUpdate()
        }


        getServiceActivities()

    }

    private fun menuUpdate() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog_service_activity, null)
        val dialogBuilder = AlertDialog.Builder(context!!, R.style.PinDialog)
        val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
        alertDialog.show()

        val message = dialogView.findViewById<EditText>(R.id.message)

        val list = dialogView.findViewById<AdvancedRecyclerView>(R.id.images)
        images = java.util.ArrayList()
        images!!.add(Packet())
        imagesAdapter = ActivityImagesAdapter(context, images, ActivityImagesAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.image_chooser, null)
                val dialogBuilder = AlertDialog.Builder(context!!, R.style.PinDialog)
                val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
                alertDialog.show()
                val gallery = dialogView.findViewById<TextView>(R.id.gallery)
                val camera = dialogView.findViewById<TextView>(R.id.camera)

                gallery.setOnClickListener {
                    alertDialog.dismiss()
                    val pictureIntent = Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pictureIntent, REQUEST_GALLERY_IMAGE)
                }

                camera.setOnClickListener {
                    alertDialog.dismiss()
                    val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (pictureIntent.resolveActivity(packageManager) != null) {

                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                        } catch (ex: IOException) {
                        }

                        if (photoFile != null) {
                            val photoURI = FileProvider.getUriForFile(context!!, BuildConfig.APPLICATION_ID + ".provider", photoFile)
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            imagePath = photoFile.absolutePath
                            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
                        }
                    }
                }

                val negative = dialogView.findViewById<Button>(R.id.negative)
                negative.setOnClickListener { alertDialog.dismiss() }

            } else {
                images!!.removeAt(pos)
                imagesAdapter!!.notifyItemRemoved(pos)
                imagesAdapter!!.notifyDataSetChanged()
            }
        })
        list.adapter = imagesAdapter
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        list.setmMaxHeight(400)
//        val title = dialogView.findViewById<TextView>(R.id.alert_title)
//        title.text = "Activity Status"

        val positive = dialogView.findViewById<Button>(R.id.positive)
        positive.setOnClickListener {
            try {
                //TODO Call Submit here

                submit(serviceItem!!.serviceId,message.text.toString(),images,resultList,alertDialog)
            } catch (e: Exception) {
                Toast.makeText(context, "Fill data properly!", Toast.LENGTH_SHORT).show()
            }
        }
        val negative = dialogView.findViewById<Button>(R.id.negative)
        negative.setOnClickListener { alertDialog.dismiss() }
    }

    private fun getServiceActivities() {
        val requestUrl = Config.SERVICES_ACTIVITIES
        val params = HashMap<String, String>()
        params["service_id"] = serviceItem!!.serviceId

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
                Toast.makeText(context, "Check Network, Something went wrong", Toast.LENGTH_LONG).show()
            }

            override fun onNetworkRequestComplete(response: String) {
                console.log(response)
                utilityofActivity!!.dismissProgressDialog()
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        itemList.add(ServiceActivityItem().parseFromJSON(array.getJSONObject(i)))
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                val packet = images!!.removeAt(0)
                packet.name = imagePath
                //                Uri uri=data.getData();
                //                packet.setName(getRealPathFromURI(uri));
                console.log("Image Path " + packet.name + "EXTRAS " + packet.extra)
                images!!.add(0, Packet())
                images!!.add(packet)
                imagesAdapter!!.notifyDataSetChanged()
            } else if (resultCode == android.app.Activity.RESULT_CANCELED) {
                console.log("Canceled")
            }
        } else if (requestCode == REQUEST_GALLERY_IMAGE) {
            val packet = images!!.removeAt(0)
            //            packet.setName(imagePath);
            val uri = data!!.data
            packet.name = getRealPathFromURI(uri)
            console.log("Image Path " + packet.name + "EXTRAS " + packet.extra)
            images!!.add(0, Packet())
            images!!.add(packet)
            imagesAdapter!!.notifyDataSetChanged()
        }
    }

    @Throws(JSONException::class)
    private fun submit(service_id: String, comment: String, images: ArrayList<Packet>?,list: ArrayList<ServiceActivityItem>,alertDialog: AlertDialog) {

            utilityofActivity!!.showProgressDialog()

            val params = HashMap<String, String>()
            params["comment"] = comment
            params["service_id"] = service_id

            val jsonArray = JSONArray()

            for(i in list)
            {
               jsonArray.put(JSONObject().put("id",i.id)
                       .put("name",i.name)
                       .put("quantity",i.quantity)
                       .put("type",i.type))
            }

            params["list"]=jsonArray.toString()

            console.log("Service  $params")

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

                params["images"] = array.toString()
                console.log("Service Images  $params")
            }

            app!!.sendNetworkRequest(Config.GET_SERVICE_UPDATES, Request.Method.POST, params, object : Interfaces.NetworkInterfaceListener {
                override fun onNetworkRequestStart() {

                }

                override fun onNetworkRequestError(error: String) {
                    utilityofActivity!!.dismissProgressDialog()
                    utilityofActivity!!.toast("Something went wrong, Try again later")

                }

                override fun onNetworkRequestComplete(response: String) {
                    utilityofActivity!!.dismissProgressDialog()
                    if (response == "1") {
                        Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show()
                        alertDialog.dismiss()
                        finish()
                    }
                    else
                    {
                        utilityofActivity!!.toast("Invalid Response from server")
                    }
                }
            })
    }


    // And to convert the image URI to the direct file system path of the image file
    fun getRealPathFromURI(contentUri: Uri?): String? {

        // can post image
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context!!.contentResolver.query(contentUri!!,
                proj, null, null, null)// Which columns to return
        // WHERE clause; which rows to return (all rows)
        // WHERE clause selection arguments (none)
        // Order-by clause (ascending by name)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            results = cursor.getString(column_index)
        }
        //                managedQuery( );
        cursor.moveToFirst()
        cursor.close()
        return results
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
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
