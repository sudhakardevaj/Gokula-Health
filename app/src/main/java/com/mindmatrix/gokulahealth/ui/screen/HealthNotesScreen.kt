package com.mindmatrix.gokulahealth.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.gokulahealth.data.local.entity.HealthNote
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.component.gokulaTextFieldColors
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.HeartRed
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthNotesScreen(
    cattleId: Int,        // ✅ FIXED: Added cattleId for DB queries!
    cattleName: String,   // ✅ PRESERVED: for display in title
    onNavigateBack: () -> Unit,
    viewModel: CattleViewModel = hiltViewModel()
) {
    // ✅ FIXED: Fetch REAL notes from DB for this cattle!
    // Before this screen had ZERO DB connection - all notes were fake!
    LaunchedEffect(cattleId) {
        if (cattleId != -1) {
            viewModel.fetchHealthNotes(cattleId)
        }
    }

    val healthNotes by viewModel.healthNotes.collectAsState()

    // ✅ NEW: Dialog state for adding new notes
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        // ✅ FIXED: Shows real cattle name
                        "Health Notes — $cattleName",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
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
                    // ✅ FIXED: Add button now ACTUALLY opens a dialog!
                    // Was doing nothing before!
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Note",
                            tint = MeadowGreen
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ✅ FIXED: Shows REAL notes from DB!
            // Was always showing 3 fake hardcoded notes before!
            if (healthNotes.isEmpty()) {
                item {
                    // ✅ NEW: Proper empty state
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📝", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "No health notes yet!",
                            color = WarmGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap + to add first note",
                            color = MeadowGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // ✅ FIXED: Show REAL notes from DB!
                items(healthNotes) { note ->
                    RealHealthNoteCard(
                        note = note,
                        onDelete = {
                            viewModel.deleteHealthNote(note)
                        }
                    )
                }
            }

            // ✅ PRESERVED: Tip card - was good, kept it!
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SoftCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MeadowGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                "Tip",
                                fontWeight = FontWeight.Bold,
                                color = EarthBrown
                            )
                            Text(
                                "Keep regular notes to maintain good health records for your cattle.",
                                color = WarmGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // ✅ NEW: Add Note Dialog - was completely missing before!
    if (showAddDialog) {
        AddHealthNoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { noteContent ->
                if (noteContent.isNotBlank()) {
                    val today = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(Date())
                    viewModel.addHealthNote(
                        HealthNote(
                            cattleId = cattleId,
                            date = today,
                            content = noteContent
                        )
                    )
                }
                showAddDialog = false
            }
        )
    }
}

// ✅ NEW: Real note card with delete functionality!
// Replaces the fake HealthNoteCard that showed hardcoded data!
@Composable
fun RealHealthNoteCard(
    note: HealthNote,
    onDelete: () -> Unit
) {
    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MeadowGreen,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                // ✅ Shows REAL date from DB!
                Text(
                    note.date,
                    fontWeight = FontWeight.Bold,
                    color = WarmGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                // ✅ Shows REAL content from DB!
                Text(
                    note.content,
                    color = EarthBrown,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // ✅ NEW: Delete button for each note!
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Note",
                    tint = HeartRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ✅ NEW: Dialog to add a health note
// The Add button was doing NOTHING before! Now it opens this!
@Composable
fun AddHealthNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add Health Note",
                fontWeight = FontWeight.Bold,
                color = EarthBrown
            )
        },
        text = {
            Column {
                Text(
                    "Describe the health observation:",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = {
                        Text(
                            "e.g. Slight fever observed, called vet...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    colors = gokulaTextFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(noteText) },
                enabled = noteText.isNotBlank()
            ) {
                Text(
                    "Save Note",
                    color = MeadowGreen,
                    fontWeight = FontWeight.Bold
                )
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