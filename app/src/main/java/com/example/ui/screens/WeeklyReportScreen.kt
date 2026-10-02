package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.util.ReportImageExporter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPlanEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.WeeklyReportEntity
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PlannerViewModel
import com.example.util.toLatinDigits
import com.example.util.toPersianDigits

data class SubjectWeeklyStat(
    val subjectName: String,
    val totalMinutes: Int,
    val totalTests: Int,
    val mockPercent: String = ""
)

@Composable
fun WeeklyReportScreen(
    report: WeeklyReportEntity?,
    weekSessions: List<StudySessionEntity>,
    weekPlans: List<DailyPlanEntity>,
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    var showEditNotesDialog by remember { mutableStateOf(false) }
    var editingSubjectMock by remember { mutableStateOf<String?>(null) }
    var mockPercentInput by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary

    // Parse stored subjects mock percentages
    val mockPercentsMap = remember(report?.subjectsData) {
        val map = mutableMapOf<String, String>()
        report?.subjectsData?.split(";")?.forEach { entry ->
            val parts = entry.split(":")
            if (parts.size >= 2) {
                map[parts[0]] = parts[1]
            }
        }
        map
    }

    val defaultSubjects = listOf(
        "زیست", "شیمی", "فیزیک", "ریاضی", "زمین", "ادبیات", "عربی", "زبان", "دینی"
    )

    val subjectStats = remember(weekSessions, mockPercentsMap) {
        val sessionSubjects = weekSessions
            .map { ReportImageExporter.normalizeSubjectName(it.subject) }
            .filter { it.isNotBlank() }
            .distinct()
        val allSubjects = (defaultSubjects + sessionSubjects).distinct()

        allSubjects.map { subj ->
            val matching = weekSessions.filter {
                ReportImageExporter.normalizeSubjectName(it.subject).equals(subj, ignoreCase = true)
            }
            val minutes = matching.sumOf { it.studyDuration.toIntOrNull() ?: 0 }
            val tests = matching.sumOf { it.testCount.toIntOrNull() ?: 0 }
            SubjectWeeklyStat(
                subjectName = subj,
                totalMinutes = minutes,
                totalTests = tests,
                mockPercent = mockPercentsMap[subj] ?: ""
            )
        }
    }

    // Dynamic calculation from daily logs and study sessions as requested
    val totalStudyHoursCalculated: Double = run {
        val sessionsByDate = weekSessions.groupBy { it.dateKey }
        val plansByDate = weekPlans.associateBy { it.dateKey }
        val allDates = (sessionsByDate.keys + plansByDate.keys).distinct()

        var sumHours = 0.0
        for (dKey in allDates) {
            val plan = plansByDate[dKey]
            val declaredStudy = plan?.actualStudyHours?.toLatinDigits()?.toDoubleOrNull()
            if (declaredStudy != null && declaredStudy > 0) {
                sumHours += declaredStudy
            } else {
                val sessionMins = sessionsByDate[dKey]?.sumOf { it.studyDuration.toLatinDigits().toIntOrNull() ?: 0 } ?: 0
                sumHours += sessionMins / 60.0
            }
        }
        sumHours
    }

    val totalStudyHoursStr = if (totalStudyHoursCalculated > 0) {
        val totalMinutes = (totalStudyHoursCalculated * 60).toInt()
        val h = totalMinutes / 60
        val m = totalMinutes % 60
        if (m > 0) "${h.toPersianDigits()} ساعت و ${m.toPersianDigits()} دقیقه" else "${h.toPersianDigits()} ساعت"
    } else {
        report?.manualTotalStudy?.ifBlank { "۰ ساعت" } ?: "۰ ساعت"
    }

    val totalTestsCalculated: Int = run {
        val sessionsByDate = weekSessions.groupBy { it.dateKey }
        val plansByDate = weekPlans.associateBy { it.dateKey }
        val allDates = (sessionsByDate.keys + plansByDate.keys).distinct()

        var sumTests = 0
        for (dKey in allDates) {
            val plan = plansByDate[dKey]
            val declaredTests = plan?.testsDone?.toLatinDigits()?.toIntOrNull()
            if (declaredTests != null && declaredTests > 0) {
                sumTests += declaredTests
            } else {
                val sessionTests = sessionsByDate[dKey]?.sumOf { it.testCount.toLatinDigits().toIntOrNull() ?: 0 } ?: 0
                sumTests += sessionTests
            }
        }
        sumTests
    }

    val totalTestsStr = if (totalTestsCalculated > 0) {
        "${totalTestsCalculated.toPersianDigits()} تست"
    } else {
        report?.manualTotalTests?.ifBlank { "۰ تست" } ?: "۰ تست"
    }

    // Social media hours sum from 2-digit daily inputs
    val totalSocialHours = weekPlans.sumOf { plan ->
        plan.socialMediaSlots.toLatinDigits().filter { it.isDigit() }.toIntOrNull() ?: 0
    }
    val totalSocialStr = if (totalSocialHours > 0) {
        "${totalSocialHours.toPersianDigits()} ساعت"
    } else {
        report?.manualTotalSocial?.ifBlank { "۰ ساعت" } ?: "۰ ساعت"
    }

    val totalSleepDaysLogged = weekPlans.count { it.sleepTime.isNotBlank() }
    val sleepSummaryStr = if (totalSleepDaysLogged > 0) "${totalSleepDaysLogged.toPersianDigits()} روز ثبت شده" else "—"

    val conclusion = report?.conclusion?.ifBlank { "لمس برای ثبت نتیجه‌گیری هفته..." } ?: "لمس برای ثبت نتیجه‌گیری هفته..."
    val motivationQuote = report?.motivationQuote?.ifBlank { "لمس برای ثبت جمله انگیزشی این هفته..." } ?: "لمس برای ثبت جمله انگیزشی این هفته..."

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Page Title Header
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
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(primaryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "گزارش هفتگی",
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "گزارش هفتگی",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "تجمیع خودکار دروس هفته و ارزیابی نهایی",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 1. Weekly Subjects Table matching PDF Page 8
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "جدول تفکیک دروس و آزمون‌ها",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = primaryColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PurpleAccent.copy(alpha = 0.2f))
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "درس",
                    modifier = Modifier.weight(1.4f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "جمع ساعت مطالعه",
                    modifier = Modifier.weight(1.5f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "جمع تعداد تست",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "درصد آزمون",
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            subjectStats.forEach { stat ->
                val hoursText = if (stat.totalMinutes > 0) {
                    val h = stat.totalMinutes / 60
                    val m = stat.totalMinutes % 60
                    if (m > 0) "$h:${String.format("%02d", m)}".toPersianDigits() else "$h ساعت".toPersianDigits()
                } else {
                    "—"
                }

                val testsText = if (stat.totalTests > 0) stat.totalTests.toPersianDigits() else "—"
                val percentText = if (stat.mockPercent.isNotBlank()) "%${stat.mockPercent.toPersianDigits()}" else "ثبت درصد"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable {
                            editingSubjectMock = stat.subjectName
                            mockPercentInput = stat.mockPercent
                        }
                        .padding(vertical = 8.dp, horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stat.subjectName,
                        modifier = Modifier.weight(1.4f),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = primaryColor,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = hoursText,
                        modifier = Modifier.weight(1.5f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = testsText,
                        modifier = Modifier.weight(1.3f),
                        style = MaterialTheme.typography.bodySmall,
                        color = GreenAccent,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (stat.mockPercent.isNotBlank()) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                            .padding(vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = percentText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (stat.mockPercent.isNotBlank()) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (stat.mockPercent.isNotBlank()) primaryColor else TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            }
        }

        // 2. Weekly Summary Card matching PDF
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "جمع‌بندی هفته",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryColor,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatPill(
                    label = "ساعت مطالعه",
                    value = totalStudyHoursStr.toPersianDigits(),
                    icon = Icons.Default.HourglassBottom,
                    tint = primaryColor,
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "تعداد تست",
                    value = totalTestsStr.toPersianDigits(),
                    icon = Icons.Default.Quiz,
                    tint = GreenAccent,
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "خواب",
                    value = sleepSummaryStr.toPersianDigits(),
                    icon = Icons.Default.Bedtime,
                    tint = PurpleAccent,
                    modifier = Modifier.weight(1f)
                )

                StatPill(
                    label = "فضای مجازی",
                    value = totalSocialStr.toPersianDigits(),
                    icon = Icons.Default.PhoneAndroid,
                    tint = PinkAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Weekly Conclusion matching PDF
        WeeklyNoteCard(
            title = "نتیجه‌گیری هفته:",
            content = conclusion,
            icon = Icons.Default.AutoAwesome,
            accentColor = GoldAccent,
            onClick = { showEditNotesDialog = true }
        )

        // 4. Weekly Motivation Quote matching PDF
        WeeklyNoteCard(
            title = "جمله‌ای که بهت بیشترین انگیزه رو داد این هفته:",
            content = motivationQuote,
            icon = Icons.Default.Bolt,
            accentColor = primaryColor,
            onClick = { showEditNotesDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialog for editing mock percentage of a subject (Numbers only 0-100)
    editingSubjectMock?.let { subj ->
        AlertDialog(
            onDismissRequest = { editingSubjectMock = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "درصد آزمون آزمایشی: $subj",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("درصد کسب شده در آزمون را وارد کنید (فقط عدد ۰ تا ۱۰۰):", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = mockPercentInput,
                        onValueChange = {
                            val digits = it.toLatinDigits().filter { c -> c.isDigit() }
                            val n = digits.toIntOrNull()
                            mockPercentInput = if (n != null && n > 100) "100" else digits
                        },
                        placeholder = { Text("مثلاً ۶۵", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newMap = mockPercentsMap.toMutableMap()
                        newMap[subj] = mockPercentInput
                        val serialized = newMap.entries.joinToString(";") { "${it.key}:${it.value}" }
                        viewModel.saveWeeklyReport { it.copy(subjectsData = serialized) }
                        editingSubjectMock = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ذخیره", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingSubjectMock = null }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }

    // Dialog for editing weekly conclusion and motivation quote
    if (showEditNotesDialog) {
        var currentConclusion by remember { mutableStateOf(report?.conclusion ?: "") }
        var currentQuote by remember { mutableStateOf(report?.motivationQuote ?: "") }

        AlertDialog(
            onDismissRequest = { showEditNotesDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "نتیجه‌گیری و انگیزه هفتگی",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("نتیجه‌گیری هفته:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = currentConclusion,
                        onValueChange = { currentConclusion = it },
                        placeholder = { Text("ارزیابی کلی هفته، نقاط قوت و تصمیمات برای هفته بعد...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )

                    Text("جمله‌ای که بهت بیشترین انگیزه رو داد این هفته:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = currentQuote,
                        onValueChange = { currentQuote = it },
                        placeholder = { Text("جمله انگیزشی هفته...", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveWeeklyReport {
                            it.copy(
                                conclusion = currentConclusion,
                                motivationQuote = currentQuote
                            )
                        }
                        showEditNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ذخیره", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditNotesDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = tint,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun WeeklyNoteCard(
    title: String,
    content: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = if (content.startsWith("لمس")) TextMuted else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
