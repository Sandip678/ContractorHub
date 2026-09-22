package com.contractorhub.app.ui.labour

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.AttendanceStatus
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import com.contractorhub.app.databinding.ItemAttendanceRowBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceAdapter(
    private var items: List<LabourAttendanceEntity> = emptyList(),
    private val onDelete: (LabourAttendanceEntity) -> Unit
) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    fun submitList(newItems: List<LabourAttendanceEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemAttendanceRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LabourAttendanceEntity) {
            val context = binding.root.context
            binding.textAttendanceDate.text = dateFormat.format(item.date)

            val statusLabel = when (item.status) {
                AttendanceStatus.PRESENT.name -> context.getString(R.string.attendance_status_present)
                AttendanceStatus.HALF_DAY.name -> context.getString(R.string.attendance_status_half_day)
                AttendanceStatus.ABSENT.name -> context.getString(R.string.attendance_status_absent)
                AttendanceStatus.LEAVE.name -> context.getString(R.string.attendance_status_leave)
                else -> item.status
            }
            binding.textAttendanceStatus.text = if (item.otHours > 0) {
                "$statusLabel • ${item.otHours} OT hrs"
            } else {
                statusLabel
            }

            binding.imageDeleteAttendance.setOnClickListener { onDelete(item) }
        }
    }
}
