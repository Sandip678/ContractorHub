package com.contractorhub.app.ui.bills

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.BillEntity
import com.contractorhub.app.data.local.entity.BillStatus
import com.contractorhub.app.data.local.entity.status
import com.contractorhub.app.databinding.ItemBillRowBinding
import com.contractorhub.app.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class BillListAdapter(
    private var items: List<BillEntity> = emptyList(),
    private val supplierNameOf: (String?) -> String,
    private val onClick: (BillEntity) -> Unit
) : RecyclerView.Adapter<BillListAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun submitList(newItems: List<BillEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBillRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemBillRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BillEntity) {
            val context = binding.root.context
            binding.textRowBillSupplier.text = supplierNameOf(item.supplierId)

            val billNumberPart = item.billNumber?.takeIf { it.isNotBlank() }?.let { "#$it • " } ?: ""
            binding.textRowBillMeta.text = "$billNumberPart${dateFormat.format(item.billDate)}"

            binding.textRowBillTotal.text = CurrencyFormatter.format(item.total)

            val (label, colorRes) = when (item.status()) {
                BillStatus.PAID -> context.getString(R.string.bill_status_paid) to R.color.chub_success
                BillStatus.UNPAID -> context.getString(R.string.bill_status_unpaid) to R.color.chub_error
                BillStatus.PARTIAL -> context.getString(R.string.bill_status_partial) to R.color.chub_warning
            }
            binding.textRowBillStatus.text = label
            binding.textRowBillStatus.setTextColor(context.getColor(colorRes))

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
