package com.contractorhub.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.contractorhub.app.data.local.dao.AppInfoDao
import com.contractorhub.app.data.local.dao.BillPaymentDao
import com.contractorhub.app.data.local.dao.ClientPaymentDao
import com.contractorhub.app.data.local.dao.DiaryDao
import com.contractorhub.app.data.local.dao.ExpenseDao
import com.contractorhub.app.data.local.dao.BillDao
import com.contractorhub.app.data.local.dao.ClientDao
import com.contractorhub.app.data.local.dao.ContactDao
import com.contractorhub.app.data.local.dao.LabourAttendanceDao
import com.contractorhub.app.data.local.dao.LabourDao
import com.contractorhub.app.data.local.dao.LabourTransactionDao
import com.contractorhub.app.data.local.dao.MaterialDao
import com.contractorhub.app.data.local.dao.MaterialStockTransactionDao
import com.contractorhub.app.data.local.dao.SiteDao
import com.contractorhub.app.data.local.dao.SupplierDao
import com.contractorhub.app.data.local.entity.AppInfoEntity
import com.contractorhub.app.data.local.entity.BillPaymentEntity
import com.contractorhub.app.data.local.entity.ClientPaymentEntity
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import com.contractorhub.app.data.local.entity.ExpenseEntity
import com.contractorhub.app.data.local.entity.BillEntity
import com.contractorhub.app.data.local.entity.ClientEntity
import com.contractorhub.app.data.local.entity.ContactEntity
import com.contractorhub.app.data.local.entity.LabourAttendanceEntity
import com.contractorhub.app.data.local.entity.LabourEntity
import com.contractorhub.app.data.local.entity.LabourTransactionEntity
import com.contractorhub.app.data.local.entity.MaterialEntity
import com.contractorhub.app.data.local.entity.MaterialStockTransactionEntity
import com.contractorhub.app.data.local.entity.SiteEntity
import com.contractorhub.app.data.local.entity.SupplierEntity

/**
 * ContractorHub चा central Room database.
 *
 * PART 58 — Database Relationship Principle नुसार:
 * Client → Site → Estimate → Department → Material/Labour → Bill/Expense →
 * Payment → Diary → Profit/Loss
 *
 * ही सगळी tables पुढच्या phases मध्ये (PART 6.2 entity list) इथेच जोडली जातील.
 *
 * Phase 3: Client + Site tables जोडले (version 1 → 2).
 * Phase 4: Contact table जोडला (version 2 → 3).
 * Phase 5-6: Labour + LabourAttendance + LabourTransaction tables जोडले (version 3 → 4).
 * Phase 7-8: Material + MaterialStockTransaction + Supplier tables जोडले (version 4 → 5).
 * Phase 9: Bill table जोडला (version 5 → 6).
 * Phase 10-11: Expense + ClientPayment + BillPayment tables जोडले (version 6 → 7).
 * Phase 12: DiaryTransaction table जोडला (version 7 → 8).
 *
 * ⚠️ सध्या fallbackToDestructiveMigration() वापरलंय — app अजून release झालेला
 * नाही, त्यामुळे local dev data गमावणं ठीक आहे. **Release आधी (Phase 25) हे
 * काढून प्रत्येक version bump साठी खरी Migration() लिहिणं बंधनकारक** — नाहीतर
 * users चा साठवलेला financial data update नंतर उडेल (PART 68 विरुद्ध जाईल).
 */
@Database(
    entities = [
        AppInfoEntity::class,
        ClientEntity::class,
        SiteEntity::class,
        ContactEntity::class,
        LabourEntity::class,
        LabourAttendanceEntity::class,
        LabourTransactionEntity::class,
        MaterialEntity::class,
        MaterialStockTransactionEntity::class,
        SupplierEntity::class,
        BillEntity::class,
        ExpenseEntity::class,
        ClientPaymentEntity::class,
        BillPaymentEntity::class,
        DiaryTransactionEntity::class
        // TODO(Phase 14+): EstimateEntity, EstimateItemEntity  (PART 6.2)
    ],
    version = 8,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao
    abstract fun clientDao(): ClientDao
    abstract fun siteDao(): SiteDao
    abstract fun contactDao(): ContactDao
    abstract fun labourDao(): LabourDao
    abstract fun labourAttendanceDao(): LabourAttendanceDao
    abstract fun labourTransactionDao(): LabourTransactionDao
    abstract fun materialDao(): MaterialDao
    abstract fun materialStockTransactionDao(): MaterialStockTransactionDao
    abstract fun supplierDao(): SupplierDao
    abstract fun billDao(): BillDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun clientPaymentDao(): ClientPaymentDao
    abstract fun billPaymentDao(): BillPaymentDao
    abstract fun diaryDao(): DiaryDao

    companion object {
        private const val DATABASE_NAME = "contractorhub.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    // TODO(before Phase 25 / release): replace with real Migration(1,2)...(4,5) { ... }
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
