package buildnlive.com.buildem.IndentItem;

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

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.ViewIndentAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.RequestList;
import buildnlive.com.buildem.utils.Config;

public class ViewIndentFragment extends Fragment {
    private RecyclerView items;
    private static App app;
    private ProgressBar progress;
    private TextView hider;
    private static List<RequestList> requestList =new ArrayList<>();

    public static ViewIndentFragment newInstance(App a) {
        app = a;
        return new ViewIndentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_indent_item, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        items = view.findViewById(R.id.requests);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);

        refresh();
    }

    private void refresh() {
        requestList.clear();
        String url = Config.SEND_REQUEST_STATUS;
        url = url.replace("[0]", App.userId);
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
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject obj = array.getJSONObject(i);
                        requestList.add(new RequestList().parseFromJSON(obj));

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
                    ViewIndentAdapter adapter = new ViewIndentAdapter(getContext(), requestList);
                    items.setAdapter(adapter);
                    items.setLayoutManager(new LinearLayoutManager(getContext()));
//                    realm.close();

                } catch (JSONException e) {

                }
            }
        });
    }
}