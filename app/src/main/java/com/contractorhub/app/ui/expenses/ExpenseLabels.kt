package com.contractorhub.app.ui.expenses

import android.content.Context
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.ExpenseCategory
import com.contractorhub.app.data.local.entity.PaymentMethod

object ExpenseCategoryLabels {
    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.expense_category_material) to ExpenseCategory.MATERIAL.name,
        context.getString(R.string.expense_category_labour) to ExpenseCategory.LABOUR.name,
        context.getString(R.string.expense_category_electrical) to ExpenseCategory.ELECTRICAL.name,
        context.getString(R.string.expense_category_plumbing) to ExpenseCategory.PLUMBING.name,
        context.getString(R.string.expense_category_transport) to ExpenseCategory.TRANSPORT.name,
        context.getString(R.string.expense_category_fuel) to ExpenseCategory.FUEL.name,
        context.getString(R.string.expense_category_machinery) to ExpenseCategory.MACHINERY.name,
        context.getString(R.string.expense_category_tools) to ExpenseCategory.TOOLS.name,
        context.getString(R.string.expense_category_food) to ExpenseCategory.FOOD.name,
        context.getString(R.string.expense_category_other) to ExpenseCategory.OTHER.name
    )

    fun label(context: Context, value: String): String =
        options(context).firstOrNull { it.second == value }?.first ?: value
}

object PaymentMethodLabels {
    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.payment_method_cash) to PaymentMethod.CASH.name,
        context.getString(R.string.payment_method_bank) to PaymentMethod.BANK.name,
        context.getString(R.string.payment_method_upi) to PaymentMethod.UPI.name
    )

    fun label(context: Context, value: String): String =
        options(context).firstOrNull { it.second == value }?.first ?: value
}
