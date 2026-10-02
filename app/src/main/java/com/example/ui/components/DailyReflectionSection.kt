package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.VolunteerActivism
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
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.TextMuted

@Composable
fun DailyReflectionSection(
    plan: DailyPlanEntity?,
    onEditReflectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val goodEvent = plan?.goodEvent?.ifBlank { "لمس برای نوشتن اتفاق خوب امروز..." } ?: "لمس برای نوشتن اتفاق خوب امروز..."
    val performanceNotes = plan?.performanceNotes?.ifBlank { "لمس برای ثبت توضیحات و نکات عملکرد امروز..." } ?: "لمس برای ثبت توضیحات و نکات عملکرد امروز..."
    val gratitudeNotes = plan?.gratitudeNotes?.ifBlank { "۱. \n۲. " } ?: "۱. \n۲. "

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable { onEditReflectionClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PinkAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "بازخورد روزانه",
                    tint = PinkAccent,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "ارزیابی، حال خوب و شکرگزاری روز",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }

        // اتفاق خوب امروز چی بود؟
        ReflectionBox(
            title = "اتفاق خوب امروز چی بود؟",
            content = goodEvent,
            icon = Icons.Default.SentimentSatisfiedAlt,
            accentColor = GoldAccent
        )

        // توضیحاتی در مورد عملکرد امروز
        ReflectionBox(
            title = "توضیحاتی درمورد عملکرد امروز:",
            content = performanceNotes,
            icon = Icons.Default.Comment,
            accentColor = primaryColor
        )

        // حداقل ۲ شکرگزاری بابت داشته‌هات
        ReflectionBox(
            title = "حداقل ۲ شکرگزاری بابت داشته‌هات:",
            content = gratitudeNotes,
            icon = Icons.Default.VolunteerActivism,
            accentColor = GreenAccent
        )
    }
}

@Composable
private fun ReflectionBox(
    title: String,
    content: String,
    icon: ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = accentColor,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = if (content.startsWith("لمس") || content.trim() == "۱. \n۲.") TextMuted else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
