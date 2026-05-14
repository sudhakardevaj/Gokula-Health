package com.mindmatrix.gokulahealth.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CattleViewModel @Inject constructor(
    private val repository: CattleRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ✅ PRESERVED - was working correctly
    val allCattle: StateFlow<List<Cattle>> = repository.getAllCattle()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val selectedCattleId = savedStateHandle.getStateFlow("cattleId", -1)

    // ✅ FIXED: Added flowOf(null) guard when id = -1 - was CRASHING before!
    val selectedCattle: StateFlow<Cattle?> = selectedCattleId.flatMapLatest { id ->
        if (id == -1) flowOf(null)
        else repository.getCattleById(id)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    // ✅ NEW: Today's Milk Logs Map
    val todayLogsMap: StateFlow<Map<Int, MilkLog>> = repository
        .getAllLogsForDate(LocalDate.now().toString())
        .map { logs -> logs.associateBy { it.cattleId } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap()
        )

    // ✅ NEW - Health Notes StateFlow
    private val _healthNotes = MutableStateFlow<List<HealthNote>>(emptyList())
    val healthNotes: StateFlow<List<HealthNote>> = _healthNotes.asStateFlow()

    // ✅ PRESERVED - was working correctly
    fun selectCattle(id: Int) {
        savedStateHandle["cattleId"] = id
    }

    // ✅ FIXED - Now saves gender field too!
    fun addCattle(cattle: Cattle) {
        viewModelScope.launch {
            repository.insertCattle(cattle)
        }
    }

    // ✅ PRESERVED - was working correctly
    fun updateCattle(cattle: Cattle) {
        viewModelScope.launch {
            repository.updateCattle(cattle)
        }
    }

    // ✅ PRESERVED - was working correctly
    fun deleteCattle(cattle: Cattle) {
        viewModelScope.launch {
            repository.deleteCattle(cattle)
        }
    }

    // ✅ NEW - Fetch health notes for a cattle
    fun fetchHealthNotes(cattleId: Int) {
        viewModelScope.launch {
            repository.getNotesByCattle(cattleId).collect { notes ->
                _healthNotes.value = notes
            }
        }
    }

    // ✅ NEW - Add a health note
    fun addHealthNote(note: HealthNote) {
        viewModelScope.launch {
            repository.insertHealthNote(note)
        }
    }

    // ✅ NEW - Delete a health note
    fun deleteHealthNote(note: HealthNote) {
        viewModelScope.launch {
            repository.deleteHealthNote(note)
        }
    }
}
