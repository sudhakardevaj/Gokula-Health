package com.mindmatrix.gokulahealth.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "breeding_record",
    foreignKeys = [
        ForeignKey(
            entity = Cattle::class,
            parentColumns = ["id"],
            childColumns = ["cattleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cattleId"])]
)
data class BreedingRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cattleId: Int,

    // ── Heat Cycle ─────────────────────────────────────
    val heatObservedDate: String = "",
    val nextExpectedHeatDate: String = "",  // Auto: +21 days

    // ── Insemination ───────────────────────────────────
    val inseminationDate: String = "",
    val inseminationType: String = "",      // "AI" or "Natural"
    val bullOrSemenDetails: String = "",

    // ── Pregnancy ──────────────────────────────────────
    val pregnancyConfirmedDate: String = "",
    val isPregnant: Boolean = false,

    // ── Calving ────────────────────────────────────────
    val expectedCalvingDate: String = "",   // Auto: insemination + 280 days
    val actualCalvingDate: String = "",
    val calfCount: Int = 0,
    val calfGender: String = "",            // "Male" / "Female" / "Both"

    // ── Lactation ──────────────────────────────────────
    val lactationStartDate: String = "",
    val lactationNumber: Int = 0,
    val dryOffDate: String = "",            // Auto: calving - 60 days

    val notes: String = ""
)
