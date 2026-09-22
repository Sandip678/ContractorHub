package com.contractorhub.app.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.databinding.ItemClientRowBinding

class ClientListAdapter(
    private var items: List<ClientEntity> = emptyList(),
    private val onClick: (ClientEntity) -> Unit
) : RecyclerView.Adapter<ClientListAdapter.ViewHolder>() {

    fun submitList(newItems: List<ClientEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClientRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemClientRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClientEntity) {
            binding.textRowClientName.text = item.name
            binding.textRowClientMobile.text = item.mobile ?: "—"
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
