package com.mindmatrix.gokulahealth.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object BreedingCalculator {

    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)

    // ── Heat Cycle: Every 21 days ─────────────────────────────
    fun calculateNextHeatDate(heatObservedDate: String): String {
        val date = parseDate(heatObservedDate) ?: return ""
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, 21)
        }
        return displayFormat.format(cal.time)
    }

    // ── Expected Calving: Insemination + 280 days ─────────────
    fun calculateExpectedCalvingDate(inseminationDate: String): String {
        val date = parseDate(inseminationDate) ?: return ""
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, 280)
        }
        return displayFormat.format(cal.time)
    }

    // ── Dry-off Date: 60 days before expected calving ─────────
    fun calculateDryOffDate(expectedCalvingDate: String): String {
        val date = parseDate(expectedCalvingDate) ?: return ""
        val cal = Calendar.getInstance().apply {
            time = date
            add(Calendar.DAY_OF_YEAR, -60)
        }
        return displayFormat.format(cal.time)
    }

    // ── Days Until Calving ────────────────────────────────────
    fun daysUntilCalving(expectedCalvingDate: String): Int {
        val date = parseDate(expectedCalvingDate) ?: return -1
        val diff = date.time - Date().time
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }

    // ── Pregnancy Progress % ──────────────────────────────────
    fun pregnancyProgressPercent(
        inseminationDate: String,
        expectedCalvingDate: String
    ): Float {
        val start = parseDate(inseminationDate) ?: return 0f
        val end = parseDate(expectedCalvingDate) ?: return 0f
        val totalDays = (end.time - start.time).toFloat()
        val elapsedDays = (Date().time - start.time).toFloat()
        return (elapsedDays / totalDays * 100f).coerceIn(0f, 100f)
    }

    // ── Lactation Stage ───────────────────────────────────────
    fun getLactationStage(lactationStartDate: String): String {
        val start = parseDate(lactationStartDate) ?: return "Unknown"
        val daysIn = TimeUnit.MILLISECONDS
            .toDays(Date().time - start.time).toInt()
        return when {
            daysIn <= 100 -> "🔥 Early Lactation (Peak Yield)"
            daysIn <= 200 -> "✅ Mid Lactation (Stable Phase)"
            daysIn <= 305 -> "📉 Late Lactation (Declining)"
            else          -> "⏸️ Beyond 305 Days"
        }
    }

    // ── Parse date supporting both formats ────────────────────
    fun parseDate(dateStr: String): Date? {
        if (dateStr.isBlank()) return null
        val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return try { sdf1.parse(dateStr) }
        catch (e: Exception) {
            try { sdf2.parse(dateStr) } catch (e2: Exception) { null }
        }
    }

    fun formatDate(date: Date): String = displayFormat.format(date)
}
