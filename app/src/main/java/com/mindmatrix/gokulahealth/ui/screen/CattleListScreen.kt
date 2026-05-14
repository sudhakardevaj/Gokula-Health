package com.mindmatrix.gokulahealth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mindmatrix.gokulahealth.ui.component.CowCard
import com.mindmatrix.gokulahealth.ui.component.GokulaIcons
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CattleListScreen(
    onCattleClick: (Int) -> Unit,
    onAddCattle: () -> Unit,
    onSearchClick: () -> Unit, // ✅ NEW
    viewModel: CattleViewModel = hiltViewModel()
) {
    val cattleList by viewModel.allCattle.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    // ✅ FIXED: Menu icon now shows app info dialog!
    var showAboutDialog by remember { mutableStateOf(false) }

    val filteredCattle = cattleList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.earTagId.contains(searchQuery, ignoreCase = true)
    }

    // ✅ FIXED: About Dialog for Menu button
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
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
                        "Gokula-Health",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                }
            },
            text = {
                Column {
                    Text(
                        "Digital Health Passport for Dairy Cattle",
                        fontWeight = FontWeight.Bold,
                        color = MeadowGreen,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AppInfoRow("📌 Project", "Gokula-Health #98")
                    AppInfoRow("🏫 By", "MindMatrix")
                    AppInfoRow("📱 Platform", "Android - Jetpack Compose")
                    AppInfoRow("🗄️ Storage", "Room DB (Offline First)")
                    AppInfoRow("🤖 AI", "GenAI Health Suggestions")
                    AppInfoRow("💉 Alerts", "AlarmManager Reminders")
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                LightMeadow,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            "🌾 White Revolution 2.0 — " +
                                    "Bringing precision technology " +
                                    "to animal husbandry.",
                            color = MeadowGreen,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
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
                    Text(
                        "All Cattle",
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                },
                navigationIcon = {
                    // ✅ FIXED: Menu ≡ now opens About Dialog!
                    // Was onClick = {} before - did NOTHING!
                    IconButton(onClick = { showAboutDialog = true }) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            // ✅ Draw 3 lines manually for hamburger icon
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .height(2.dp)
                                        .background(
                                            MeadowGreen,
                                            RoundedCornerShape(1.dp)
                                        )
                                )
                            }
                        }
                    }
                },
                actions = {
                    // ✅ FIXED: TopBar + now ALSO navigates to Add Cattle!
                    // Was onClick = {} before - did NOTHING!
                    // Now matches the FAB behavior!
                    IconButton(onClick = onAddCattle) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Cattle",
                            tint = MeadowGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = LightMeadow,
        floatingActionButton = {
            // ✅ PRESERVED: FAB also adds cattle
            FloatingActionButton(
                onClick = onAddCattle,
                containerColor = MeadowGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Cattle")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { onSearchClick() }, // ✅ NEW: Jump to global search
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchClick() }, // ✅ Jump to global search
                readOnly = true, // ✅ Read-only here, search happens in GlobalSearchScreen
                placeholder = { Text("Search by name, tag, breed...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = WarmGray
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MeadowGreen
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ✅ Show cattle count
            Text(
                "${filteredCattle.size} cattle registered",
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredCattle.isEmpty() && searchQuery.isNotBlank()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No cattle found for \"$searchQuery\"",
                                color = WarmGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else if (cattleList.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🐮", fontSize = 56.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No cattle registered yet.",
                                color = WarmGray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Tap + to add your first one!",
                                color = MeadowGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    items(filteredCattle) { cattle ->
                        CowCard(
                            cattle = cattle,
                            onClick = { onCattleClick(cattle.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ✅ NEW: Helper composable for About dialog rows
@Composable
fun AppInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = WarmGray,
            modifier = Modifier.width(100.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = EarthBrown
        )
    }
}