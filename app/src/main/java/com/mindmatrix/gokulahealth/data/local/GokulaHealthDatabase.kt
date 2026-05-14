package com.mindmatrix.gokulahealth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        Cattle::class,
        MilkLog::class,
        Vaccination::class,
        HealthNote::class,
        BreedingRecord::class   // ✅ NEW
    ],
    version = 3,                // ✅ BUMPED from 2 to 3
    exportSchema = false
)
abstract class GokulaHealthDatabase : RoomDatabase() {
    abstract fun cattleDao(): CattleDao
    abstract fun milkLogDao(): MilkLogDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun healthNoteDao(): HealthNoteDao
    abstract fun breedingDao(): BreedingDao  // ✅ NEW
}
