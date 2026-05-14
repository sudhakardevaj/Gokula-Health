package com.mindmatrix.gokulahealth.ui.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mindmatrix.gokulahealth.data.local.entity.Cattle
import com.mindmatrix.gokulahealth.data.local.entity.MilkLog
import com.mindmatrix.gokulahealth.ui.theme.EarthBrown
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen
import com.mindmatrix.gokulahealth.ui.theme.WarmGray
import java.util.Locale

// ✅ FIXED: CowCard now accepts todayLog as parameter!
// This fixes the bug where ALL cards showed the SAME yield!
// The parent screen (HomeScreen/CattleListScreen) passes
// the correct log for each cattle!
@Composable
fun CowCard(
    cattle: Cattle,
    onClick: () -> Unit,
    todayLog: MilkLog? = null // ✅ FIXED: Accept real log as parameter!
) {
    SoftCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Photo or Initials ──────────────────────────────
            if (cattle.photoUri.isNotBlank()) {
                AsyncImage(
                    model = cattle.photoUri,
                    contentDescription = cattle.name,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightMeadow),
                    contentScale = ContentScale.Crop
                )
            } else {
                // ✅ PRESERVED: Initials when no photo
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LightMeadow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cattle.name.take(1).uppercase(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = MeadowGreen
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // ── Cattle Info ────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cattle.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown
                )
                Text(
                    text = "Tag: ${cattle.earTagId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmGray
                )
                Text(
                    text = "Breed: ${cattle.breed}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmGray
                )
                Text(
                    text = "Age: ${cattle.age} Yrs • ${cattle.gender}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmGray
                )
            }

            // ── Today's Yield ──────────────────────────────────
            // ✅ FIXED: Each cattle shows its OWN yield!
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Today's Yield",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmGray
                )
                if (todayLog != null) {
                    Text(
                        text = String.format(
                            Locale.getDefault(),
                            "%.1f L",
                            todayLog.totalLitres
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        color = MeadowGreen,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${todayLog.morningLitres}+${todayLog.eveningLitres}",
                        style = MaterialTheme.typography.labelSmall,
                        color = WarmGray,
                        fontSize = 10.sp
                    )
                } else {
                    Text(
                        text = "Not logged",
                        style = MaterialTheme.typography.titleSmall,
                        color = WarmGray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap to log 🥛",
                        style = MaterialTheme.typography.labelSmall,
                        color = MeadowGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ✅ PRESERVED: CowCommandButton - was working correctly!
@Composable
fun CowCommandButton(
    label: String,
    icon: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SoftCard(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color.copy(alpha = 0.05f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
// ✅ FIXED: Removed private remember() that was shadowing Compose's built-in!