package com.mindmatrix.gokulahealth.ui.viewmodel

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import com.mindmatrix.gokulahealth.util.VaccinationReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val repository: CattleRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // ✅ PRESERVED - was working correctly
    private val _vaccinations = MutableStateFlow<List<Vaccination>>(emptyList())
    val vaccinations: StateFlow<List<Vaccination>> = _vaccinations.asStateFlow()

    // ✅ NEW - Separate upcoming and history lists for tabs
    private val _upcomingVaccinations = MutableStateFlow<List<Vaccination>>(emptyList())
    val upcomingVaccinations: StateFlow<List<Vaccination>> = _upcomingVaccinations.asStateFlow()

    private val _pastVaccinations = MutableStateFlow<List<Vaccination>>(emptyList())
    val pastVaccinations: StateFlow<List<Vaccination>> = _pastVaccinations.asStateFlow()

    // ✅ NEW: Only vaccinations due within 7 days — for alert banner
    private val _dueWithin7Days = MutableStateFlow<List<Vaccination>>(emptyList())
    val dueWithin7Days: StateFlow<List<Vaccination>> = _dueWithin7Days.asStateFlow()

    // ✅ NEW: Fetch vaccinations due within a week
    fun fetchDueWithin7Days() {
        viewModelScope.launch {
            val today = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(java.util.Date())

            repository.getUpcomingVaccinations(today).collectLatest { allUpcoming ->

                val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)

                val filtered = allUpcoming.filter { vaccination ->
                    try {
                        // ✅ Handle both date formats safely
                        val dueDate = try {
                            sdf1.parse(vaccination.nextDueDate)
                        } catch (e: Exception) {
                            try { sdf2.parse(vaccination.nextDueDate) }
                            catch (e2: Exception) { null }
                        }

                        if (dueDate != null) {
                            val diffMillis = dueDate.time - java.util.Date().time
                            val diffDays = java.util.concurrent.TimeUnit
                                .MILLISECONDS
                                .toDays(diffMillis)
                            // ✅ Only include if due in 0 to 7 days
                            diffDays in 0..7
                        } else {
                            false
                        }
                    } catch (e: Exception) {
                        false
                    }
                }
                _dueWithin7Days.value = filtered
            }
        }
    }

    // ✅ PRESERVED - was working correctly
    fun fetchVaccinations(cattleId: Int) {
        viewModelScope.launch {
            repository.getVaccinationsByCattle(cattleId).collectLatest { list ->
                _vaccinations.value = list
            }
        }
    }

    // ✅ NEW - Fetch for global Vaccination tab (all cattle)
    fun fetchAllUpcoming() {
        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(java.util.Date())
        viewModelScope.launch {
            repository.getUpcomingVaccinations(today).collectLatest { list ->
                _upcomingVaccinations.value = list
            }
        }
    }

    // ✅ NEW - Fetch past/history vaccinations
    fun fetchAllPast() {
        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(java.util.Date())
        viewModelScope.launch {
            repository.getPastVaccinations(today).collectLatest { list ->
                _pastVaccinations.value = list
            }
        }
    }

    // ✅ PRESERVED + IMPROVED - Now creates notification channel too
    fun addVaccination(vaccination: Vaccination) {
        viewModelScope.launch {
            repository.insertVaccination(vaccination)
            createNotificationChannel() // ✅ NEW - Ensure channel exists first
            
            // ✅ FIX: Fetch cattle name to include in notification!
            repository.getCattleById(vaccination.cattleId).collectLatest { cattle ->
                if (cattle != null) {
                    scheduleReminder(vaccination, cattle.name)
                }
            }
        }
    }

    // ✅ PRESERVED - was working correctly
    fun deleteVaccination(vaccination: Vaccination) {
        viewModelScope.launch {
            repository.deleteVaccination(vaccination)
        }
    }

    // ✅ NEW - Create notification channel (required for Android 8+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "vaccination_reminders",
                "Vaccination Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you when cattle vaccination is due"
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // ✅ FIXED - Now uses proper date format matching AddVaccination screen
    // ✅ FIXED - Uses explicit Intent with component (required for Android 12+)
    private fun scheduleReminder(vaccination: Vaccination, cattleName: String) {
        // Try both date formats for flexibility
        val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val dueDate = try {
            sdf1.parse(vaccination.nextDueDate)
        } catch (e: Exception) {
            try { sdf2.parse(vaccination.nextDueDate) } catch (e2: Exception) { null }
        }

        if (dueDate != null) {
            val calendar = Calendar.getInstance().apply {
                time = dueDate
                set(Calendar.HOUR_OF_DAY, 8) // 8 AM reminder
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            // Skip if due date is in the past
            if (calendar.timeInMillis <= System.currentTimeMillis()) return

            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // ✅ FIXED - Explicit intent with component (Android 12+ requirement)
            val intent = Intent(context, VaccinationReceiver::class.java).apply {
                action = "com.mindmatrix.gokulahealth.VACCINATION_REMINDER"
                putExtra("vaccineName", vaccination.vaccineName)
                putExtra("cattleId", vaccination.cattleId)
                putExtra("cattleName", cattleName) // ✅ NEW: Pass the name!
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                vaccination.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // ✅ FIXED - Use setAndAllowWhileIdle for better reliability
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        }
    }
}