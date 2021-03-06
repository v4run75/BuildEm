package buildnlive.com.buildem.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.R;
import buildnlive.com.buildem.elements.Complaint;

public class ComplaintListAdapter extends RecyclerView.Adapter<ComplaintListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Complaint project, int pos, View view);
    }

    private List<Complaint> items;
    private Context context;
    private final OnItemClickListener listener;

    public ComplaintListAdapter(Context context, ArrayList<Complaint> users, OnItemClickListener listener) {
        this.items = users;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NotNull
    @Override
    public ComplaintListAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_complaint_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, items.get(position), position, listener);
    }


    public void addItems(List<Complaint> borrowModelList) {
        this.items = borrowModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView address;
        private TextView mobileNo;
        private TextView date;
        private TextView time;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            mobileNo = view.findViewById(R.id.mobileNo);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);


        }

        public void bind(final Context context, final Complaint item, final int pos, final OnItemClickListener listener) {

            name.setText(item.getCustomerName());
            address.setText(item.getCustomerAddress());
            date.setText(item.getComplaintDate());
//            time.setText(item.getTime());
            mobileNo.setText(item.getCustomerContact());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item, pos, itemView);
                }
            });
        }
    }

}