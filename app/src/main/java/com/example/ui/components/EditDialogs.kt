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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyPlanEntity
import com.example.data.model.StudySessionEntity
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.util.toLatinDigits
import com.example.util.toPersianDigits

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditHeaderDialog(
    plan: DailyPlanEntity?,
    onDismiss: () -> Unit,
    onSave: (sleepTime: String, wakeTime: String, mood: String, countdown: String) -> Unit
) {
    var sleepTime by remember { mutableStateOf(plan?.sleepTime ?: "") }
    var wakeTime by remember { mutableStateOf(plan?.wakeTime ?: "") }
    var mood by remember { mutableStateOf(plan?.mood ?: "") }
    var countdown by remember { mutableStateOf(plan?.examCountdown ?: "") }

    val moodOptions = listOf(
        "عالی 😄", "باانگیزه 💪", "پرانرژی ⚡", "متمرکز 🎯", "معمولی 😐", "خسته 🥱", "استرس‌دار 😰"
    )

    val quickSleeps = listOf("۲۲:۳۰", "۲۳:۰۰", "۲۳:۳۰", "۰۰:۰۰", "۰۰:۳۰")
    val quickWakes = listOf("۰۵:۳۰", "۰۶:۰۰", "۰۶:۳۰", "۰۷:۰۰", "۰۷:۳۰")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "تنظیم اطلاعات نوار بالا",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sleep time
                Text(
                    text = "ساعت خواب:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = sleepTime,
                    onValueChange = { sleepTime = it },
                    placeholder = { Text("مثال: ۲۳:۳۰", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickSleeps.forEach { t ->
                        ChipOption(label = t, isSelected = sleepTime == t) { sleepTime = t }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Wake time
                Text(
                    text = "ساعت بیداری:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = wakeTime,
                    onValueChange = { wakeTime = it },
                    placeholder = { Text("مثال: ۰۶:۳۰", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickWakes.forEach { t ->
                        ChipOption(label = t, isSelected = wakeTime == t) { wakeTime = t }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Mood
                Text(
                    text = "حال دل امروزت چطوره؟",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moodOptions.forEach { m ->
                        ChipOption(label = m, isSelected = mood == m) { mood = m }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Days to Konkur (Numbers only!)
                Text(
                    text = "تعداد روز مانده تا کنکور (فقط عدد):",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = countdown,
                    onValueChange = { countdown = it.toLatinDigits().filter { c -> c.isDigit() } },
                    placeholder = { Text("مثلاً ۲۴۵", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(sleepTime, wakeTime, mood, countdown) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ذخیره", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditGoalsDialog(
    plan: DailyPlanEntity?,
    onDismiss: () -> Unit,
    onSave: (challenge: String, goal: String, quote: String) -> Unit
) {
    var challenge by remember { mutableStateOf(plan?.challenge ?: "") }
    var goal by remember { mutableStateOf(plan?.goal ?: "") }
    var quote by remember { mutableStateOf(plan?.quote ?: "") }

    val sampleQuotes = listOf(
        "هر تستی که امروز میزنی، یک گام به رتبه هدف نزدیک‌تری!",
        "سختی امروز، لبخند افتخار فردا در کارنامه کنکوره.",
        "روی هدفت قفل باش و صدای بهونه‌ها رو نشنو!",
        "امروز رو طوری بساز که شب با خیال راحت بخوابی."
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "تعیین هدف، چالش و انگیزه امروز",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("هدف اصلی امروز:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    placeholder = { Text("مثلاً: اتمام گفتار ۳ زیست دهم و ۶۰ تست فیزیک", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text("چالش درس امروز:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = challenge,
                    onValueChange = { challenge = it },
                    placeholder = { Text("مثلاً: غلبه بر تست‌های زماندار شیمی", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("جمله انگیزشی خفن امروز:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = quote,
                    onValueChange = { quote = it },
                    placeholder = { Text("جمله دلخواهت رو بنویس یا از پیشنهادها انتخاب کن", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text("پیشنهادهای انگیزشی:", color = TextMuted, fontSize = 11.sp)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sampleQuotes.forEach { q ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { quote = q }
                                .padding(8.dp)
                        ) {
                            Text(text = q, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(challenge, goal, quote) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ذخیره", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditSessionDialog(
    session: StudySessionEntity?,
    onDismiss: () -> Unit,
    onSave: (StudySessionEntity) -> Unit
) {
    var startTime by remember { mutableStateOf(session?.startTime ?: "") }
    var endTime by remember { mutableStateOf(session?.endTime ?: "") }
    var subject by remember { mutableStateOf(session?.subject ?: "") }
    var topic by remember { mutableStateOf(session?.topic ?: "") }
    var taskType by remember { mutableStateOf(session?.taskType ?: "یادگیری") }
    var distractions by remember { mutableIntStateOf(session?.distractions ?: 0) }
    var studyDuration by remember { mutableStateOf(session?.studyDuration ?: "") }
    var testCount by remember { mutableStateOf(session?.testCount ?: "") }
    var testDuration by remember { mutableStateOf(session?.testDuration ?: "") }
    var testQuizPercent by remember { mutableStateOf(session?.testQuizPercent ?: "") }
    var isWeeklyRepeat by remember { mutableStateOf(session?.isWeeklyRepeat ?: false) }
    var hasReminder by remember { mutableStateOf(session?.hasReminder ?: false) }

    val commonSubjects = listOf(
        "زیست‌شناسی", "شیمی", "فیزیک", "ریاضی", "زمین‌شناسی",
        "هندسه", "حسابان", "گسسته", "ادبیات", "عربی", "زبان", "دینی"
    )

    val commonTaskTypes = listOf(
        "یادگیری", "تست آموزشی", "تست زماندار", "کلاس آموزشی", "مرور", "خلاصه‌نویسی", "آزمونک"
    )

    // Dynamic test fields based on category/taskType:
    // - "تست آموزشی" / "تست زماندار": testCount & testDuration shown, testQuizPercent hidden
    // - "کلاس آموزشی" / "مرور" / "یادگیری" / "خلاصه‌نویسی": testCount, testDuration & testQuizPercent ALL hidden
    // - "آزمونک" / "آزمون": all test fields shown
    val showTestCountAndTime = taskType == "تست آموزشی" || taskType == "تست زماندار" || taskType == "آزمونک" || taskType == "آزمون"
    val showQuizPercent = taskType == "آزمونک" || taskType == "آزمون"

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = if (session == null || session.id == 0L) "افزودن پارت مطالعه جدید" else "ویرایش پارت مطالعه",
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
                Text("درس:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    placeholder = { Text("نام درس را وارد کنید یا انتخاب کنید", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    commonSubjects.forEach { s ->
                        ChipOption(label = s, isSelected = subject == s) { subject = s }
                    }
                }

                Text("مبحث / فصل:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    placeholder = { Text("مثلاً حرکت‌شناسی، ساختار اتم، تابع...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("نوع کار:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    commonTaskTypes.forEach { t ->
                        ChipOption(label = t, isSelected = taskType == t) { taskType = t }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ساعت شروع:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            placeholder = { Text("۰۸:۰۰", color = TextMuted) }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ساعت پایان:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            placeholder = { Text("۰۹:۳۰", color = TextMuted) }
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("زمان (دقیقه):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = studyDuration,
                            onValueChange = { studyDuration = it.toLatinDigits().filter { c -> c.isDigit() } },
                            placeholder = { Text("۹۰", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("تعداد حواس‌پرتی در این پارت:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (distractions > 0) distractions-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "کاهش", tint = TextSecondary)
                        }
                        Text(
                            text = distractions.toPersianDigits(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { distractions++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "افزایش", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                if (showTestCountAndTime) {
                    Text(
                        text = if (showQuizPercent) "تست و آزمونک (فقط عدد):" else "آمار تست (فقط عدد):",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("تعداد تست:", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = testCount,
                                onValueChange = { testCount = it.toLatinDigits().filter { c -> c.isDigit() } },
                                placeholder = { Text("۳۰", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("زمان تست (دقیقه):", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            OutlinedTextField(
                                value = testDuration,
                                onValueChange = { testDuration = it.toLatinDigits().filter { c -> c.isDigit() } },
                                placeholder = { Text("۴۵", color = TextMuted) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                        if (showQuizPercent) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("درصد آزمونک (۰-۱۰۰):", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                                OutlinedTextField(
                                    value = testQuizPercent,
                                    onValueChange = {
                                        val d = it.toLatinDigits().filter { c -> c.isDigit() }
                                        val n = d.toIntOrNull()
                                        testQuizPercent = if (n != null && n > 100) "100" else d
                                    },
                                    placeholder = { Text("۷۵", color = TextMuted) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    }
                }

                androidx.compose.material3.HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

                // Weekly repeat toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .clickable { isWeeklyRepeat = !isWeeklyRepeat }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "تکرار هفتگی",
                            tint = if (isWeeklyRepeat) MaterialTheme.colorScheme.primary else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "تکرار هفتگی در این روز",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "ثبت خودکار این پارت در هفته‌های بعدی",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    androidx.compose.material3.Switch(
                        checked = isWeeklyRepeat,
                        onCheckedChange = { isWeeklyRepeat = it }
                    )
                }

                // Reminder notification toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .clickable { hasReminder = !hasReminder }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = "یادآوری کتاب باز",
                            tint = if (hasReminder) MaterialTheme.colorScheme.primary else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "یادآوری قبل از شروع پارت",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "ارسال نوتیفیکیشن در ساعت شروع",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                    androidx.compose.material3.Switch(
                        checked = hasReminder,
                        onCheckedChange = { hasReminder = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCount = if (showTestCountAndTime) testCount else ""
                    val finalDuration = if (showTestCountAndTime) testDuration else ""
                    val finalQuiz = if (showQuizPercent) testQuizPercent else ""

                    val updated = (session ?: StudySessionEntity(dateKey = "")).copy(
                        startTime = startTime,
                        endTime = endTime,
                        subject = subject,
                        topic = topic,
                        taskType = taskType,
                        distractions = distractions,
                        studyDuration = studyDuration,
                        testCount = finalCount,
                        testDuration = finalDuration,
                        testQuizPercent = finalQuiz,
                        isWeeklyRepeat = isWeeklyRepeat,
                        hasReminder = hasReminder
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ذخیره پارت", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}

@Composable
fun EditSummaryDialog(
    plan: DailyPlanEntity?,
    onDismiss: () -> Unit,
    onSave: (
        plannedStudy: String,
        actualStudy: String,
        plannedTests: String,
        testsDone: String,
        descriptive: String,
        adherence: String,
        satisfaction: String
    ) -> Unit
) {
    var plannedStudy by remember { mutableStateOf(plan?.plannedStudyHours ?: "") }
    var actualStudy by remember { mutableStateOf(plan?.actualStudyHours ?: "") }
    var plannedTests by remember { mutableStateOf(plan?.minPredictedTests ?: "") }
    var testsDone by remember { mutableStateOf(plan?.testsDone ?: "") }
    var descriptive by remember { mutableStateOf(plan?.descriptiveQuestions ?: "") }
    var adherence by remember { mutableStateOf(plan?.adherencePercent ?: "80") }
    var satisfaction by remember { mutableStateOf(plan?.satisfactionPercent ?: "85") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "ثبت آمار و جمع‌بندی روزانه (فقط اعداد)",
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
                        Text("ساعت مطالعه پیش‌بینی:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = plannedStudy,
                            onValueChange = { plannedStudy = it },
                            placeholder = { Text("مثلاً ۸", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ساعت انجام شده:", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = actualStudy,
                            onValueChange = { actualStudy = it },
                            placeholder = { Text("مثلاً ۷.۵", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("حداقل تست پیش‌بینی (عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = plannedTests,
                            onValueChange = { plannedTests = it.toLatinDigits().filter { c -> c.isDigit() } },
                            placeholder = { Text("۱۵۰", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("تست زده شده (عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                        OutlinedTextField(
                            value = testsDone,
                            onValueChange = { testsDone = it.toLatinDigits().filter { c -> c.isDigit() } },
                            placeholder = { Text("۱۴۰", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                Text("حل سوال تشریحی (عدد):", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                OutlinedTextField(
                    value = descriptive,
                    onValueChange = { descriptive = it.toLatinDigits().filter { c -> c.isDigit() } },
                    placeholder = { Text("۲۰", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text("درصد عمل به برنامه: %${adherence.toPersianDigits()}", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = adherence.toFloatOrNull() ?: 50f,
                    onValueChange = { adherence = it.toInt().toString() },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Text("درصد رضایت از امروز: %${satisfaction.toPersianDigits()}", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                Slider(
                    value = satisfaction.toFloatOrNull() ?: 50f,
                    onValueChange = { satisfaction = it.toInt().toString() },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(plannedStudy, actualStudy, plannedTests, testsDone, descriptive, adherence, satisfaction)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ذخیره", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}

@Composable
fun EditReflectionDialog(
    plan: DailyPlanEntity?,
    onDismiss: () -> Unit,
    onSave: (goodEvent: String, notes: String, gratitude: String) -> Unit
) {
    var goodEvent by remember { mutableStateOf(plan?.goodEvent ?: "") }
    var notes by remember { mutableStateOf(plan?.performanceNotes ?: "") }
    var gratitude by remember {
        mutableStateOf(
            plan?.gratitudeNotes?.ifBlank { "۱. \n۲. " } ?: "۱. \n۲. "
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        title = {
            Text(
                text = "ارزیابی، حال خوب و شکرگزاری",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("اتفاق خوب امروز چی بود؟", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = goodEvent,
                    onValueChange = { goodEvent = it },
                    placeholder = { Text("مثلاً حل یک تست سخت یا بیدار شدن سر ساعت!", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text("توضیحاتی در مورد عملکرد امروز:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("نقاط قوت و ضعف مطالعه امروز...", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Text("حداقل ۲ شکرگزاری بابت داشته‌هات:", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                OutlinedTextField(
                    value = gratitude,
                    onValueChange = { gratitude = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(goodEvent, notes, gratitude) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("ذخیره", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("انصراف", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ChipOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
            .border(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else TextSecondary,
            fontSize = 11.sp
        )
    }
}
