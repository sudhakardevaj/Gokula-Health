package com.mindmatrix.gokulahealth.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.ui.component.GokulaTextField
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.MilkViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogMilkScreen(
    cattleId: Int,
    onNavigateBack: () -> Unit,
    viewModel: MilkViewModel = hiltViewModel(),
    cattleViewModel: CattleViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(cattleId) {
        if (cattleId != -1) {
            cattleViewModel.selectCattle(cattleId)
            viewModel.fetchTodayLog(cattleId)
        }
    }

    val cattle by cattleViewModel.selectedCattle.collectAsState()
    val todayLog by viewModel.todayLog.collectAsState()

    var morningLitres by remember { mutableStateOf("") }
    var eveningLitres by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // ✅ FIXED: Date is now mutable so it can be changed!
    // Was a fixed remember{} before - could never change!
    var selectedDisplayDate by remember {
        mutableStateOf(
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
    }
    var selectedSaveDate by remember {
        mutableStateOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
    }

    LaunchedEffect(todayLog) {
        todayLog?.let { log ->
            morningLitres = log.morningLitres.toString()
            eveningLitres = log.eveningLitres.toString()
        }
    }

    val total = (morningLitres.toFloatOrNull() ?: 0f) +
            (eveningLitres.toFloatOrNull() ?: 0f)

    // ✅ FIXED: DatePickerDialog setup!
    // Calendar icon NOW opens a real date picker!
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            // Update both display and save dates when user picks
            val picked = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            selectedDisplayDate = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(picked.time)
            selectedSaveDate = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(picked.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        // ✅ Don't allow future dates!
        datePicker.maxDate = System.currentTimeMillis()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocalDrink,
                            contentDescription = null,
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            if (todayLog != null) "Edit Milk Log" else "Add Milk Log",
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
                            morningLitres.isBlank() && eveningLitres.isBlank() -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Please enter at least morning or evening milk!"
                                    )
                                }
                            }
                            else -> {
                                val morning = morningLitres.toFloatOrNull() ?: 0f
                                val evening = eveningLitres.toFloatOrNull() ?: 0f
                                viewModel.logMilk(
                                    MilkLog(
                                        id = todayLog?.id ?: 0,
                                        cattleId = cattleId,
                                        date = selectedSaveDate, // ✅ Uses selected date!
                                        morningLitres = morning,
                                        eveningLitres = evening,
                                        totalLitres = morning + evening
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
            // ✅ FIXED: Date field now has working calendar icon!
            GokulaTextField(
                value = selectedDisplayDate,
                onValueChange = { selectedDisplayDate = it },
                label = "Date (tap 📅 to pick)",
                trailingIcon = {
                    IconButton(
                        onClick = {
                            // ✅ FIXED: Actually opens DatePickerDialog!
                            datePickerDialog.show()
                        }
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pick Date",
                            tint = MeadowGreen // ✅ Green to show it's clickable!
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GokulaTextField(
                value = cattle?.let {
                    "${it.name} (${it.earTagId})"
                } ?: "Loading...",
                onValueChange = {},
                label = "Cattle"
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (todayLog != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MeadowGreen.copy(alpha = 0.1f))
                        .padding(12.dp)
                ) {
                    Text(
                        "📝 Today's log exists. You are editing it.",
                        color = MeadowGreen,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                GokulaTextField(
                    value = morningLitres,
                    onValueChange = {
                        if (it.isEmpty() || it.toFloatOrNull() != null) {
                            morningLitres = it
                        }
                    },
                    label = "Morning Milk (L)",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300)
                        )
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                GokulaTextField(
                    value = eveningLitres,
                    onValueChange = {
                        if (it.isEmpty() || it.toFloatOrNull() != null) {
                            eveningLitres = it
                        }
                    },
                    label = "Evening Milk (L)",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    trailingIcon = {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF5C6BC0)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightMeadow)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        "Total Milk",
                        style = MaterialTheme.typography.labelMedium,
                        color = MeadowGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f L", total),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        color = MeadowGreen
                    )
                    if (total == 0f) {
                        Text(
                            "Enter morning and evening values above",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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