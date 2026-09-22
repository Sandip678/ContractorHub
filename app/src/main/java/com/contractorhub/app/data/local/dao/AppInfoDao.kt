package com.contractorhub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.contractorhub.app.data.local.entity.AppInfoEntity

@Dao
interface AppInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appInfo: AppInfoEntity)

    @Query("SELECT * FROM app_info WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): AppInfoEntity?
}
