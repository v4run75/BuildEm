package buildnlive.com.buildem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import buildnlive.com.buildem.R
import buildnlive.com.buildem.elements.AMCItemDetails
import buildnlive.com.buildem.elements.WorkListItem
import java.util.*

class AddAMCAdapter(private val context: Context, users: ArrayList<WorkListItem>, private val listener: OnItemClickListener) : RecyclerView.Adapter<AddAMCAdapter.ViewHolder>() {

    private var items: List<WorkListItem>? = null

    interface OnItemClickListener {
        fun onItemClick(serviceItem: WorkListItem, pos: Int, view: View)

        fun onItemCheck(serviceItem: WorkListItem, pos: Int, view: View, qty: TextView, checked: Boolean, check: CheckBox)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAMCAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_add_complaint_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, items!![position], position, listener)
    }


    fun addItems(borrowModelList: List<WorkListItem>) {
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

        fun bind(context: Context, item: WorkListItem, pos: Int, listener: OnItemClickListener) {

            name.text = item.workName

            unit.text = item.units
            /*      qty.isEnabled = false
                  qty.isClickable = false*/

            check.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (!qty.text.isNullOrBlank()) {
                        listener.onItemCheck(item, pos, check, qty, isChecked, check)
                    } else {
                        Toast.makeText(context, "Enter Quantity", Toast.LENGTH_SHORT).show()
                        check.isChecked = false
                    }
                }
            }
        }
    }

}
