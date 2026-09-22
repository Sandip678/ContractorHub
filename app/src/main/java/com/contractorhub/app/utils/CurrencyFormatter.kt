package com.contractorhub.app.utils

import java.text.NumberFormat
import java.util.Locale

/**
 * PART 48, Rule 6 — Money नेहमी ₹25,000 / ₹2,40,000 (Indian lakh/crore grouping)
 * अशा format मध्ये दाखवायची.
 *
 * PART 68 — Financial Data Safety: सध्या Long (पूर्ण रुपये) वापरतोय; Bills/Payments
 * phase मध्ये पैसे (paise) किंवा BigDecimal सारखी अचूक representation आणायची —
 * इथे टाकलेला हा util वापरणारे सगळे call-sites त्यामुळे सहज अपडेट करता येतील.
 */
object CurrencyFormatter {

    private val indianLocale = Locale("en", "IN")
    private val formatter = NumberFormat.getNumberInstance(indianLocale)

    fun format(amountInRupees: Long): String {
        return "₹${formatter.format(amountInRupees)}"
    }
}
