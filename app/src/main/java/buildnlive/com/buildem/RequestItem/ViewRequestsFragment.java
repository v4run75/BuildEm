package buildnlive.com.buildem.RequestItem;

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
import org.json.JSONObject;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.ViewRequestAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Request;
import buildnlive.com.buildem.utils.Config;
import io.realm.Realm;
import io.realm.RealmResults;

public class ViewRequestsFragment extends Fragment {
    private RecyclerView items;
    private static App app;
    private ProgressBar progress;
    private TextView hider;

    public static ViewRequestsFragment newInstance(App a) {
        app = a;
        return new ViewRequestsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_requests, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        items = view.findViewById(R.id.requests);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Request> requests = realm.where(Request.class).equalTo("belongsTo", App.belongsTo).findAllAsync();
        ViewRequestAdapter adapter = new ViewRequestAdapter(getContext(), requests);
        items.setAdapter(adapter);
        items.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh();
    }

    private void refresh() {
        String url = Config.GET_REQUEST_LIST;
        url = url.replace("[0]", App.projectId);
        url = url.replace("[1]", App.userId);
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
                Realm realm = Realm.getDefaultInstance();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject obj = array.getJSONObject(i);
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                try {
                                    Request request = new Request().parseFromJSON(obj);
                                    realm.copyToRealmOrUpdate(request);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    realm.close();
                } catch (JSONException e) {

                }
            }
        });
    }
}