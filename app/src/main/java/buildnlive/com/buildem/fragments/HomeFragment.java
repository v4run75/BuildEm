package buildnlive.com.buildem.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.AssetJobSheet.MachineList;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.Inventory.AddItem;
import buildnlive.com.buildem.IssueItem.IssuedItems;
import buildnlive.com.buildem.LabourReport.LabourReportActivity;
import buildnlive.com.buildem.LocalPurchaseAndPayment.LocalPurchaseKotlin;
import buildnlive.com.buildem.MarkAttendance.MarkAttendanceKotlin;
import buildnlive.com.buildem.PurchaseOrder.PurchaseOrder;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.RequestItem.RequestItems;
import buildnlive.com.buildem.WorkPlanning.PlanningLoc;
import buildnlive.com.buildem.WorkProgress.WorkProgressLoc;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Project;
import buildnlive.com.buildem.elements.ProjectMember;
import buildnlive.com.buildem.utils.Config;
import io.realm.Realm;
import io.realm.RealmResults;

import static buildnlive.com.buildem.utils.Config.PREF_NAME;

//import buildnlive.com.buildem.activities.Planning;

public class HomeFragment extends Fragment implements View.OnClickListener {
    //    private TextView title;
    private LinearLayout markAttendance, manageInventory, issuedItems, requestItems, workProgress, purchaseOrder, localPurchase, labourReport, planning, machine;
    private SharedPreferences pref;
    private Spinner projects;
    private static App app;
    private TextView badge;

    public static HomeFragment newInstance(App a) {
        app = a;
        return new HomeFragment();
    }

    private ArrayList<String> userProjects = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pref = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        markAttendance = view.findViewById(R.id.mark_attendance);
        manageInventory = view.findViewById(R.id.manage_inventory);
        issuedItems = view.findViewById(R.id.issued_items);
        requestItems = view.findViewById(R.id.request_items);
        projects = view.findViewById(R.id.projects);
//        siteRequest=view.findViewById(R.id.request_list);
        localPurchase = view.findViewById(R.id.local_purchase);
//        labour=view.findViewById(R.id.labour);
        labourReport = view.findViewById(R.id.manage_labour);
        planning = view.findViewById(R.id.planning);
        machine = view.findViewById(R.id.machine);

        badge = getActivity().findViewById(R.id.badge_notification);

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Project> projects = realm.where(Project.class).findAll();
        for (Project p : projects) {
            userProjects.add(p.getName());
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, userProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.projects.setAdapter(adapter);
        this.projects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                App.projectId = projects.get(position).getId();
                App.belongsTo = App.projectId + App.userId;
                App.projectName = projects.get(position).getName();
                syncProject();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        title = view.findViewById(R.id.title);
        workProgress = view.findViewById(R.id.work_progress);
        purchaseOrder = view.findViewById(R.id.purchase);

        markAttendance.setOnClickListener(this);
        manageInventory.setOnClickListener(this);
        issuedItems.setOnClickListener(this);
        requestItems.setOnClickListener(this);
        workProgress.setOnClickListener(this);
        purchaseOrder.setOnClickListener(this);
//        siteRequest.setOnClickListener(this);
        localPurchase.setOnClickListener(this);
//        labour.setOnClickListener(this);
        labourReport.setOnClickListener(this);
        planning.setOnClickListener(this);
        machine.setOnClickListener(this);

        workProgress.setVisibility(View.VISIBLE);
        planning.setVisibility(View.VISIBLE);
        workProgress.setVisibility(View.VISIBLE);
        labourReport.setVisibility(View.VISIBLE);
        manageInventory.setVisibility(View.VISIBLE);
        manageInventory.setVisibility(View.VISIBLE);
        issuedItems.setVisibility(View.VISIBLE);
        purchaseOrder.setVisibility(View.VISIBLE);
        localPurchase.setVisibility(View.VISIBLE);
        machine.setVisibility(View.VISIBLE);
        requestItems.setVisibility(View.VISIBLE);
        markAttendance.setVisibility(View.VISIBLE);

/*
        ArrayList<String> permissionList = PrefernceFile.Companion.getInstance(getContext()).getArrayList("Perm");

        for (String permission : permissionList) {
            switch (permission) {
                case "Work": {
                    workProgress.setVisibility(View.VISIBLE);
                    break;
                }
                case "Progress": {
                    planning.setVisibility(View.VISIBLE);
                    break;
                }
                case "Work Schedule": {
                    workProgress.setVisibility(View.VISIBLE);
                    break;
                }
                case "Labour Progress": {
                    labourReport.setVisibility(View.VISIBLE);
                    break;
                }
                case "Inventory": {
                    manageInventory.setVisibility(View.VISIBLE);
                    break;
                }
                case "Indent Item": {
                    manageInventory.setVisibility(View.VISIBLE);
                    break;
                }
                case "Issue Item": {
                    issuedItems.setVisibility(View.VISIBLE);
                    break;
                }
                case "Receive Item": {
                    purchaseOrder.setVisibility(View.VISIBLE);
                    break;
                }
                case "Local Purchase": {
                    localPurchase.setVisibility(View.VISIBLE);
                    break;
                }
                case "Assets": {
                    machine.setVisibility(View.VISIBLE);
                    break;
                }
                case "Request": {
                    requestItems.setVisibility(View.VISIBLE);
                    break;
                }
                case "Labour Attendance": {
                    markAttendance.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
*/

/*
        switch (App.permissions) {
            case "Storekeeper":
//                labour.setVisibility(View.GONE);
//                siteRequest.setVisibility(View.GONE);
                workProgress.setVisibility(View.GONE);
                break;
            case "Siteengineer":
                markAttendance.setVisibility(View.GONE);
                manageInventory.setVisibility(View.GONE);
                purchaseOrder.setVisibility(View.GONE);
                issuedItems.setVisibility(View.GONE);
                localPurchase.setVisibility(View.GONE);

                break;
            case "Siteadmin":
//                siteRequest.setVisibility(View.GONE);
                issuedItems.setVisibility(View.GONE);
                purchaseOrder.setVisibility(View.GONE);
                manageInventory.setVisibility(View.GONE);
                workProgress.setVisibility(View.GONE);
//                labour.setVisibility(View.GONE);

                break;
            case "Siteincharge":
//                siteRequest.setVisibility(View.GONE);
//                labour.setVisibility(View.GONE);
                break;
            case "labourmanager":
                workProgress.setVisibility(View.GONE);
                purchaseOrder.setVisibility(View.GONE);
                markAttendance.setVisibility(View.GONE);
                manageInventory.setVisibility(View.GONE);
                issuedItems.setVisibility(View.GONE);
                requestItems.setVisibility(View.GONE);
                workProgress.setVisibility(View.GONE);
                purchaseOrder.setVisibility(View.GONE);
//                siteRequest.setVisibility(View.GONE);
                localPurchase.setVisibility(View.GONE);
//                labour.setVisibility(View.GONE);
                break;

        }*/

//            title.setText("Welcome " + pref.getString(PREF_KEY_NAME, "Dummy").split(" ")[0]);


    }

    private void syncProject() {
        String url = Config.REQ_SYNC_PROJECT;
        url = url.replace("[0]", App.userId).replace("[1]", App.projectId);
        app.sendNetworkRequest(url, 0, null, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {

            }

            @Override
            public void onNetworkRequestError(String error) {

            }

            @Override
            public void onNetworkRequestComplete(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    final JSONArray array = obj.getJSONArray("project_members");
                    Realm realm = Realm.getDefaultInstance();
                    for (int i = 0; i < array.length(); i++) {
                        final ProjectMember member = new ProjectMember().parseFromJSON(array.getJSONObject(i));
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealmOrUpdate(member);
                            }
                        });
                    }
                    realm.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mark_attendance:
//                Handler handler=new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                startActivity(new Intent(getContext(), MarkAttendanceKotlin.class));
//
//                    }
//                },2000);
                break;
            case R.id.manage_inventory:
                startActivity(new Intent(getContext(), AddItem.class));
                break;
            case R.id.issued_items:
                startActivity(new Intent(getContext(), IssuedItems.class));
                break;
            case R.id.request_items:
                startActivity(new Intent(getContext(), RequestItems.class));
                break;
            case R.id.work_progress:
                startActivity(new Intent(getContext(), WorkProgressLoc.class));
                break;
            case R.id.purchase:
                startActivity(new Intent(getContext(), PurchaseOrder.class));
                break;
//            case R.id.request_list:
//                startActivity(new Intent(getContext(),IndentItems.class));
//                break;
            case R.id.local_purchase:
                startActivity(new Intent(getContext(), LocalPurchaseKotlin.class));
                break;
//            case R.id.labour:
//                startActivity(new Intent(getContext(),LabourActivity.class));
//                break;
            case R.id.manage_labour:
                startActivity(new Intent(getContext(), LabourReportActivity.class));
                break;
            case R.id.planning:
                startActivity(new Intent(getContext(), PlanningLoc.class));
                break;
            case R.id.machine:
                startActivity(new Intent(getContext(), MachineList.class));
                break;

        }
    }

    private void sendRequest() throws JSONException {
        App app = ((App) getActivity().getApplication());
        HashMap<String, String> params = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("project_id", App.projectId).put("user_id", App.userId);
        params.put("notification_count", jsonObject.toString());
        console.log("Res:" + params);
        app.sendNetworkRequest(Config.GET_NOTIFICATIONS_COUNT, 1, params, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {

            }

            @Override
            public void onNetworkRequestError(String error) {

            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log(response);
                if (response.equals("0")) {
                    badge.setVisibility(View.GONE);
                } else {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(response);
                }
            }
        });
    }
}