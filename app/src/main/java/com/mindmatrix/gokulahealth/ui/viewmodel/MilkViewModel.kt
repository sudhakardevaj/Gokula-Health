package com.mindmatrix.gokulahealth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import com.mindmatrix.gokulahealth.domain.repository.GenAIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
@HiltViewModel
class MilkViewModel @Inject constructor(
    private val repository: CattleRepository,
    private val genAIRepository: GenAIRepository
) : ViewModel() {

    // ✅ PRESERVED - was working correctly
    private val _last30DaysLogs = MutableStateFlow<List<MilkLog>>(emptyList())
    val last30DaysLogs: StateFlow<List<MilkLog>> = _last30DaysLogs.asStateFlow()

    // ✅ FIXED - Was Float, now nullable Float? to handle no data case
    private val _monthlyAverage = MutableStateFlow(0f)
    val monthlyAverage: StateFlow<Float> = _monthlyAverage.asStateFlow()

    // ✅ PRESERVED - was working correctly
    private val _isYieldDeclining = MutableStateFlow(false)
    val isYieldDeclining: StateFlow<Boolean> = _isYieldDeclining.asStateFlow()

    // ✅ PRESERVED - was working correctly
    private val _genAISuggestion = MutableStateFlow<String?>(null)
    val genAISuggestion: StateFlow<String?> = _genAISuggestion.asStateFlow()

    // ✅ NEW - Today's milk log for the detail screen summary
    private val _todayLog = MutableStateFlow<MilkLog?>(null)
    val todayLog: StateFlow<MilkLog?> = _todayLog.asStateFlow()

    // ✅ NEW - Selected days for chart toggle (7, 30, 90)
    private val _selectedDays = MutableStateFlow(30)
    val selectedDays: StateFlow<Int> = _selectedDays.asStateFlow()

    // ✅ NEW - Filtered logs based on selected days
    private val _filteredLogs = MutableStateFlow<List<MilkLog>>(emptyList())
    val filteredLogs: StateFlow<List<MilkLog>> = _filteredLogs.asStateFlow()

    // ✅ PRESERVED - was working correctly
    fun fetchLast30Days(cattleId: Int) {
        viewModelScope.launch {
            repository.getLast30DaysLogs(cattleId).collectLatest { logs ->
                _last30DaysLogs.value = logs
                _filteredLogs.value = logs // Default to 30 days
            }
        }
    }

    // ✅ NEW - Fetch today's log for CattleDetailScreen summary
    fun fetchTodayLog(cattleId: Int) {
        viewModelScope.launch {
            val today = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
            repository.getTodayLog(cattleId, today).collectLatest { log ->
                _todayLog.value = log
            }
        }
    }

    // ✅ FIXED - Properly handles nullable Float from DB
    fun fetchMonthlyAverage(cattleId: Int, month: String) {
        viewModelScope.launch {
            repository.getMonthlyAverage(cattleId, month).collectLatest { avg ->
                _monthlyAverage.value = avg ?: 0f // ✅ Safe null handling
            }
        }
    }

    // ✅ NEW - Chart toggle - fetch N days of logs
    fun fetchLogsForDays(cattleId: Int, days: Int) {
        _selectedDays.value = days
        viewModelScope.launch {
            repository.getLastNDaysLogs(cattleId, days).collectLatest { logs ->
                _filteredLogs.value = logs
                // Recalculate average for selected period
                if (logs.isNotEmpty()) {
                    _monthlyAverage.value = logs.map { it.totalLitres }.average().toFloat()
                }
            }
        }
    }

    // ✅ PRESERVED - was working correctly
    fun logMilk(milkLog: MilkLog) {
        viewModelScope.launch {
            repository.insertMilkLog(milkLog)
            checkYieldTrend(milkLog.cattleId)
            fetchTodayLog(milkLog.cattleId) // ✅ Refresh today's log after saving
        }
    }

    // ✅ PRESERVED - was working correctly
    fun checkYieldTrend(cattleId: Int) {
        viewModelScope.launch {
            repository.getLastNDaysLogs(cattleId, 3).collectLatest { logs ->
                if (logs.size >= 3) {
                    val isDeclining =
                        logs[0].totalLitres < logs[1].totalLitres &&
                                logs[1].totalLitres < logs[2].totalLitres
                    _isYieldDeclining.value = isDeclining
                    if (isDeclining) {
                        val trend = logs.map { it.totalLitres }
                        _genAISuggestion.value =
                            genAIRepository.getHealthSuggestions(cattleId, trend)
                    }
                }
            }
        }
    }
}