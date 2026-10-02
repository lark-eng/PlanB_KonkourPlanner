package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.PlannerDatabase
import com.example.data.model.DailyPlanEntity
import com.example.data.model.ExamAnalysisEntity
import com.example.data.model.ExamQuestionEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.WeeklyReportEntity
import com.example.data.repository.PlannerRepository
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.ElectricBlue
import com.example.util.JalaliDate
import com.example.util.PersianDateHelper
import com.example.util.StudyReminderHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlannerRepository

    init {
        val db = PlannerDatabase.getDatabase(application)
        repository = PlannerRepository(db.plannerDao())
    }

    // App Theme State
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _accentColor = MutableStateFlow<Color>(ElectricBlue)
    val accentColor: StateFlow<Color> = _accentColor.asStateFlow()

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun setAccentColor(color: Color) {
        _accentColor.value = color
    }

    // Active reference date - calibrated to real academic year (1403/1404)
    private val effectiveToday = PersianDateHelper.getEffectiveToday()

    private val _currentReferenceDate = MutableStateFlow(effectiveToday)
    val currentReferenceDate: StateFlow<LocalDate> = _currentReferenceDate.asStateFlow()

    // 0: شنبه ... 6: جمعه, 7: گزارش هفته, 8: تحلیل آزمون
    private val _selectedTab = MutableStateFlow(
        PersianDateHelper.getPersianDayOfWeekIndex(effectiveToday)
    )
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // 7 days of the active Persian week
    val weekDates: StateFlow<List<LocalDate>> = _currentReferenceDate.combine(_selectedTab) { ref, _ ->
        PersianDateHelper.getWeekDates(ref)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PersianDateHelper.getWeekDates(effectiveToday)
    )

    // Current selected date in the week
    val selectedDate: StateFlow<LocalDate> = combine(_currentReferenceDate, _selectedTab) { ref, tab ->
        val dates = PersianDateHelper.getWeekDates(ref)
        if (tab in 0..6) dates[tab] else dates.first()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = effectiveToday
    )

    // Current Jalali date
    val selectedJalaliDate: StateFlow<JalaliDate> = selectedDate.combine(flowOf(Unit)) { date, _ ->
        PersianDateHelper.gregorianToJalali(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PersianDateHelper.gregorianToJalali(effectiveToday)
    )

    // Current daily plan entity flow
    val currentDailyPlan: StateFlow<DailyPlanEntity?> = selectedDate.flatMapLatest { date ->
        val jDate = PersianDateHelper.gregorianToJalali(date)
        repository.getDailyPlanFlow(jDate.toKey())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Current study sessions flow
    val currentStudySessions: StateFlow<List<StudySessionEntity>> = selectedDate.flatMapLatest { date ->
        val jDate = PersianDateHelper.gregorianToJalali(date)
        val dayIdx = PersianDateHelper.getPersianDayOfWeekIndex(date)
        repository.getStudySessionsFlow(jDate.toKey(), dayIdx)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Week start key (Saturday's Jalali key)
    val currentWeekKey: StateFlow<String> = weekDates.combine(flowOf(Unit)) { dates, _ ->
        val sat = dates.first()
        PersianDateHelper.gregorianToJalali(sat).toKey()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = PersianDateHelper.gregorianToJalali(PersianDateHelper.getWeekDates(effectiveToday).first()).toKey()
    )

    // Current weekly report flow
    val currentWeeklyReport: StateFlow<WeeklyReportEntity?> = currentWeekKey.flatMapLatest { key ->
        repository.getWeeklyReportFlow(key)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // All study sessions for the current week
    val weekStudySessions: StateFlow<List<StudySessionEntity>> = weekDates.flatMapLatest { dates ->
        val dateKeys = dates.map { PersianDateHelper.gregorianToJalali(it).toKey() }
        repository.getStudySessionsForWeek(dateKeys)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All daily plans for the current week
    val weekDailyPlans: StateFlow<List<DailyPlanEntity>> = weekDates.flatMapLatest { dates ->
        val dateKeys = dates.map { PersianDateHelper.gregorianToJalali(it).toKey() }
        repository.getDailyPlansForDates(dateKeys)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Exam analyses list for the current week (separated per week as requested)
    val examAnalyses: StateFlow<List<ExamAnalysisEntity>> = currentWeekKey.flatMapLatest { weekKey ->
        repository.getExamAnalysesForWeekFlow(weekKey)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedExamId = MutableStateFlow<Long?>(null)

    // Current effective selected exam ID, scoped strictly to the current week's exams
    val selectedExamId: StateFlow<Long?> = combine(examAnalyses, _selectedExamId) { list, explicitId ->
        if (explicitId != null && list.any { it.id == explicitId }) {
            explicitId
        } else {
            list.firstOrNull()?.id
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val currentExamQuestions: StateFlow<List<ExamQuestionEntity>> = selectedExamId.flatMapLatest { id ->
        if (id != null) repository.getExamQuestionsFlow(id) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Week navigation
    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun previousWeek() {
        _currentReferenceDate.value = _currentReferenceDate.value.minusWeeks(1)
    }

    fun nextWeek() {
        _currentReferenceDate.value = _currentReferenceDate.value.plusWeeks(1)
    }

    fun goToToday() {
        val today = PersianDateHelper.getEffectiveToday()
        _currentReferenceDate.value = today
        _selectedTab.value = PersianDateHelper.getPersianDayOfWeekIndex(today)
    }

    fun jumpToJalaliDate(jalaliDate: JalaliDate) {
        val gregorian = PersianDateHelper.jalaliToGregorian(jalaliDate.year, jalaliDate.month, jalaliDate.day)
        _currentReferenceDate.value = gregorian
        _selectedTab.value = PersianDateHelper.getPersianDayOfWeekIndex(gregorian)
    }

    // Daily plan actions
    fun saveDailyPlan(update: (DailyPlanEntity) -> DailyPlanEntity) {
        viewModelScope.launch {
            val date = selectedDate.value
            val jDate = PersianDateHelper.gregorianToJalali(date)
            val key = jDate.toKey()
            val existing = repository.getDailyPlan(key) ?: DailyPlanEntity(
                dateKey = key,
                gregorianDate = date.toString(),
                dayOfWeek = PersianDateHelper.getDayOfWeekName(date),
                examCountdown = PersianDateHelper.getDaysUntilKonkur(date).toString()
            )
            val newPlan = update(existing)
            repository.upsertDailyPlan(newPlan)
        }
    }

    fun toggleSocialMediaSlot(slotNumber: Int) {
        viewModelScope.launch {
            val date = selectedDate.value
            val jDate = PersianDateHelper.gregorianToJalali(date)
            val key = jDate.toKey()
            val existing = repository.getDailyPlan(key) ?: DailyPlanEntity(
                dateKey = key,
                gregorianDate = date.toString(),
                dayOfWeek = PersianDateHelper.getDayOfWeekName(date),
                examCountdown = PersianDateHelper.getDaysUntilKonkur(date).toString()
            )
            val currentSlots = existing.socialMediaSlots.split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { it.trim().toIntOrNull() }
                .toMutableSet()

            if (currentSlots.contains(slotNumber)) {
                currentSlots.remove(slotNumber)
            } else {
                currentSlots.add(slotNumber)
            }

            val updatedSlotsStr = currentSlots.sorted().joinToString(",")
            repository.upsertDailyPlan(existing.copy(socialMediaSlots = updatedSlotsStr))
        }
    }

    // Study sessions actions
    fun addStudySession(session: StudySessionEntity) {
        viewModelScope.launch {
            val date = selectedDate.value
            val jDate = PersianDateHelper.gregorianToJalali(date)
            val dayIdx = PersianDateHelper.getPersianDayOfWeekIndex(date)
            val sessionToInsert = session.copy(
                dateKey = jDate.toKey(),
                dayOfWeekIndex = dayIdx
            )
            val id = repository.insertStudySession(sessionToInsert)
            val savedSession = sessionToInsert.copy(id = id)
            if (savedSession.hasReminder) {
                StudyReminderHelper.scheduleReminder(getApplication(), savedSession, date)
            }
        }
    }

    fun updateStudySession(session: StudySessionEntity) {
        viewModelScope.launch {
            val date = selectedDate.value
            val dayIdx = PersianDateHelper.getPersianDayOfWeekIndex(date)
            val updated = session.copy(dayOfWeekIndex = dayIdx)
            repository.updateStudySession(updated)

            if (updated.hasReminder) {
                StudyReminderHelper.scheduleReminder(getApplication(), updated, date)
            } else {
                StudyReminderHelper.cancelReminder(getApplication(), updated.id)
            }
        }
    }

    fun deleteStudySession(id: Long) {
        viewModelScope.launch {
            StudyReminderHelper.cancelReminder(getApplication(), id)
            repository.deleteStudySession(id)
        }
    }

    // Weekly report actions
    fun saveWeeklyReport(update: (WeeklyReportEntity) -> WeeklyReportEntity) {
        viewModelScope.launch {
            val weekKey = currentWeekKey.value
            val existing = repository.getWeeklyReportFlow(weekKey)
            val current = existing.stateIn(viewModelScope).value ?: WeeklyReportEntity(weekKey = weekKey)
            val updated = update(current)
            repository.upsertWeeklyReport(updated)
        }
    }

    // Exam analysis actions
    fun selectExam(examId: Long?) {
        _selectedExamId.value = examId
    }

    fun setSocialMediaHours(hours: String) {
        viewModelScope.launch {
            val date = selectedDate.value
            val jDate = PersianDateHelper.gregorianToJalali(date)
            val key = jDate.toKey()
            val existing = repository.getDailyPlan(key) ?: DailyPlanEntity(
                dateKey = key,
                gregorianDate = date.toString(),
                dayOfWeek = PersianDateHelper.getDayOfWeekName(date),
                examCountdown = PersianDateHelper.getDaysUntilKonkur(date).toString()
            )
            repository.upsertDailyPlan(existing.copy(socialMediaSlots = hours))
        }
    }

    fun createNewExamAnalysis(
        title: String,
        date: String,
        taraz: String,
        rank: String = "",
        bio: String = "",
        physics: String = "",
        chem: String = "",
        math: String = "",
        geo: String = "",
        onCreated: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val currentWeek = currentWeekKey.value
            val id = repository.insertExamAnalysis(
                ExamAnalysisEntity(
                    weekKey = currentWeek,
                    examTitle = title.ifBlank { "آزمون آزمایشی" },
                    examDate = date.ifBlank { PersianDateHelper.gregorianToJalali(LocalDate.now()).format() },
                    totalTaraz = taraz,
                    totalRank = rank,
                    biologyPercent = bio,
                    physicsPercent = physics,
                    chemistryPercent = chem,
                    mathPercent = math,
                    geologyPercent = geo
                )
            )
            _selectedExamId.value = id
            onCreated(id)
        }
    }

    fun updateExamAnalysis(exam: ExamAnalysisEntity) {
        viewModelScope.launch {
            repository.updateExamAnalysis(exam)
        }
    }

    fun deleteExamAnalysis(examId: Long) {
        viewModelScope.launch {
            repository.deleteExamAnalysis(examId)
            if (_selectedExamId.value == examId) {
                _selectedExamId.value = null
            }
        }
    }

    fun addExamQuestion(question: ExamQuestionEntity) {
        viewModelScope.launch {
            val examId = _selectedExamId.value ?: return@launch
            repository.insertExamQuestion(question.copy(examId = examId))
        }
    }

    fun updateExamQuestion(question: ExamQuestionEntity) {
        viewModelScope.launch {
            repository.updateExamQuestion(question)
        }
    }

    fun deleteExamQuestion(id: Long) {
        viewModelScope.launch {
            repository.deleteExamQuestion(id)
        }
    }
}
