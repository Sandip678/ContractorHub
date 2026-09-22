package com.contractorhub.app.utils

/**
 * PART 22/54 — Bill Scanner: OCR ने text काढल्यावर त्यातून उपयोगी माहिती
 * (मुख्यतः total amount) अंदाजे शोधायचा प्रयत्न.
 *
 * ⚠️ हे केवळ heuristic आहे — "Total", "Grand Total", "Amount" यासारख्या शब्दांजवळ
 * किंवा ₹/Rs चिन्हांजवळ असलेली सगळ्यात मोठी संख्या total समजून घेतो. हे चुकीचं
 * असू शकतं, म्हणूनच PART 54 प्रमाणे **OCR वर पूर्ण विश्वास ठेवायचा नाही — user
 * confirmation mandatory**. हा फक्त फॉर्म आधीच अंशतः भरून वेळ वाचवण्यासाठी आहे.
 */
object BillOcrParser {

    private val amountRegex = Regex("""(?:₹|Rs\.?|INR)?\s*([0-9]{1,3}(?:,[0-9]{2,3})*(?:\.[0-9]{1,2})?)""")

    fun guessTotalAmount(rawText: String): Long? {
        val candidates = amountRegex.findAll(rawText)
            .mapNotNull { match ->
                match.groupValues[1].replace(",", "").toDoubleOrNull()
            }
            .filter { it >= 10.0 } // खूप छोट्या संख्या (qty, page no.) टाळायला
            .toList()

        return candidates.maxOrNull()?.toLong()
    }
}
