package buildnlive.com.buildem.AMC


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import buildnlive.com.buildem.*
import buildnlive.com.buildem.activities.HomeActivity
import buildnlive.com.buildem.adapters.ActivityImagesAdapter
import buildnlive.com.buildem.adapters.ReviewDetailsAdapter
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.WorkListItem
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.UtilityofActivity
import com.android.volley.Request
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_review.*
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class ReviewAMC : AppCompatActivity() {

    private var context: Context? = null
    private var appCompatActivity: AppCompatActivity? = this
    private var utilityofActivity: UtilityofActivity? = null
    private var app: App? = null
    private var listAdapter: ReviewDetailsAdapter? = null
    private var resultList: ArrayList<WorkListItem> = ArrayList()
    private var workArray: ArrayList<WorkListItem>? = ArrayList()

    private var images: java.util.ArrayList<Packet>? = null
    private var imagesAdapter: ActivityImagesAdapter? = null
    private var imagePath: String? = null
    private var results: String? = null
    var quantity: String? = null


    companion object {
        var amcId: String? = ""
        val QUALITY = 10
        val REQUEST_GALLERY_IMAGE = 7191
        val REQUEST_CAPTURE_IMAGE = 7190
    }


    private var listener = object : ReviewDetailsAdapter.OnItemClickListener {
        override fun onItemClick(serviceItem: WorkListItem, pos: Int, view: View) {
          }

        override fun onItemCheck(serviceItem: WorkListItem, pos: Int, view: View, qty: TextView) {
            serviceItem.qty = qty.text.toString()
            resultList.add(serviceItem)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        context = this


        if (intent != null) {
            amcId = intent.getStringExtra("amcId")
            workArray = intent.getParcelableArrayListExtra<WorkListItem>("workArray")
        }

        app = application as App

        utilityofActivity = UtilityofActivity(appCompatActivity!!)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = getString(R.string.reviewAndSave)



        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        items!!.addItemDecoration(dividerItemDecoration)
        items!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        listAdapter = ReviewDetailsAdapter(context!!, workArray!!, listener)
        items!!.adapter = listAdapter


        save.setOnClickListener {
            if (amountReceived.text.toString() != "" && status.selectedItem.toString() != "Select Status" && tax.selectedItem.toString()!="Select Tax")
                menuUpdate()
            else {
                utilityofActivity!!.toast("Please fill all fields")
            }
        }
    }

    private fun saveAMC(comment: String, images: java.util.ArrayList<Packet>?, alertDialog: AlertDialog, reason: String) {
        val requestUrl = Config.SaveAMCUpdate

        val params = HashMap<String, String>()

        params["amc_id"] = amcId!!
        val json = Gson()
        params["array"] = json.toJson(workArray)
        params["user_id"] = App.userId
        params["comments"] = comment
        params["tax"] = tax.selectedItem.toString()
        params["status"] = status.selectedItem.toString()
        params["amount"] = amountReceived.text.toString()
        params["reason"] = reason
        params["signature"] = ""

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

        params["images"]=array.toString()
        console.log("AMC URL:  $requestUrl")
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
                        alertDialog.dismiss()
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


    private fun menuUpdate() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog_image_upload, null)

        val alert_title = dialogView.findViewById<TextView>(R.id.alert_title)
        alert_title.text = getString(R.string.amc)

        val dialogBuilder = AlertDialog.Builder(context!!, R.style.PinDialog)
        val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
        alertDialog.show()

        val message = dialogView.findViewById<EditText>(R.id.message)

        val list = dialogView.findViewById<AdvancedRecyclerView>(R.id.images)
        images = java.util.ArrayList()
        images!!.add(Packet())
        imagesAdapter = ActivityImagesAdapter(context, images, ActivityImagesAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflaterChooser = layoutInflater
                val dialogViewChooser = inflaterChooser.inflate(R.layout.image_chooser, null)
                val dialogBuilderChooser = AlertDialog.Builder(context!!, R.style.PinDialog)
                val alertDialogChooser = dialogBuilderChooser.setCancelable(false).setView(dialogViewChooser).create()
                alertDialogChooser.show()
                val gallery = dialogViewChooser.findViewById<TextView>(R.id.gallery)
                val camera = dialogViewChooser.findViewById<TextView>(R.id.camera)

                gallery.setOnClickListener {
                    if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            )) != PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                104
                        )
                    } else {
                        alertDialogChooser.dismiss()
                        val pictureIntent = Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pictureIntent, REQUEST_GALLERY_IMAGE)
                    }
                }

                camera.setOnClickListener {

                    if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.CAMERA
                            )) != PackageManager.PERMISSION_GRANTED)
                    ) {
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                                101
                        )
                    } else {
                        alertDialogChooser.dismiss()
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

                }

                val negative = dialogViewChooser.findViewById<Button>(R.id.negative)
                negative.setOnClickListener { alertDialogChooser.dismiss() }

            } else {
                images!!.removeAt(pos)
                imagesAdapter!!.notifyItemRemoved(pos)
                imagesAdapter!!.notifyDataSetChanged()
            }
        })
        list.adapter = imagesAdapter
        list.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        list.setmMaxHeight(400)

        val positive = dialogView.findViewById<Button>(R.id.positive)
        positive.setOnClickListener {
            try {
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
                            saveAMC(message.text.toString(), images!!, alertDialog, reason.text.toString())
                        else
                            reason.error="Enter Reason"
                    } else {
                        val intent = Intent(context, AmcSignatureActivity::class.java)
                        intent.putExtra("message",message.text.toString())
                        intent.putExtra("images",images)
                        intent.putExtra("workArray", workArray)
                        intent.putExtra("amcId", amcId)
                        intent.putExtra("status",status.selectedItem.toString())
                        intent.putExtra("amount", amountReceived.text.toString())

                        alertDialog.dismiss()
                        alertDialogChooser1.dismiss()
                        startActivity(intent)
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(context, "Fill data properly!", Toast.LENGTH_SHORT).show()
            }
        }
        val negative = dialogView.findViewById<Button>(R.id.negative)
        negative.setOnClickListener { alertDialog.dismiss() }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty()) {
            when (requestCode) {
                101 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                    } else {
                        utilityofActivity!!.toast("Some functions may not work unless you allow permissions")
                    }
                }
                104 -> {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        val pictureIntent = Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(pictureIntent, REQUEST_GALLERY_IMAGE)
                    } else {
                        utilityofActivity!!.toast("Some functions may not work unless you allow permissions")
                    }

                }

            }
        }
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
            if(data!=null)
            {
                val uri = data.data
                packet.name = getRealPathFromURI(uri)
                console.log("Image Path " + packet.name + "EXTRAS " + packet.extra)
                images!!.add(0, Packet())
                images!!.add(packet)
                imagesAdapter!!.notifyDataSetChanged()
            }

        }
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