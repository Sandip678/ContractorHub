package com.contractorhub.app.data.local.relation

/**
 * Site list screen साठी — Site आणि त्याच्या Client चं नाव एका query मध्ये.
 * पूर्ण SiteEntity/ClientEntity object आणण्याची गरज नाही, फक्त list ला लागणारे columns.
 */
data class SiteListItem(
    val siteId: String,
    val siteName: String,
    val clientName: String?,
    val status: String,
    val contractAmount: Long
)
