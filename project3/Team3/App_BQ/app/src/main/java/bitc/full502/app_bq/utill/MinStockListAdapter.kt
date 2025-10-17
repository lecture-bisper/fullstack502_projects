package bitc.full502.app_bq.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import bitc.full502.app_bq.R
import bitc.full502.app_bq.data.api.ApiService
import bitc.full502.app_bq.data.model.ItemDto
import bitc.full502.app_bq.data.model.MinStockDto
import bitc.full502.app_bq.databinding.MinStockListRowBinding
import kotlin.jvm.java

class MinStockAdapter(private val context: Context) :
    RecyclerView.Adapter<MinStockAdapter.MinStockViewHolder>() {
    private val minStockList = mutableListOf<MinStockDto>()

    inner class MinStockViewHolder(private val binding: MinStockListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(minStock: MinStockDto, position: Int) {
            binding.apply {
                itemIdx.text = (position + 1).toString()
                itemName.text = minStock.itemName
                itemCode.text = minStock.itemCode
                itemManufacturer.text = minStock.itemManufacturer
                itemCategoryId.text = minStock.categoryKrName
                savecount.text = "${minStock.safetyQty}/${minStock.stockQuantity}"
                minStockOutBtn.setOnClickListener {
                    val orderQty = (minStock.safetyQty - minStock.stockQuantity).coerceAtLeast(0)
                    val totalPrice = orderQty * minStock.itemPrice
                    val itemPrice = minStock.itemPrice

                    val intent = Intent(context, OrderingActivity::class.java).apply {
                        putExtra("itemName", minStock.itemName)
                        putExtra("itemCode", minStock.itemCode)
                        putExtra("itemManufacturer", minStock.itemManufacturer)
                        putExtra("categoryKrName", minStock.categoryKrName)
                        putExtra("orderQty", orderQty)
                        putExtra("totalPrice", totalPrice)
                        putExtra("itemPrice", itemPrice)
                        putExtra("itemId", minStock.itemId)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinStockViewHolder {
        val binding =
            MinStockListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MinStockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MinStockViewHolder, position: Int) {
        holder.bind(minStockList[position], position)
    }

    override fun getItemCount(): Int = minStockList.size

    fun updateList(newList: List<MinStockDto>) {
        minStockList.clear()
        minStockList.addAll(newList)
        notifyDataSetChanged()
    }
}


