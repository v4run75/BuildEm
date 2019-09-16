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
import buildnlive.com.buildem.elements.SearchModel;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(SearchModel project, int pos, View view);
    }

    private List<SearchModel> items;
    private Context context;
    private final OnItemClickListener listener;

    public SearchAdapter(Context context, ArrayList<SearchModel> users, OnItemClickListener listener) {
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
    public SearchAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(context, items.get(position), position, listener);
    }


    public void addItems(List<SearchModel> borrowModelList) {
        this.items = borrowModelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView modelNo,modelName,brandName,available,mrp,displayPrice;

        public ViewHolder(View view) {
            super(view);
            modelNo= view.findViewById(R.id.modelNo);
            modelName= view.findViewById(R.id.modelName);
            brandName= view.findViewById(R.id.brandName);
            available= view.findViewById(R.id.available);
            mrp= view.findViewById(R.id.mrp);
            displayPrice= view.findViewById(R.id.displayPrice);

        }

        public void bind(final Context context, final SearchModel item, final int pos, final OnItemClickListener listener) {

            modelNo.setText(item.getModel_no());
            modelName.setText(item.getModel_name());
            brandName.setText(item.getBrand_name());
            available.setText(item.getAvailable_qty());
            mrp.setText(item.getMrp());
            displayPrice.setText(item.getDisplay_price());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item,pos,itemView);
                }
            });
        }
    }

}