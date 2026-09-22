package com.contractorhub.app

import android.app.Application

/**
 * ContractorHub Application class.
 *
 * Phase 0-1: फक्त placeholder. पुढच्या phases मध्ये इथे:
 *  - AppDatabase चा singleton instance init होईल
 *  - Notification channels create होतील (PART 57)
 *  - App-wide preferences load होतील
 */
class ContractorHubApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // TODO(Phase 5+): AppDatabase.getInstance(this)
        // TODO(Phase 21): create notification channels
    }
}
