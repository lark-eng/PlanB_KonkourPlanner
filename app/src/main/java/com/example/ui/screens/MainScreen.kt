package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.example.util.ReportImageExporter
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PersianDatePickerDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkDivider
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LightBlueAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PlannerViewModel
import com.example.util.PersianDateHelper
import com.example.util.toPersianDigits
import java.time.LocalDate

@Composable
fun MainScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val weekDates by viewModel.weekDates.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedJalaliDate by viewModel.selectedJalaliDate.collectAsStateWithLifecycle()

    val currentDailyPlan by viewModel.currentDailyPlan.collectAsStateWithLifecycle()
    val currentStudySessions by viewModel.currentStudySessions.collectAsStateWithLifecycle()

    val currentWeeklyReport by viewModel.currentWeeklyReport.collectAsStateWithLifecycle()
    val weekStudySessions by viewModel.weekStudySessions.collectAsStateWithLifecycle()
    val weekDailyPlans by viewModel.weekDailyPlans.collectAsStateWithLifecycle()

    val examAnalyses by viewModel.examAnalyses.collectAsStateWithLifecycle()
    val selectedExamId by viewModel.selectedExamId.collectAsStateWithLifecycle()
    val currentExamQuestions by viewModel.currentExamQuestions.collectAsStateWithLifecycle()

    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val accentColor by viewModel.accentColor.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val today = remember { PersianDateHelper.getEffectiveToday() }
    val isTodayInCurrentWeek = weekDates.any { it == today }

    // Week range formatted string in Persian: e.g. "شنبه ۶ مهر تا جمعه ۱۲ مهر ۱۴۰۳"
    val weekRangeText = remember(weekDates) {
        val sat = PersianDateHelper.gregorianToJalali(weekDates.first())
        val fri = PersianDateHelper.gregorianToJalali(weekDates.last())
        "${sat.day.toPersianDigits()} ${sat.monthName} تا ${fri.day.toPersianDigits()} ${fri.monthName} ${fri.year.toPersianDigits()}"
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(0.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Top App Bar with App Branding & Top-Left Settings Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App title with App by @COD_LARK subtitle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0B0F19))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.planner_app_icon_1790885761223),
                                contentDescription = "Plan B Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column {
                            Text(
                                text = "Plan B",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 17.sp
                            )
                        }
                    }

                    val context = LocalContext.current

                    // Action buttons: Jump to Today + Share Button + Settings Button (top-left)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Jump to Today button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isTodayInCurrentWeek && selectedDate == today)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    1.dp,
                                    if (isTodayInCurrentWeek && selectedDate == today)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.goToToday() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = "امروز",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "برو به امروز",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isTodayInCurrentWeek && selectedDate == today) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Top-left Share Button (saves image to Downloads & launches share sheet)
                        IconButton(
                            onClick = {
                                when (selectedTab) {
                                    in 0..6 -> {
                                        val dayName = PersianDateHelper.getDayOfWeekName(selectedDate)
                                        ReportImageExporter.shareDailyReport(
                                            context = context,
                                            jalaliDate = selectedJalaliDate,
                                            dayOfWeekName = dayName,
                                            plan = currentDailyPlan,
                                            sessions = currentStudySessions
                                        )
                                    }
                                    7 -> {
                                        ReportImageExporter.shareWeeklyReport(
                                            context = context,
                                            weekRangeText = weekRangeText,
                                            report = currentWeeklyReport,
                                            weekSessions = weekStudySessions,
                                            weekPlans = weekDailyPlans
                                        )
                                    }
                                    8 -> {
                                        val exam = examAnalyses.find { it.id == selectedExamId } ?: examAnalyses.firstOrNull()
                                        ReportImageExporter.shareExamAnalysis(
                                            context = context,
                                            exam = exam,
                                            questions = currentExamQuestions
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.IosShare,
                                contentDescription = "اشتراک‌گذاری و ذخیره در پوشه Download",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Top-left Settings Button
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "تنظیمات",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Week Navigation Row with Date Picker trigger
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousWeek() },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "هفته قبل",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Tapping week range opens Persian Date Picker to jump to any date!
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showDatePickerDialog = true }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "تقویم",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "هفته: $weekRangeText",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(
                        onClick = { viewModel.nextWeek() },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "هفته بعد",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // If in Daily View (0..6), show horizontal 7 days selector
                if (selectedTab in 0..6) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        weekDates.forEachIndexed { index, date ->
                            val jDate = PersianDateHelper.gregorianToJalali(date)
                            val dayName = PersianDateHelper.weekDayNames[index]
                            val isSelected = (selectedTab == index)
                            val isActualToday = (date == today)

                            Box(
                                modifier = Modifier
                                    .width(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isActualToday && !isSelected) GoldAccent else if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.selectTab(index) }
                                    .padding(vertical = 6.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = dayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = jDate.day.toPersianDigits(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else if (isActualToday) GoldAccent else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                    if (isActualToday) {
                                        Text(
                                            text = "امروز",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else GoldAccent,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Tab 0..6: Daily Planner
                val isDailyActive = selectedTab in 0..6
                NavigationBarItem(
                    selected = isDailyActive,
                    onClick = {
                        if (!isDailyActive) {
                            val dayIdx = if (isTodayInCurrentWeek) {
                                PersianDateHelper.getPersianDayOfWeekIndex(today)
                            } else 0
                            viewModel.selectTab(dayIdx)
                        }
                    },
                    icon = {
                        Icon(Icons.Default.MenuBook, contentDescription = "پلنر روزانه")
                    },
                    label = {
                        Text("گزارش روزانه", fontSize = 11.sp, fontWeight = if (isDailyActive) FontWeight.Bold else FontWeight.Normal)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                // Tab 7: Weekly Report
                val isWeeklyActive = (selectedTab == 7)
                NavigationBarItem(
                    selected = isWeeklyActive,
                    onClick = { viewModel.selectTab(7) },
                    icon = {
                        Icon(Icons.Default.DateRange, contentDescription = "گزارش هفتگی")
                    },
                    label = {
                        Text("گزارش هفتگی", fontSize = 11.sp, fontWeight = if (isWeeklyActive) FontWeight.Bold else FontWeight.Normal)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )

                // Tab 8: Exam Analysis
                val isExamActive = (selectedTab == 8)
                NavigationBarItem(
                    selected = isExamActive,
                    onClick = { viewModel.selectTab(8) },
                    icon = {
                        Icon(Icons.Default.Assessment, contentDescription = "تحلیل آزمون")
                    },
                    label = {
                        Text("تحلیل آزمون", fontSize = 11.sp, fontWeight = if (isExamActive) FontWeight.Bold else FontWeight.Normal)
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(160, easing = FastOutSlowInEasing)) +
                            scaleIn(initialScale = 0.98f, animationSpec = tween(160, easing = FastOutSlowInEasing)))
                        .togetherWith(
                            fadeOut(animationSpec = tween(120, easing = FastOutSlowInEasing)) +
                                    scaleOut(targetScale = 1.02f, animationSpec = tween(120, easing = FastOutSlowInEasing))
                        )
                },
                label = "TabContentAnimation"
            ) { targetTab ->
                when (targetTab) {
                    in 0..6 -> {
                        DailyPlannerScreen(
                            jalaliDate = selectedJalaliDate,
                            dayOfWeekName = PersianDateHelper.weekDayNames[targetTab],
                            plan = currentDailyPlan,
                            sessions = currentStudySessions,
                            viewModel = viewModel
                        )
                    }

                    7 -> {
                        WeeklyReportScreen(
                            report = currentWeeklyReport,
                            weekSessions = weekStudySessions,
                            weekPlans = weekDailyPlans,
                            viewModel = viewModel
                        )
                    }

                    8 -> {
                        ExamAnalysisScreen(
                            exams = examAnalyses,
                            selectedExamId = selectedExamId,
                            questions = currentExamQuestions,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }

    // Floating Settings Dialog as requested!
    if (showSettingsDialog) {
        SettingsDialog(
            currentThemeMode = themeMode,
            currentAccentColor = accentColor,
            onThemeModeChange = { viewModel.setThemeMode(it) },
            onAccentColorChange = { viewModel.setAccentColor(it) },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // Persian Date Picker to jump to any date directly
    if (showDatePickerDialog) {
        PersianDatePickerDialog(
            initialDate = selectedJalaliDate,
            onDismiss = { showDatePickerDialog = false },
            onDateSelected = { picked ->
                viewModel.jumpToJalaliDate(picked)
                showDatePickerDialog = false
            }
        )
    }
}
