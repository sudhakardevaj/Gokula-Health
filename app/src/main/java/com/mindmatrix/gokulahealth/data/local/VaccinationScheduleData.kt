// app/src/main/java/com/mindmatrix/gokulahealth/data/local/VaccinationScheduleData.kt

package com.mindmatrix.gokulahealth.data.local

import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ── Template Model ────────────────────────────────────────────
data class VaccineScheduleTemplate(
    val vaccineName: String,
    val diseaseProtected: String,
    val recommendedAgeMonthsMin: Int,   // Minimum age in months
    val repeatEveryMonths: Int,         // 0 = one-time only
    val applicableBreeds: List<String>, // Empty = all breeds
    val applicableGenders: List<String>,// Empty = all genders
    val notes: String,
    val priority: TemplatePriority = TemplatePriority.MANDATORY
)

enum class TemplatePriority(val label: String, val emoji: String) {
    MANDATORY("Mandatory", "🔴"),
    RECOMMENDED("Recommended", "🟡"),
    OPTIONAL("Optional", "🟢")
}

// ── Central Vaccination Library ───────────────────────────────
object VaccinationScheduleLibrary {

    val ALL_SCHEDULES = listOf(

        // ── FMD (Foot & Mouth Disease) ────────────────────────
        VaccineScheduleTemplate(
            vaccineName = "FMD Vaccine",
            diseaseProtected = "Foot & Mouth Disease",
            recommendedAgeMonthsMin = 4,
            repeatEveryMonths = 6,
            applicableBreeds = emptyList(), // All breeds
            applicableGenders = emptyList(), // All genders
            notes = "Critical for all cattle. First dose at 4 months, booster at 6 months, then every 6 months.",
            priority = TemplatePriority.MANDATORY
        ),

        // ── HS (Haemorrhagic Septicaemia) ─────────────────────
        VaccineScheduleTemplate(
            vaccineName = "HS Vaccine",
            diseaseProtected = "Haemorrhagic Septicaemia",
            recommendedAgeMonthsMin = 6,
            repeatEveryMonths = 12,
            applicableBreeds = emptyList(),
            applicableGenders = emptyList(),
            notes = "Annual vaccination. Best given before monsoon (May-June). Often combined with BQ.",
            priority = TemplatePriority.MANDATORY
        ),

        // ── BQ (Black Quarter) ────────────────────────────────
        VaccineScheduleTemplate(
            vaccineName = "BQ Vaccine",
            diseaseProtected = "Black Quarter",
            recommendedAgeMonthsMin = 6,
            repeatEveryMonths = 12,
            applicableBreeds = emptyList(),
            applicableGenders = emptyList(),
            notes = "Annual vaccination. Can be given as HS-BQ combined vaccine.",
            priority = TemplatePriority.MANDATORY
        ),

        // ── Brucellosis (Female only, one-time) ───────────────
        VaccineScheduleTemplate(
            vaccineName = "Brucellosis Vaccine",
            diseaseProtected = "Brucellosis (Bang's Disease)",
            recommendedAgeMonthsMin = 4,
            repeatEveryMonths = 0, // One-time
            applicableBreeds = emptyList(),
            applicableGenders = listOf("Female"), // FEMALE ONLY
            notes = "One-time dose for female calves (4-8 months only). Mandatory by law in many states.",
            priority = TemplatePriority.MANDATORY
        ),

        // ── Anthrax ───────────────────────────────────────────
        VaccineScheduleTemplate(
            vaccineName = "Anthrax Vaccine",
            diseaseProtected = "Anthrax",
            recommendedAgeMonthsMin = 6,
            repeatEveryMonths = 12,
            applicableBreeds = emptyList(),
            applicableGenders = emptyList(),
            notes = "Required in endemic zones. Consult local veterinarian for area-specific advice.",
            priority = TemplatePriority.RECOMMENDED
        ),

        // ── Theileria (HF Jersey - exotic breeds) ─────────────
        VaccineScheduleTemplate(
            vaccineName = "Theileria Vaccine",
            diseaseProtected = "East Coast Fever / Theileriosis",
            recommendedAgeMonthsMin = 3,
            repeatEveryMonths = 0, // One-time
            applicableBreeds = listOf("HF Jersey"), // Exotic breeds at higher risk
            applicableGenders = emptyList(),
            notes = "Exotic and crossbred cattle are more susceptible. One-time vaccination recommended.",
            priority = TemplatePriority.RECOMMENDED
        ),

        // ── PPR (Indigenous breeds) ────────────────────────────
        VaccineScheduleTemplate(
            vaccineName = "PPR Vaccine",
            diseaseProtected = "Peste des Petits Ruminants",
            recommendedAgeMonthsMin = 4,
            repeatEveryMonths = 36, // Every 3 years
            applicableBreeds = listOf("Gir", "Sahiwal"),
            applicableGenders = emptyList(),
            notes = "Every 3 years for indigenous breeds in mixed farms. Especially important near goat farms.",
            priority = TemplatePriority.OPTIONAL
        ),

        // ── Rabies ────────────────────────────────────────────
        VaccineScheduleTemplate(
            vaccineName = "Rabies Vaccine",
            diseaseProtected = "Rabies",
            recommendedAgeMonthsMin = 3,
            repeatEveryMonths = 12,
            applicableBreeds = emptyList(),
            applicableGenders = emptyList(),
            notes = "Annual vaccination. Especially important in areas with stray dog activity.",
            priority = TemplatePriority.OPTIONAL
        )
    )

    // ── Core Filter Function ──────────────────────────────────
    fun getSuggestedSchedule(
        breed: String,
        ageInYears: Int,
        gender: String
    ): List<VaccineScheduleTemplate> {
        val ageInMonths = ageInYears * 12

        return ALL_SCHEDULES.filter { template ->

            // 1. Age check — cattle must be old enough
            val ageMatches = ageInMonths >= template.recommendedAgeMonthsMin

            // 2. Breed check — empty list means applies to all breeds
            val breedMatches = template.applicableBreeds.isEmpty() ||
                    template.applicableBreeds.any {
                        it.equals(breed, ignoreCase = true)
                    }

            // 3. Gender check — empty list means applies to all genders
            val genderMatches = template.applicableGenders.isEmpty() ||
                    template.applicableGenders.any {
                        it.equals(gender, ignoreCase = true)
                    }

            ageMatches && breedMatches && genderMatches
        }
    }

    // ── Generate Vaccination objects ready for DB insert ──────
    fun generateVaccinationRecords(
        cattleId: Int,
        breed: String,
        ageInYears: Int,
        gender: String
    ): List<Vaccination> {
        val templates = getSuggestedSchedule(breed, ageInYears, gender)
        val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val today = Date()

        return templates.map { template ->
            val calendar = Calendar.getInstance()
            calendar.time = today

            // Calculate next due date
            val monthsToAdd = when {
                template.repeatEveryMonths > 0 -> template.repeatEveryMonths
                else -> 1 // One-time vaccines → show as due next month to prompt scheduling
            }
            calendar.add(Calendar.MONTH, monthsToAdd)

            Vaccination(
                id = 0,
                cattleId = cattleId,
                vaccineName = template.vaccineName,
                dateGiven = displayFormat.format(today),
                nextDueDate = displayFormat.format(calendar.time),
                notes = buildString {
                    append("Auto-suggested | ")
                    append("Protects: ${template.diseaseProtected} | ")
                    if (template.repeatEveryMonths == 0) {
                        append("One-time dose")
                    } else {
                        append("Every ${template.repeatEveryMonths} months")
                    }
                }
            )
        }
    }

    // ── Get breeds list for dropdown ──────────────────────────
    val SUPPORTED_BREEDS = listOf("HF Jersey", "Gir", "Sahiwal", "Murrah", "Other")
}