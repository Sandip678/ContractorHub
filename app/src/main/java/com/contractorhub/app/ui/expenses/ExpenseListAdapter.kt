package com.contractorhub.app.ui.expenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.data.local.entity.ExpenseEntity
import com.contractorhub.app.databinding.ItemExpenseRowBinding
import com.contractorhub.app.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseListAdapter(
    private var items: List<ExpenseEntity> = emptyList(),
    private val siteNameOf: (String?) -> String?,
    private val onClick: (ExpenseEntity) -> Unit
) : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun submitList(newItems: List<ExpenseEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemExpenseRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExpenseEntity) {
            val context = binding.root.context
            binding.textRowExpenseCategory.text = ExpenseCategoryLabels.label(context, item.category)

            val siteName = siteNameOf(item.siteId)
            val sitePart = siteName?.let { "$it • " } ?: ""
            binding.textRowExpenseMeta.text = "$sitePart${dateFormat.format(item.date)}"

            binding.textRowExpenseAmount.text = "-${CurrencyFormatter.format(item.amount)}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
