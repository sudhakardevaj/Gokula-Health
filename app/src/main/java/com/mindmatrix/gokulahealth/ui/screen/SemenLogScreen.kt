package com.mindmatrix.gokulahealth.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.ui.component.GokulaTextField
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.*
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemenLogScreen(
    cattleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: CattleViewModel = hiltViewModel()
) {
    LaunchedEffect(cattleId) {
        viewModel.selectCattle(cattleId)
        viewModel.fetchHealthNotes(cattleId)
    }

    val cattle by viewModel.selectedCattle.collectAsState()
    val allNotes by viewModel.healthNotes.collectAsState()
    
    // Filter only notes that are Semen Logs
    val semenLogs = allNotes.filter { it.content.startsWith("[SEMEN LOG]") }

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = LightMeadow,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧬", fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Semen Log — ${cattle?.name ?: "..."}",
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MeadowGreen)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, null, tint = MeadowGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (semenLogs.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🧬", fontSize = 56.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No semen collections yet", color = WarmGray, fontWeight = FontWeight.Bold)
                        Text("Tap + to add a record", color = MeadowGreen, style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                items(semenLogs) { log ->
                    SemenLogCard(
                        log = log,
                        onDelete = { viewModel.deleteHealthNote(log) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddSemenLogDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { date, batch, volume, motility ->
                val formattedContent = "[SEMEN LOG]\nBatch: $batch\nVolume: $volume ml\nMotility: $motility%"
                viewModel.addHealthNote(
                    HealthNote(cattleId = cattleId, date = date, content = formattedContent)
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SemenLogCard(log: HealthNote, onDelete: () -> Unit) {
    // Remove the hidden tag for display
    val displayContent = log.content.replace("[SEMEN LOG]\n", "")
    
    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Text("❄️", fontSize = 24.sp)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(log.date, color = WarmGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(displayContent, color = EarthBrown, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, null, tint = HeartRed.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun AddSemenLogDialog(
    onDismiss: () -> Unit,
    onConfirm: (date: String, batch: String, volume: String, motility: String) -> Unit
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var date by remember { mutableStateOf(sdf.format(Date())) }
    var batch by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }
    var motility by remember { mutableStateOf("") }

    val datePicker = DatePickerDialog(
        context, { _, y, m, d ->
            val cal = Calendar.getInstance().apply { set(y, m, d) }
            date = sdf.format(cal.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Semen Collection", color = EarthBrown, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                GokulaTextField(
                    value = date, onValueChange = {}, readOnly = true, label = "Collection Date",
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.DateRange, null, tint = MeadowGreen)
                        }
                    }
                )
                Spacer(Modifier.height(8.dp))
                GokulaTextField(value = batch, onValueChange = { batch = it }, label = "Batch Number (e.g. B-102)")
                Spacer(Modifier.height(8.dp))
                Row {
                    GokulaTextField(value = volume, onValueChange = { volume = it }, label = "Volume (ml)", modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    GokulaTextField(value = motility, onValueChange = { motility = it }, label = "Motility (%)", modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(date, batch, volume, motility) }) {
                Text("Save", color = MeadowGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = WarmGray) }
        },
        containerColor = Color.White
    )
}
