package com.mindmatrix.gokulahealth.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mindmatrix.gokulahealth.data.local.entity.BreedingRecord
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import kotlinx.coroutines.flow.Flow

@Dao
interface CattleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCattle(cattle: Cattle)

    @Update
    suspend fun updateCattle(cattle: Cattle)

    @Delete
    suspend fun deleteCattle(cattle: Cattle)

    @Query("SELECT * FROM cattle")
    fun getAllCattle(): Flow<List<Cattle>>

    @Query("SELECT * FROM cattle WHERE id = :id")
    fun getCattleById(id: Int): Flow<Cattle?> // ✅ FIXED: Was Flow<Cattle> - crashes when id=-1
}

@Dao
interface MilkLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilkLog(milkLog: MilkLog)

    @Update
    suspend fun updateMilkLog(milkLog: MilkLog)

    @Delete
    suspend fun deleteMilkLog(milkLog: MilkLog)

    @Query(
        "SELECT * FROM milk_log WHERE cattleId = :cattleId " +
                "AND date >= date('now', '-30 days') ORDER BY date DESC"
    )
    fun getLast30DaysLogs(cattleId: Int): Flow<List<MilkLog>>

    @Query(
        "SELECT * FROM milk_log WHERE cattleId = :cattleId " +
                "AND date = :date LIMIT 1"
    )
    fun getTodayLog(cattleId: Int, date: String): Flow<MilkLog?>

    @Query(
        "SELECT AVG(totalLitres) FROM milk_log WHERE cattleId = :cattleId " +
                "AND strftime('%m', date) = :month"
    )
    fun getMonthlyAverage(cattleId: Int, month: String): Flow<Float?> // ✅ FIXED: nullable Float

    @Query(
        "SELECT * FROM milk_log WHERE cattleId = :cattleId " +
                "ORDER BY date DESC LIMIT :limit"
    )
    fun getLastNDaysLogs(cattleId: Int, limit: Int): Flow<List<MilkLog>>

    @Query(
        "SELECT * FROM milk_log WHERE cattleId = :cattleId " +
                "AND date = :date LIMIT 1"
    )
    fun getLogForDate(cattleId: Int, date: String): Flow<MilkLog?> // ✅ NEW: for today's summary

    @Query("SELECT * FROM milk_log WHERE date = :date")
    fun getAllLogsForDate(date: String): Flow<List<MilkLog>>
}

@Dao
interface VaccinationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaccination(vaccination: Vaccination)

    @Update
    suspend fun updateVaccination(vaccination: Vaccination)

    @Delete
    suspend fun deleteVaccination(vaccination: Vaccination)

    @Query("SELECT * FROM vaccination WHERE cattleId = :cattleId ORDER BY nextDueDate ASC")
    fun getVaccinationsByCattle(cattleId: Int): Flow<List<Vaccination>>

    @Query(
        "SELECT * FROM vaccination WHERE nextDueDate >= :today " +
                "ORDER BY nextDueDate ASC"
    )
    fun getUpcomingVaccinations(today: String): Flow<List<Vaccination>>

    @Query(
        "SELECT * FROM vaccination WHERE nextDueDate < :today " +
                "ORDER BY nextDueDate DESC"
    )
    fun getPastVaccinations(today: String): Flow<List<Vaccination>> // ✅ NEW: for History tab
}

// ✅ NEW DAO - Was completely missing!
@Dao
interface HealthNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthNote(note: HealthNote)

    @Delete
    suspend fun deleteHealthNote(note: HealthNote)

    @Query(
        "SELECT * FROM health_note WHERE cattleId = :cattleId " +
                "ORDER BY date DESC"
    )
    fun getNotesByCattle(cattleId: Int): Flow<List<HealthNote>>
}

@Dao
interface BreedingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreedingRecord(record: BreedingRecord)

    @Update
    suspend fun updateBreedingRecord(record: BreedingRecord)

    @Delete
    suspend fun deleteBreedingRecord(record: BreedingRecord)

    // All records for one cattle — newest first
    @Query("""
        SELECT * FROM breeding_record 
        WHERE cattleId = :cattleId 
        ORDER BY heatObservedDate DESC
    """)
    fun getBreedingHistory(cattleId: Int): Flow<List<BreedingRecord>>

    // Current active pregnancy for one cattle
    @Query("""
        SELECT * FROM breeding_record 
        WHERE cattleId = :cattleId 
        AND isPregnant = 1 
        LIMIT 1
    """)
    fun getCurrentPregnancy(cattleId: Int): Flow<BreedingRecord?>

    @Query("""
        SELECT * FROM breeding_record 
        WHERE isPregnant = 1 
        ORDER BY expectedCalvingDate ASC
    """)
    fun getAllPregnantCattle(): Flow<List<BreedingRecord>>
}
