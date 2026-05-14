package com.mindmatrix.gokulahealth.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.ui.component.SoftCard
import com.mindmatrix.gokulahealth.ui.theme.*
import com.mindmatrix.gokulahealth.ui.viewmodel.SearchViewModel
import com.mindmatrix.gokulahealth.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    onCattleClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery        by viewModel.searchQuery.collectAsState()
    val searchResults      by viewModel.searchResults.collectAsState()
    val selectedBreed      by viewModel.selectedBreedFilter.collectAsState()
    val selectedGender     by viewModel.selectedGenderFilter.collectAsState()
    val selectedSort       by viewModel.selectedSortOption.collectAsState()
    val activeFilterCount  by viewModel.activeFilterCount.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val focusRequester  = remember { FocusRequester() }

    // Auto-focus search bar when screen opens
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        placeholder = {
                            Text(
                                "Search cattle, breed, vaccine...",
                                color = WarmGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, null, tint = MeadowGreen)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Close, null, tint = WarmGray)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor    = MeadowGreen,
                            unfocusedBorderColor  = LightMeadow,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
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
                    // Filter button with badge
                    BadgedBox(
                        badge = {
                            if (activeFilterCount > 0) {
                                Badge(containerColor = HeartRed) {
                                    Text("$activeFilterCount", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                Icons.Default.FilterList,
                                "Filter",
                                tint = if (activeFilterCount > 0) HeartRed else MeadowGreen
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = LightMeadow
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // ── Active Filter Chips ───────────────────────────
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    // Sort chip
                    item {
                        FilterChip(
                            selected = true,
                            onClick = { showFilterSheet = true },
                            label = {
                                Text(
                                    selectedSort.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Sort,
                                    null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MeadowGreen.copy(alpha = 0.12f),
                                selectedLabelColor = MeadowGreen
                            )
                        )
                    }

                    // Active breed chip
                    selectedBreed?.let { breed ->
                        item {
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.setBreedFilter(null) },
                                label = {
                                    Text(
                                        breed,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF9C27B0)
                                        .copy(alpha = 0.12f),
                                    selectedLabelColor = Color(0xFF9C27B0)
                                )
                            )
                        }
                    }

                    // Active gender chip
                    selectedGender?.let { gender ->
                        item {
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.setGenderFilter(null) },
                                label = {
                                    Text(
                                        gender,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF2196F3)
                                        .copy(alpha = 0.12f),
                                    selectedLabelColor = Color(0xFF2196F3)
                                )
                            )
                        }
                    }
                }
            }

            // ── Results count ─────────────────────────────────
            item {
                Text(
                    "${searchResults.totalCount} results",
                    color = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // ── Cattle Results ────────────────────────────────
            if (searchResults.matchedCattle.isNotEmpty()) {
                item {
                    SearchSectionHeader(
                        title = "🐄 Cattle",
                        count = searchResults.matchedCattle.size
                    )
                }
                items(
                    searchResults.matchedCattle,
                    key = { it.id }
                ) { cattle ->
                    SearchCattleCard(
                        cattle = cattle,
                        query  = searchQuery,
                        onClick = { onCattleClick(cattle.id) }
                    )
                }
            }

            // ── Vaccination Results ───────────────────────────
            if (searchResults.matchedVaccinations.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(4.dp))
                    SearchSectionHeader(
                        title = "💉 Vaccinations",
                        count = searchResults.matchedVaccinations.size
                    )
                }
                items(
                    searchResults.matchedVaccinations,
                    key = { it.id }
                ) { vacc ->
                    SearchVaccinationCard(
                        vaccination = vacc,
                        query = searchQuery
                    )
                }
            }

            // ── No Results ────────────────────────────────────
            if (searchResults.totalCount == 0 && searchQuery.isNotBlank()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 56.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No results for \"$searchQuery\"",
                            color = WarmGray,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Try different keywords\nor clear filters",
                            color = MeadowGreen,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ── Empty State (no query + no filter) ───────────
            if (searchQuery.isBlank() && activeFilterCount == 0) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Search your farm",
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Find cattle by name, tag, breed\nor search vaccination records",
                            color = WarmGray,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    // ── Filter Bottom Sheet ────────────────────────────────────
    if (showFilterSheet) {
        SearchFilterSheet(
            selectedBreed = selectedBreed,
            selectedGender = selectedGender,
            selectedSort = selectedSort,
            onBreedSelected = { viewModel.setBreedFilter(it) },
            onGenderSelected = { viewModel.setGenderFilter(it) },
            onSortSelected = { viewModel.setSortOption(it) },
            onClearAll = { viewModel.clearAllFilters() },
            onDismiss = { showFilterSheet = false }
        )
    }
}

// ── Section Header ─────────────────────────────────────────────
@Composable
fun SearchSectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = EarthBrown,
            style = MaterialTheme.typography.titleSmall
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MeadowGreen.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                "$count",
                color = MeadowGreen,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// ── Highlighted Text ───────────────────────────────────────────
@Composable
fun HighlightedText(
    text: String,
    query: String,
    baseColor: Color = EarthBrown,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight = FontWeight.Bold
) {
    if (query.isBlank()) {
        Text(text, color = baseColor, style = style, fontWeight = fontWeight)
        return
    }

    val annotatedString = buildAnnotatedString {
        val lowerText  = text.lowercase()
        val lowerQuery = query.lowercase()
        var startIndex = 0

        while (startIndex < text.length) {
            val matchIndex = lowerText.indexOf(lowerQuery, startIndex)
            if (matchIndex == -1) {
                withStyle(SpanStyle(color = baseColor, fontWeight = fontWeight)) {
                    append(text.substring(startIndex))
                }
                break
            }
            if (matchIndex > startIndex) {
                withStyle(SpanStyle(color = baseColor, fontWeight = fontWeight)) {
                    append(text.substring(startIndex, matchIndex))
                }
            }
            withStyle(
                SpanStyle(
                    color = EarthBrown,
                    fontWeight = FontWeight.Black,
                    background = Color(0xFFFFEB3B)
                )
            ) {
                append(text.substring(matchIndex, matchIndex + query.length))
            }
            startIndex = matchIndex + query.length
        }
    }
    Text(text = annotatedString, style = style)
}

// ── Search Cattle Card ─────────────────────────────────────────
@Composable
fun SearchCattleCard(
    cattle: Cattle,
    query: String,
    onClick: () -> Unit
) {
    SoftCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LightMeadow),
                contentAlignment = Alignment.Center
            ) {
                if (cattle.photoUri.isNotBlank()) {
                    AsyncImage(
                        model = cattle.photoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        cattle.name.take(1).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = MeadowGreen,
                        fontSize = 22.sp
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                HighlightedText(
                    text = cattle.name,
                    query = query,
                    style = MaterialTheme.typography.titleSmall
                )
                HighlightedText(
                    text = "Tag: ${cattle.earTagId}",
                    query = query,
                    baseColor = WarmGray,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal
                )
                HighlightedText(
                    text = "${cattle.breed} • ${cattle.age}yr • ${cattle.gender}",
                    query = query,
                    baseColor = WarmGray,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Normal
                )
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MeadowGreen,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Search Vaccination Card ────────────────────────────────────
@Composable
fun SearchVaccinationCard(
    vaccination: Vaccination,
    query: String
) {
    SoftCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF9C27B0).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    vaccination.vaccineName.take(2).uppercase(),
                    color = Color(0xFF9C27B0),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                HighlightedText(
                    text = vaccination.vaccineName,
                    query = query,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Due: ${vaccination.nextDueDate}",
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
                if (vaccination.notes.isNotBlank()) {
                    HighlightedText(
                        text = vaccination.notes,
                        query = query,
                        baseColor = MeadowGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightMeadow)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    "#${vaccination.cattleId}",
                    color = MeadowGreen,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Filter Bottom Sheet ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterSheet(
    selectedBreed: String?,
    selectedGender: String?,
    selectedSort: SortOption,
    onBreedSelected: (String?) -> Unit,
    onGenderSelected: (String?) -> Unit,
    onSortSelected: (SortOption) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val breeds = listOf("HF Jersey", "Gir", "Sahiwal", "Murrah", "Other")
    val genders = listOf("Female", "Male")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🔧 Filter & Sort",
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown,
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onClearAll) {
                    Text(
                        "Clear All",
                        color = HeartRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = LightMeadow)
            Spacer(Modifier.height(16.dp))

            Text(
                "Sort By",
                fontWeight = FontWeight.Bold,
                color = EarthBrown,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SortOption.entries.toTypedArray()) { option ->
                    FilterChip(
                        selected = selectedSort == option,
                        onClick = { onSortSelected(option) },
                        label = {
                            Text(
                                option.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MeadowGreen.copy(alpha = 0.15f),
                            selectedLabelColor = MeadowGreen
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Filter by Breed",
                fontWeight = FontWeight.Bold,
                color = EarthBrown,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(breeds) { breed ->
                    FilterChip(
                        selected = selectedBreed == breed,
                        onClick = {
                            onBreedSelected(
                                if (selectedBreed == breed) null else breed
                            )
                        },
                        label = {
                            Text(
                                breed,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF9C27B0)
                                .copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF9C27B0)
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Filter by Gender",
                fontWeight = FontWeight.Bold,
                color = EarthBrown,
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                genders.forEach { gender ->
                    FilterChip(
                        selected = selectedGender == gender,
                        onClick = {
                            onGenderSelected(
                                if (selectedGender == gender) null else gender
                            )
                        },
                        label = {
                            Text(
                                if (gender == "Female") "🐄 Female"
                                else "🐂 Male",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2196F3)
                                .copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF2196F3)
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MeadowGreen
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Apply Filters",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
