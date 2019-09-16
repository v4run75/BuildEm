package buildnlive.com.buildem.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.activities.ReturnIssuedItem;
import buildnlive.com.buildem.adapters.ViewIssueAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.ViewIssue;
import buildnlive.com.buildem.utils.Config;
import io.realm.Realm;

public class ViewIssueFragment extends Fragment {
    private static List<ViewIssue> itemsList=new ArrayList<>();
    private RecyclerView items;
    private Realm realm;
    private ProgressBar progress;
    private static App app;
    private TextView hider;

    public static ViewIssueFragment newInstance(App a) {
        app=a;
        return new ViewIssueFragment();
    }

    private ViewIssueAdapter.OnItemClickListener listner=new ViewIssueAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(ViewIssue items, int pos, View view) {
            Bundle bundle= new Bundle();
            bundle.putSerializable("Item",items);
            Intent intent= new Intent(getActivity(),ReturnIssuedItem.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_issues, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        realm = Realm.getDefaultInstance();
//        itemsList = realm.where(Issue.class).equalTo("belongsTo", App.belongsTo).findAll();
        items = view.findViewById(R.id.items);
        progress=view.findViewById(R.id.progress);
        hider=view.findViewById(R.id.hider);
        ViewIssueAdapter adapter = new ViewIssueAdapter(getContext(), itemsList,listner);
        items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        items.setAdapter(adapter);
        refresh();
       }


    private void refresh() {
        itemsList.clear();
        String url = Config.GET_ISSUE_STATUS;
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
                        itemsList.add(new ViewIssue().parseFromJSON(array.getJSONObject(i)));

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
//                      realm.close();

                } catch (JSONException e) {
                }
                ViewIssueAdapter adapter = new ViewIssueAdapter(getContext(), itemsList,listner);
                items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                items.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                for(ViewIssue i: itemsList){
                    console.log(i.getReciever());
                }
            }
        });
    }
}