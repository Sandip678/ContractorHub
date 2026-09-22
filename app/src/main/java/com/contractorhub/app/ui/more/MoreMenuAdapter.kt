package com.contractorhub.app.ui.more

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.databinding.ItemMoreMenuBinding

data class MoreMenuItem(val id: String, val label: String, val iconRes: Int)

class MoreMenuAdapter(
    private val items: List<MoreMenuItem>,
    private val onClick: (MoreMenuItem) -> Unit
) : RecyclerView.Adapter<MoreMenuAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMoreMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemMoreMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MoreMenuItem) {
            binding.textMoreLabel.text = item.label
            binding.imageMoreIcon.setImageResource(item.iconRes)
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
