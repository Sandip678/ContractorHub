package com.contractorhub.app.domain.calculator

import com.contractorhub.app.data.local.entity.AttendanceStatus
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import com.contractorhub.app.domain.model.LabourBalance

/**
 * PART 17 — "Attendance × Wage + OT" इथे प्रत्यक्ष calculate होतं.
 *
 * ⚠️ OT rate साठी roadmap मध्ये नेमका formula दिलेला नाही — इथे 8-तासांचा दिवस
 * गृहीत धरून hourly rate = dailyRate / 8 वापरलंय. प्रत्यक्ष business rule वेगळा
 * असेल (उदा. 1.5x OT rate) तर हे फंक्शन इथेच बदलायचं — बाकी कुठेही OT ची गणना
 * duplicate केलेली नाही.
 */
object WageCalculator {

    fun calculateBalance(
        dailyRate: Long,
        attendance: List<LabourAttendanceEntity>,
        transactions: List<LabourTransactionEntity>
    ): LabourBalance {
        val presentDays = attendance.count { it.status == AttendanceStatus.PRESENT.name }
        val halfDays = attendance.count { it.status == AttendanceStatus.HALF_DAY.name }
        val otHours = attendance.sumOf { it.otHours }

        val otHourlyRate = if (dailyRate > 0) dailyRate / 8.0 else 0.0
        val totalEarned = (presentDays * dailyRate) +
            (halfDays * dailyRate / 2) +
            (otHours * otHourlyRate).toLong()

        val paid = transactions.sumOf { it.amount }
        val advanceTotal = transactions
            .filter { it.type == "ADVANCE" }
            .sumOf { it.amount }
        val angavarTotal = transactions
            .filter { it.type == "ANGAVAR" }
            .sumOf { it.amount }

        return LabourBalance(
            totalEarned = totalEarned,
            paid = paid,
            advanceTotal = advanceTotal,
            angavarTotal = angavarTotal,
            presentDays = presentDays,
            halfDays = halfDays,
            otHours = otHours
        )
    }
}
