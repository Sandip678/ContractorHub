package com.contractorhub.app.ui.materials

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.databinding.ItemMaterialRowBinding
import com.contractorhub.app.utils.CurrencyFormatter
import java.text.DecimalFormat

class MaterialListAdapter(
    private var items: List<MaterialEntity> = emptyList(),
    private val onClick: (MaterialEntity) -> Unit
) : RecyclerView.Adapter<MaterialListAdapter.ViewHolder>() {

    private val qtyFormat = DecimalFormat("#,##0.##")

    fun submitList(newItems: List<MaterialEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMaterialRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemMaterialRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MaterialEntity) {
            val context = binding.root.context
            binding.textRowMaterialName.text = item.name

            val categoryLabel = MaterialCategoryLabels.label(context, item.category)
            val unitLabel = MaterialUnitLabels.label(context, item.unit)
            binding.textRowMaterialCategory.text =
                "$categoryLabel • ${CurrencyFormatter.format(item.rate)}/$unitLabel"

            binding.textRowMaterialStock.text = "${qtyFormat.format(item.currentStock)} $unitLabel"

            val isLowStock = item.currentStock < item.minimumStock
            binding.textRowLowStockBadge.isVisible = isLowStock

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
