package buildnlive.com.buildem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.R;
import buildnlive.com.buildem.elements.ServiceItem;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(ServiceItem project, int pos, View view);
    }

    private List<ServiceItem> items;
    private Context context;
    private final OnItemClickListener listener;

    public ServiceListAdapter(Context context, ArrayList<ServiceItem> users, OnItemClickListener listener) {
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
    public ServiceListAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, items.get(position), position, listener);
    }


    public void addItems(List<ServiceItem> borrowModelList) {
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
        private TextView date;
        private TextView time;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);


        }

        public void bind(final Context context, final ServiceItem item, final int pos, final OnItemClickListener listener) {

            name.setText(item.getName());
            address.setText(item.getAddress());
            date.setText(item.getDate());
            time.setText(item.getTime());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item, pos, itemView);
                }
            });
        }
    }

}