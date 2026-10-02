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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySessionEntity
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.util.toPersianDigits

@Composable
fun StudyTable(
    sessions: List<StudySessionEntity>,
    onAddSessionClick: () -> Unit,
    onEditSessionClick: (StudySessionEntity) -> Unit,
    onDeleteSessionClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Table Title & Add button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "جدول مطالعه",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "جدول برنامه‌ریزی و ثبت دروس مطالعه",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }

            IconButton(
                onClick = onAddSessionClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(primaryColor)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "+",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "هنوز پارت مطالعه‌ای برای امروز ثبت نشده است",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "برای شروع ثبت درس روی + بزنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryColor
                    )
                }
            }
        } else {
            val horizontalScrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .horizontalScroll(horizontalScrollState)
            ) {
                Row(
                    modifier = Modifier
                        .background(primaryColor.copy(alpha = 0.15f))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableHeaderCell("ردیف", 45.dp)
                    TableHeaderCell("ساعت", 90.dp)
                    TableHeaderCell("درس", 100.dp)
                    TableHeaderCell("مبحث / فصل", 110.dp)
                    TableHeaderCell("نوع کار", 85.dp)
                    TableHeaderCell("حواس‌پرتی", 75.dp)
                    TableHeaderCell("زمان مطالعه", 90.dp)
                    TableHeaderCell("تست: تعداد", 80.dp)
                    TableHeaderCell("تست: زمان", 80.dp)
                    TableHeaderCell("درصد آزمونک", 85.dp)
                    TableHeaderCell("عملیات", 80.dp)
                }

                sessions.forEachIndexed { index, session ->
                    androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.8.dp)

                    Row(
                        modifier = Modifier
                            .clickable { onEditSessionClick(session) }
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell((index + 1).toPersianDigits(), 45.dp, isBold = true)

                        val timeStr = if (session.startTime.isNotBlank() || session.endTime.isNotBlank()) {
                            "${session.startTime.ifBlank { "--" }} الی ${session.endTime.ifBlank { "--" }}".toPersianDigits()
                        } else {
                            "—"
                        }
                        TableCell(timeStr, 90.dp)

                        Row(
                            modifier = Modifier.width(100.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = session.subject.ifBlank { "—" },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = primaryColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (session.hasReminder) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "یادآوری کتاب باز",
                                    tint = primaryColor,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        TableCell(session.topic.ifBlank { "—" }, 110.dp)
                        TableCell(session.taskType.ifBlank { "—" }, 85.dp)
                        TableCell(session.distractions.toPersianDigits(), 75.dp)
                        TableCell(session.studyDuration.ifBlank { "—" }.toPersianDigits(), 90.dp, color = primaryColor)
                        TableCell(session.testCount.ifBlank { "—" }.toPersianDigits(), 80.dp)
                        TableCell(session.testDuration.ifBlank { "—" }.toPersianDigits(), 80.dp)
                        TableCell(session.testQuizPercent.ifBlank { "—" }.toPersianDigits(), 85.dp)

                        Row(
                            modifier = Modifier.width(80.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onEditSessionClick(session) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "ویرایش",
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteSessionClick(session.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف",
                                    tint = PinkAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        fontSize = 11.sp
    )
}

@Composable
private fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    color: Color = TextSecondary,
    isBold: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        ),
        color = color,
        textAlign = TextAlign.Center,
        fontSize = 11.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}
