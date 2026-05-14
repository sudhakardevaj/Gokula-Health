package com.mindmatrix.gokulahealth.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.gokulahealth.data.local.TemplatePriority
import com.mindmatrix.gokulahealth.data.local.VaccinationScheduleLibrary
import com.mindmatrix.gokulahealth.data.local.VaccineScheduleTemplate
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.ui.component.GokulaTextField
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.VaccinationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVaccinationScreen(
    cattleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: VaccinationViewModel = hiltViewModel(),
    cattleViewModel: CattleViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(cattleId) {
        if (cattleId != -1) {
            cattleViewModel.selectCattle(cattleId)
        }
    }

    val cattle by cattleViewModel.selectedCattle.collectAsState()

    // ✅ Standardized format for DB: yyyy-MM-dd
    // ✅ Display format for UI: dd MMM yyyy
    val dbDateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayDateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    var vaccineName by remember { mutableStateOf("") }

    // ✅ FIXED: Store as DB format, display as human-readable
    var dateGivenDb by remember { mutableStateOf(dbDateFormat.format(Date())) }
    var dateGivenDisplay by remember { mutableStateOf(displayDateFormat.format(Date())) }

    var nextDueDateDb by remember { mutableStateOf("") }
    var nextDueDateDisplay by remember { mutableStateOf("") }

    var veterinarian by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // ── DatePickerDialog for "Date Given" ────────────────────────
    // ✅ NEW: Opens real calendar when calendar icon tapped!
    val dateGivenCalendar = Calendar.getInstance()
    val dateGivenPicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val picked = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            dateGivenDb = dbDateFormat.format(picked.time)
            dateGivenDisplay = displayDateFormat.format(picked.time)
        },
        dateGivenCalendar.get(Calendar.YEAR),
        dateGivenCalendar.get(Calendar.MONTH),
        dateGivenCalendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // ✅ Cannot pick future date for "date given"
        datePicker.maxDate = System.currentTimeMillis()
    }

    // ── DatePickerDialog for "Next Due Date" ─────────────────────
    // ✅ NEW: Opens real calendar when calendar icon tapped!
    val nextDueDateCalendar = Calendar.getInstance()
    val nextDueDatePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val picked = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            nextDueDateDb = dbDateFormat.format(picked.time)
            nextDueDateDisplay = displayDateFormat.format(picked.time)
        },
        nextDueDateCalendar.get(Calendar.YEAR),
        nextDueDateCalendar.get(Calendar.MONTH),
        nextDueDateCalendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // ✅ Cannot pick past date for "next due date"
        datePicker.minDate = System.currentTimeMillis()
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Vaccines,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Add Vaccination",
                            fontWeight = FontWeight.ExtraBold,
                            color = EarthBrown
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MeadowGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        when {
                            vaccineName.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Please enter vaccine name!"
                                    )
                                }
                            }
                            nextDueDateDb.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Please pick a next due date!"
                                    )
                                }
                            }
                            else -> {
                                viewModel.addVaccination(
                                    Vaccination(
                                        cattleId = cattleId,
                                        vaccineName = vaccineName.trim(),
                                        dateGiven = dateGivenDb,
                                        nextDueDate = nextDueDateDb,
                                        notes = notes.ifBlank {
                                            if (veterinarian.isNotBlank())
                                                "Vet: $veterinarian"
                                            else ""
                                        }
                                    )
                                )
                                onNavigateBack()
                            }
                        }
                    }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
                            tint = MeadowGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Cattle Name (read-only) ───────────────────────────
            GokulaTextField(
                value = cattle?.let {
                    "${it.name} (${it.earTagId})"
                } ?: if (cattleId == -1) "All Cattle" else "Loading...",
                onValueChange = {},
                label = "Cattle"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── ✅ NEW: Smart Suggestions (breed + age based) ─────
            SuggestedVaccinationSection(
                cattle = cattle,
                onApplySuggestion = { template ->

                    // ── Auto-fill vaccine name ────────────────────
                    vaccineName = template.vaccineName

                    // ── Auto-fill notes ───────────────────────────
                    notes = buildString {
                        append("${template.priority.emoji} ${template.priority.label} | ")
                        append("Protects: ${template.diseaseProtected} | ")
                        if (template.repeatEveryMonths == 0)
                            append("One-time dose")
                        else
                            append("Repeat every ${template.repeatEveryMonths} months")
                    }

                    // ── Auto-calculate next due date ──────────────
                    val calendar = Calendar.getInstance()
                    val monthsToAdd = if (template.repeatEveryMonths > 0)
                        template.repeatEveryMonths
                    else
                        1 // One-time → prompt to schedule next month
                    calendar.add(Calendar.MONTH, monthsToAdd)

                    nextDueDateDb = dbDateFormat.format(calendar.time)
                    nextDueDateDisplay = displayDateFormat.format(calendar.time)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Vaccine Name ──────────────────────────────────────
            GokulaTextField(
                value = vaccineName,
                onValueChange = { vaccineName = it },
                label = "Vaccine Name * (e.g. FMD, HS, BQ)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Date Given ────────────────────────────────────────
            GokulaTextField(
                value = dateGivenDisplay,
                onValueChange = { },
                readOnly = true,
                label = "Date Given (tap 📅 to pick)",
                trailingIcon = {
                    IconButton(
                        onClick = {
                            dateGivenPicker.show()
                        }
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pick Date Given",
                            tint = MeadowGreen
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Next Due Date ─────────────────────────────────────
            GokulaTextField(
                value = nextDueDateDisplay.ifBlank { "Tap 📅 to pick due date" },
                onValueChange = { },
                readOnly = true,
                label = "Next Due Date *",
                trailingIcon = {
                    IconButton(
                        onClick = {
                            nextDueDatePicker.show()
                        }
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pick Next Due Date",
                            tint = MeadowGreen
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Veterinarian ──────────────────────────────────────
            GokulaTextField(
                value = veterinarian,
                onValueChange = { veterinarian = it },
                label = "Veterinarian Name (Optional)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Notes ─────────────────────────────────────────────
            GokulaTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (Optional)",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SuggestedVaccinationSection(
    cattle: com.mindmatrix.gokulahealth.data.local.entity.Cattle?,
    onApplySuggestion: (VaccineScheduleTemplate) -> Unit
) {
    if (cattle == null) return

    val suggestions = remember(cattle) {
        VaccinationScheduleLibrary.getSuggestedSchedule(
            breed = cattle.breed,
            ageInYears = cattle.age,
            gender = cattle.gender
        )
    }

    if (suggestions.isEmpty()) return

    Column {
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = LightMeadow, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // ── Header ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF9C27B0).copy(alpha = 0.07f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🤖", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    "Smart Suggestions — ${cattle.breed} (${cattle.age}yr ${cattle.gender})",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9C27B0),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    "Tap any vaccine below to auto-fill the form",
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Suggestion Cards ────────────────────────────────
        suggestions.forEach { template ->
            val priorityColor = when (template.priority) {
                TemplatePriority.MANDATORY    -> Color(0xFFEF5350)    // Red
                TemplatePriority.RECOMMENDED  -> Color(0xFFFF9800)    // Orange
                TemplatePriority.OPTIONAL     -> MeadowGreen          // Green
            }

            SoftCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onApplySuggestion(template) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(priorityColor.copy(alpha = 0.1f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            "${template.priority.emoji} ${template.priority.label}",
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            template.vaccineName,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            "Protects: ${template.diseaseProtected}",
                            color = WarmGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            if (template.repeatEveryMonths == 0)
                                "⚡ One-time dose"
                            else
                                "🔄 Every ${template.repeatEveryMonths} months",
                            color = if (template.repeatEveryMonths == 0)
                                Color(0xFF9C27B0) else MeadowGreen,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Tap arrow
                    Text("→", color = MeadowGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
