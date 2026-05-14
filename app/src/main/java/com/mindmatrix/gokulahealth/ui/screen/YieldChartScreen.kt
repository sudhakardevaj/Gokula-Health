package com.mindmatrix.gokulahealth.ui.screen

import android.graphics.Color
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.HeartRed
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.MilkViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YieldChartScreen(
    cattleId: Int,
    onNavigateBack: () -> Unit,
    milkViewModel: MilkViewModel = hiltViewModel(),
    cattleViewModel: CattleViewModel = hiltViewModel()
) {
    // ✅ FIXED: Track selected days for toggle buttons
    var selectedDays by remember { mutableStateOf(30) }

    LaunchedEffect(cattleId) {
        cattleViewModel.selectCattle(cattleId)
        milkViewModel.fetchLast30Days(cattleId)
        milkViewModel.checkYieldTrend(cattleId)
    }

    // ✅ FIXED: Reload data when toggle changes
    LaunchedEffect(selectedDays) {
        milkViewModel.fetchLogsForDays(cattleId, selectedDays)
    }

    val cattle by cattleViewModel.selectedCattle.collectAsState()

    // ✅ FIXED: Uses filteredLogs based on selected toggle!
    // Was always showing last30DaysLogs regardless of toggle!
    val filteredLogs by milkViewModel.filteredLogs.collectAsState()
    val monthlyAverage by milkViewModel.monthlyAverage.collectAsState()
    val isYieldDeclining by milkViewModel.isYieldDeclining.collectAsState()

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.White,
        topBar = {
            TopAppBar(
                title = {
                    // ✅ FIXED: Shows REAL cattle name! Was hardcoded "Gauri" before!
                    Text(
                        "Yield — ${cattle?.name ?: "Loading..."}",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Toggle Buttons ───────────────────────────────────────
            // ✅ FIXED: Buttons NOW WORK and filter real data!
            // Was completely non-functional before - onClick was empty!
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightMeadow)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ChartToggleButton(
                    label = "7 Days",
                    selected = selectedDays == 7,
                    onClick = { selectedDays = 7 }, // ✅ FIXED!
                    modifier = Modifier.weight(1f)
                )
                ChartToggleButton(
                    label = "30 Days",
                    selected = selectedDays == 30,
                    onClick = { selectedDays = 30 }, // ✅ FIXED!
                    modifier = Modifier.weight(1f)
                )
                ChartToggleButton(
                    label = "90 Days",
                    selected = selectedDays == 90,
                    onClick = { selectedDays = 90 }, // ✅ FIXED!
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Average Yield ────────────────────────────────────────
            // ✅ FIXED: Shows REAL calculated average from DB!
            // Was ALWAYS hardcoded "10.8 L/day" before!
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Average Yield (${selectedDays}d)",
                    color = WarmGray,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    // ✅ FIXED: Real average from DB!
                    String.format(
                        Locale.getDefault(),
                        "%.1f L/day",
                        monthlyAverage
                    ),
                    color = MeadowGreen,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black
                )
                // ✅ NEW: Show records count for transparency
                Text(
                    "${filteredLogs.size} records",
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Chart ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightMeadow.copy(alpha = 0.5f))
            ) {
                if (filteredLogs.size < 2) {
                    // ✅ FIXED: Better empty state!
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📊", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Not enough data",
                            color = WarmGray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Log milk for ${selectedDays} days to see trend",
                            color = MeadowGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // ✅ FIXED: Real chart with filteredLogs!
                    RealChart(filteredLogs)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Yield Decline Alert ──────────────────────────────────
            // ✅ NEW: Shows ONLY when yield is ACTUALLY declining!
            if (isYieldDeclining) {
                SoftCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .background(HeartRed.copy(alpha = 0.05f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = HeartRed,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "⚠️ Yield Declining!",
                                fontWeight = FontWeight.Bold,
                                color = HeartRed
                            )
                            Text(
                                "3+ consecutive days of drop detected.\nConsult veterinarian immediately.",
                                color = EarthBrown,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Insight Card ─────────────────────────────────────────
            // ✅ FIXED: Dynamic insight based on real data!
            SoftCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MeadowGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Insight",
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                        Text(
                            // ✅ FIXED: Real insight based on actual data!
                            // Was always hardcoded "Good! Keep maintaining..." before!
                            when {
                                filteredLogs.isEmpty() ->
                                    "Start logging milk to see insights here."
                                isYieldDeclining ->
                                    "⚠️ Yield dropping. Consider consulting a vet."
                                monthlyAverage > 15f ->
                                    "🌟 Excellent! Above average production."
                                monthlyAverage > 10f ->
                                    "✅ Good! Maintain feeding schedule."
                                monthlyAverage > 5f ->
                                    "📈 Average yield. Improve feed quality."
                                else ->
                                    "📉 Low yield. Vet consultation recommended."
                            },
                            color = WarmGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Stats Summary ────────────────────────────────────────
            // ✅ NEW: Real stats from actual DB data!
            if (filteredLogs.isNotEmpty()) {
                SoftCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Quick Stats",
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem(
                                label = "Highest",
                                value = String.format(
                                    Locale.getDefault(),
                                    "%.1f L",
                                    filteredLogs.maxOf { it.totalLitres }
                                ),
                                color = MeadowGreen
                            )
                            StatItem(
                                label = "Lowest",
                                value = String.format(
                                    Locale.getDefault(),
                                    "%.1f L",
                                    filteredLogs.minOf { it.totalLitres }
                                ),
                                color = HeartRed
                            )
                            StatItem(
                                label = "Days Logged",
                                value = "${filteredLogs.size}",
                                color = EarthBrown
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ✅ FIXED: Added onClick parameter - was ignoring all clicks before!
@Composable
fun ChartToggleButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit, // ✅ FIXED: Added!
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        onClick = onClick, // ✅ FIXED: Actually responds to clicks!
        color = if (selected)
            androidx.compose.ui.graphics.Color.White
        else
            androidx.compose.ui.graphics.Color.Transparent,
        tonalElevation = if (selected) 2.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
        ) {
            Text(
                label,
                color = if (selected) MeadowGreen else WarmGray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}

// ✅ PRESERVED + IMPROVED: Real chart rendering
@Composable
fun RealChart(logs: List<MilkLog>) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = Color.LTGRAY
                axisLeft.axisMinimum = 0f
                axisRight.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                animateX(500)
            }
        },
        update = { chart ->
            // ✅ FIXED: Oldest on left, newest on right
            val entries = logs.asReversed().mapIndexed { index, log ->
                Entry(index.toFloat(), log.totalLitres)
            }
            val dataSet = LineDataSet(entries, "Milk Yield").apply {
                color = MeadowGreen.toArgb()
                setCircleColor(MeadowGreen.toArgb())
                lineWidth = 3f
                circleRadius = 4f
                setDrawCircleHole(false)
                setDrawFilled(true)
                fillColor = MeadowGreen.toArgb()
                fillAlpha = 30
                mode = LineDataSet.Mode.CUBIC_BEZIER
                valueTextSize = 9f
                setDrawValues(logs.size <= 10) // Show values only if small dataset
            }
            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}

// ✅ NEW: StatItem composable for Quick Stats section
@Composable
fun StatItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = WarmGray
        )
    }
}