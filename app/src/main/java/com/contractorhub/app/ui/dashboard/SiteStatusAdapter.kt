package com.contractorhub.app.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.databinding.ItemSiteStatusBinding
import com.contractorhub.app.domain.model.SiteStatusItem
import com.contractorhub.app.utils.CurrencyFormatter

class SiteStatusAdapter(
    private var items: List<SiteStatusItem> = emptyList()
) : RecyclerView.Adapter<SiteStatusAdapter.ViewHolder>() {

    fun submitList(newItems: List<SiteStatusItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSiteStatusBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemSiteStatusBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SiteStatusItem) {
            binding.textSiteName.text = item.siteName
            binding.textSiteStatus.text = item.status
            binding.textSiteContract.text = "${CurrencyFormatter.format(item.contractAmount)} Contract"
            binding.textSiteReceived.text = "${CurrencyFormatter.format(item.receivedAmount)} Received"
            binding.textSitePending.text = "${CurrencyFormatter.format(item.pendingAmount)} Pending"
            binding.textSiteExpense.text = "Expense ${CurrencyFormatter.format(item.expense)}"
            binding.textSiteProfit.text = "Profit ${CurrencyFormatter.format(item.profit)}"
        }
    }
}
