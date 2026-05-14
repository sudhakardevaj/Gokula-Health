package com.mindmatrix.gokulahealth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gokulahealth.data.local.entity.BreedingRecord
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import com.mindmatrix.gokulahealth.util.BreedingCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BreedingViewModel @Inject constructor(
    private val repository: CattleRepository
) : ViewModel() {

    private val _breedingHistory = MutableStateFlow<List<BreedingRecord>>(emptyList())
    val breedingHistory: StateFlow<List<BreedingRecord>> = _breedingHistory.asStateFlow()

    private val _currentPregnancy = MutableStateFlow<BreedingRecord?>(null)
    val currentPregnancy: StateFlow<BreedingRecord?> = _currentPregnancy.asStateFlow()

    private val _upcomingCalvings = MutableStateFlow<List<BreedingRecord>>(emptyList())
    val upcomingCalvings: StateFlow<List<BreedingRecord>> = _upcomingCalvings.asStateFlow()

    // ── Fetch history for one cattle ─────────────────────────
    fun fetchBreedingHistory(cattleId: Int) {
        viewModelScope.launch {
            repository.getBreedingHistory(cattleId).collectLatest {
                _breedingHistory.value = it
            }
        }
    }

    // ── Fetch current active pregnancy ────────────────────────
    fun fetchCurrentPregnancy(cattleId: Int) {
        viewModelScope.launch {
            repository.getCurrentPregnancy(cattleId).collectLatest {
                _currentPregnancy.value = it
            }
        }
    }

    // ── Fetch calvings due within 30 days (for home screen) ───
    fun fetchUpcomingCalvings() {
        viewModelScope.launch {
            repository.getAllPregnantCattle().collectLatest { allPregnant ->
                val filtered = allPregnant.filter { record ->
                    val daysLeft = BreedingCalculator.daysUntilCalving(record.expectedCalvingDate)
                    daysLeft in 0..30
                }
                _upcomingCalvings.value = filtered
            }
        }
    }

    // ── Log a heat event ──────────────────────────────────────
    fun logHeatEvent(cattleId: Int, heatDate: String) {
        viewModelScope.launch {
            val record = BreedingRecord(
                cattleId = cattleId,
                heatObservedDate = heatDate,
                nextExpectedHeatDate = BreedingCalculator
                    .calculateNextHeatDate(heatDate)
            )
            repository.insertBreedingRecord(record)
        }
    }

    // ── Record insemination on existing heat record ───────────
    fun recordInsemination(
        existingRecord: BreedingRecord,
        inseminationDate: String,
        inseminationType: String,
        details: String
    ) {
        viewModelScope.launch {
            val updated = existingRecord.copy(
                inseminationDate = inseminationDate,
                inseminationType = inseminationType,
                bullOrSemenDetails = details,
                expectedCalvingDate = BreedingCalculator
                    .calculateExpectedCalvingDate(inseminationDate)
            )
            repository.updateBreedingRecord(updated)
        }
    }

    // ── Confirm pregnancy ─────────────────────────────────────
    fun confirmPregnancy(
        existingRecord: BreedingRecord,
        confirmationDate: String
    ) {
        viewModelScope.launch {
            val updated = existingRecord.copy(
                isPregnant = true,
                pregnancyConfirmedDate = confirmationDate,
                dryOffDate = BreedingCalculator
                    .calculateDryOffDate(existingRecord.expectedCalvingDate)
            )
            repository.updateBreedingRecord(updated)
        }
    }

    // ── Record calving ────────────────────────────────────────
    fun recordCalving(
        existingRecord: BreedingRecord,
        actualCalvingDate: String,
        calfCount: Int,
        calfGender: String,
        lactationNumber: Int
    ) {
        viewModelScope.launch {
            val updated = existingRecord.copy(
                isPregnant = false,
                actualCalvingDate = actualCalvingDate,
                calfCount = calfCount,
                calfGender = calfGender,
                lactationStartDate = actualCalvingDate,
                lactationNumber = lactationNumber
            )
            repository.updateBreedingRecord(updated)
        }
    }

    // ── Delete a record ───────────────────────────────────────
    fun deleteBreedingRecord(record: BreedingRecord) {
        viewModelScope.launch {
            repository.deleteBreedingRecord(record)
        }
    }
}
