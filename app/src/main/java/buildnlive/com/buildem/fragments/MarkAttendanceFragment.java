package buildnlive.com.buildem.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import buildnlive.com.buildem.activities.CreateLabour;
import buildnlive.com.buildem.adapters.AttendanceAdapter;
import buildnlive.com.buildem.adapters.ListAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Packet;
import buildnlive.com.buildem.elements.Worker;
import buildnlive.com.buildem.utils.Config;
import buildnlive.com.buildem.utils.PrefernceFile;
import buildnlive.com.buildem.utils.Utils;
import io.realm.Realm;
import io.realm.RealmResults;

public class MarkAttendanceFragment extends Fragment implements Interfaces.SyncListener {
    private Button submit;
    private RecyclerView attendees;
    private ProgressBar progress;
    private TextView hider;
    private boolean LOADING;
    private static RealmResults<Worker> workers;
    private static App app;
    private AttendanceAdapter adapter;
    private FloatingActionButton fab;


    public static MarkAttendanceFragment newInstance(App a, RealmResults<Worker> u) {
        workers = u;
        app = a;
        return new MarkAttendanceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mark_attendance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(PrefernceFile.Companion.getInstance(getContext()).getString("Lat")!=null){
            console.log("Fragment: "+PrefernceFile.Companion.getInstance(getContext()).getString("Lat"));
        }

        attendees = view.findViewById(R.id.attendees);
        submit = view.findViewById(R.id.submit);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateLabour.class));
            }
        });

        adapter = new AttendanceAdapter(getContext(), workers, new AttendanceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Worker worker, int pos, View view) {
                showUser(worker);
            }
        });
        attendees.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        attendees.setAdapter(adapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Worker> checkingOutWorkers = new ArrayList<>();
                final JSONArray checkIn = new JSONArray();
                final JSONArray checkOut = new JSONArray();
                Realm realm = Realm.getDefaultInstance();
                for (final String s : AttendanceAdapter.ViewHolder.changedUsers.keySet()) {
                    Worker u = realm.where(Worker.class).equalTo("id", s).findFirst();
                    try {
                        if (AttendanceAdapter.ViewHolder.changedUsers.get(s)) {
                            checkingOutWorkers.add(u);
                            checkOut.put(new JSONObject().put("starttime", Utils.fromTimeStampToDate(u.getCheckInTime())).put("finishtime", Utils.fromTimeStampToDate(System.currentTimeMillis())).put("attendence_id", u.getAttendanceId()));
                        } else {
                            checkIn.put(new JSONObject().put("starttime", Utils.fromTimeStampToDate(System.currentTimeMillis())).put("labour_id", u.getWorkerId()));
                        }
                    } catch (JSONException e) {

                    }
                }
                realm.close();
                if (checkIn.length() > 0) {
                    progress.setVisibility(View.VISIBLE);
                    hider.setVisibility(View.VISIBLE);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", App.userId);
                    params.put("project_id", App.projectId);
                    params.put("attendence", checkIn.toString()); // TODO PLEASE CHECK SPELLING MISTAKES
                    app.sendNetworkRequest(Config.REQ_POST_CHECK_IN, Request.Method.POST, params, new Interfaces.NetworkInterfaceListener() {
                        @Override
                        public void onNetworkRequestStart() {

                        }

                        @Override
                        public void onNetworkRequestError(String error) {
                            progress.setVisibility(View.GONE);
                            hider.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNetworkRequestComplete(String response) {
                            progress.setVisibility(View.GONE);
                            hider.setVisibility(View.GONE);
                            AttendanceAdapter.ViewHolder.changedUsers.clear();
                            console.log("CheckIn Response:" + response);
                            try {
                                JSONArray array = new JSONArray(response);
                                Realm realm = Realm.getDefaultInstance();
                                for (int i = 0; i < array.length(); i++) {
                                    final JSONObject obj = array.getJSONObject(i);
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            try {
                                                Worker u = realm.where(Worker.class).equalTo("id", obj.getString("labour_id") + App.belongsTo).findFirst();
                                                u.setCheckedIn(true);
                                                u.setCheckInTime(System.currentTimeMillis());
                                                u.setAttendanceId(obj.getString("attendence_id"));
                                                realm.copyToRealmOrUpdate(u);
                                            } catch (JSONException e) {

                                            }
                                        }
                                    });
                                }
                                realm.close();
                            } catch (JSONException e) {

                            }
                            Toast.makeText(getContext(), "Attendance Updated", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if (checkOut.length() > 0) {
                    progress.setVisibility(View.VISIBLE);
                    hider.setVisibility(View.VISIBLE);
                    final HashMap<String, String> params = new HashMap<>();
                    params.put("user_id", App.userId);
                    params.put("project_id", App.projectId);
                    params.put("attendence", checkOut.toString()); // TODO PLEASE CHECK SPELLING MISTAKES
                    app.sendNetworkRequest(Config.REQ_POST_CHECK_OUT, Request.Method.POST, params, new Interfaces.NetworkInterfaceListener() {
                        @Override
                        public void onNetworkRequestStart() {
                            console.log(checkOut.toString());
                        }

                        @Override
                        public void onNetworkRequestError(String error) {
                            progress.setVisibility(View.GONE);
                            hider.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNetworkRequestComplete(String response) {
                            AttendanceAdapter.ViewHolder.changedUsers.clear();
                            progress.setVisibility(View.GONE);
                            hider.setVisibility(View.GONE);
                            console.log("CheckOut Response:" + response);
                            if (response.equals("1")) {
                                Realm realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        for (Worker u : checkingOutWorkers) {
                                            u.setCheckOutTime(System.currentTimeMillis());
                                            u.setCheckedOut(true);
                                            realm.copyToRealmOrUpdate(u);
                                        }

                                    }
                                });
                                realm.close();
                                Toast.makeText(getContext(), "Attendance Updated", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });

        if (LOADING) {
            progress.setVisibility(View.VISIBLE);
            hider.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
            hider.setVisibility(View.GONE);
        }
    }

    private void showUser(final Worker worker) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(), R.style.PinDialog);
        final AlertDialog alertDialog = dialogBuilder.setCancelable(true).setView(dialogView).create();
        alertDialog.show();
        final TextView disable = dialogView.findViewById(R.id.disableView);
        final ProgressBar progress = dialogView.findViewById(R.id.progress);
        final RecyclerView list = dialogView.findViewById(R.id.list);
//        list.setmMaxHeight(400);
        TextView title = dialogView.findViewById(R.id.alert_title);
        title.setText("Worker Details");
        TextView message = dialogView.findViewById(R.id.alert_message);
        message.setText(worker.getName() + " (" + worker.getRoleAssigned() + ")");
        Button positive = dialogView.findViewById(R.id.positive);
        positive.setText("Close");
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        Button negative = dialogView.findViewById(R.id.negative);
        negative.setVisibility(View.GONE);
        String requestUrl = Config.REQ_GET_USER_ATTENDANCE;
        requestUrl = requestUrl.replace("[0]", App.userId);
        requestUrl = requestUrl.replace("[1]", worker.getWorkerId());
        requestUrl = requestUrl.replace("[2]", App.projectId);
        app.sendNetworkRequest(requestUrl, Request.Method.GET, null, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {

            }

            @Override
            public void onNetworkRequestError(String error) {

            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log("Request :" + response);
                try {
                    List<Packet> packets = parseRequest(response);
                    ListAdapter adapter = new ListAdapter(getContext(), packets, new ListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(Packet packet, int pos, View view) {

                        }
                    });
                    disable.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    list.setLayoutManager(manager);
                    list.setVisibility(View.VISIBLE);
                    list.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private List<Packet> parseRequest(String response) throws JSONException {
        JSONArray array = new JSONArray(response);
        List<Packet> packets = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            packets.add(new Packet(obj.getString("start_date_time"), obj.getString("end_date_time"), 7190));
        }
        return packets;
    }

    @Override
    public void onSyncError(String error) {

    }

    @Override
    public void onSync(Object object) {
        workers = (RealmResults<Worker>) object;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSyncFinish() {
        LOADING = false;
        if (progress != null) {
            progress.setVisibility(View.GONE);
            hider.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSyncStart() {
        LOADING = true;
        if (progress != null) {
            progress.setVisibility(View.VISIBLE);
            hider.setVisibility(View.VISIBLE);
        }
    }
}