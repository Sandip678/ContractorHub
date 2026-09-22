package com.contractorhub.app.ui.materials

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.MaterialStockTransactionEntity
import com.contractorhub.app.data.local.entity.StockTransactionType
import com.contractorhub.app.databinding.ItemStockTransactionRowBinding
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class StockTransactionAdapter(
    private var items: List<MaterialStockTransactionEntity> = emptyList(),
    private val unit: String,
    private val onDelete: (MaterialStockTransactionEntity) -> Unit
) : RecyclerView.Adapter<StockTransactionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val qtyFormat = DecimalFormat("#,##0.##")

    fun submitList(newItems: List<MaterialStockTransactionEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStockTransactionRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemStockTransactionRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MaterialStockTransactionEntity) {
            val context = binding.root.context
            binding.textStockType.text = StockTransactionTypeLabels.label(context, item.type)
            binding.textStockDate.text = dateFormat.format(item.date)

            val isPositive = when (item.type) {
                StockTransactionType.PURCHASE.name, StockTransactionType.RETURN.name -> true
                StockTransactionType.ISSUE.name, StockTransactionType.WASTE.name -> false
                StockTransactionType.ADJUSTMENT.name -> item.isIncrease
                else -> true
            }
            val sign = if (isPositive) "+" else "-"
            binding.textStockQuantity.text = "$sign${qtyFormat.format(item.quantity)} $unit"
            binding.textStockQuantity.setTextColor(
                context.getColor(if (isPositive) R.color.chub_success else R.color.chub_error)
            )

            binding.imageDeleteStock.setOnClickListener { onDelete(item) }
        }
    }
}
