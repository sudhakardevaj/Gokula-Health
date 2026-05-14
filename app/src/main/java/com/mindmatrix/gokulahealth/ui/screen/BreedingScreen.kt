package com.mindmatrix.gokulahealth.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.gokulahealth.data.local.entity.BreedingRecord
import com.mindmatrix.gokulahealth.ui.component.GokulaTextField
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.*
import com.mindmatrix.gokulahealth.ui.viewmodel.BreedingViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import com.mindmatrix.gokulahealth.util.BreedingCalculator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingScreen(
    cattleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: BreedingViewModel = hiltViewModel(),
    cattleViewModel: CattleViewModel = hiltViewModel()
) {
    LaunchedEffect(cattleId) {
        cattleViewModel.selectCattle(cattleId)
        viewModel.fetchBreedingHistory(cattleId)
        viewModel.fetchCurrentPregnancy(cattleId)
    }

    val cattle by cattleViewModel.selectedCattle.collectAsState()
    val currentPregnancy by viewModel.currentPregnancy.collectAsState()
    val breedingHistory by viewModel.breedingHistory.collectAsState()

    var showLogHeatDialog by remember { mutableStateOf(false) }
    var showInseminationDialog by remember { mutableStateOf<BreedingRecord?>(null) }
    var showPregnancyDialog by remember { mutableStateOf<BreedingRecord?>(null) }
    var showCalvingDialog by remember { mutableStateOf<BreedingRecord?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐄", fontSize = 22.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Breeding — ${cattle?.name ?: "..."}",
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = MeadowGreen
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showLogHeatDialog = true }) {
                        Icon(Icons.Default.Add, null, tint = MeadowGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = LightMeadow
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Active Pregnancy Card ──────────────────────────
            currentPregnancy?.let { pregnancy ->
                item {
                    ActivePregnancyCard(pregnancy = pregnancy)
                }
            }

            // ── Lactation Status Card ──────────────────────────
            breedingHistory.firstOrNull {
                it.lactationStartDate.isNotBlank()
            }?.let { record ->
                item {
                    LactationStatusCard(record = record)
                }
            }

            // ── Section Header ─────────────────────────────────
            item {
                Text(
                    "Breeding History",
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ── Empty State ────────────────────────────────────
            if (breedingHistory.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐄", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No breeding records yet",
                            color = WarmGray,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Tap + to log a heat cycle",
                            color = MeadowGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                items(breedingHistory, key = { it.id }) { record ->
                    BreedingHistoryCard(
                        record = record,
                        onDelete = { viewModel.deleteBreedingRecord(record) },
                        onLogInsemination = { showInseminationDialog = record },
                        onConfirmPregnancy = { showPregnancyDialog = record },
                        onRecordCalving = { showCalvingDialog = record }
                    )
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // ── Log Heat Dialog ────────────────────────────────────────
    if (showLogHeatDialog) {
        LogHeatDialog(
            onDismiss = { showLogHeatDialog = false },
            onConfirm = { heatDate ->
                viewModel.logHeatEvent(cattleId, heatDate)
                showLogHeatDialog = false
            }
        )
    }

    if (showInseminationDialog != null) {
        LogInseminationDialog(
            onDismiss = { showInseminationDialog = null },
            onConfirm = { date, type, details ->
                viewModel.recordInsemination(showInseminationDialog!!, date, type, details)
                showInseminationDialog = null
            }
        )
    }

    if (showPregnancyDialog != null) {
        ConfirmPregnancyDialog(
            onDismiss = { showPregnancyDialog = null },
            onConfirm = { date ->
                viewModel.confirmPregnancy(showPregnancyDialog!!, date)
                showPregnancyDialog = null
            }
        )
    }

    if (showCalvingDialog != null) {
        RecordCalvingDialog(
            onDismiss = { showCalvingDialog = null },
            onConfirm = { date, count, gender ->
                viewModel.recordCalving(
                    showCalvingDialog!!,
                    date,
                    count,
                    gender,
                    showCalvingDialog!!.lactationNumber + 1
                )
                showCalvingDialog = null
            }
        )
    }
}

@Composable
fun ActivePregnancyCard(pregnancy: BreedingRecord) {
    val daysLeft = BreedingCalculator.daysUntilCalving(pregnancy.expectedCalvingDate)
    val progress = BreedingCalculator.pregnancyProgressPercent(
        pregnancy.inseminationDate,
        pregnancy.expectedCalvingDate
    )

    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .background(Color(0xFFF3E5F5))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🤰", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Active Pregnancy",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7B1FA2),
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "Expected: ${pregnancy.expectedCalvingDate}",
                        color = WarmGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$daysLeft",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF7B1FA2),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "days left",
                        color = WarmGray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "Pregnancy Progress: ${progress.toInt()}%",
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF9C27B0),
                trackColor = Color(0xFFE1BEE7)
            )

            if (pregnancy.dryOffDate.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF3E0))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⚠️", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Dry off by: ${pregnancy.dryOffDate}",
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun LactationStatusCard(record: BreedingRecord) {
    val stage = BreedingCalculator.getLactationStage(record.lactationStartDate)

    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🥛", fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Lactation #${record.lactationNumber}",
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Started: ${record.lactationStartDate}",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    stage,
                    color = MeadowGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun BreedingHistoryCard(
    record: BreedingRecord,
    onDelete: () -> Unit,
    onLogInsemination: () -> Unit,
    onConfirmPregnancy: () -> Unit,
    onRecordCalving: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗑️", fontSize = 22.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Delete Record?",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                }
            },
            text = {
                Text(
                    "Delete this breeding record?\nThis cannot be undone.",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(
                        "Delete",
                        color = HeartRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = WarmGray)
                }
            },
            containerColor = Color.White
        )
    }

    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (statusText, statusColor) = when {
                    record.isPregnant -> "🤰 Pregnant" to Color(0xFF9C27B0)
                    record.actualCalvingDate.isNotBlank() -> "🐄 Calved" to MeadowGreen
                    record.inseminationDate.isNotBlank() -> "💉 Inseminated" to Color(0xFF2196F3)
                    else -> "🌡️ Heat Observed" to Color(0xFFFF9800)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(statusColor.copy(alpha = 0.1f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        statusText,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = HeartRed.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (record.heatObservedDate.isNotBlank()) {
                BreedingTimelineRow("🌡️", "Heat Observed", record.heatObservedDate, Color(0xFFFF9800))
            }
            if (record.nextExpectedHeatDate.isNotBlank() && !record.isPregnant && record.actualCalvingDate.isBlank()) {
                BreedingTimelineRow("📅", "Next Expected Heat", record.nextExpectedHeatDate, WarmGray, isDimmed = true)
            }
            if (record.inseminationDate.isNotBlank()) {
                BreedingTimelineRow("💉", "Inseminated (${record.inseminationType})", record.inseminationDate, Color(0xFF2196F3))
            }
            if (record.pregnancyConfirmedDate.isNotBlank()) {
                BreedingTimelineRow("✅", "Pregnancy Confirmed", record.pregnancyConfirmedDate, Color(0xFF9C27B0))
            }
            if (record.expectedCalvingDate.isNotBlank()) {
                val daysLeft = BreedingCalculator.daysUntilCalving(record.expectedCalvingDate)
                BreedingTimelineRow("🍼", "Expected Calving" + if (daysLeft > 0) " ($daysLeft days)" else "", record.expectedCalvingDate, Color(0xFF9C27B0), isDimmed = record.actualCalvingDate.isNotBlank())
            }
            if (record.dryOffDate.isNotBlank() && record.isPregnant) {
                BreedingTimelineRow("⏸️", "Dry-Off Date", record.dryOffDate, Color(0xFFE65100), isDimmed = true)
            }
            if (record.actualCalvingDate.isNotBlank()) {
                BreedingTimelineRow("🐄", "Calved — ${record.calfCount} calf (${record.calfGender})", record.actualCalvingDate, MeadowGreen)
            }

            if (record.notes.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("📝 ${record.notes}", color = WarmGray, style = MaterialTheme.typography.bodySmall)
            }

            // ✅ NEW: Action buttons to advance the breeding cycle
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = LightMeadow)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // 1. If heat is logged but NOT inseminated
                if (record.heatObservedDate.isNotBlank() && record.inseminationDate.isBlank()) {
                    TextButton(onClick = onLogInsemination) {
                        Text("💉 Log Insemination", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold)
                    }
                }

                // 2. If inseminated but pregnancy NOT confirmed
                if (record.inseminationDate.isNotBlank() && record.pregnancyConfirmedDate.isBlank()) {
                    TextButton(onClick = onConfirmPregnancy) {
                        Text("✅ Confirm Pregnancy", color = Color(0xFF9C27B0), fontWeight = FontWeight.Bold)
                    }
                }

                // 3. If pregnant but NOT calved yet
                if (record.isPregnant && record.actualCalvingDate.isBlank()) {
                    TextButton(onClick = onRecordCalving) {
                        Text("🍼 Record Calving", color = MeadowGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BreedingTimelineRow(
    icon: String,
    label: String,
    date: String,
    color: Color,
    isDimmed: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isDimmed) color.copy(alpha = 0.1f) else color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = if (isDimmed) WarmGray else EarthBrown,
            fontWeight = if (isDimmed) FontWeight.Normal else FontWeight.Medium
        )
        Text(
            date,
            style = MaterialTheme.typography.labelSmall,
            color = if (isDimmed) WarmGray else EarthBrown,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogHeatDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(sdf.format(Date())) }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = sdf.format(cal.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Heat Event", fontWeight = FontWeight.Bold, color = EarthBrown) },
        text = {
            Column {
                Text("Select the date heat was observed:", color = WarmGray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                GokulaTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = "Heat Date",
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.DateRange, null, tint = MeadowGreen)
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDate) }) {
                Text("Log Event", color = MeadowGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WarmGray)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun LogInseminationDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(sdf.format(Date())) }
    var type by remember { mutableStateOf("AI") }
    var details by remember { mutableStateOf("") }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = sdf.format(cal.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Insemination", fontWeight = FontWeight.Bold, color = EarthBrown) },
        text = {
            Column {
                GokulaTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = "Insemination Date",
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.DateRange, null, tint = MeadowGreen)
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))
                Text("Type:", style = MaterialTheme.typography.labelMedium, color = EarthBrown)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "AI", onClick = { type = "AI" })
                    Text("AI", color = EarthBrown)
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = type == "Natural", onClick = { type = "Natural" })
                    Text("Natural", color = EarthBrown)
                }
                Spacer(Modifier.height(12.dp))
                GokulaTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = "Bull / Semen Details"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDate, type, details) }) {
                Text("Log Insemination", color = MeadowGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WarmGray)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun ConfirmPregnancyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(sdf.format(Date())) }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = sdf.format(cal.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Pregnancy", fontWeight = FontWeight.Bold, color = EarthBrown) },
        text = {
            Column {
                Text("When was the pregnancy confirmed?", color = WarmGray, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                GokulaTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = "Confirmation Date",
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.DateRange, null, tint = MeadowGreen)
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDate) }) {
                Text("Confirm", color = MeadowGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WarmGray)
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun RecordCalvingDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String) -> Unit
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(sdf.format(Date())) }
    var calfCount by remember { mutableStateOf("1") }
    var gender by remember { mutableStateOf("Female") }

    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = sdf.format(cal.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Calving", fontWeight = FontWeight.Bold, color = EarthBrown) },
        text = {
            Column {
                GokulaTextField(
                    value = selectedDate,
                    onValueChange = {},
                    readOnly = true,
                    label = "Calving Date",
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.DateRange, null, tint = MeadowGreen)
                        }
                    }
                )
                Spacer(Modifier.height(12.dp))
                GokulaTextField(
                    value = calfCount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) calfCount = it },
                    label = "Calf Count",
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
                Spacer(Modifier.height(12.dp))
                Text("Gender:", style = MaterialTheme.typography.labelMedium, color = EarthBrown)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = gender == "Female", onClick = { gender = "Female" })
                    Text("Female", color = EarthBrown)
                    Spacer(Modifier.width(12.dp))
                    RadioButton(selected = gender == "Male", onClick = { gender = "Male" })
                    Text("Male", color = EarthBrown)
                    Spacer(Modifier.width(12.dp))
                    RadioButton(selected = gender == "Both", onClick = { gender = "Both" })
                    Text("Both", color = EarthBrown)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDate, calfCount.toIntOrNull() ?: 1, gender) }) {
                Text("Record", color = MeadowGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WarmGray)
            }
        },
        containerColor = Color.White
    )
}
