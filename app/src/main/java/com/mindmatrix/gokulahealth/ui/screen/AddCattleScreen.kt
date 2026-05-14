package com.mindmatrix.gokulahealth.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.ui.component.GokulaIcons
import com.mindmatrix.gokulahealth.ui.component.gokulaTextFieldColors
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.HeartRed
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import com.mindmatrix.gokulahealth.ui.viewmodel.CattleViewModel
import java.io.File
import kotlinx.coroutines.launch
import android.content.Context
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCattleScreen(
    onNavigateBack: () -> Unit,
    cattleId: Int = -1,
    viewModel: CattleViewModel = hiltViewModel()
) {
    LaunchedEffect(cattleId) {
        if (cattleId != -1) viewModel.selectCattle(cattleId)
    }

    val existingCattle by viewModel.selectedCattle.collectAsState()

    // ── Form State ────────────────────────────────────────────
    var name by remember { mutableStateOf("") }
    var earTagId by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Female") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // ── Error States ──────────────────────────────────────────
    // ✅ NEW: Each field has its own error message!
    var nameError by remember { mutableStateOf<String?>(null) }
    var earTagError by remember { mutableStateOf<String?>(null) }
    var breedError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ── Pre-fill for Edit Mode ────────────────────────────────
    LaunchedEffect(existingCattle) {
        existingCattle?.let { cattle ->
            if (cattleId != -1) {
                name = cattle.name
                earTagId = cattle.earTagId
                breed = cattle.breed
                age = if (cattle.age > 0) cattle.age.toString() else ""
                notes = cattle.notes
                gender = cattle.gender
                if (cattle.photoUri.isNotBlank()) {
                    photoUri = Uri.parse(cattle.photoUri)
                }
            }
        }
    }

    // ── Camera Setup ──────────────────────────────────────────
    val tempUri = remember {
        val tempFile = File.createTempFile("cattle_", ".jpg", context.cacheDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) photoUri = tempUri
    }

    // ── Validation Function ───────────────────────────────────
    // ✅ NEW: Validates ALL fields before saving!
    fun validateAndSave(): Boolean {
        var isValid = true

        // Name validation
        when {
            name.isBlank() -> {
                nameError = "Cattle name is required"
                isValid = false
            }
            name.trim().length < 2 -> {
                nameError = "Name must be at least 2 characters"
                isValid = false
            }
            name.trim().length > 50 -> {
                nameError = "Name must be less than 50 characters"
                isValid = false
            }
            !name.trim().matches(Regex("^[a-zA-Z0-9 .'-]+$")) -> {
                nameError = "Name can only contain letters, numbers and spaces"
                isValid = false
            }
            else -> nameError = null
        }

        // Ear Tag ID validation
        when {
            earTagId.isBlank() -> {
                earTagError = "Ear Tag ID is required"
                isValid = false
            }
            earTagId.trim().length < 3 -> {
                earTagError = "Ear Tag must be at least 3 characters"
                isValid = false
            }
            earTagId.trim().length > 20 -> {
                earTagError = "Ear Tag must be less than 20 characters"
                isValid = false
            }
            !earTagId.trim().matches(Regex("^[a-zA-Z0-9]+$")) -> {
                earTagError = "Ear Tag can only contain letters and numbers (no spaces)"
                isValid = false
            }
            else -> earTagError = null
        }

        // Breed validation
        when {
            breed.isBlank() -> {
                breedError = "Please select a breed"
                isValid = false
            }
            else -> breedError = null
        }

        // Age validation
        when {
            age.isBlank() -> {
                ageError = "Age is required"
                isValid = false
            }
            age.toIntOrNull() == null -> {
                // ✅ FIXED: Age cannot be text!
                ageError = "Age must be a valid number"
                isValid = false
            }
            age.toInt() < 0 -> {
                ageError = "Age cannot be negative"
                isValid = false
            }
            age.toInt() > 30 -> {
                ageError = "Age cannot exceed 30 years"
                isValid = false
            }
            age.toInt() == 0 -> {
                ageError = "Age must be at least 1 year"
                isValid = false
            }
            else -> ageError = null
        }

        return isValid
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            if (cattleId == -1) "Add Cattle" else "Edit Cattle",
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
                        if (validateAndSave()) {
                            // ✅ NEW: Save image permanently before saving to DB
                            val permanentPhotoUri = photoUri?.let { uri ->
                                // Only copy if it's a new cache file (not already saved)
                                if (uri.toString().contains("cache")) {
                                    saveImageToInternalStorage(context, uri)?.toString()
                                } else {
                                    uri.toString()
                                }
                            } ?: ""

                            val cattle = Cattle(
                                id = if (cattleId != -1) cattleId else 0,
                                name = name.trim(),
                                earTagId = earTagId.trim().uppercase(),
                                breed = breed,
                                age = age.toIntOrNull() ?: 0,
                                notes = notes.trim(),
                                gender = gender,
                                photoUri = permanentPhotoUri // ✅ Use the permanent URI
                            )
                            if (cattleId != -1) {
                                viewModel.updateCattle(cattle)
                            } else {
                                viewModel.addCattle(cattle)
                            }
                            onNavigateBack()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Please fix the errors below before saving!"
                                )
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

            // ── Photo Picker ──────────────────────────────────
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(LightMeadow)
                    .align(Alignment.CenterHorizontally)
                    .clickable { cameraLauncher.launch(tempUri) },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Cattle Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = MeadowGreen,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Add Photo",
                            color = MeadowGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MeadowGreen)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Tap to take photo",
                color = WarmGray,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Cattle Name ───────────────────────────────────
            // ✅ FIXED: Only letters/numbers + error shown!
            ValidatedTextField(
                value = name,
                onValueChange = {
                    name = it
                    // ✅ Clear error on typing
                    if (nameError != null) nameError = null
                },
                label = "Cattle Name *",
                error = nameError,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text
                ),
                placeholder = "e.g. Gauri, Laxmi"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Ear Tag ID ────────────────────────────────────
            // ✅ FIXED: Only alphanumeric, auto-uppercase!
            ValidatedTextField(
                value = earTagId,
                onValueChange = {
                    // ✅ Only allow alphanumeric characters!
                    val filtered = it.filter { char ->
                        char.isLetterOrDigit()
                    }.uppercase()
                    earTagId = filtered
                    if (earTagError != null) earTagError = null
                },
                label = "Ear Tag ID *",
                error = earTagError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters
                ),
                placeholder = "e.g. IND1234"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Breed + Age Row ───────────────────────────────
            Row(modifier = Modifier.fillMaxWidth()) {

                // Breed Dropdown
                val breeds = listOf("HF Jersey", "Gir", "Sahiwal", "Murrah", "Other")
                var expanded by remember { mutableStateOf(false) }

                Column(modifier = Modifier.weight(1.5f)) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = breed,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Breed *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            isError = breedError != null,
                            colors = if (breedError != null)
                                OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = HeartRed,
                                    unfocusedBorderColor = HeartRed,
                                    focusedLabelColor = HeartRed,
                                    unfocusedLabelColor = HeartRed
                                )
                            else
                                gokulaTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            breeds.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        breed = option
                                        breedError = null
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    // ✅ NEW: Breed error message shown!
                    if (breedError != null) {
                        Text(
                            text = breedError!!,
                            color = HeartRed,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Age Field
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            // ✅ FIXED: Only allow digits - no text allowed!
                            val filtered = it.filter { char -> char.isDigit() }
                            // ✅ Max 2 digits (0-30 years)
                            if (filtered.length <= 2) {
                                age = filtered
                                ageError = null
                            }
                        },
                        label = { Text("Age *") },
                        suffix = { Text("Yrs") },
                        // ✅ FIXED: Number keyboard - no text keyboard!
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = ageError != null,
                        placeholder = { Text("1-30") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = if (ageError != null)
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HeartRed,
                                unfocusedBorderColor = HeartRed,
                                focusedLabelColor = HeartRed,
                                unfocusedLabelColor = HeartRed
                            )
                        else
                            gokulaTextFieldColors()
                    )
                    // ✅ NEW: Age error message shown below field!
                    if (ageError != null) {
                        Text(
                            text = ageError!!,
                            color = HeartRed,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Gender Selection ──────────────────────────────────
            Text(
                "Gender *",
                style = MaterialTheme.typography.labelLarge,
                color = EarthBrown,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gender == "Female",
                    onClick = { gender = "Female" },
                    colors = RadioButtonDefaults.colors(selectedColor = MeadowGreen)
                )
                Text("Female 🐄", color = EarthBrown)
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(
                    selected = gender == "Male",
                    onClick = { gender = "Male" },
                    colors = RadioButtonDefaults.colors(selectedColor = MeadowGreen)
                )
                Text("Male 🐂", color = EarthBrown)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Health Notes ──────────────────────────────────────
            // ✅ Notes is optional - no validation needed
            OutlinedTextField(
                value = notes,
                onValueChange = {
                    // ✅ Max 500 characters for notes
                    if (it.length <= 500) notes = it
                },
                label = { Text("Health Notes (Optional)") },
                placeholder = { Text("e.g. Vaccinated last month, good health...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                supportingText = {
                    // ✅ Show character count
                    Text(
                        "${notes.length}/500",
                        color = WarmGray,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = gokulaTextFieldColors()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Required Fields Note ──────────────────────────────
            Text(
                "* Required fields",
                color = WarmGray,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ✅ NEW: Reusable validated text field with error display
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = {
                Text(
                    placeholder,
                    color = WarmGray,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            isError = error != null,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            colors = if (error != null)
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HeartRed,
                    unfocusedBorderColor = HeartRed,
                    focusedLabelColor = HeartRed,
                    unfocusedLabelColor = HeartRed,
                    focusedTextColor = EarthBrown,
                    unfocusedTextColor = EarthBrown
                )
            else
                gokulaTextFieldColors()
        )
        // ✅ Error message shown below field
        if (error != null) {
            Text(
                text = "⚠ $error",
                color = HeartRed,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}
fun saveImageToInternalStorage(context: Context, uri: Uri): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "cattle_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
