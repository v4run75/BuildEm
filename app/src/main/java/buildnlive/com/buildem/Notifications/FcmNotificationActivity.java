package buildnlive.com.buildem.Notifications;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.activities.BillImageView;
import buildnlive.com.buildem.activities.HomeActivity;
import buildnlive.com.buildem.adapters.NotificationsAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Notification;
import buildnlive.com.buildem.utils.Config;
import io.realm.Realm;

public class FcmNotificationActivity extends AppCompatActivity {
    private App app;
    private Realm realm;
    private ProgressBar progressBar;
    private ArrayList<Notification> notificationList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageButton back;
    NotificationsAdapter adapter;
    AlertDialog.Builder builder;
    private static String userId;

    public NotificationsAdapter.OnItemClickListener listener = new NotificationsAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final Notification notification, final int pos, final View view) {

            if (view.getId() == R.id.image) {
                Intent intent = new Intent(FcmNotificationActivity.this, BillImageView.class);
                Bundle bundle = new Bundle();
                bundle.putString("Link", notification.getImage());
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                builder.setMessage("Do you want to Submit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                switch (view.getId()) {
                                    case R.id.receive:
                                        try {
                                            sendRequest(notification.getId(), "Received", userId);
                                            notificationList.remove(pos);
                                            adapter.notifyItemRemoved(pos);
                                            adapter.notifyItemRangeChanged(pos, notificationList.size());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case R.id.not_receive:
                                        try {
                                            sendRequest(notification.getId(), "Not Received", userId);
                                            notificationList.remove(pos);
                                            adapter.notifyItemRemoved(pos);
                                            adapter.notifyItemRangeChanged(pos, notificationList.size());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case R.id.approve:
                                        try {
                                            sendRequest(notification.getId(), "Approved", userId);
                                            notificationList.remove(pos);
                                            adapter.notifyItemRemoved(pos);
                                            adapter.notifyItemRangeChanged(pos, notificationList.size());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case R.id.reject:
                                        try {
                                            sendRequest(notification.getId(), "Rejected", userId);
                                            notificationList.remove(pos);
                                            adapter.notifyItemRemoved(pos);
                                            adapter.notifyItemRangeChanged(pos, notificationList.size());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case R.id.review:
                                        try {
                                            sendRequest(notification.getId(), "Revision", userId);
                                            notificationList.remove(pos);
                                            adapter.notifyItemRemoved(pos);
                                            adapter.notifyItemRangeChanged(pos, notificationList.size());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case R.id.read_notification:
                                        try {
                                            sendRequest(notification.getId(), "Read", userId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        notificationList.remove(pos);
                                        adapter.notifyItemRemoved(pos);
                                        adapter.notifyItemRangeChanged(pos, notificationList.size());
                                        break;
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();

                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Submit");
                alert.show();

            }


        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText("Notifications");
        app = (App) getApplication();
        progressBar = findViewById(R.id.progress);
        realm = Realm.getDefaultInstance();

        Intent intent = getIntent();

        if (intent != null) {
            refresh(intent.getStringExtra("user_id"), intent.getStringExtra("project_id"));
        }

        builder = new AlertDialog.Builder(this);
        recyclerView = (RecyclerView) findViewById(R.id.notifications);


//        final String adapter=new ArrayAdapter<String>(this,mobileArray);
    }

    protected Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) getBaseContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isActivityRunning(HomeActivity.class)) {
            finish();
        } else {
            startActivity(new Intent(FcmNotificationActivity.this, HomeActivity.class));
            finish();
        }
    }


    private void refresh(String userId, String projectId) {
        String requestUrl = Config.SEND_NOTIFICATIONS;
        notificationList.clear();
        requestUrl = requestUrl.replace("[0]", userId);
        requestUrl = requestUrl.replace("[1]", projectId);
        console.log(requestUrl);
        app.sendNetworkRequest(requestUrl, Request.Method.GET, null, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNetworkRequestError(String error) {
                progressBar.setVisibility(View.GONE);
                console.error("Network request failed with error :" + error);
                Toast.makeText(getApplicationContext(), "Check Network, Something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log(response);
                console.log(response);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject obj = array.getJSONObject(i);
                        notificationList.add(new Notification().parseFromJSON(obj));
                    }
                    adapter = new NotificationsAdapter(getApplicationContext(), notificationList, listener);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.hasFixedSize();
                    recyclerView.setAdapter(adapter);

                } catch (JSONException e) {

                }
            }
        });
    }


    private void sendRequest(String id, String answer, String userId) throws JSONException {
        App app = ((App) getApplication());
        HashMap<String, String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id).put("response", answer).put("user_id", userId);
        params.put("notification", jsonObject.toString());
        console.log("Res:" + params);
        app.sendNetworkRequest(Config.GET_NOTIFICATIONS, 1, params, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {

            }

            @Override
            public void onNetworkRequestError(String error) {

            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log(response);
                if (response.equals("1")) {
                    Toast.makeText(getApplicationContext(), "Request Generated", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


}
