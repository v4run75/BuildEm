package buildnlive.com.buildem.WorkPlanning


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import buildnlive.com.buildem.*
import buildnlive.com.buildem.R
import buildnlive.com.buildem.adapters.ActivityImagesAdapter
import buildnlive.com.buildem.adapters.DailyWorkAdapter
import buildnlive.com.buildem.elements.Packet
import buildnlive.com.buildem.elements.Work
import buildnlive.com.buildem.utils.AdvancedRecyclerView
import buildnlive.com.buildem.utils.Config
import buildnlive.com.buildem.utils.PrefernceFile
import buildnlive.com.buildem.utils.UtilityofActivity
import com.google.android.gms.location.*
import io.realm.Realm
import io.realm.RealmResults
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PlanningLoc : AppCompatActivity() {
    private var app: App? = null
    private val edit: TextView? = null
    private val filter: TextView? = null
    private val view: TextView? = null
    private val reset: TextView? = null
    private val fragment: Fragment? = null
    private val status_text: String? = null
    private val category_text: String? = null
    private var results: String? = null
    private var items: RecyclerView? = null
    private var progress: ProgressBar? = null
    private var hider: TextView? = null
    private var no_content: TextView? = null
    private var adapter: DailyWorkAdapter? = null
    private val realm: Realm? = null
    private val LOADING = true
    private val back: ImageButton? = null
    private var context: Context? = null
    private var images: ArrayList<Packet>? = null
    private var imagesAdapter: ActivityImagesAdapter? = null
    private var imagePath: String? = null


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var outputLocation: String? = null


    private var lastLocation: Location?=null
    private lateinit var resultReceiver: AddressResultReceiver
    var locationRequest: LocationRequest? = null
    private lateinit var locationCallback: LocationCallback
    var utilityofActivity: UtilityofActivity? = null
    var appCompatActivity: AppCompatActivity? = this
    var handler: Handler? = null


    object Constants {
        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val PACKAGE_NAME = "com.google.android.gms.location.sample.locationaddress"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
        const val LOCATION_DATA_EXTRA = "$PACKAGE_NAME.LOCATION_DATA_EXTRA"
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

        val intent = Intent(context, FetchAddressIntentService::class.java).apply {
            putExtra(Constants.RECEIVER, resultReceiver)
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }
        context!!.startService(intent)
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_progress)
        context = this
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        app = application as App
        //        edit = findViewById(R.id.edit);
        //        view = findViewById(R.id.view);
        //        items = view.findViewById(R.id.items);
        //        progress = view.findViewById(R.id.progress);
        //        hider = view.findViewById(R.id.hider);
        //        no_content = view.findViewById(R.id.no_content);
        //        filter = findViewById(R.id.filter);
        //        fragment = DailyWorkProgressFragment.newInstance(app);
        //        changeScreen();
        //        filter.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                LayoutInflater inflater = getLayoutInflater();
        //                View dialogView = inflater.inflate(R.layout.alert_filter, null);
        //                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.PinDialog);
        //                final AlertDialog alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create();
        //                alertDialog.show();
        //            }
        //        });
        val toolbar_title = findViewById<TextView>(R.id.toolbar_title)
        toolbar_title.text = "Work Planning"
        loadWorks()
        items = findViewById(R.id.items)
        progress = findViewById(R.id.progress)
        hider = findViewById(R.id.hider)
        no_content = findViewById(R.id.no_content)
        //        back =view.findViewById(R.id.back);
        //        back.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                getActivity().finish();
        //            }
        //        });
        //        reset =view.findViewById(R.id.reset);
        //        reset.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                loadWorks();
        //            }
        //        });
        //        filter = view.findViewById(R.id.filter);
        //        filter.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                LayoutInflater inflater = getLayoutInflater();
        //                View dialogView = inflater.inflate(R.layout.alert_filter, null);
        //                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.PinDialog);
        //                final AlertDialog alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create();
        //                alertDialog.show();
        //                final Spinner status= dialogView.findViewById(R.id.status);
        //                final Spinner catfilt= dialogView.findViewById(R.id.category_filter);
        //                final EditText startDateDD= dialogView.findViewById(R.id.start_date_dd);
        //                final EditText startDateMM= dialogView.findViewById(R.id.start_date_mm);
        //                final EditText startDateYYYY= dialogView.findViewById(R.id.start_date_yyyy);
        //                final EditText endDateDD= dialogView.findViewById(R.id.end_date_dd);
        //                final EditText endDateMM= dialogView.findViewById(R.id.end_date_mm);
        //                final EditText endDateYYYY= dialogView.findViewById(R.id.end_date_yyyy);
        //
        //                Button positive = dialogView.findViewById(R.id.positive);
        //                positive.setOnClickListener(new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //
        //                        if(!(status.getSelectedItem().toString().equals("Select Status")))
        //                        {
        //                            status_text = status.getSelectedItem().toString();
        //                            console.log(status_text);
        //                        }
        //                        else
        //                        {
        //                            status_text="";
        //                        }
        //                        if(!(catfilt.getSelectedItem().toString().equals("Select Category")))
        //                        {
        //                            category_text = catfilt.getSelectedItem().toString();
        //                            console.log(category_text);
        //                        }
        //                        else
        //                        {
        //                            category_text="";
        //                        }
        //                        String start_date=startDateDD.getText()+"/"+startDateMM.getText()+"/"+startDateYYYY.getText();
        //                        String end_date=endDateDD.getText()+"/"+endDateMM.getText()+"/"+endDateYYYY.getText();
        //                        console.log(start_date+" "+end_date);
        //                        filter(status_text,category_text,start_date,end_date);
        //                        alertDialog.dismiss();
        //
        //                    }
        //                });
        //                Button negative = dialogView.findViewById(R.id.negative);
        //                negative.setOnClickListener(new View.OnClickListener() {
        //                    @Override
        //                    public void onClick(View v) {
        //                        alertDialog.dismiss();
        //                    }
        //                });
        //            }
        //        });
        //        realm = Realm.getDefaultInstance();
        //        works = realm.where(Work.class).equalTo("belongsTo", App.belongsTo).findAllAsync();

        if (LOADING) {
            progress!!.visibility = View.VISIBLE
            hider!!.visibility = View.VISIBLE
        } else {
            progress!!.visibility = View.GONE
            hider!!.visibility = View.GONE
        }

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
                        PrefernceFile.getInstance(context!!).setString("Lat",lastLocation!!.latitude.toString())
                        PrefernceFile.getInstance(context!!).setString("Long",lastLocation!!.longitude.toString())
//                        fetchAddressButtonHander()
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

            val lm = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

                val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)

                // Set the alert dialog title
                builder.setTitle("Location Settings")

                builder.setMessage("Location services are required for posting please switch them on to continue.")
                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("Open Settings") { dialog, which ->
                    // Do something when user press the positive button
                    context!!.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                }


                // Display a negative button on alert dialog
                builder.setNegativeButton("Dismiss") { dialog, which ->
                    Toast.makeText(
                            context,
                            "Location Services are necessary for posting",
                            Toast.LENGTH_SHORT
                    ).show()
                }


                // Finally, make the alert dialog using builder
                val dialog: androidx.appcompat.app.AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            } else {

//            utilityofActivity!!.showProgressDialog()
                startLocationUpdates()
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

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        getMenuInflater().inflate(R.menu.work_progress_menu, menu);
    //        return super.onCreateOptionsMenu(menu);
    //    }
    //
    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        int id = item.getItemId();
    //        switch (id){
    //            case R.id.filter:
    //                LayoutInflater inflater = getLayoutInflater();
    //                View dialogView = inflater.inflate(R.layout.alert_filter, null);
    //                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.PinDialog);
    //                final AlertDialog alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create();
    //                alertDialog.show();
    //                final Spinner status= dialogView.findViewById(R.id.status);
    //                final Spinner catfilt= dialogView.findViewById(R.id.category_filter);
    //                final EditText startDateDD= dialogView.findViewById(R.id.start_date_dd);
    //                final EditText startDateMM= dialogView.findViewById(R.id.start_date_mm);
    //                final EditText startDateYYYY= dialogView.findViewById(R.id.start_date_yyyy);
    //                final EditText endDateDD= dialogView.findViewById(R.id.end_date_dd);
    //                final EditText endDateMM= dialogView.findViewById(R.id.end_date_mm);
    //                final EditText endDateYYYY= dialogView.findViewById(R.id.end_date_yyyy);
    //
    //                Button positive = dialogView.findViewById(R.id.positive);
    //                positive.setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //
    //                        if(!(status.getSelectedItem().toString().equals("Select Status")))
    //                        {
    //                            status_text = status.getSelectedItem().toString();
    //                            console.log(status_text);
    //                        }
    //                        else
    //                        {
    //                            status_text="";
    //                        }
    //                        if(!(catfilt.getSelectedItem().toString().equals("Select Category")))
    //                        {
    //                            category_text = catfilt.getSelectedItem().toString();
    //                            console.log(category_text);
    //                        }
    //                        else
    //                        {
    //                            category_text="";
    //                        }
    //                        String start_date=startDateDD.getText()+"/"+startDateMM.getText()+"/"+startDateYYYY.getText();
    //                        String end_date=endDateDD.getText()+"/"+endDateMM.getText()+"/"+endDateYYYY.getText();
    //                        console.log(start_date+" "+end_date);
    //                        filter(status_text,category_text,start_date,end_date);
    //                        alertDialog.dismiss();
    //
    //                    }
    //                });
    //                Button negative = dialogView.findViewById(R.id.negative);
    //                negative.setOnClickListener(new View.OnClickListener() {
    //                    @Override
    //                    public void onClick(View v) {
    //                        alertDialog.dismiss();
    //                    }
    //                });
    //                return true;
    //            case R.id.reset:
    //                loadWorks();
    //                return true;
    //            default:
    //                return super.onOptionsItemSelected(item);
    //        }
    //    }
    //
    //    private void filter(String status,String category,String startdate,String enddate) {
    //        workslist.clear();
    //        progress.setVisibility(View.VISIBLE);
    //        hider.setVisibility(View.VISIBLE);
    //        String filterURL = Config.WORK_FILTERS;
    //        HashMap<String, String> params = new HashMap<>();
    //        params.put("status", status);
    //        params.put("project_id", App.projectId);
    //        params.put("category_filter",category);
    //        params.put("start_date",startdate);
    //        params.put("end_date",enddate);
    //        console.log("Params: "+params);
    //        app.sendNetworkRequest(filterURL, Request.Method.POST, params, new Interfaces.NetworkInterfaceListener() {
    //            @Override
    //            public void onNetworkRequestStart() {
    //
    //            }
    //
    //            @Override
    //            public void onNetworkRequestError(String error) {
    //                progress.setVisibility(View.VISIBLE);
    //                hider.setVisibility(View.VISIBLE);
    //                Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_LONG).show();
    //            }
    //
    //            @Override
    //            public void onNetworkRequestComplete(String response) {
    //                progress.setVisibility(View.GONE);
    //                hider.setVisibility(View.GONE);
    //                console.log("Response Planning:" + response);
    //                try{
    //                    JSONArray array = new JSONArray(response);
    //                    for (int i=0; i<array.length(); i++){
    //                        JSONObject par = array.getJSONObject(i);
    //                        JSONObject sch = par.getJSONObject("work_schedule");
    //                        final Work work = new Work().parseFromJSON(sch.getJSONObject("work_details"), par.getString("work_list_id"),
    //                                sch.getString("work_duration"), sch.getString("qty"), sch.getString("schedule_start_date"), sch.getString("schedule_finish_date")
    //                                , sch.getString("current_status"),sch.getString("qty_completed"),sch.getString("percent_compl"));
    //                        workslist.add(work);
    ////                        , par.getString("completed_activities"), par.getString("total_activities")
    //                        //                        realm.executeTransaction(new Realm.Transaction() {
    ////                            @Override
    ////                            public void execute(Realm realm) {
    ////                                realm.copyToRealmOrUpdate(work);
    ////                            }
    ////                        });
    //                        console.log("Worklist"+workslist.get(i));
    //                    }
    //                    if(workslist.isEmpty()){
    //                        no_content.setVisibility(View.VISIBLE);
    //                    }
    //                    adapter = new DailyWorkAdapter(context, workslist, new DailyWorkAdapter.OnItemClickListener() {
    //                        @Override
    //                        public void onItemClick(int pos, View view) {
    //                            Intent intent = new Intent(context, DailyWorkProgressActivities.class);
    //                            intent.putExtra("id", workslist.get(pos).getWorkListId());
    //                            startActivity(intent);
    //                        }
    //                    }, new DailyWorkAdapter.OnButtonClickListener() {
    //                        @Override
    //                        public void onButtonClick(int pos,View view) {
    //                            menuUpdate(workslist.get(pos));
    //                        }
    //                    });
    //                    items.setLayoutManager(new LinearLayoutManager(context));
    //                    items.setAdapter(adapter);
    //
    //                } catch (JSONException e){
    //
    //                }
    //            }
    //        });
    //    }

    private fun loadWorks() {
        workslist.clear()
        var url = Config.GET_PLANNED_LIST
        url = url.replace("[0]", App.userId)
        url = url.replace("[1]", App.projectId)
        console.log("URL:$url")
        app!!.sendNetworkRequest(url, 0, null, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {

            }

            override fun onNetworkRequestError(error: String) {

            }

            override fun onNetworkRequestComplete(response: String) {
                workslist.clear()
                progress!!.visibility = View.GONE
                hider!!.visibility = View.GONE
                console.log("Response:$response")
                try {
                    val array = JSONArray(response)
                    for (i in 0 until array.length()) {
                        val par = array.getJSONObject(i)
                        val sch = par.getJSONObject("work_schedule")
                        val work = Work().parseFromJSON(sch.getJSONObject("work_details"), par.getString("planned_id"), par.getString("planned_detail_id"),
                                sch.getString("work_duration"), sch.getString("qty"), sch.getString("schedule_start_date"), sch.getString("schedule_finish_date"), sch.getString("current_status"), sch.getString("qty_completed"), sch.getString("percent_compl"), par.getString("assign_qty"), "Plan")
                        workslist.add(work)

                        //                        , par.getString("completed_activities"), par.getString("total_activities")
                        //                        realm.executeTransaction(new Realm.Transaction() {
                        //                            @Override
                        //                            public void execute(Realm realm) {
                        //                                realm.copyToRealmOrUpdate(work);
                        //                            }
                        //                        });
                        console.log("Plan" + work.planned_id)
                    }
                    if (workslist.isEmpty()) {
                        no_content!!.visibility = View.VISIBLE
                    } else
                        no_content!!.visibility = View.GONE
                    adapter = DailyWorkAdapter(context, workslist, "Plan", DailyWorkAdapter.OnItemClickListener { pos, view ->
                        val intent = Intent(context, buildnlive.com.buildem.WorkProgress.DailyWorkProgressActivities::class.java)
                        intent.putExtra("id", workslist[pos].workListId)
                        startActivity(intent)
                    }, DailyWorkAdapter.OnButtonClickListener { pos, view -> menuUpdate(workslist[pos]) })
                    items!!.layoutManager = LinearLayoutManager(context)
                    items!!.adapter = adapter

                } catch (e: JSONException) {

                }

            }
        })
    }


    private fun menuUpdate(activity: Work) {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_dialog_activity, null)
        val dialogBuilder = AlertDialog.Builder(context!!, R.style.PinDialog)
        val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
        alertDialog.show()
        val disable = dialogView.findViewById<TextView>(R.id.disableView)
        val max = dialogView.findViewById<TextView>(R.id.max)
        max.text = "Assigned Quantity: " + activity.assign_qty
        val progress = dialogView.findViewById<ProgressBar>(R.id.progress)
        val message = dialogView.findViewById<EditText>(R.id.message)
        val quantity = dialogView.findViewById<EditText>(R.id.quantity)
        val completed = dialogView.findViewById<TextView>(R.id.completed)
        completed.visibility = View.GONE
        val list = dialogView.findViewById<AdvancedRecyclerView>(R.id.images)
        images = ArrayList()
        images!!.add(Packet())
        imagesAdapter = ActivityImagesAdapter(context, images, ActivityImagesAdapter.OnItemClickListener { packet, pos, view ->
            if (pos == 0) {

                val inflater = layoutInflater
                val dialogView = inflater.inflate(R.layout.image_chooser, null)
                val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context!!, R.style.PinDialog)
                val alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create()
                alertDialog.show()
                val gallery = dialogView.findViewById<TextView>(R.id.gallery)
                val camera = dialogView.findViewById<TextView>(R.id.camera)

                gallery.setOnClickListener {
                    alertDialog.dismiss()
                    val pictureIntent = Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
        //        completed.setText("Completed: " + activity.getQty_completed() + " " + activity.getUnits());
        val title = dialogView.findViewById<TextView>(R.id.alert_title)
        title.text = "Activity Status"
        val alert_message = dialogView.findViewById<TextView>(R.id.alert_message)
        alert_message.text = "Please fill work progress."
        val positive = dialogView.findViewById<Button>(R.id.positive)
        positive.setOnClickListener {
            try {
                if (message.text.toString() != null || quantity.text.toString() != null)
                    submit(activity, message.text.toString(), quantity.text.toString(), images!!, alertDialog)
                else
                    Toast.makeText(context, "Fill data properly!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Fill data properly!", Toast.LENGTH_SHORT).show()
            }
        }
        val negative = dialogView.findViewById<Button>(R.id.negative)
        negative.setOnClickListener { alertDialog.dismiss() }
    }

    //    @Override
    //    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    //        super.onActivityResult(requestCode, resultCode, data);
    //        if (requestCode == REQUEST_CAPTURE_IMAGE) {
    //            if (resultCode == android.app.Activity.RESULT_OK) {
    //                Packet packet = images.remove(0);
    //                packet.setName(imagePath);
    //                images.add(0, new Packet());
    //                images.add(packet);
    //                imagesAdapter.notifyDataSetChanged();
    //            } else if (resultCode == android.app.Activity.RESULT_CANCELED) {
    //                console.log("Canceled");
    //            }
    //        }
    //    }

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

    @Throws(JSONException::class)
    private fun submit(activity: Work, message: String, quantity: String, images: ArrayList<Packet>, alertDialog: AlertDialog) {
        val q = java.lang.Float.parseFloat(quantity)
        //        float c = Float.parseFloat(activity.getQty_completed());
        val qo = java.lang.Float.parseFloat(activity.assign_qty)
        //        console.log("entry completed quanity "+q+" "+c+" "+qo+" " + (qo-c));
        //        if (q <= (qo)) {
        val params = HashMap<String, String>()
        params["latitude"] = lastLocation!!.latitude.toString()
        params["longitude"] = lastLocation!!.longitude.toString()
        params["work_update"] = JSONObject()
                //                    .put("activity_list_id", activity.getActivityListId())
                .put("planner_detail_id", activity.planned_detail_id)
                //                    .put("type", "work")
                .put("comment", message)
                .put("completed_qty", quantity)
                //                    .put("units", activity.getUnits())
                .put("user_id", App.userId)
                .put("project_id", App.projectId).toString()
        //        work_update: planner_detail_id, user_id, completed_qty, comment
        console.log("Work$params")
        val array = JSONArray()
        for (p in images) {
            if (p.name != null) {
                val bm = BitmapFactory.decodeFile(p.name)
                val baos = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, QUALITY, baos)
                val b = baos.toByteArray()
                val encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
                array.put(encodedImage)
            }
            params["images"] = array.toString()
        }

        app!!.sendNetworkRequest(Config.UPDATE_PLANNED_WORK, 1, params, object : Interfaces.NetworkInterfaceListener {
            override fun onNetworkRequestStart() {

            }

            override fun onNetworkRequestError(error: String) {

            }

            override fun onNetworkRequestComplete(response: String) {
                if (response == "1") {
                    Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show()
                    alertDialog.dismiss()
                    finish()
                }
            }
        })
        //        } else {
        //            Toast.makeText(context, "Put right quantity", Toast.LENGTH_SHORT).show();
        //        }
    }

    companion object {
        private val works: RealmResults<Work>? = null
        private val workslist = ArrayList<Work>()
        val QUALITY = 10

        val REQUEST_GALLERY_IMAGE = 7191

        val REQUEST_CAPTURE_IMAGE = 7190
    }

    //    private void changeScreen() {
    //        getSupportFragmentManager()
    //                .beginTransaction()
    //                .replace(R.id.attendance_content, fragment)
    //                .commit();
    //    }
    //
    //    private void disableView() {
    //        int sdk = android.os.Build.VERSION.SDK_INT;
    //        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    //            view.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_left, null));
    //        } else {
    //            view.setBackground(ContextCompat.getDrawable(context, R.drawable.round_left));
    //        }
    //        view.setTextColor(getResources().getColor(R.color.color2));
    //    }
    //
    //    private void enableView() {
    //        int sdk = android.os.Build.VERSION.SDK_INT;
    //        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    //            view.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_left_selected, null));
    //        } else {
    //            view.setBackground(ContextCompat.getDrawable(context, R.drawable.round_left_selected));
    //        }
    //        view.setTextColor(getResources().getColor(R.color.white));
    //    }
    //
    //    private void disableEdit() {
    //        int sdk = android.os.Build.VERSION.SDK_INT;
    //        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    //            edit.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_right, null));
    //        } else {
    //            edit.setBackground(ContextCompat.getDrawable(context, R.drawable.round_right));
    //        }
    //        edit.setTextColor(getResources().getColor(R.color.color2));
    //    }
    //
    //    private void enableEdit() {
    //        int sdk = android.os.Build.VERSION.SDK_INT;
    //        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
    //            edit.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.round_right_selected, null));
    //        } else {
    //            edit.setBackground(ContextCompat.getDrawable(context, R.drawable.round_right_selected));
    //        }
    //        edit.setTextColor(getResources().getColor(R.color.white));
    //    }
}
