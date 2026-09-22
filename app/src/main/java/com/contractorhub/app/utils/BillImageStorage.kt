package com.contractorhub.app.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PART 23 — Bill Image Storage: original bill image कधीही delete होऊ नये,
 * app-private storage मध्ये `Bills/YYYY/MM/SiteName/` अशा structure ने ठेवायचा.
 * Database मध्ये फक्त हा path (BillEntity.imagePath) साठवला जातो.
 */
object BillImageStorage {

    fun createImageFile(context: Context, siteName: String?): File {
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MM", Locale.getDefault())
        val now = Date()

        val safeSiteFolder = (siteName?.takeIf { it.isNotBlank() } ?: "Unassigned")
            .replace(Regex("[^A-Za-z0-9 _-]"), "_")

        val dir = File(
            context.filesDir,
            "Bills/${yearFormat.format(now)}/${monthFormat.format(now)}/$safeSiteFolder"
        )
        if (!dir.exists()) dir.mkdirs()

        val fileName = "bill_${System.currentTimeMillis()}.jpg"
        return File(dir, fileName)
    }
}
