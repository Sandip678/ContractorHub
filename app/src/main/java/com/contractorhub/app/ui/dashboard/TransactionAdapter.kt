package com.contractorhub.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.databinding.ItemTransactionRowBinding
import com.contractorhub.app.domain.model.TransactionItem
import com.contractorhub.app.domain.model.TransactionType
import com.contractorhub.app.utils.CurrencyFormatter

class TransactionAdapter(
    private var items: List<TransactionItem> = emptyList()
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    fun submitList(newItems: List<TransactionItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemTransactionRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionItem) {
            binding.textTransactionTitle.text = item.title
            binding.textTransactionSubtitle.text = item.subtitle

            val isIncome = item.type == TransactionType.INCOME
            val sign = if (isIncome) "+" else "-"
            binding.textTransactionAmount.text = "$sign${CurrencyFormatter.format(item.amount)}"

            val color = if (isIncome) R.color.chub_success else R.color.chub_error
            binding.textTransactionAmount.setTextColor(
                binding.root.context.getColor(color)
            )
            binding.imageTransactionIcon.setImageResource(
                if (isIncome) R.drawable.ic_trending_up else R.drawable.ic_trending_down
            )
        }
    }
}
