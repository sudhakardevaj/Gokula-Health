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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.ui.component.CowCard
import com.mindmatrix.gokulahealth.ui.component.GokulaIcons
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.HeartRed
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.BreedingViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.VaccinationViewModel
import com.mindmatrix.gokulahealth.util.BreedingCalculator
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddCattle: () -> Unit,
    onCattleClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: CattleViewModel = hiltViewModel(),
    vaccinationViewModel: VaccinationViewModel = hiltViewModel(),
    breedingViewModel: BreedingViewModel = hiltViewModel()
) {
    val cattleList by viewModel.allCattle.collectAsStateWithLifecycle()
    val todayLogsMap by viewModel.todayLogsMap.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vaccinationViewModel.fetchAllUpcoming()
        vaccinationViewModel.fetchDueWithin7Days()
        breedingViewModel.fetchUpcomingCalvings()
    }

    val upcomingVaccinations by vaccinationViewModel.upcomingVaccinations.collectAsStateWithLifecycle()
    val dueWithin7Days by vaccinationViewModel.dueWithin7Days.collectAsStateWithLifecycle()
    val upcomingCalvings by breedingViewModel.upcomingCalvings.collectAsStateWithLifecycle()

    var showNotificationsDialog by remember { mutableStateOf(false) }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MeadowGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Upcoming Vaccinations",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                }
            },
            text = {
                Column {
                    if (upcomingVaccinations.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("✅", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "All cattle vaccinations are up to date!",
                                color = MeadowGreen,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        Text(
                            "${upcomingVaccinations.size} vaccination(s) due soon:",
                            color = WarmGray,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        upcomingVaccinations.forEach { vaccination ->
                            NotificationVaccinationRow(vaccination)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text(
                        "Close",
                        color = MeadowGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            GokulaIcons.Cow,
                            contentDescription = null,
                            tint = MeadowGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "Gokula-Health",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = EarthBrown
                            )
                            Text(
                                "Digital Health Passport for Dairy Cattle",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmGray,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // ✅ NEW: Search icon opens GlobalSearchScreen
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MeadowGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(onClick = { showNotificationsDialog = true }) {
                        BadgedBox(
                            badge = {
                                if (dueWithin7Days.isNotEmpty()) {
                                    Badge(
                                        containerColor = HeartRed,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = if (dueWithin7Days.size > 9)
                                                "9+" else "${dueWithin7Days.size}",
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = if (dueWithin7Days.isNotEmpty()) HeartRed else MeadowGreen,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = EarthBrown
                )
            )
        },
        containerColor = LightMeadow
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "My Cattle",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                        Text(
                            "${cattleList.size} registered",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmGray
                        )
                    }
                    TextButton(onClick = onAddCattle) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MeadowGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Add Cattle",
                            fontWeight = FontWeight.Bold,
                            color = MeadowGreen
                        )
                    }
                }
            }

            if (upcomingCalvings.isNotEmpty()) {
                item {
                    val soonestCalving = upcomingCalvings.first()
                    val daysLeft = BreedingCalculator.daysUntilCalving(
                        soonestCalving.expectedCalvingDate
                    )

                    // ✅ Color changes based on urgency
                    val calvingColor = when {
                        daysLeft <= 7  -> Color(0xFFE91E63)  // Pink  — very soon!
                        daysLeft <= 14 -> Color(0xFFFF9800)  // Orange — 2 weeks
                        else           -> Color(0xFF9C27B0)  // Purple — within month
                    }

                    val calvingEmoji = when {
                        daysLeft <= 7  -> "🚨"
                        daysLeft <= 14 -> "⚠️"
                        else           -> "🍼"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(calvingColor.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(calvingEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${upcomingCalvings.size} cattle calving within 30 days!",
                                    color = calvingColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    when {
                                        daysLeft <= 0 -> "Calving expected TODAY!"
                                        daysLeft == 1 -> "Calving expected TOMORROW!"
                                        else          -> "Soonest calving in $daysLeft days"
                                    },
                                    color = WarmGray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Prepare dry feed & birthing area",
                                    color = calvingColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (dueWithin7Days.isNotEmpty()) {
                item {
                    val soonestDays = dueWithin7Days.firstOrNull()?.let { vaccination ->
                        try {
                            val sdf1 = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val date = try {
                                sdf1.parse(vaccination.nextDueDate)
                            } catch (e: Exception) {
                                try { sdf2.parse(vaccination.nextDueDate) }
                                catch (e2: Exception) { null }
                            }
                            if (date != null) {
                                java.util.concurrent.TimeUnit
                                    .MILLISECONDS
                                    .toDays(date.time - java.util.Date().time)
                                    .toInt()
                            } else 7
                        } catch (e: Exception) { 7 }
                    } ?: 7

                    val alertColor = when {
                        soonestDays <= 1 -> HeartRed
                        soonestDays <= 3 -> Color(0xFFFF9800)
                        else             -> Color(0xFFFFB300)
                    }

                    val alertEmoji = when {
                        soonestDays <= 1 -> "🚨"
                        soonestDays <= 3 -> "⚠️"
                        else             -> "💉"
                    }

                    val urgencyText = when {
                        soonestDays <= 0 -> "Due TODAY!"
                        soonestDays == 1 -> "Due TOMORROW!"
                        soonestDays <= 3 -> "Due in $soonestDays days!"
                        else             -> "Due within a week!"
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(alertColor.copy(alpha = 0.10f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(alertEmoji, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${dueWithin7Days.size} vaccination(s) $urgencyText",
                                    color = alertColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    dueWithin7Days.take(2)
                                        .joinToString(", ") { it.vaccineName }
                                        .let { names ->
                                            if (dueWithin7Days.size > 2)
                                                "$names +${dueWithin7Days.size - 2} more"
                                            else names
                                        },
                                    color = WarmGray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    "Tap 🔔 bell for full details",
                                    color = alertColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (cattleList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🐮", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No cattle registered yet.",
                            color = WarmGray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Tap + Add Cattle to get started!",
                            color = MeadowGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                items(cattleList) { cattle ->
                    CowCard(
                        cattle = cattle,
                        onClick = { onCattleClick(cattle.id) },
                        todayLog = todayLogsMap[cattle.id]
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun NotificationVaccinationRow(vaccination: Vaccination) {
    val displayDate = remember(vaccination.nextDueDate) {
        try {
            val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = dbFormat.parse(vaccination.nextDueDate)
            if (date != null) displayFormat.format(date) else vaccination.nextDueDate
        } catch (e: Exception) {
            vaccination.nextDueDate
        }
    }

    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💉", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    vaccination.vaccineName,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "Due: $displayDate",
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall
                )
                if (vaccination.notes.isNotBlank()) {
                    Text(
                        vaccination.notes,
                        color = MeadowGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "Tap bell for details",
                        color = MeadowGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
