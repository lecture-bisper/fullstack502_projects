import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.ui.ItemDetailActivity

class ItemAdapter(
    private val onDetailClick: (ItemDto) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val items = mutableListOf<ItemDto>()

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemIdx: TextView = view.findViewById(R.id.item_idx)
        val itemName: TextView = view.findViewById(R.id.item_name)
        val itemCode: TextView = view.findViewById(R.id.item_code)
        val itemManufacturer: TextView = view.findViewById(R.id.item_manufacturer)
        val itemCategory: TextView = view.findViewById(R.id.item_categoryId)
        val itemPrice: TextView = view.findViewById(R.id.item_price)
        val detailBtn: LinearLayout = view.findViewById(R.id.detail_btn)  // 상세보기 버튼
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemIdx.text = "${position + 1}"
        holder.itemName.text = item.name
        holder.itemCode.text = item.code
        holder.itemManufacturer.text = item.manufacturer
        holder.itemCategory.text = when (item.categoryId) {
            1L -> "사무용품"
            2L -> "전자기기"
            3L -> "가구/사무환경"
            4L -> "소모품"
            5L -> "안전/보안"
            6L -> "커피/간식/편의용품"
            7L -> "기타"
            else -> "알수없음"
        }
        holder.itemPrice.text = "${item.price}원"

        // 상세보기 버튼 클릭
        holder.detailBtn.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ItemDetailActivity::class.java)
            intent.putExtra("itemCode", item.code) // item.code 값 확인
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newItems: List<ItemDto>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}


