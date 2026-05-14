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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.ui.draw.clip
import com.mindmatrix.gokulahealth.data.local.VaccinationScheduleLibrary
import com.mindmatrix.gokulahealth.data.local.VaccineScheduleTemplate
import com.mindmatrix.gokulahealth.data.local.TemplatePriority
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen

// ✅ NEW SCREEN - Replaces broken VaccinationScreen(cattleId=-1)
// Shows ALL upcoming + history vaccinations across ALL cattle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllVaccinationsScreen(
    onNavigateToAddCattle: () -> Unit,
    viewModel: VaccinationViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Upcoming", "History")

    // ✅ FIXED: Fetches ALL upcoming and ALL past vaccinations!
    LaunchedEffect(Unit) {
        viewModel.fetchAllUpcoming()
        viewModel.fetchAllPast()
    }

    val upcomingVaccinations by viewModel.upcomingVaccinations.collectAsState()
    val pastVaccinations by viewModel.pastVaccinations.collectAsState()

    Scaffold(
        containerColor = Color.White,
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
                            "Vaccinations",
                            fontWeight = FontWeight.ExtraBold,
                            color = EarthBrown
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
            // ✅ FIXED: Tabs now ACTUALLY filter and show real data!
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

                        if (upcomingVaccinations.isEmpty()) {
                            item {
                                // ✅ NEW: Proper empty state
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
                                        "Go to a cattle profile and\nadd a vaccination record.",
                                        color = MeadowGreen,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // ✅ FIXED: Shows REAL upcoming vaccination data!
                            items(upcomingVaccinations, key = { it.id }) { vaccination ->
                                AllVaccinationCard(
                                    vaccination = vaccination,
                                    isUpcoming = true,
                                    onDelete = { viewModel.deleteVaccination(vaccination) }
                                )
                            }
                        }

                        // ✅ NEW: Reminder notice card
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

                        if (pastVaccinations.isEmpty()) {
                            item {
                                // ✅ NEW: Proper empty state for history
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("📋", fontSize = 56.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        "No vaccination history yet!",
                                        color = WarmGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Past vaccination records will appear here.",
                                        color = MeadowGreen,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            // ✅ FIXED: Shows REAL past vaccination data!
                            items(pastVaccinations, key = { it.id }) { vaccination ->
                                AllVaccinationCard(
                                    vaccination = vaccination,
                                    isUpcoming = false,
                                    onDelete = { viewModel.deleteVaccination(vaccination) }
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

// ✅ NEW: Smart vaccination card that works for both tabs
@Composable
fun AllVaccinationCard(
    vaccination: Vaccination,
    isUpcoming: Boolean,
    onDelete: (() -> Unit)? = null  // ✅ NEW
) {
    // Calculate days left or days since
    val daysText = calculateDaysText(vaccination.nextDueDate, isUpcoming)
    val cardColor = if (isUpcoming) Color(0xFF5C6BC0) else WarmGray

    // ✅ NEW: Confirm dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                    "Delete \"${vaccination.vaccineName}\"?\nThis cannot be undone.",
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
                    Text("Delete", color = HeartRed, fontWeight = FontWeight.Bold)
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
            // ✅ Circle avatar with vaccine initials
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
                    text = if (vaccination.notes.isNotBlank())
                        "Note: ${vaccination.notes}"
                    else
                        "Cattle #${vaccination.cattleId}",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
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

            // ✅ NEW: Delete button
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = HeartRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // ✅ Days left / days ago indicator
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = daysText.first,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isUpcoming) cardColor else WarmGray
                )
                Text(
                    text = daysText.second,
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmGray
                )
            }
        }
    }
}

// ✅ Helper: Calculate days left or days ago
fun calculateDaysText(dateStr: String, isUpcoming: Boolean): Pair<String, String> {
    return try {
        val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = try { sdf1.parse(dateStr) } catch (e: Exception) {
            try { sdf2.parse(dateStr) } catch (e2: Exception) { null }
        }
        if (date != null) {
            val diff = date.time - Date().time
            val days = TimeUnit.MILLISECONDS.toDays(Math.abs(diff)).toInt()
            if (isUpcoming) {
                Pair(days.toString(), "days left")
            } else {
                Pair(days.toString(), "days ago")
            }
        } else {
            Pair("-", "days")
        }
    } catch (e: Exception) {
        Pair("-", "days")
    }
}