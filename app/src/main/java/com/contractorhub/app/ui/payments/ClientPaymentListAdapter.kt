package com.contractorhub.app.ui.payments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import com.contractorhub.app.databinding.ItemClientPaymentRowBinding
import com.contractorhub.app.ui.expenses.PaymentMethodLabels
import com.contractorhub.app.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class ClientPaymentListAdapter(
    private var items: List<ClientPaymentEntity> = emptyList(),
    private val clientNameOf: (String?) -> String?,
    private val siteNameOf: (String?) -> String?,
    private val onClick: (ClientPaymentEntity) -> Unit
) : RecyclerView.Adapter<ClientPaymentListAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun submitList(newItems: List<ClientPaymentEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClientPaymentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemClientPaymentRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ClientPaymentEntity) {
            val context = binding.root.context
            binding.textRowPaymentClient.text = clientNameOf(item.clientId) ?: "—"

            val siteName = siteNameOf(item.siteId)
            val sitePart = siteName?.let { "$it • " } ?: ""
            val methodLabel = PaymentMethodLabels.label(context, item.paymentMethod)
            binding.textRowPaymentMeta.text = "$sitePart$methodLabel • ${dateFormat.format(item.date)}"

            binding.textRowPaymentAmount.text = "+${CurrencyFormatter.format(item.amount)}"
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
