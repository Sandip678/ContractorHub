package com.contractorhub.app.ui.contacts

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.ContactEntity
import com.contractorhub.app.databinding.ItemContactRowBinding

class ContactListAdapter(
    private var items: List<ContactEntity> = emptyList(),
    private val onClick: (ContactEntity) -> Unit
) : RecyclerView.Adapter<ContactListAdapter.ViewHolder>() {

    fun submitList(newItems: List<ContactEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemContactRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContactEntity) {
            val context = binding.root.context
            binding.textContactName.text = item.name
            binding.imageContactFavorite.isVisible = item.favorite

            val categoryLabel = ContactCategoryLabels.label(context, item.category)
            binding.textContactCategory.text = if (item.mobile.isNullOrBlank()) {
                categoryLabel
            } else {
                "$categoryLabel • ${item.mobile}"
            }

            binding.root.setOnClickListener { onClick(item) }

            // PART 53 — direct ACTION_DIAL वापरतो, CALL_PHONE permission टाळण्यासाठी.
            binding.imageActionCall.setOnClickListener {
                val mobile = item.mobile
                if (mobile.isNullOrBlank()) {
                    Toast.makeText(context, R.string.contact_no_mobile_toast, Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$mobile"))
                    context.startActivity(intent)
                }
            }

            binding.imageActionWhatsapp.setOnClickListener {
                val mobile = item.mobile
                if (mobile.isNullOrBlank()) {
                    Toast.makeText(context, R.string.contact_no_mobile_toast, Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$mobile"))
                    context.startActivity(intent)
                }
            }
        }
    }
}
