package com.mindmatrix.gokulahealth.domain.repository

import com.mindmatrix.gokulahealth.data.local.entity.BreedingRecord
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import kotlinx.coroutines.flow.Flow

interface CattleRepository {

    // ── Cattle ──────────────────────────────────────────
    suspend fun insertCattle(cattle: Cattle)
    suspend fun updateCattle(cattle: Cattle)
    suspend fun deleteCattle(cattle: Cattle)
    fun getAllCattle(): Flow<List<Cattle>>
    fun getCattleById(id: Int): Flow<Cattle?> // ✅ FIXED: nullable - was crashing with id=-1

    // ── Milk Log ─────────────────────────────────────────
    suspend fun insertMilkLog(milkLog: MilkLog)
    suspend fun updateMilkLog(milkLog: MilkLog)
    suspend fun deleteMilkLog(milkLog: MilkLog)
    fun getLast30DaysLogs(cattleId: Int): Flow<List<MilkLog>>
    fun getTodayLog(cattleId: Int, date: String): Flow<MilkLog?>
    fun getAllLogsForDate(date: String): Flow<List<MilkLog>>
    fun getMonthlyAverage(cattleId: Int, month: String): Flow<Float?> // ✅ FIXED: nullable Float
    fun getLastNDaysLogs(cattleId: Int, limit: Int): Flow<List<MilkLog>>

    // ── Vaccination ──────────────────────────────────────
    suspend fun insertVaccination(vaccination: Vaccination)
    suspend fun updateVaccination(vaccination: Vaccination)
    suspend fun deleteVaccination(vaccination: Vaccination)
    fun getVaccinationsByCattle(cattleId: Int): Flow<List<Vaccination>>
    fun getUpcomingVaccinations(today: String): Flow<List<Vaccination>>
    fun getPastVaccinations(today: String): Flow<List<Vaccination>> // ✅ NEW: for History tab

    // ── Health Notes ─────────────────────────────────────
    // ✅ COMPLETELY NEW - was missing entirely!
    suspend fun insertHealthNote(note: HealthNote)
    suspend fun deleteHealthNote(note: HealthNote)
    fun getNotesByCattle(cattleId: Int): Flow<List<HealthNote>>

    // ── Breeding ─────────────────────────────────────────────────
    suspend fun insertBreedingRecord(record: BreedingRecord)
    suspend fun updateBreedingRecord(record: BreedingRecord)
    suspend fun deleteBreedingRecord(record: BreedingRecord)
    fun getBreedingHistory(cattleId: Int): Flow<List<BreedingRecord>>
    fun getCurrentPregnancy(cattleId: Int): Flow<BreedingRecord?>
    fun getAllPregnantCattle(): Flow<List<BreedingRecord>>
}
