package bitc.full502.app_bq.utill

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bitc.full502.app_bq.data.model.StockLogDto
import bitc.full502.app_bq.databinding.MyStockoutListRowBinding
import bitc.full502.app_bq.ui.ItemDetailActivity
import kotlin.jvm.java

class MyStockOutAdapter(
    private var items: List<StockLogDto>
) : RecyclerView.Adapter<MyStockOutAdapter.MyStockOutViewHolder>() {

    fun updateList(newItems: List<StockLogDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class MyStockOutViewHolder(private val binding: MyStockoutListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StockLogDto, index: Int) {
            val onlyDate = item.logDate?.substringBefore("T") ?: ""

            binding.myStockIdx.text = (index + 1).toString()
            binding.myStockWarehouse.text = item.warehouseKrName ?: item.warehouseName
            binding.itemName.text = item.itemName
            binding.itemCode.text = item.itemCode
            binding.itemManufacturer.text = item.itemManufacturer
            binding.itemCategoryId.text = item.categoryKrName ?: item.categoryName
            binding.myStockQuantity.text = "${item.quantity}개"
            binding.myStockOutDate.text = onlyDate

            // 클릭 시 상세 페이지 이동
                binding.stockOutBtn.setOnClickListener {
                val context = binding.stockOutBtn.context
                val intent = Intent(context, ItemDetailActivity::class.java)
                intent.putExtra("itemCode", item.itemCode)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStockOutViewHolder {
        val binding = MyStockoutListRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyStockOutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyStockOutViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}
