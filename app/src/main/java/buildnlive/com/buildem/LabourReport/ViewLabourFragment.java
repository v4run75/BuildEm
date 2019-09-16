package buildnlive.com.buildem.LabourReport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Interfaces;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.ViewLabourAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.ViewLabour;
import buildnlive.com.buildem.utils.Config;

public class ViewLabourFragment extends Fragment{
    private static List<ViewLabour> viewLabourList=new ArrayList<>();
    private RecyclerView items;
    private static App app;
    private ProgressBar progress;
    private TextView hider,no_content;
    private ViewLabourAdapter adapter;

    public ViewLabourAdapter.OnItemClickListener listener= new ViewLabourAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(final ViewLabour viewLabour, int pos, View view) {
            if(view.getId()==R.id.returnbutton){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_dialog_labour, null);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.PinDialog);
                final AlertDialog alertDialog = dialogBuilder.setCancelable(false).setView(dialogView).create();
                alertDialog.show();
                final EditText alert_message=dialogView.findViewById(R.id.alert_message);
                final Button close=dialogView.findViewById(R.id.negative);
                final Button approve=dialogView.findViewById(R.id.positive);
//                alert_message.setText(ViewLabour.getName());
//                name.setText(ViewLabour.getQuantity());

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    String comment= alert_message.getText().toString();
                    String transfer_id=viewLabour.getId();
                        try {
                            sendRequest(comment,transfer_id);
                            alertDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };

    public static ViewLabourFragment newInstance(App a) {
        app=a;
        return new ViewLabourFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_labour, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        no_content=view.findViewById(R.id.no_content);
        items = view.findViewById(R.id.items);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);
        refresh();


    }

    private void refresh() {
        viewLabourList.clear();
        String url = Config.VIEW_LABOUR_LIST;
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
                console.log("Error"+error);
            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log("Response:" + response);
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
                try {
                    JSONArray array = new JSONArray(response);
                    console.log("Array:"+array.toString());
                    for (int i = 0; i < array.length(); i++) {
                        final JSONObject obj = array.getJSONObject(i);
                        console.log("OBject"+obj.toString());
                        viewLabourList.add(new ViewLabour().parseFromJSON(obj));
                        console.log("List:"+viewLabourList);
                        if(viewLabourList.isEmpty()){
                            no_content.setVisibility(View.VISIBLE);
                        }
                        else no_content.setVisibility(View.GONE);
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

                    adapter = new ViewLabourAdapter(getContext(), viewLabourList,listener);
                    items.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    items.setAdapter(adapter);
//                    realm.close();

                } catch (JSONException e) {

                }
            }
        });
    }
    private void sendRequest(String comments,String transfer_id) throws JSONException {
        App app= ((App)getActivity().getApplication());
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", App.userId);
        params.put("transfer_id", transfer_id);
        params.put("comments",comments);
        app.sendNetworkRequest(Config.SEND_COMMENTS, 1, params, new Interfaces.NetworkInterfaceListener() {
            @Override
            public void onNetworkRequestStart() {
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
            }

            @Override
            public void onNetworkRequestError(String error) {

            }

            @Override
            public void onNetworkRequestComplete(String response) {
                console.log(response);
                if (response.equals("1")) {
                    refresh();
                    Toast.makeText(getContext(), "Request Generated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
