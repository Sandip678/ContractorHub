package com.contractorhub.app.ui.labour

import android.content.Context
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.LabourTransactionType

object LabourTransactionTypeLabels {

    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.transaction_type_advance) to LabourTransactionType.ADVANCE.name,
        context.getString(R.string.transaction_type_angavar) to LabourTransactionType.ANGAVAR.name,
        context.getString(R.string.transaction_type_payment) to LabourTransactionType.PAYMENT.name,
        context.getString(R.string.transaction_type_adjustment) to LabourTransactionType.ADJUSTMENT.name,
        context.getString(R.string.transaction_type_settlement) to LabourTransactionType.SETTLEMENT.name
    )

    fun label(context: Context, typeValue: String): String =
        options(context).firstOrNull { it.second == typeValue }?.first ?: typeValue
}
