package com.contractorhub.app.ui.labour

import android.content.Context
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.AttendanceStatus

object AttendanceStatusLabels {

    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.attendance_status_present) to AttendanceStatus.PRESENT.name,
        context.getString(R.string.attendance_status_half_day) to AttendanceStatus.HALF_DAY.name,
        context.getString(R.string.attendance_status_absent) to AttendanceStatus.ABSENT.name,
        context.getString(R.string.attendance_status_leave) to AttendanceStatus.LEAVE.name
    )
}
