package buildnlive.com.buildem.LocalPurchaseAndPayment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import buildnlive.com.buildem.adapters.LocalPurchaseListAdapter;
import buildnlive.com.buildem.console;
import buildnlive.com.buildem.elements.Item;
import buildnlive.com.buildem.utils.Config;

public class LocalPurchaseListFragment extends Fragment {

    private RecyclerView recyclerView;
    private static App app;
    private ArrayList<Item> inventory;
    private ProgressBar progress;
    private TextView hider;
    private LocalPurchaseListAdapter localPurchaseListAdapter;
    private Context context;
    private LinearLayout search_view;
    private EditText search_text;
    private ImageButton search_close,search;



    LocalPurchaseListAdapter.OnItemClickListener listener= (item, pos, view) -> {
        Intent intent=new Intent(getActivity(), LocalPurchaseFormLoc.class);
        Bundle bundle= new Bundle();
        bundle.putSerializable("Items",item);
        intent.putExtras(bundle);
        startActivity(intent);

    };

    public static LocalPurchaseListFragment newInstance(App a) {
        app = a;
        return new LocalPurchaseListFragment();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context=getContext();
        recyclerView= view.findViewById(R.id.items);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);

        search_view = view.findViewById(R.id.search_view);
        search_text=view.findViewById(R.id.search_text);
        search_close=view.findViewById(R.id.search_close);
        search=view.findViewById(R.id.search);


        search.setOnClickListener(v -> {
            search_text.setVisibility(View.VISIBLE);
            search_close.setVisibility(View.VISIBLE);
            search_text.requestFocus();
            if(search_text.hasFocus()){
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(search_text.windowToken,InputMethodManager.SHOW_IMPLICIT )
                if (imm != null) {
                    imm.showSoftInput(search_text, InputMethodManager.SHOW_IMPLICIT);
                }
            }

        });

        search_text.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                search(search_text.getText().toString());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(search_text.getWindowToken(), 0);
                }
                return true;
            } else {
                return false;
            }

        });

        search_close.setOnClickListener(v -> {
            search_text.setText("");
            search_text.setVisibility(View.INVISIBLE);
            search_close.setVisibility(View.INVISIBLE);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            refresh();
        });

        inventory = new ArrayList<>();


        localPurchaseListAdapter=new LocalPurchaseListAdapter(context,inventory,listener);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(context, LinearLayoutManager.VERTICAL);
        recyclerView.setAdapter(localPurchaseListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        refresh();


    }

    private void refresh() {
        inventory.clear();
        String requestUrl = Config.REQ_GET_ITEM_INVENTORY;
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
                        inventory.add(new Item().parseFromJSON(array.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//
//                for (Item i : inventory) {
//                    items.add(i.getName());
//                    console.log("Name:"+i.getName());
//                }
                localPurchaseListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void search(String keyword) {
        inventory.clear();
        String requestUrl = Config.INVENTORY_SEARCH;
        requestUrl = requestUrl.replace("[0]", App.userId);
        requestUrl = requestUrl.replace("[1]", App.projectId);
        requestUrl = requestUrl.replace("[2]", keyword);
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
                Toast.makeText(getContext(), "Check Network, Something went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNetworkRequestComplete(String response) {
//                console.log(response);
                progress.setVisibility(View.GONE);
                hider.setVisibility(View.GONE);
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        inventory.add(new Item().parseFromJSON(array.getJSONObject(i)));
                    }
                    console.log("data set changed");

                    localPurchaseListAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
