package com.mindmatrix.gokulahealth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.mindmatrix.gokulahealth.ui.theme.HeartRed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.VaccinationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(
    cattleId: Int,
    onAddVaccination: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: VaccinationViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "History")

    // ✅ FIXED: Fetch real vaccinations for this specific cattle!
    LaunchedEffect(cattleId) {
        viewModel.fetchVaccinations(cattleId)
    }

    val vaccinations by viewModel.vaccinations.collectAsState()

    // ✅ FIXED: Filter vaccinations by tab properly!
    // Was showing all in Upcoming tab and nothing in History!
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    val upcomingList = vaccinations.filter { vaccination ->
        try {
            val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = try {
                sdf1.parse(vaccination.nextDueDate)
            } catch (e: Exception) {
                sdf2.parse(vaccination.nextDueDate)
            }
            val dateStr = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(date ?: Date())
            dateStr >= today
        } catch (e: Exception) {
            true
        }
    }

    val historyList = vaccinations.filter { vaccination ->
        try {
            val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = try {
                sdf1.parse(vaccination.nextDueDate)
            } catch (e: Exception) {
                sdf2.parse(vaccination.nextDueDate)
            }
            val dateStr = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(date ?: Date())
            dateStr < today
        } catch (e: Exception) {
            false
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Vaccination",
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
                    // ✅ PRESERVED: Add button navigates correctly
                    IconButton(onClick = onAddVaccination) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Vaccination",
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
        ) {
            // ✅ FIXED: Tabs now properly filter data!
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = MeadowGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MeadowGreen
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal,
                                color = if (selectedTab == index)
                                    MeadowGreen
                                else
                                    WarmGray
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightMeadow)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedTab) {

                    // ── UPCOMING TAB ──────────────────────────────────
                    0 -> {
                        item {
                            Text(
                                "Upcoming Vaccinations",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EarthBrown
                            )
                        }

                        if (upcomingList.isEmpty()) {
                            item {
                                // ✅ FIXED: Real empty state!
                                // No more fake "FMD Vaccine", "Gauri (IND1234)"!
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("💉", fontSize = 56.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "No upcoming vaccinations!",
                                        color = WarmGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Tap + above to add one",
                                        color = MeadowGreen,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // ✅ FIXED: Shows REAL vaccination data from DB!
                            items(upcomingList, key = { it.id }) { vaccination ->
                                CattleVaccinationCard(
                                    vaccination = vaccination,
                                    isUpcoming = true,
                                    onDelete = {
                                        viewModel.deleteVaccination(vaccination) // ✅ Wire delete!
                                    }
                                )
                            }
                        }
                        // Reminder card
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SoftCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = MeadowGreen,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Reminders are ON",
                                            fontWeight = FontWeight.Bold,
                                            color = EarthBrown
                                        )
                                        Text(
                                            "You will be notified at 8 AM on the due date.",
                                            color = WarmGray,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ── HISTORY TAB ───────────────────────────────────
                    1 -> {
                        item {
                            Text(
                                "Vaccination History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = EarthBrown
                            )
                        }

                        if (historyList.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("📋", fontSize = 56.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "No history yet!",
                                        color = WarmGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Past vaccinations will appear here.",
                                        color = MeadowGreen,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // ✅ FIXED: Shows REAL past vaccination data!
                            items(historyList, key = { it.id }) { vaccination ->
                                CattleVaccinationCard(
                                    vaccination = vaccination,
                                    isUpcoming = false,
                                    onDelete = {
                                        viewModel.deleteVaccination(vaccination) // ✅ Wire delete!
                                    }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ✅ NEW: Smart card for per-cattle vaccination screen
@Composable
fun CattleVaccinationCard(
    vaccination: com.mindmatrix.gokulahealth.data.local.entity.Vaccination,
    isUpcoming: Boolean,
    onDelete: (() -> Unit)? = null  // ✅ NEW: delete callback
) {
    val cardColor = if (isUpcoming) Color(0xFF5C6BC0) else WarmGray

    // ✅ NEW: Confirm delete dialog state
    var showDeleteDialog by remember { mutableStateOf(false) }

    // ✅ NEW: Confirm delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗑️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Delete Vaccination?",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                }
            },
            text = {
                Text(
                    "Are you sure you want to delete\n\"${vaccination.vaccineName}\"?\nThis cannot be undone.",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete?.invoke()
                        showDeleteDialog = false
                    }
                ) {
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(cardColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = vaccination.vaccineName.take(2).uppercase(),
                    color = cardColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = vaccination.vaccineName,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = if (isUpcoming)
                        "Due: ${vaccination.nextDueDate}"
                    else
                        "Given: ${vaccination.dateGiven}",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
                if (vaccination.notes.isNotBlank()) {
                    Text(
                        text = vaccination.notes,
                        color = MeadowGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ✅ NEW: Delete icon button
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Vaccination",
                    tint = HeartRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Status badge
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(cardColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isUpcoming) "Upcoming" else "Done ✓",
                    color = cardColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
