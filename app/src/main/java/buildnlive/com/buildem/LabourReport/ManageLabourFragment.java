package buildnlive.com.buildem.LabourReport;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.ContractorAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Contractor;
import buildnlive.com.buildem.utils.Config;
import io.realm.Realm;

public class ManageLabourFragment extends Fragment {
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Contractor> contractorList = new ArrayList<>();
    private ContractorAdapter adapter;
    private App app;
    private Fragment fragment;
    private android.app.AlertDialog.Builder builder;
    public static String name_s,user_id_s;
    private ProgressBar progress;
    private TextView hider;
    private CoordinatorLayout coordinatorLayout;

    public static ManageLabourFragment newInstance() {
        return new ManageLabourFragment();
    }

    private ContractorAdapter.OnItemClickListener listner=new ContractorAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Contractor contractor, int pos, View view) {
            name_s= contractor.getContractor_name();
            user_id_s=contractor.getContractor_id();
            startActivity(new Intent(getActivity(), ManageLabourReportLoc.class));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_labour, container, false);;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Realm realm =Realm.getDefaultInstance();
        recyclerView=view.findViewById(R.id.items);
        app=(App) getActivity().getApplication();
        builder = new android.app.AlertDialog.Builder(getContext());
        progress=view.findViewById(R.id.progress);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        hider=view.findViewById(R.id.hider);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(decoration);
        adapter = new ContractorAdapter(getContext(),contractorList,listner);
        recyclerView.setAdapter(adapter);
//        floatingActionButton=view.findViewById(R.id.add);

    }

    @Override
    public void onStart() {
        super.onStart();
        refresh();
    }

    private void refresh() {
        App app= ((App)getActivity().getApplication());
        contractorList.clear();
        String requestUrl = Config.GET_CONTRACTORS;
        requestUrl = requestUrl.replace("[0]", App.userId);
        requestUrl = requestUrl.replace("[1]", App.projectId);
        console.log(requestUrl);
        app.sendNetworkRequest(requestUrl, Request.Method.GET, null, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {
                progress.setVisibility(View.VISIBLE);
                hider.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNetworkRequestError(String error) {
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
                console.error("Network request failed with error :" + error);
//                Toast.makeText(getContext(), "Check Network, Something went wrong", Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Something went wrong, Try again later", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log(response);
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        contractorList.add(new Contractor().parseFromJSON(array.getJSONObject(i)));
                    }
                    console.log("data set changed");
                    adapter = new ContractorAdapter(getContext(), contractorList, listner);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
