package com.mindmatrix.gokulahealth.util

import com.mindmatrix.gokulahealth.data.local.entity.BreedingRecord
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MockDataGenerator {

    suspend fun populateDatabase(repository: CattleRepository) {
        // Only inject data if the database is completely empty
        val existingCattle = repository.getAllCattle().firstOrNull()
        if (!existingCattle.isNullOrEmpty()) return

        val dbSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val displaySdf = SimpleDateFormat("dd MMM yyyy", Locale.US)

        // Helper function to get dates relative to today
        fun getRelativeDate(daysOffset: Int, useDisplayFormat: Boolean = false): String {
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, daysOffset) }
            return if (useDisplayFormat) displaySdf.format(cal.time) else dbSdf.format(cal.time)
        }

        // ── 1. Create Cattle Profiles ───────────────────────────────────────────────
        val gauri = Cattle(1, "IND1001", "Gauri", "Gir", "", 5, "Our best indigenous cow.", "Female")
        val laxmi = Cattle(2, "IND1002", "Laxmi", "HF Jersey", "", 4, "High yielding crossbreed.", "Female")
        val nandi = Cattle(3, "IND1003", "Nandi", "Sahiwal", "", 3, "Strong farm bull.", "Male")

        repository.insertCattle(gauri)
        repository.insertCattle(laxmi)
        repository.insertCattle(nandi)

        // ── 2. Create Milk Logs (Triggers GenAI & Charts) ─────────────────────────
        // Gauri: 3-day decline to trigger the GenAI Alert!
        for (i in 0..14) { // 15 days of data
            val dateStr = getRelativeDate(-i, useDisplayFormat = false)
            val morning = when (i) {
                0 -> 4.0f // Today (Lowest)
                1 -> 5.5f // Yesterday
                2 -> 6.5f // Day before
                else -> 7.0f + (Math.random().toFloat() * 1.5f) // Normal days
            }
            val evening = when (i) {
                0 -> 3.5f
                1 -> 4.5f
                2 -> 5.5f
                else -> 6.0f + (Math.random().toFloat() * 1.0f)
            }
            repository.insertMilkLog(MilkLog(cattleId = 1, date = dateStr, morningLitres = morning, eveningLitres = evening))
        }

        // Laxmi: Stable, high yield
        for (i in 0..14) {
            val dateStr = getRelativeDate(-i, useDisplayFormat = false)
            val morning = 10.0f + (Math.random().toFloat() * 2.0f)
            val evening = 9.5f + (Math.random().toFloat() * 1.5f)
            repository.insertMilkLog(MilkLog(cattleId = 2, date = dateStr, morningLitres = morning, eveningLitres = evening))
        }

        // ── 3. Create Vaccinations (Triggers Reminders & History) ─────────────────
        // Gauri: Upcoming vaccination (Due in 3 days - triggers Home Screen alert)
        repository.insertVaccination(
            Vaccination(0, 1, "FMD Vaccine", getRelativeDate(-177, true), getRelativeDate(3, true), "Annual booster")
        )
        // Laxmi: Past vaccination (Shows in History tab)
        repository.insertVaccination(
            Vaccination(0, 2, "Brucellosis", getRelativeDate(-400, true), getRelativeDate(-35, true), "One-time dose completed")
        )

        // ── 4. Create Health Notes ────────────────────────────────────────────────
        repository.insertHealthNote(
            HealthNote(0, 1, getRelativeDate(0, true), "Slight drop in milk yield observed today. Monitoring feed intake closely.")
        )
        repository.insertHealthNote(
            HealthNote(0, 3, getRelativeDate(-2, true), "Routine hoof trimming completed by Vet Dr. Sharma.")
        )

        // ── 5. Create Breeding Records (Triggers Progress Bars & Lactation) ───────
        // Gauri: Currently Pregnant (Shows purple progress bar)
        repository.insertBreedingRecord(
            BreedingRecord(
                id = 0,
                cattleId = 1,
                heatObservedDate = getRelativeDate(-150, true),
                inseminationDate = getRelativeDate(-149, true),
                inseminationType = "AI",
                bullOrSemenDetails = "Gir Premium Semen (Batch #442)",
                pregnancyConfirmedDate = getRelativeDate(-120, true),
                isPregnant = true,
                expectedCalvingDate = getRelativeDate(131, true), // 280 days from insemination
                notes = "First trimester progressing well."
            )
        )

        // Laxmi: Recently Calved (Shows green Lactation Status)
        repository.insertBreedingRecord(
            BreedingRecord(
                id = 0,
                cattleId = 2,
                heatObservedDate = getRelativeDate(-300, true),
                inseminationDate = getRelativeDate(-299, true),
                pregnancyConfirmedDate = getRelativeDate(-260, true),
                isPregnant = false,
                expectedCalvingDate = getRelativeDate(-19, true),
                actualCalvingDate = getRelativeDate(-20, true),
                calfCount = 1,
                calfGender = "Female",
                lactationStartDate = getRelativeDate(-20, true),
                lactationNumber = 2,
                notes = "Healthy female calf delivered without complications."
            )
        )
    }
}