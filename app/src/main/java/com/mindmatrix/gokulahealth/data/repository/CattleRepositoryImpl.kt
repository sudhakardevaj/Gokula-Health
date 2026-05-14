package com.mindmatrix.gokulahealth.data.repository

import com.mindmatrix.gokulahealth.data.local.dao.BreedingDao
import com.mindmatrix.gokulahealth.data.local.dao.CattleDao
import com.mindmatrix.gokulahealth.data.local.dao.HealthNoteDao
import com.mindmatrix.gokulahealth.data.local.dao.MilkLogDao
import com.mindmatrix.gokulahealth.data.local.dao.VaccinationDao
import com.mindmatrix.gokulahealth.data.local.entity.BreedingRecord
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CattleRepositoryImpl @Inject constructor(
    private val cattleDao: CattleDao,
    private val milkLogDao: MilkLogDao,
    private val vaccinationDao: VaccinationDao,
    private val healthNoteDao: HealthNoteDao,
    private val breedingDao: BreedingDao // ✅ NEW injection
) : CattleRepository {

    // ── Cattle ──────────────────────────────────────────────────────────────
    override suspend fun insertCattle(cattle: Cattle) =
        cattleDao.insertCattle(cattle)

    override suspend fun updateCattle(cattle: Cattle) =
        cattleDao.updateCattle(cattle)

    override suspend fun deleteCattle(cattle: Cattle) =
        cattleDao.deleteCattle(cattle)

    override fun getAllCattle(): Flow<List<Cattle>> =
        cattleDao.getAllCattle()

    override fun getCattleById(id: Int): Flow<Cattle?> =
        cattleDao.getCattleById(id) // ✅ FIXED: nullable return

    // ── Milk Log ─────────────────────────────────────────────────────────────
    override suspend fun insertMilkLog(milkLog: MilkLog) =
        milkLogDao.insertMilkLog(milkLog)

    override suspend fun updateMilkLog(milkLog: MilkLog) =
        milkLogDao.updateMilkLog(milkLog)

    override suspend fun deleteMilkLog(milkLog: MilkLog) =
        milkLogDao.deleteMilkLog(milkLog)

    override fun getLast30DaysLogs(cattleId: Int): Flow<List<MilkLog>> =
        milkLogDao.getLast30DaysLogs(cattleId)

    override fun getTodayLog(cattleId: Int, date: String): Flow<MilkLog?> =
        milkLogDao.getTodayLog(cattleId, date)

    override fun getAllLogsForDate(date: String): Flow<List<MilkLog>> =
        milkLogDao.getAllLogsForDate(date)

    override fun getMonthlyAverage(cattleId: Int, month: String): Flow<Float?> =
        milkLogDao.getMonthlyAverage(cattleId, month) // ✅ FIXED: nullable

    override fun getLastNDaysLogs(cattleId: Int, limit: Int): Flow<List<MilkLog>> =
        milkLogDao.getLastNDaysLogs(cattleId, limit)

    // ── Vaccination ──────────────────────────────────────────────────────────
    override suspend fun insertVaccination(vaccination: Vaccination) =
        vaccinationDao.insertVaccination(vaccination)

    override suspend fun updateVaccination(vaccination: Vaccination) =
        vaccinationDao.updateVaccination(vaccination)

    override suspend fun deleteVaccination(vaccination: Vaccination) =
        vaccinationDao.deleteVaccination(vaccination)

    override fun getVaccinationsByCattle(cattleId: Int): Flow<List<Vaccination>> =
        vaccinationDao.getVaccinationsByCattle(cattleId)

    override fun getUpcomingVaccinations(today: String): Flow<List<Vaccination>> =
        vaccinationDao.getUpcomingVaccinations(today)

    override fun getPastVaccinations(today: String): Flow<List<Vaccination>> =
        vaccinationDao.getPastVaccinations(today) // ✅ NEW: History tab

    // ── Health Notes ──────────────────────────────────────────────────────────
    // ✅ ALL NEW - was completely missing!
    override suspend fun insertHealthNote(note: HealthNote) =
        healthNoteDao.insertHealthNote(note)

    override suspend fun deleteHealthNote(note: HealthNote) =
        healthNoteDao.deleteHealthNote(note)

    override fun getNotesByCattle(cattleId: Int): Flow<List<HealthNote>> =
        healthNoteDao.getNotesByCattle(cattleId)

    // ── Breeding ──────────────────────────────────────────────────────────────
    override suspend fun insertBreedingRecord(record: BreedingRecord) =
        breedingDao.insertBreedingRecord(record)

    override suspend fun updateBreedingRecord(record: BreedingRecord) =
        breedingDao.updateBreedingRecord(record)

    override suspend fun deleteBreedingRecord(record: BreedingRecord) =
        breedingDao.deleteBreedingRecord(record)

    override fun getBreedingHistory(cattleId: Int): Flow<List<BreedingRecord>> =
        breedingDao.getBreedingHistory(cattleId)

    override fun getCurrentPregnancy(cattleId: Int): Flow<BreedingRecord?> =
        breedingDao.getCurrentPregnancy(cattleId)

    override fun getAllPregnantCattle(): Flow<List<BreedingRecord>> =
        breedingDao.getAllPregnantCattle()
}
