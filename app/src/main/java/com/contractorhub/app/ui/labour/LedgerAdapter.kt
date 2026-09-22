package com.contractorhub.app.ui.labour

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import com.contractorhub.app.databinding.ItemLedgerRowBinding
import com.contractorhub.app.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class LedgerAdapter(
    private var items: List<LabourTransactionEntity> = emptyList(),
    private val onDelete: (LabourTransactionEntity) -> Unit
) : RecyclerView.Adapter<LedgerAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun submitList(newItems: List<LabourTransactionEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLedgerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemLedgerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LabourTransactionEntity) {
            val context = binding.root.context
            binding.textLedgerType.text = LabourTransactionTypeLabels.label(context, item.type)
            binding.textLedgerDate.text = dateFormat.format(item.date)
            binding.textLedgerAmount.text = "-${CurrencyFormatter.format(item.amount)}"
            binding.imageDeleteLedger.setOnClickListener { onDelete(item) }
        }
    }
}
