package com.contractorhub.app.ui.diary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.contractorhub.app.data.local.database.AppDatabase
import com.contractorhub.app.data.local.entity.DiaryTransactionEntity
import com.contractorhub.app.data.repository.DiaryRepository
import com.contractorhub.app.domain.model.DiaryDayGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DiaryRepository(AppDatabase.getInstance(application).diaryDao())

    val dayGroups: StateFlow<List<DiaryDayGroup>>

    init {
        dayGroups = repository.getEntries()
            .map { entries -> groupByDay(entries) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    private fun groupByDay(entries: List<DiaryTransactionEntity>): List<DiaryDayGroup> {
        return entries
            .groupBy { startOfDay(it.date) }
            .toSortedMap(compareByDescending { it })
            .map { (dayStart, dayEntries) -> DiaryDayGroup(dayStart, dayEntries) }
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun addManualNote(date: Long, note: String) {
        viewModelScope.launch { repository.addManualNote(date, note) }
    }

    fun deleteEntry(entry: DiaryTransactionEntity) {
        viewModelScope.launch { repository.deleteEntry(entry) }
    }
}
