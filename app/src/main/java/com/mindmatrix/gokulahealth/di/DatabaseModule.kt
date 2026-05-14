package com.mindmatrix.gokulahealth.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mindmatrix.gokulahealth.data.local.GokulaHealthDatabase
import com.mindmatrix.gokulahealth.data.local.dao.BreedingDao
import com.mindmatrix.gokulahealth.data.local.dao.CattleDao
import com.mindmatrix.gokulahealth.data.local.dao.HealthNoteDao
import com.mindmatrix.gokulahealth.data.local.dao.MilkLogDao
import com.mindmatrix.gokulahealth.data.local.dao.VaccinationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ✅ Safe migration - preserves existing user data!
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add gender column to existing cattle table
        database.execSQL(
            "ALTER TABLE cattle ADD COLUMN gender TEXT NOT NULL DEFAULT 'Female'"
        )
        // Create the new health_note table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `health_note` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `cattleId` INTEGER NOT NULL,
                `date` TEXT NOT NULL,
                `content` TEXT NOT NULL,
                FOREIGN KEY(`cattleId`) REFERENCES `cattle`(`id`) 
                ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        // Create index for health_note
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_health_note_cattleId` " +
                    "ON `health_note` (`cattleId`)"
        )
    }
}

// ✅ NEW Migration 2 -> 3 for Breeding Tracker
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `breeding_record` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `cattleId` INTEGER NOT NULL,
                `heatObservedDate` TEXT NOT NULL DEFAULT '',
                `nextExpectedHeatDate` TEXT NOT NULL DEFAULT '',
                `inseminationDate` TEXT NOT NULL DEFAULT '',
                `inseminationType` TEXT NOT NULL DEFAULT '',
                `bullOrSemenDetails` TEXT NOT NULL DEFAULT '',
                `pregnancyConfirmedDate` TEXT NOT NULL DEFAULT '',
                `isPregnant` INTEGER NOT NULL DEFAULT 0,
                `expectedCalvingDate` TEXT NOT NULL DEFAULT '',
                `actualCalvingDate` TEXT NOT NULL DEFAULT '',
                `calfCount` INTEGER NOT NULL DEFAULT 0,
                `calfGender` TEXT NOT NULL DEFAULT '',
                `lactationStartDate` TEXT NOT NULL DEFAULT '',
                `lactationNumber` INTEGER NOT NULL DEFAULT 0,
                `dryOffDate` TEXT NOT NULL DEFAULT '',
                `notes` TEXT NOT NULL DEFAULT '',
                FOREIGN KEY(`cattleId`) REFERENCES `cattle`(`id`)
                ON UPDATE NO ACTION ON DELETE CASCADE
            )
        """.trimIndent())

        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_breeding_record_cattleId` " +
            "ON `breeding_record` (`cattleId`)"
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GokulaHealthDatabase {
        return Room.databaseBuilder(
            context,
            GokulaHealthDatabase::class.java,
            "gokula_health_db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // ✅ Added Migration 2->3
            .build()
    }

    @Provides
    fun provideCattleDao(db: GokulaHealthDatabase): CattleDao = db.cattleDao()

    @Provides
    fun provideMilkLogDao(db: GokulaHealthDatabase): MilkLogDao = db.milkLogDao()

    @Provides
    fun provideVaccinationDao(db: GokulaHealthDatabase): VaccinationDao = db.vaccinationDao()

    @Provides
    fun provideHealthNoteDao(db: GokulaHealthDatabase): HealthNoteDao = db.healthNoteDao() // ✅ NEW

    @Provides
    fun provideBreedingDao(db: GokulaHealthDatabase): BreedingDao = db.breedingDao() // ✅ NEW
}
