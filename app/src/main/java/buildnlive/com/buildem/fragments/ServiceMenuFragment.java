package buildnlive.com.buildem.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import buildnlive.com.buildem.AMC.AMCActivity;
import buildnlive.com.buildem.App;
import buildnlive.com.buildem.Complaints.ComplaintActivity;
import buildnlive.com.buildem.Installations.InstallationActivity;
import buildnlive.com.buildem.R;
import buildnlive.com.buildem.Services.ServicesActivity;
import buildnlive.com.buildem.utils.PrefernceFile;

import static buildnlive.com.buildem.utils.Config.PREF_NAME;


public class ServiceMenuFragment extends Fragment implements View.OnClickListener {

    private LinearLayout installations, complaints, amc, services;
    private SharedPreferences pref;
    private static App app;

    public static ServiceMenuFragment newInstance(App a) {
        app = a;
        return new ServiceMenuFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pref = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        installations = view.findViewById(R.id.installations);
        complaints = view.findViewById(R.id.complaints);
        amc = view.findViewById(R.id.amc);
        services = view.findViewById(R.id.services);
        installations.setOnClickListener(this);
        complaints.setOnClickListener(this);
        amc.setOnClickListener(this);
        services.setOnClickListener(this);

        ArrayList<String> permissionList = PrefernceFile.Companion.getInstance(getContext()).getArrayList("Perm");

        for (String permission : permissionList) {
            switch (permission) {
                case "Installations": {
                    installations.setVisibility(View.VISIBLE);
                    break;
                }
                case "Complaints": {
                    complaints.setVisibility(View.VISIBLE);
                    break;
                }
                case "AMC": {
                    amc.setVisibility(View.VISIBLE);
                    break;
                }
                case "Services": {
                    services.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.installations:
                startActivity(new Intent(getContext(), InstallationActivity.class));
                break;
            case R.id.complaints:
                startActivity(new Intent(getContext(), ComplaintActivity.class));
                break;
            case R.id.amc:
                startActivity(new Intent(getContext(), AMCActivity.class));
                break;
            case R.id.services:
                startActivity(new Intent(getContext(), ServicesActivity.class));
                break;
        }
    }

}