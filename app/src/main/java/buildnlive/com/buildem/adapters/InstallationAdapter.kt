package buildnlive.com.buildem.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

import java.util.ArrayList

import buildnlive.com.buildem.R
import buildnlive.com.buildem.elements.InstallationActivityItem

class InstallationAdapter(private val context: Context, users: ArrayList<InstallationActivityItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<InstallationAdapter.ViewHolder>() {

    private var items: List<InstallationActivityItem>? = null

    interface OnItemClickListener {
        fun onItemClick(installationItem: InstallationActivityItem, pos: Int, view: View)

        fun onItemCheck(installationItem: InstallationActivityItem, pos: Int, view: View, qty: TextView)
    }

    init {
        this.items = users
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstallationAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_service_activity, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, items!![position], position, listener)
    }


    fun addItems(borrowModelList: List<InstallationActivityItem>) {
        this.items = borrowModelList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.name)
        private val unit: TextView = view.findViewById(R.id.units)
        private val qty: TextView = view.findViewById(R.id.qty)
        private val check: CheckBox = view.findViewById(R.id.check)

        fun bind(context: Context, item: InstallationActivityItem, pos: Int, listener: OnItemClickListener) {

            name.text = item.name

            if(item.type == "item")
            {
                unit.text= item.units
                qty.isEnabled=false
                qty.isClickable=false
                qty.text = item.quantity
                name.compoundDrawablePadding=30
                name.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_settings),null,null,null)
            }
            else
            {
                unit.text= item.units
                qty.isEnabled=true
                qty.isClickable=true
                qty.text = item.quantity
                name.compoundDrawablePadding=30
                name.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_customer_support),null,null,null)
            }

            check.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!qty.text.isNullOrBlank()) {
                        listener.onItemCheck(item, pos, check, qty)
                    } else {
                        Toast.makeText(context, "Enter Quantity", Toast.LENGTH_SHORT).show()
                        check.isChecked = false
                    }
                }
            }

            //            itemView.setOnClickListener(new View.OnClickListener() {
            //                @Override
            //                public void onClick(View view) {
            //                    listener.onItemClick(item, pos, itemView);
            //                }
            //            });
        }
    }

}