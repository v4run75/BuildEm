package buildnlive.com.buildem.AssetJobSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.ViewJobSheetAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.ViewJobSheet;
import buildnlive.com.buildem.utils.Config;
import io.realm.Realm;

public class ViewMachineFragment extends Fragment {
    private static List<ViewJobSheet> itemsList=new ArrayList<>();
    private RecyclerView items;
    private Realm realm;
    private ProgressBar progress;
    private static App app;
    private TextView hider;

    public static ViewMachineFragment newInstance(App a) {
        app=a;
        return new ViewMachineFragment();
    }

    private ViewJobSheetAdapter.OnItemClickListener listner=new ViewJobSheetAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(ViewJobSheet items, int pos, View view) {

        }

    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_jobsheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        realm = Realm.getDefaultInstance();
//        itemsList = realm.where(Issue.class).equalTo("belongsTo", App.belongsTo).findAll();
        items = view.findViewById(R.id.items);
        progress=view.findViewById(R.id.progress);
        hider=view.findViewById(R.id.hider);
        ViewJobSheetAdapter adapter = new ViewJobSheetAdapter(getContext(), itemsList,listner);
        items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        items.setAdapter(adapter);
        refresh();
    }


    private void refresh() {
        itemsList.clear();
        String url = Config.VIEW_MACHINE_LIST;
        url = url.replace("[0]", App.userId);
        url = url.replace("[1]", App.projectId);
        app.sendNetworkRequest(url, 0, null, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {
                progress.setVisibility(View.VISIBLE);
                hider.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNetworkRequestError(String error) {
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log("Response:" + response);
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
//                Realm realm = Realm.getDefaultInstance();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        itemsList.add(new ViewJobSheet().parseFromJSON(array.getJSONObject(i)));


//                        realm.executeTransaction(new Realm.Transaction() {
//                            @Override
//                            public void execute(Realm realm) {
//                                try {
//                                    RequestList request = new RequestList().parseFromJSON(obj);
//                                    realm.copyToRealmOrUpdate(request);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
                    }
                    console.log("Size Of JobSheet: "+itemsList.size());
//                      realm.close();

                } catch (JSONException e) {
                }

                ViewJobSheetAdapter adapter = new ViewJobSheetAdapter(getContext(),itemsList,listner);
                items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                items.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });
    }
}