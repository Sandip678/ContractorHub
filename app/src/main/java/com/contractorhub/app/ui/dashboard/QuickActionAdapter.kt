package com.contractorhub.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.databinding.ItemQuickActionBinding

data class QuickAction(
    val id: String,
    val label: String,
    val iconRes: Int
)

class QuickActionAdapter(
    private val items: List<QuickAction>,
    private val onClick: (QuickAction) -> Unit
) : RecyclerView.Adapter<QuickActionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuickActionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemQuickActionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(action: QuickAction) {
            binding.textActionLabel.text = action.label
            binding.imageActionIcon.setImageResource(action.iconRes)
            binding.root.setOnClickListener { onClick(action) }
        }
    }
}
