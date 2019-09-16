package buildnlive.com.buildem.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import buildnlive.com.buildem.R;
import buildnlive.com.buildem.elements.CustomerData;

public class CustomerDataAdapter extends RecyclerView.Adapter<CustomerDataAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(CustomerData.Details project, int pos, View view);
    }

    private List<CustomerData.Details> items;
    private Context context;
    private final OnItemClickListener listener;

    public CustomerDataAdapter(Context context, ArrayList<CustomerData.Details> users, OnItemClickListener listener) {
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
    public CustomerDataAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, items.get(position), position, listener);
    }


    public void addItems(List<CustomerData.Details> borrowModelList) {
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

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);

        }

        public void bind(final Context context, final CustomerData.Details item, final int pos, final OnItemClickListener listener) {

            name.setText(item.getModelName());
            address.setText(item.getQty());

        }
    }

}