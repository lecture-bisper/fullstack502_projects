package bitc.full502.lostandfound.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bitc.full502.lostandfound.R
import bitc.full502.lostandfound.data.model.ItemData

class ItemAdapter(
    private val items: List<ItemData>,
    private val onItemClick: (ItemData) -> Unit
) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val name: TextView = view.findViewById(R.id.itemName)
        val eventDate: TextView = view.findViewById(R.id.itemeventDate)
        val status: TextView = view.findViewById(R.id.itemStatus)
        val type: TextView = view.findViewById(R.id.itemType)
        val createDate: TextView = view.findViewById(R.id.itemCreateDate) // 새로 추가
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.name.text = item.ownerName

        holder.eventDate.text = Formatter.isoToDisplayNotTime(item.eventDate)

        // 상태 변환
        holder.status.text = when (item.status) {
            "COMPLETE" -> "처리완료"
            "PENDING" -> "진행중"
            else -> item.status
        }

        // 타입 변환
        holder.type.text = when (item.type) {
            "LOST" -> "분실"
            "FOUND" -> "습득"
            else -> item.type
        }
        holder.createDate.text = Formatter.isoToDisplayNotTime(item.createDate)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }



    override fun getItemCount(): Int = items.size
}