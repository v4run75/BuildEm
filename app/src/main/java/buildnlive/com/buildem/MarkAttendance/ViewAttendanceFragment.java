package buildnlive.com.buildem.MarkAttendance;

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

import buildnlive.com.buildem.R;
import buildnlive.com.buildem.adapters.ViewAttendanceAdapter;
import buildnlive.com.buildem.elements.Worker;
import io.realm.RealmResults;

public class ViewAttendanceFragment extends Fragment {
    private RecyclerView attendees;
    private ProgressBar progress;
    private TextView hider;
    private static RealmResults<Worker> workers;
    private ViewAttendanceAdapter adapter;
    private boolean LOADING;

    public static ViewAttendanceFragment newInstance(RealmResults<Worker> u) {
        workers = u;
        return new ViewAttendanceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_attendance, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attendees = view.findViewById(R.id.attendees);
        progress = view.findViewById(R.id.progress);
        hider = view.findViewById(R.id.hider);
        adapter = new ViewAttendanceAdapter(getContext(), workers);
        attendees.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        attendees.setAdapter(adapter);
        if (LOADING)
            progress.setVisibility(View.VISIBLE);
        else
            progress.setVisibility(View.GONE);
    }
}