package buildnlive.com.buildem.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import buildnlive.com.buildem.R
import buildnlive.com.buildem.elements.AMCItemDetails
import java.util.*

class AMCItemDetailsAdapter(private val context: Context, users: ArrayList<AMCItemDetails.Details>, private val listener: OnItemClickListener) : RecyclerView.Adapter<AMCItemDetailsAdapter.ViewHolder>() {

    private var items: List<AMCItemDetails.Details>? = null

    interface OnItemClickListener {
        fun onItemClick(serviceItem: AMCItemDetails.Details, pos: Int, view: View)

        fun onItemCheck(serviceItem: AMCItemDetails.Details, pos: Int, view: View, qty: TextView)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AMCItemDetailsAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_complaint_details_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, items!![position], position, listener)
    }


    fun addItems(borrowModelList: List<AMCItemDetails.Details>) {
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

        fun bind(context: Context, item: AMCItemDetails.Details, pos: Int, listener: OnItemClickListener) {

            name.text = item.workName

            unit.text = item.units
            qty.isEnabled = false
            qty.isClickable = false
            qty.text = item.qty


        }
    }

}