package com.contractorhub.app.ui.labour

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.local.entity.LabourStatus
import com.contractorhub.app.databinding.ItemLabourRowBinding
import com.contractorhub.app.utils.CurrencyFormatter

class LabourListAdapter(
    private var items: List<LabourEntity> = emptyList(),
    private val onClick: (LabourEntity) -> Unit
) : RecyclerView.Adapter<LabourListAdapter.ViewHolder>() {

    fun submitList(newItems: List<LabourEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLabourRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemLabourRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LabourEntity) {
            val context = binding.root.context
            binding.textRowLabourName.text = item.name

            val workType = item.workType?.takeIf { it.isNotBlank() }
            binding.textRowLabourWorktype.text = if (workType != null) {
                "$workType • ${CurrencyFormatter.format(item.dailyRate)}/day"
            } else {
                "${CurrencyFormatter.format(item.dailyRate)}/day"
            }

            binding.textRowLabourStatus.text = if (item.status == LabourStatus.ACTIVE.name) {
                context.getString(R.string.labour_status_active)
            } else {
                context.getString(R.string.labour_status_inactive)
            }

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
