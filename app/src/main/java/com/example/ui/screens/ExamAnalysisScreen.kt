package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Score
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamAnalysisEntity
import com.example.data.model.ExamQuestionEntity
import com.example.ui.components.PersianDatePickerDialog
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkDivider
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.LightBlueAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PlannerViewModel
import com.example.util.PersianDateHelper
import com.example.util.toLatinDigits
import com.example.util.toPersianDigits

val PDF_REASONS = listOf(
    "فراموشی",
    "بی‌دقتی در خواندن",
    "بی‌دقتی محاسباتی",
    "کمبود وقت",
    "متوجه نشدن سوال",
    "عدم حل تست مشابه",
    "اشکال انتقال پاسخ"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamAnalysisScreen(
    exams: List<ExamAnalysisEntity>,
    selectedExamId: Long?,
    questions: List<ExamQuestionEntity>,
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    var showNewExamDialog by remember { mutableStateOf(false) }
    var showEditScoresDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<ExamQuestionEntity?>(null) }

    val currentExam = exams.find { it.id == selectedExamId } ?: exams.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Title
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
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = "فرم تحلیل آزمون",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "فرم تحلیل آزمون",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "ثبت درصد دروس، تراز، رتبه و تحلیل اشتباهات این هفته",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Button(
                onClick = { showNewExamDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "آزمون جدید", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "آزمون جدید",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Exam selector tabs for the current week
        if (exams.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                exams.forEach { exam ->
                    val isSelected = (currentExam?.id == exam.id)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectExam(exam.id) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "${exam.examTitle} (${exam.examDate.toPersianDigits()})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (currentExam == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(vertical = 40.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "هنوز آزمونی برای این هفته ثبت نشده است",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "برای شروع ثبت درصدها، تراز، رتبه و تحلیل سوالات این هفته، روی دکمه «آزمون جدید» بزنید",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showNewExamDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("ثبت آزمون برای این هفته", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Exam Header Card: Date, Separate Taraz, Separate Rank
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentExam.examTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تاریخ آزمون: ${currentExam.examDate.toPersianDigits()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { showEditScoresDialog = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "ویرایش نمرات", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { viewModel.deleteExamAnalysis(currentExam.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف آزمون", tint = PinkAccent, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Separate Taraz and Rank Badges as requested!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("تراز کل", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = currentExam.totalTaraz.ifBlank { "ثبت نشده" }.toPersianDigits(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldAccent
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("رتبه کل", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                                Text(
                                    text = currentExam.totalRank.ifBlank { "ثبت نشده" }.toPersianDigits(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Subject Percentages Grid for: زیست، فیزیک، شیمی، ریاضی، زمین‌شناسی
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Percent, contentDescription = "درصدها", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Text(
                            text = "درصد دروس اختصاصی آزمون",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "ویرایش درصدها",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showEditScoresDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SubjectPercentBadge("زیست", currentExam.biologyPercent, GreenAccent, Modifier.weight(1f)) { showEditScoresDialog = true }
                    SubjectPercentBadge("فیزیک", currentExam.physicsPercent, ElectricBlue, Modifier.weight(1f)) { showEditScoresDialog = true }
                    SubjectPercentBadge("شیمی", currentExam.chemistryPercent, PurpleAccent, Modifier.weight(1f)) { showEditScoresDialog = true }
                    SubjectPercentBadge("ریاضی", currentExam.mathPercent, GoldAccent, Modifier.weight(1f)) { showEditScoresDialog = true }
                    SubjectPercentBadge("زمین", currentExam.geologyPercent, PinkAccent, Modifier.weight(1f)) { showEditScoresDialog = true }
                }
            }

            // Error Reasons Analysis Badge Summary
            val wrongCount = questions.count { it.isWrong }
            val unansweredCount = questions.count { it.isUnanswered }
            val topReasonsMap = questions.filter { it.reason.isNotBlank() }
                .groupingBy { it.reason }
                .eachCount()

            if (questions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "آمار علل خطاهای آزمون",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = LightBlueAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PinkAccent.copy(alpha = 0.15f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "تعداد غلط: ${wrongCount.toPersianDigits()}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PinkAccent
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldAccent.copy(alpha = 0.15f))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "تعداد نزده: ${unansweredCount.toPersianDigits()}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldAccent
                            )
                        }
                    }

                    if (topReasonsMap.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            topReasonsMap.forEach { (reason, count) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$reason: ${count.toPersianDigits()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Questions Table matching PDF Page 9 & 10
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "جدول تحلیل سوالات آزمون",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Button(
                        onClick = { showAddQuestionDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "افزودن سوال", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("افزودن سوال", color = MaterialTheme.colorScheme.onPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (questions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "هنوز سوالی برای این آزمون ثبت نشده است.\nروی «افزودن سوال» بزنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val horizontalScroll = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .horizontalScroll(horizontalScroll)
                    ) {
                        // Header row matching PDF
                        Row(
                            modifier = Modifier
                                .background(PurpleAccent.copy(alpha = 0.25f))
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderCell("شماره", 55.dp)
                            TableHeaderCell("نام درس", 95.dp)
                            TableHeaderCell("نزده", 55.dp)
                            TableHeaderCell("غلط", 55.dp)
                            TableHeaderCell("علت خطا", 130.dp)
                            TableHeaderCell("مبحث / بهترین تصمیم", 160.dp)
                            TableHeaderCell("عملیات", 65.dp)
                        }

                        // Data rows
                        questions.forEachIndexed { idx, q ->
                            androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.8.dp)

                            Row(
                                modifier = Modifier
                                    .clickable { editingQuestion = q }
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(q.questionNumber.ifBlank { (idx + 1).toString() }.toPersianDigits(), 55.dp, isBold = true)
                                TableCell(q.subject.ifBlank { "—" }, 95.dp, color = MaterialTheme.colorScheme.primary)
                                TableCell(if (q.isUnanswered) "✓" else "—", 55.dp, color = GoldAccent, isBold = q.isUnanswered)
                                TableCell(if (q.isWrong) "✗" else "—", 55.dp, color = PinkAccent, isBold = q.isWrong)
                                TableCell(q.reason.ifBlank { "—" }, 130.dp, color = LightBlueAccent)
                                TableCell(q.topicAndDecision.ifBlank { "—" }, 160.dp)

                                Row(
                                    modifier = Modifier.width(65.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    IconButton(
                                        onClick = { viewModel.deleteExamQuestion(q.id) },
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = PinkAccent, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // New Exam Dialog with strict Persian Date Picker and Number-only validation
    if (showNewExamDialog) {
        var examTitle by remember { mutableStateOf("") }
        var selectedExamDate by remember {
            mutableStateOf(PersianDateHelper.gregorianToJalali(PersianDateHelper.getEffectiveToday()).toStandardString())
        }
        var showDatePickerForNewExam by remember { mutableStateOf(false) }
        var tarazInput by remember { mutableStateOf("") }
        var rankInput by remember { mutableStateOf("") }
        var bioInput by remember { mutableStateOf("") }
        var physicsInput by remember { mutableStateOf("") }
        var chemInput by remember { mutableStateOf("") }
        var mathInput by remember { mutableStateOf("") }
        var geoInput by remember { mutableStateOf("") }

        val commonExamTitles = listOf("سنجش", "قلم‌چی", "گزینه دو", "ماز", "گاج")

        AlertDialog(
            onDismissRequest = { showNewExamDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "ایجاد فرم آزمون جدید",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("نام آزمون:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    OutlinedTextField(
                        value = examTitle,
                        onValueChange = { examTitle = it },
                        placeholder = { Text("مثلاً آزمون ۲ سنجش", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        commonExamTitles.forEach { t ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (examTitle == t) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                                    .clickable { examTitle = t }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = t,
                                    color = if (examTitle == t) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Exact Date Picker trigger (System Recognizable Date)
                    Text("تاریخ آزمون:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                            .clickable { showDatePickerForNewExam = true }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedExamDate.toPersianDigits(),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "انتخاب تقویم",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Separate Taraz & Rank (Numbers only!)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تراز کل (فقط عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = tarazInput,
                                onValueChange = { input ->
                                    val digits = input.toLatinDigits().filter { it.isDigit() }
                                    tarazInput = digits
                                },
                                placeholder = { Text("۶۸۵۰", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("رتبه کل (فقط عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = rankInput,
                                onValueChange = { input ->
                                    val digits = input.toLatinDigits().filter { it.isDigit() }
                                    rankInput = digits
                                },
                                placeholder = { Text("۲۴۰", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    // Subject Percentages (Numbers only!)
                    Text("درصد دروس اختصاصی (فقط عدد بین ۰ تا ۱۰۰):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("زیست", fontSize = 10.sp, color = GreenAccent)
                            OutlinedTextField(
                                value = bioInput,
                                onValueChange = { bioInput = clampPercent(it) },
                                placeholder = { Text("٪", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("فیزیک", fontSize = 10.sp, color = ElectricBlue)
                            OutlinedTextField(
                                value = physicsInput,
                                onValueChange = { physicsInput = clampPercent(it) },
                                placeholder = { Text("٪", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("شیمی", fontSize = 10.sp, color = PurpleAccent)
                            OutlinedTextField(
                                value = chemInput,
                                onValueChange = { chemInput = clampPercent(it) },
                                placeholder = { Text("٪", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ریاضی", fontSize = 10.sp, color = GoldAccent)
                            OutlinedTextField(
                                value = mathInput,
                                onValueChange = { mathInput = clampPercent(it) },
                                placeholder = { Text("٪", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("زمین", fontSize = 10.sp, color = PinkAccent)
                            OutlinedTextField(
                                value = geoInput,
                                onValueChange = { geoInput = clampPercent(it) },
                                placeholder = { Text("٪", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createNewExamAnalysis(
                            title = examTitle,
                            date = selectedExamDate,
                            taraz = tarazInput,
                            rank = rankInput,
                            bio = bioInput,
                            physics = physicsInput,
                            chem = chemInput,
                            math = mathInput,
                            geo = geoInput
                        )
                        showNewExamDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ایجاد آزمون", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewExamDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )

        if (showDatePickerForNewExam) {
            PersianDatePickerDialog(
                initialDate = PersianDateHelper.parseJalaliDate(selectedExamDate),
                onDismiss = { showDatePickerForNewExam = false },
                onDateSelected = { pickedDate ->
                    selectedExamDate = pickedDate.toStandardString()
                    showDatePickerForNewExam = false
                }
            )
        }
    }

    // Edit Scores & Subject Percentages Dialog for Current Exam
    if (showEditScoresDialog && currentExam != null) {
        var editTaraz by remember { mutableStateOf(currentExam.totalTaraz) }
        var editRank by remember { mutableStateOf(currentExam.totalRank) }
        var editBio by remember { mutableStateOf(currentExam.biologyPercent) }
        var editPhysics by remember { mutableStateOf(currentExam.physicsPercent) }
        var editChem by remember { mutableStateOf(currentExam.chemistryPercent) }
        var editMath by remember { mutableStateOf(currentExam.mathPercent) }
        var editGeo by remember { mutableStateOf(currentExam.geologyPercent) }

        AlertDialog(
            onDismissRequest = { showEditScoresDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "ویرایش تراز، رتبه و درصدها: ${currentExam.examTitle}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Separate Taraz and Rank (Number only)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تراز کل (فقط عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = editTaraz,
                                onValueChange = { editTaraz = it.toLatinDigits().filter { c -> c.isDigit() } },
                                placeholder = { Text("مثلاً ۶۸۵۰", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("رتبه کل (فقط عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = editRank,
                                onValueChange = { editRank = it.toLatinDigits().filter { c -> c.isDigit() } },
                                placeholder = { Text("مثلاً ۲۴۰", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    // 5 Subjects Percentages
                    Text("درصد دروس اختصاصی (۰ تا ۱۰۰):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("زیست", fontSize = 10.sp, color = GreenAccent)
                            OutlinedTextField(
                                value = editBio,
                                onValueChange = { editBio = clampPercent(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("فیزیک", fontSize = 10.sp, color = ElectricBlue)
                            OutlinedTextField(
                                value = editPhysics,
                                onValueChange = { editPhysics = clampPercent(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("شیمی", fontSize = 10.sp, color = PurpleAccent)
                            OutlinedTextField(
                                value = editChem,
                                onValueChange = { editChem = clampPercent(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ریاضی", fontSize = 10.sp, color = GoldAccent)
                            OutlinedTextField(
                                value = editMath,
                                onValueChange = { editMath = clampPercent(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("زمین", fontSize = 10.sp, color = PinkAccent)
                            OutlinedTextField(
                                value = editGeo,
                                onValueChange = { editGeo = clampPercent(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateExamAnalysis(
                            currentExam.copy(
                                totalTaraz = editTaraz,
                                totalRank = editRank,
                                biologyPercent = editBio,
                                physicsPercent = editPhysics,
                                chemistryPercent = editChem,
                                mathPercent = editMath,
                                geologyPercent = editGeo
                            )
                        )
                        showEditScoresDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ذخیره نمرات", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditScoresDialog = false }) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }

    // Add / Edit Question Dialog
    val activeQuestion = editingQuestion ?: if (showAddQuestionDialog) ExamQuestionEntity(examId = currentExam?.id ?: 0L) else null
    activeQuestion?.let { q ->
        var questionNum by remember { mutableStateOf(q.questionNumber) }
        var subject by remember { mutableStateOf(q.subject) }
        var isUnanswered by remember { mutableStateOf(q.isUnanswered) }
        var isWrong by remember { mutableStateOf(q.isWrong) }
        var selectedReason by remember { mutableStateOf(q.reason) }
        var topicAndDecision by remember { mutableStateOf(q.topicAndDecision) }

        val commonSubjects = listOf("زیست‌شناسی", "شیمی", "فیزیک", "ریاضی", "زمین‌شناسی")

        AlertDialog(
            onDismissRequest = {
                showAddQuestionDialog = false
                editingQuestion = null
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = if (q.id == 0L) "تحلیل سوال جدید" else "ویرایش تحلیل سوال",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("شماره سوال (فقط عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = questionNum,
                                onValueChange = { questionNum = it.toLatinDigits().filter { c -> c.isDigit() } },
                                placeholder = { Text("مثلاً ۴۵", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text("نام درس:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it },
                                placeholder = { Text("زیست، فیزیک...", color = TextMuted) }
                            )
                        }
                    }

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        commonSubjects.forEach { s ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (subject == s) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                                    .clickable { subject = s }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(text = s, fontSize = 10.sp, color = if (subject == s) MaterialTheme.colorScheme.onPrimary else TextSecondary)
                            }
                        }
                    }

                    Text("وضعیت سوال در آزمون:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isWrong,
                                onCheckedChange = {
                                    isWrong = it
                                    if (it) isUnanswered = false
                                },
                                colors = CheckboxDefaults.colors(checkedColor = PinkAccent)
                            )
                            Text("پاسخ غلط", color = PinkAccent, style = MaterialTheme.typography.bodySmall)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isUnanswered,
                                onCheckedChange = {
                                    isUnanswered = it
                                    if (it) isWrong = false
                                },
                                colors = CheckboxDefaults.colors(checkedColor = GoldAccent)
                            )
                            Text("نزده", color = GoldAccent, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Text("علت خطا (مطابق فرم آزمون):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PDF_REASONS.forEach { r ->
                            val isSel = (selectedReason == r)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                                    .border(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                    .clickable { selectedReason = r }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = r,
                                    fontSize = 11.sp,
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Text("مبحث / بهترین تصمیم:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    OutlinedTextField(
                        value = topicAndDecision,
                        onValueChange = { topicAndDecision = it },
                        placeholder = { Text("مثلاً: مدار الکتریکی - حل ۳۰ تست محاسباتی زماندار", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toSave = q.copy(
                            questionNumber = questionNum,
                            subject = subject,
                            isUnanswered = isUnanswered,
                            isWrong = isWrong,
                            reason = selectedReason,
                            topicAndDecision = topicAndDecision
                        )
                        if (q.id == 0L) {
                            viewModel.addExamQuestion(toSave)
                        } else {
                            viewModel.updateExamQuestion(toSave)
                        }
                        showAddQuestionDialog = false
                        editingQuestion = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("ذخیره سوال", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showAddQuestionDialog = false
                        editingQuestion = null
                    }
                ) {
                    Text("انصراف", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SubjectPercentBadge(
    subjectName: String,
    percentStr: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val displayPercent = if (percentStr.isNotBlank()) "%${percentStr.toPersianDigits()}" else "—"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = subjectName,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = displayPercent,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = accentColor,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun clampPercent(input: String): String {
    val digits = input.toLatinDigits().filter { it.isDigit() }
    val num = digits.toIntOrNull() ?: return ""
    return if (num > 100) "100" else num.toString()
}

@Composable
private fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
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
