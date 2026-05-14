package com.mindmatrix.gokulahealth.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray

@Composable
fun GokulaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        readOnly = readOnly,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        colors = gokulaTextFieldColors()
    )
}

@Composable
fun gokulaTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MeadowGreen,
    unfocusedBorderColor = LightMeadow,
    focusedLabelColor = MeadowGreen,
    unfocusedLabelColor = WarmGray,
    focusedTextColor = EarthBrown,
    unfocusedTextColor = EarthBrown
)
