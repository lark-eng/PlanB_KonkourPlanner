package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPlanEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.util.JalaliDate
import com.example.util.toPersianDigits

@Composable
fun PersianHeader(
    jalaliDate: JalaliDate,
    dayOfWeekName: String,
    plan: DailyPlanEntity?,
    onEditHeaderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sleepTime = plan?.sleepTime?.ifBlank { "ثبت نشده" } ?: "ثبت نشده"
    val wakeTime = plan?.wakeTime?.ifBlank { "ثبت نشده" } ?: "ثبت نشده"
    val mood = plan?.mood?.ifBlank { "انتخاب حال" } ?: "انتخاب حال"
    val countdown = plan?.examCountdown?.ifBlank { "—" } ?: "—"

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderPill(
                icon = Icons.Default.Today,
                iconTint = primaryColor,
                label = "روز هفته",
                value = dayOfWeekName,
                onClick = onEditHeaderClick
            )

            HeaderPill(
                icon = Icons.Default.CalendarMonth,
                iconTint = primaryColor,
                label = "تاریخ",
                value = jalaliDate.format(includeMonthName = true),
                onClick = onEditHeaderClick
            )

            HeaderPill(
                icon = Icons.Default.Bedtime,
                iconTint = PurpleAccent,
                label = "ساعت خواب",
                value = sleepTime.toPersianDigits(),
                onClick = onEditHeaderClick
            )

            HeaderPill(
                icon = Icons.Default.LightMode,
                iconTint = GoldAccent,
                label = "ساعت بیداری",
                value = wakeTime.toPersianDigits(),
                onClick = onEditHeaderClick
            )

            HeaderPill(
                icon = Icons.Default.Favorite,
                iconTint = PinkAccent,
                label = "حال دل",
                value = mood,
                onClick = onEditHeaderClick
            )

            HeaderPill(
                icon = Icons.Default.HourglassTop,
                iconTint = primaryColor,
                label = "روز شمار کنکور",
                value = "$countdown روز".toPersianDigits(),
                onClick = onEditHeaderClick
            )
        }
    }
}

@Composable
private fun HeaderPill(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp
                )
            }
        }
    }
}
