package com.contractorhub.app.ui.sites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.SiteStatus
import com.contractorhub.app.data.local.relation.SiteListItem
import com.contractorhub.app.databinding.ItemSiteRowBinding
import com.contractorhub.app.utils.CurrencyFormatter

class SiteListAdapter(
    private var items: List<SiteListItem> = emptyList(),
    private val onClick: (SiteListItem) -> Unit
) : RecyclerView.Adapter<SiteListAdapter.ViewHolder>() {

    fun submitList(newItems: List<SiteListItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSiteRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSiteRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SiteListItem) {
            binding.textRowSiteName.text = item.siteName
            binding.textRowClientName.text = if (item.clientName.isNullOrBlank()) {
                binding.root.context.getString(R.string.site_field_client) + ": —"
            } else {
                "${binding.root.context.getString(R.string.site_field_client)}: ${item.clientName}"
            }
            binding.textRowContractAmount.text =
                "${CurrencyFormatter.format(item.contractAmount)} Contract"

            val statusLabel = when (item.status) {
                SiteStatus.ACTIVE.name -> binding.root.context.getString(R.string.site_status_active)
                SiteStatus.ON_HOLD.name -> binding.root.context.getString(R.string.site_status_on_hold)
                SiteStatus.COMPLETED.name -> binding.root.context.getString(R.string.site_status_completed)
                else -> item.status
            }
            binding.textRowStatus.text = statusLabel

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
