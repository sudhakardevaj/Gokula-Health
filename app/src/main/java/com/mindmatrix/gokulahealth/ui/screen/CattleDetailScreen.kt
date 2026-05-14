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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mindmatrix.gokulahealth.ui.component.CowCommandButton
import com.mindmatrix.gokulahealth.ui.component.GokulaIcons
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.HeartRed
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.MilkViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CattleDetailScreen(
    cattleId: Int,
    onLogMilk: () -> Unit,
    onViewYieldChart: () -> Unit,
    onViewVaccinations: () -> Unit,
    onViewHealthNotes: (String) -> Unit,
    onEditProfile: () -> Unit,
    onNavigateToBreeding: () -> Unit,
    onNavigateToSemenLog: () -> Unit, // ✅ NEW
    onNavigateToGenAI: () -> Unit,
    onNavigateBack: () -> Unit,
    cattleViewModel: CattleViewModel = hiltViewModel(),
    milkViewModel: MilkViewModel = hiltViewModel() // ✅ NEW: for real today's summary
) {
    // ✅ PRESERVED - was working correctly
    LaunchedEffect(cattleId) {
        cattleViewModel.selectCattle(cattleId)
        milkViewModel.fetchTodayLog(cattleId)      // ✅ NEW: fetch real today's data
        milkViewModel.checkYieldTrend(cattleId)    // ✅ NEW: check if yield is declining
    }

    val cattle by cattleViewModel.selectedCattle.collectAsState()

    // ✅ NEW: Real data from DB instead of hardcoded values!
    val todayLog by milkViewModel.todayLog.collectAsState()
    val isYieldDeclining by milkViewModel.isYieldDeclining.collectAsState()

    Scaffold(
        containerColor = Color.White,
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
                        Text(
                            "Cattle Details",
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
                    IconButton(onClick = onEditProfile) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
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
                .verticalScroll(rememberScrollState())
        ) {

            // ── Profile Card ─────────────────────────────────────────
            SoftCard(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = cattle?.photoUri,
                        contentDescription = cattle?.name,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(LightMeadow),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            // ✅ FIXED: Shows real name from DB, fallback to Loading
                            text = cattle?.name ?: "Loading...",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = EarthBrown
                        )
                        Text(
                            "Ear Tag: ${cattle?.earTagId ?: "-"}",
                            color = WarmGray
                        )
                        Text(
                            "Breed: ${cattle?.breed ?: "-"}",
                            color = WarmGray
                        )
                        Text(
                            "Age: ${cattle?.age ?: "-"} Years",
                            color = WarmGray
                        )
                        // ✅ FIXED: Shows real gender from DB!
                        // Was hardcoded as "Female" before!
                        Text(
                            "Gender: ${cattle?.gender ?: "-"}",
                            color = WarmGray
                        )
                    }
                }
            }

            // ── GenAI Alert Banner ───────────────────────────────────
            // ✅ NEW: Shows ONLY when yield is actually declining!
            // Was never shown before - now connected to real data!
            if (isYieldDeclining) {
                SoftCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HeartRed.copy(alpha = 0.05f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = HeartRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Yield Declining!",
                                color = HeartRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "3+ days of drop detected. Tap AI Suggest below.",
                                color = EarthBrown,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // ── Command Grid ─────────────────────────────────────────
            // ✅ PRESERVED - was working correctly
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // ONLY show Milk and Yield buttons if it's a Female
                if (cattle?.gender == "Female") {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CowCommandButton(
                            "Milk Log", "🥛", Color(0xFF2196F3), onLogMilk, Modifier.weight(1f)
                        )
                        CowCommandButton(
                            "Yield Graph", "📈", MeadowGreen, onViewYieldChart, Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CowCommandButton(
                        "Vaccination", "💉",
                        Color(0xFF9C27B0),
                        onViewVaccinations,
                        Modifier.weight(1f)
                    )
                    CowCommandButton(
                        "Health Notes", "📝",
                        Color(0xFFFF9800),
                        { onViewHealthNotes(cattle?.name ?: "") },
                        Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // ✅ Show Breeding for Females, Semen Log for Males
                    if (cattle?.gender == "Female") {
                        CowCommandButton(
                            "Breeding", "🐄", Color(0xFFE91E63), onNavigateToBreeding, Modifier.weight(1f)
                        )
                    } else {
                        CowCommandButton(
                            "Semen Log", "🧬", Color(0xFF2196F3), onNavigateToSemenLog, Modifier.weight(1f)
                        )
                    }

                    // AI Suggest button - shared half width
                    CowCommandButton(
                        label = "AI Suggest",
                        icon = "🤖",
                        color = HeartRed,
                        onClick = onNavigateToGenAI,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (cattle?.gender == "Female") {
                Spacer(modifier = Modifier.height(24.dp))

                // ── Today's Summary ──────────────────────────────────────
                Text(
                    "Today's Summary",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown
                )

                SoftCard(modifier = Modifier.padding(16.dp)) {
                    // ✅ FIXED: Shows REAL data from DB!
                    // Was hardcoded as "6.0 L", "6.5 L", "12.5 L" before!
                    if (todayLog != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryItem(
                                "Morning",
                                "${todayLog!!.morningLitres} L"
                            )
                            Text("+", fontWeight = FontWeight.Bold, color = WarmGray)
                            SummaryItem(
                                "Evening",
                                "${todayLog!!.eveningLitres} L"
                            )
                            Text("=", fontWeight = FontWeight.Bold, color = WarmGray)
                            SummaryItem(
                                "Total",
                                "${todayLog!!.totalLitres} L",
                                MeadowGreen
                            )
                        }
                    } else {
                        // ✅ NEW: Shows proper empty state when no log exists today!
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🥛", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No milk logged today",
                                color = WarmGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Tap 'Milk Log' to add today's entry",
                                color = MeadowGreen,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── Notes Section ────────────────────────────────────────
            // ✅ NEW: Shows cattle notes if available
            if (!cattle?.notes.isNullOrBlank()) {
                Text(
                    "Notes",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown
                )
                SoftCard(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = cattle?.notes ?: "",
                        modifier = Modifier.padding(16.dp),
                        color = WarmGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ✅ PRESERVED - was working correctly
@Composable
fun SummaryItem(
    label: String,
    value: String,
    color: Color = EarthBrown
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = WarmGray
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}