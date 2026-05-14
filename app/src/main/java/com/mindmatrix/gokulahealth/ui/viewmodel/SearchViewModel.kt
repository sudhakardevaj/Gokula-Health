package com.mindmatrix.gokulahealth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.Vaccination
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// ── Models ────────────────────────────────────────────────────
data class SearchResult(
    val matchedCattle: List<Cattle> = emptyList(),
    val matchedVaccinations: List<Vaccination> = emptyList(),
    val totalCount: Int = 0
)

enum class SortOption(val label: String) {
    NAME_AZ("Name A→Z"),
    NAME_ZA("Name Z→A"),
    AGE_LOW("Age ↑"),
    AGE_HIGH("Age ↓"),
    NEWEST("Recently Added")
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CattleRepository
) : ViewModel() {

    // ── Raw data loaded once ──────────────────────────────────
    private val _allCattle = MutableStateFlow<List<Cattle>>(emptyList())
    private val _allVaccinations = MutableStateFlow<List<Vaccination>>(emptyList())

    // ── Search query ──────────────────────────────────────────
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Filters ───────────────────────────────────────────────
    private val _selectedBreedFilter = MutableStateFlow<String?>(null)
    val selectedBreedFilter = _selectedBreedFilter.asStateFlow()

    private val _selectedGenderFilter = MutableStateFlow<String?>(null)
    val selectedGenderFilter = _selectedGenderFilter.asStateFlow()

    private val _selectedSortOption = MutableStateFlow(SortOption.NAME_AZ)
    val selectedSortOption = _selectedSortOption.asStateFlow()

    // ── Results ───────────────────────────────────────────────
    private val _searchResults = MutableStateFlow(SearchResult())
    val searchResults: StateFlow<SearchResult> = _searchResults.asStateFlow()

    // ── Active filter badge count ─────────────────────────────
    val activeFilterCount: StateFlow<Int> = combine(
        _selectedBreedFilter,
        _selectedGenderFilter
    ) { breed, gender ->
        listOfNotNull(breed, gender).size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadAllData()
        observeAndSearch()
    }

    // ── Load all data once ─────────────────────────────────────
    private fun loadAllData() {
        viewModelScope.launch {
            repository.getAllCattle().collectLatest {
                _allCattle.value = it
            }
        }
        viewModelScope.launch {
            val today = SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault()
            ).format(Date())
            repository.getUpcomingVaccinations(today).collectLatest {
                _allVaccinations.value = it
            }
        }
    }

    // ── React to any change and re-search ─────────────────────
    private fun observeAndSearch() {
        viewModelScope.launch {
            combine(
                _searchQuery,
                _allCattle,
                _allVaccinations,
                _selectedBreedFilter,
                _selectedGenderFilter,
                _selectedSortOption
            ) { args ->
                @Suppress("UNCHECKED_CAST")
                performSearch(
                    query        = args[0] as String,
                    cattle       = args[1] as List<Cattle>,
                    vaccinations = args[2] as List<Vaccination>,
                    breedFilter  = args[3] as String?,
                    genderFilter = args[4] as String?,
                    sortOption   = args[5] as SortOption
                )
            }.collectLatest { result ->
                _searchResults.value = result
            }
        }
    }

    // ── Core search + filter + sort logic ─────────────────────
    private fun performSearch(
        query: String,
        cattle: List<Cattle>,
        vaccinations: List<Vaccination>,
        breedFilter: String?,
        genderFilter: String?,
        sortOption: SortOption
    ): SearchResult {

        // No query + no filter → return all sorted cattle
        if (query.isBlank() && breedFilter == null && genderFilter == null) {
            val sorted = applySorting(cattle, sortOption)
            return SearchResult(
                matchedCattle = sorted,
                totalCount = sorted.size
            )
        }

        val q = query.trim().lowercase()

        // ── Filter cattle ──────────────────────────────────────
        val matchedCattle = cattle.filter { c ->
            val matchesQuery = q.isEmpty() ||
                c.name.lowercase().contains(q) ||
                c.earTagId.lowercase().contains(q) ||
                c.breed.lowercase().contains(q) ||
                c.notes.lowercase().contains(q)

            val matchesBreed = breedFilter == null ||
                c.breed.equals(breedFilter, ignoreCase = true)

            val matchesGender = genderFilter == null ||
                c.gender.equals(genderFilter, ignoreCase = true)

            matchesQuery && matchesBreed && matchesGender
        }

        // ── Filter vaccinations (only when query non-blank) ────
        val matchedVaccinations = if (q.isNotEmpty()) {
            vaccinations.filter { v ->
                v.vaccineName.lowercase().contains(q) ||
                v.notes.lowercase().contains(q)
            }
        } else emptyList()

        val sorted = applySorting(matchedCattle, sortOption)

        return SearchResult(
            matchedCattle = sorted,
            matchedVaccinations = matchedVaccinations,
            totalCount = sorted.size + matchedVaccinations.size
        )
    }

    // ── Sort ──────────────────────────────────────────────────
    private fun applySorting(
        cattle: List<Cattle>,
        sort: SortOption
    ): List<Cattle> = when (sort) {
        SortOption.NAME_AZ  -> cattle.sortedBy { it.name.lowercase() }
        SortOption.NAME_ZA  -> cattle.sortedByDescending { it.name.lowercase() }
        SortOption.AGE_LOW  -> cattle.sortedBy { it.age }
        SortOption.AGE_HIGH -> cattle.sortedByDescending { it.age }
        SortOption.NEWEST   -> cattle.sortedByDescending { it.id }
    }

    // ── Public setters ────────────────────────────────────────
    fun onQueryChange(query: String) { _searchQuery.value = query }
    fun setBreedFilter(breed: String?) { _selectedBreedFilter.value = breed }
    fun setGenderFilter(gender: String?) { _selectedGenderFilter.value = gender }
    fun setSortOption(sort: SortOption) { _selectedSortOption.value = sort }

    fun clearAllFilters() {
        _selectedBreedFilter.value = null
        _selectedGenderFilter.value = null
        _selectedSortOption.value = SortOption.NAME_AZ
        _searchQuery.value = ""
    }
}
