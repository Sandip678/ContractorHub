package com.contractorhub.app.ui.contacts

import android.content.Context
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.ContactCategory

object ContactCategoryLabels {

    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.category_engineer) to ContactCategory.ENGINEER.name,
        context.getString(R.string.category_architect) to ContactCategory.ARCHITECT.name,
        context.getString(R.string.category_contractor) to ContactCategory.CONTRACTOR.name,
        context.getString(R.string.category_sub_contractor) to ContactCategory.SUB_CONTRACTOR.name,
        context.getString(R.string.category_labour_contractor) to ContactCategory.LABOUR_CONTRACTOR.name,
        context.getString(R.string.category_electrician) to ContactCategory.ELECTRICIAN.name,
        context.getString(R.string.category_plumber) to ContactCategory.PLUMBER.name,
        context.getString(R.string.category_supplier) to ContactCategory.SUPPLIER.name,
        context.getString(R.string.category_transporter) to ContactCategory.TRANSPORTER.name,
        context.getString(R.string.category_hardware_shop) to ContactCategory.HARDWARE_SHOP.name,
        context.getString(R.string.category_machinery) to ContactCategory.MACHINERY.name,
        context.getString(R.string.category_client) to ContactCategory.CLIENT.name,
        context.getString(R.string.category_emergency) to ContactCategory.EMERGENCY.name,
        context.getString(R.string.category_other) to ContactCategory.OTHER.name
    )

    fun label(context: Context, categoryValue: String): String =
        options(context).firstOrNull { it.second == categoryValue }?.first ?: categoryValue
}
