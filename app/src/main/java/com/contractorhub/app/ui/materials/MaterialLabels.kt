package com.contractorhub.app.ui.materials

import android.content.Context
import com.contractorhub.app.R
import com.contractorhub.app.data.local.entity.MaterialCategory
import com.contractorhub.app.data.local.entity.MaterialUnit
import com.contractorhub.app.data.local.entity.StockTransactionType

object MaterialCategoryLabels {
    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.category_cement) to MaterialCategory.CEMENT.name,
        context.getString(R.string.category_steel) to MaterialCategory.STEEL.name,
        context.getString(R.string.category_sand) to MaterialCategory.SAND.name,
        context.getString(R.string.category_aggregate) to MaterialCategory.AGGREGATE.name,
        context.getString(R.string.category_bricks) to MaterialCategory.BRICKS.name,
        context.getString(R.string.category_tiles) to MaterialCategory.TILES.name,
        context.getString(R.string.category_pipes) to MaterialCategory.PIPES.name,
        context.getString(R.string.category_wire) to MaterialCategory.WIRE.name,
        context.getString(R.string.category_paint) to MaterialCategory.PAINT.name,
        context.getString(R.string.category_hardware) to MaterialCategory.HARDWARE.name,
        context.getString(R.string.category_custom) to MaterialCategory.CUSTOM.name
    )

    fun label(context: Context, value: String): String =
        options(context).firstOrNull { it.second == value }?.first ?: value
}

object MaterialUnitLabels {
    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.unit_bag) to MaterialUnit.BAG.name,
        context.getString(R.string.unit_kg) to MaterialUnit.KG.name,
        context.getString(R.string.unit_ton) to MaterialUnit.TON.name,
        context.getString(R.string.unit_piece) to MaterialUnit.PIECE.name,
        context.getString(R.string.unit_litre) to MaterialUnit.LITRE.name,
        context.getString(R.string.unit_sqft) to MaterialUnit.SQFT.name,
        context.getString(R.string.unit_meter) to MaterialUnit.METER.name,
        context.getString(R.string.unit_brass) to MaterialUnit.BRASS.name,
        context.getString(R.string.unit_custom) to MaterialUnit.CUSTOM.name
    )

    fun label(context: Context, value: String): String =
        options(context).firstOrNull { it.second == value }?.first ?: value
}

object StockTransactionTypeLabels {
    fun options(context: Context): List<Pair<String, String>> = listOf(
        context.getString(R.string.stock_type_purchase) to StockTransactionType.PURCHASE.name,
        context.getString(R.string.stock_type_issue) to StockTransactionType.ISSUE.name,
        context.getString(R.string.stock_type_return) to StockTransactionType.RETURN.name,
        context.getString(R.string.stock_type_adjustment) to StockTransactionType.ADJUSTMENT.name,
        context.getString(R.string.stock_type_waste) to StockTransactionType.WASTE.name
    )

    fun label(context: Context, value: String): String =
        options(context).firstOrNull { it.second == value }?.first ?: value
}
