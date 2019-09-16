package buildnlive.com.buildem.AssetJobSheet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.MachineListAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Machine;
import buildnlive.com.buildem.utils.Config;

public class MachineListFragment extends Fragment {

    private RecyclerView recyclerView;
    private static App app;
    private ArrayList<Machine> inventory;
    private ProgressBar progress;
    private TextView hider;
    private MachineListAdapter issueItemListAdapter;
    private Context context;


    MachineListAdapter.OnItemClickListener listener= (item, pos, view) -> {
        Intent intent=new Intent(getActivity(), MachineListFormLoc.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("Items",item);
        intent.putExtras(bundle);
        startActivity(intent);

    };

    public static MachineListFragment newInstance(App a) {
        app = a;
        return new MachineListFragment();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobsheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context=getContext();
        recyclerView= view.findViewById(R.id.items);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);


        inventory = new ArrayList<>();


        issueItemListAdapter=new MachineListAdapter(context,inventory,listener);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(issueItemListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        refresh();


    }

    private void refresh() {
        inventory.clear();
        String requestUrl = Config.GET_MACHINE_LIST;
        requestUrl = requestUrl.replace("[0]", App.userId);
        requestUrl = requestUrl.replace("[1]", App.projectId);

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
                Toast.makeText(getContext(), "Check Network, Something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log(response);
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        inventory.add(new Machine().parseFromJSON(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//
//                for (Item i : inventory) {
//                    items.add(i.getName());
//                    console.log("Name:"+i.getName());
//                }
                issueItemListAdapter.notifyDataSetChanged();
            }
        });
    }



}
