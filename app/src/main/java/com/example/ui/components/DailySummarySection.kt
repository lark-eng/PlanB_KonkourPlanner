package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPlanEntity
import com.example.data.model.StudySessionEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.util.toPersianDigits

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailySummarySection(
    plan: DailyPlanEntity?,
    sessions: List<StudySessionEntity>,
    onEditSummaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val autoTestsCount = sessions.sumOf { it.testCount.toIntOrNull() ?: 0 }
    val autoTestsStr = if (autoTestsCount > 0) autoTestsCount.toString() else ""

    val plannedStudy = plan?.plannedStudyHours?.ifBlank { "—" } ?: "—"
    val actualStudy = plan?.actualStudyHours?.ifBlank { "—" } ?: "—"
    val plannedTests = plan?.minPredictedTests?.ifBlank { "—" } ?: "—"
    val testsDone = plan?.testsDone?.ifBlank { autoTestsStr.ifBlank { "—" } } ?: autoTestsStr.ifBlank { "—" }
    val descriptive = plan?.descriptiveQuestions?.ifBlank { "—" } ?: "—"
    val adherence = plan?.adherencePercent?.ifBlank { "—" } ?: "—"
    val satisfaction = plan?.satisfactionPercent?.ifBlank { "—" } ?: "—"

    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditSummaryClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "جمع‌بندی روزانه",
                        tint = GreenAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "جمع‌بندی و آمار عملکرد روزانه",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "ویرایش آمار",
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "ویرایش آمار",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Clear, readable 2-column structured layout
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryItem(
                    label = "ساعت مطالعه پیش‌بینی شده",
                    value = if (plannedStudy != "—") "${plannedStudy.toPersianDigits()} ساعت" else "ثبت نشده",
                    accent = primaryColor,
                    onClick = onEditSummaryClick,
                    modifier = Modifier.weight(1f)
                )

                SummaryItem(
                    label = "ساعت مطالعه انجام شده",
                    value = if (actualStudy != "—") "${actualStudy.toPersianDigits()} ساعت" else "ثبت نشده",
                    accent = GreenAccent,
                    onClick = onEditSummaryClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryItem(
                    label = "حداقل تست پیش‌بینی شده",
                    value = if (plannedTests != "—") "${plannedTests.toPersianDigits()} تست" else "ثبت نشده",
                    accent = PurpleAccent,
                    onClick = onEditSummaryClick,
                    modifier = Modifier.weight(1f)
                )

                SummaryItem(
                    label = "تعداد تست زده شده",
                    value = if (testsDone != "—") "${testsDone.toPersianDigits()} تست" else "ثبت نشده",
                    accent = GreenAccent,
                    onClick = onEditSummaryClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryItem(
                    label = "حل سوالات تشریحی",
                    value = if (descriptive != "—") "${descriptive.toPersianDigits()} سوال" else "ثبت نشده",
                    accent = GoldAccent,
                    onClick = onEditSummaryClick,
                    modifier = Modifier.weight(1f)
                )

                SummaryItem(
                    label = "درصد عمل به برنامه",
                    value = if (adherence != "—") "%${adherence.toPersianDigits()}" else "ثبت نشده",
                    accent = primaryColor,
                    onClick = onEditSummaryClick,
                    modifier = Modifier.weight(1f)
                )
            }

            SummaryItem(
                label = "درصد رضایت از عملکرد امروز",
                value = if (satisfaction != "—") "%${satisfaction.toPersianDigits()}" else "ثبت نشده",
                accent = PinkAccent,
                onClick = onEditSummaryClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = accent,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}
