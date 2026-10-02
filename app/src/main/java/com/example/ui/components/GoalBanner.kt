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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrackChanges
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPlanEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted

@Composable
fun GoalBanner(
    plan: DailyPlanEntity?,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val challenge = plan?.challenge?.ifBlank { "لمس برای تعیین چالش درس امروز..." } ?: "لمس برای تعیین چالش درس امروز..."
    val goal = plan?.goal?.ifBlank { "هدف اصلی امروزت چیه؟" } ?: "هدف اصلی امروزت چیه؟"
    val quote = plan?.quote?.ifBlank { "جمله انگیزشی خفن امروز..." } ?: "جمله انگیزشی خفن امروز..."

    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onEditClick() },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // چالش درس امروز (Right card in RTL)
        GoalCard(
            modifier = Modifier.weight(1f),
            title = "چالش درس امروز",
            content = challenge,
            icon = Icons.Default.TrackChanges,
            accentColor = primaryColor
        )

        // هدف (Center card in PDF)
        GoalCard(
            modifier = Modifier.weight(1.3f),
            title = "هدف",
            content = goal,
            icon = Icons.Default.Flag,
            accentColor = GoldAccent
        )

        // جمله انگیزشی خفن امروز (Left card in RTL)
        GoalCard(
            modifier = Modifier.weight(1f),
            title = "جمله انگیزشی خفن",
            content = quote,
            icon = Icons.Default.Bolt,
            accentColor = PurpleAccent
        )
    }
}

@Composable
private fun GoalCard(
    title: String,
    content: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = accentColor,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = if (content.startsWith("لمس") || content.startsWith("هدف") || content.startsWith("جمله")) TextMuted else MaterialTheme.colorScheme.onSurface,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
