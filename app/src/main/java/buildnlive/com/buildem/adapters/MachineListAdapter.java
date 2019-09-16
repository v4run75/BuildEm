package buildnlive.com.buildem.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import buildnlive.com.buildem.R;
import buildnlive.com.buildem.elements.Machine;

public class MachineListAdapter extends RecyclerView.Adapter<MachineListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Machine packet, int pos, View view);
    }

    private final List<Machine> items;
    private Context context;
    private final OnItemClickListener listener;

    public MachineListAdapter(Context context, List<Machine> users, OnItemClickListener listener) {
        this.items = users;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_issue_item, parent, false);
        return new ViewHolder(v);
    }@Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(context, items.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, extra;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            extra = view.findViewById(R.id.extra);
        }

        public void bind(final Context context, final Machine item, final int pos, final OnItemClickListener listener) {
            name.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, pos, itemView);
                }
            });
        }
    }
}