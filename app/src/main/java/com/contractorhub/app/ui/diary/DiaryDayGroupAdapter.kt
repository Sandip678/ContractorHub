package com.contractorhub.app.ui.diary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.DiaryEntryType
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import com.contractorhub.app.databinding.ItemDiaryDayGroupBinding
import com.contractorhub.app.databinding.ItemDiaryEntryRowBinding
import com.contractorhub.app.domain.model.DiaryDayGroup
import com.contractorhub.app.ui.expenses.ExpenseCategoryLabels
import com.contractorhub.app.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class DiaryDayGroupAdapter(
    private var groups: List<DiaryDayGroup> = emptyList(),
    private val onDeleteNote: (DiaryTransactionEntity) -> Unit
) : RecyclerView.Adapter<DiaryDayGroupAdapter.ViewHolder>() {

    private val dayFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    fun submitList(newGroups: List<DiaryDayGroup>) {
        groups = newGroups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryDayGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    inner class ViewHolder(private val binding: ItemDiaryDayGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: DiaryDayGroup) {
            val context = binding.root.context
            binding.textDiaryDate.text = dayFormat.format(group.dayStartMillis)
            binding.textDiaryIncome.text = CurrencyFormatter.format(group.income)
            binding.textDiaryExpense.text = CurrencyFormatter.format(group.expense)
            binding.textDiaryNet.text = CurrencyFormatter.format(group.net)

            binding.layoutDiaryEntries.removeAllViews()
            group.entries
                .sortedByDescending { it.date }
                .forEach { entry ->
                    val rowBinding = ItemDiaryEntryRowBinding.inflate(
                        LayoutInflater.from(context), binding.layoutDiaryEntries, false
                    )
                    bindEntry(rowBinding, entry, context)
                    binding.layoutDiaryEntries.addView(rowBinding.root)
                }
        }

        private fun bindEntry(
            rowBinding: ItemDiaryEntryRowBinding,
            entry: DiaryTransactionEntity,
            context: android.content.Context
        ) {
            when (entry.type) {
                DiaryEntryType.INCOME.name -> {
                    rowBinding.imageEntryIcon.setImageResource(R.drawable.ic_trending_up)
                    rowBinding.textEntryLabel.text = entry.note?.takeIf { it.isNotBlank() }
                        ?: context.getString(R.string.transactions_menu_payments)
                    rowBinding.textEntryAmount.text = "+${CurrencyFormatter.format(entry.amount)}"
                    rowBinding.textEntryAmount.setTextColor(context.getColor(R.color.chub_success))
                }
                DiaryEntryType.EXPENSE.name -> {
                    rowBinding.imageEntryIcon.setImageResource(R.drawable.ic_trending_down)
                    val categoryLabel = entry.category?.let { ExpenseCategoryLabels.label(context, it) }
                    rowBinding.textEntryLabel.text = categoryLabel ?: context.getString(R.string.expense_screen_title)
                    rowBinding.textEntryAmount.text = "-${CurrencyFormatter.format(entry.amount)}"
                    rowBinding.textEntryAmount.setTextColor(context.getColor(R.color.chub_error))
                }
                else -> {
                    rowBinding.imageEntryIcon.setImageResource(R.drawable.ic_note)
                    rowBinding.textEntryLabel.text = entry.note ?: ""
                    rowBinding.textEntryAmount.text = ""
                    rowBinding.imageEntryDelete.isVisible = true
                    rowBinding.imageEntryDelete.setOnClickListener { onDeleteNote(entry) }
                }
            }
        }
    }
}
