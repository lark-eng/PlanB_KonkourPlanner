package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey
    val dateKey: String, // Format: YYYY-MM-DD (Jalali)
    val gregorianDate: String = "",
    val dayOfWeek: String = "",
    val sleepTime: String = "",
    val wakeTime: String = "",
    val mood: String = "",
    val examCountdown: String = "",
    val challenge: String = "",
    val goal: String = "",
    val quote: String = "",
    val socialMediaSlots: String = "", // Comma-separated slots e.g. "1,2,5"
    val plannedStudyHours: String = "",
    val actualStudyHours: String = "",
    val minPredictedTests: String = "",
    val testsDone: String = "",
    val descriptiveQuestions: String = "",
    val adherencePercent: String = "",
    val satisfactionPercent: String = "",
    val goodEvent: String = "",
    val performanceNotes: String = "",
    val gratitudeNotes: String = ""
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String,
    val startTime: String = "",
    val endTime: String = "",
    val subject: String = "",
    val topic: String = "",
    val taskType: String = "",
    val distractions: Int = 0,
    val studyDuration: String = "",
    val testCount: String = "",
    val testDuration: String = "",
    val testQuizPercent: String = "",
    val isWeeklyRepeat: Boolean = false,
    val dayOfWeekIndex: Int = -1, // 0 = شنبه, ..., 6 = جمعه
    val hasReminder: Boolean = false
)

@Entity(tableName = "weekly_reports")
data class WeeklyReportEntity(
    @PrimaryKey
    val weekKey: String, // Start date of week (Saturday Jalali key)
    val conclusion: String = "",
    val motivationQuote: String = "",
    val manualTotalStudy: String = "",
    val manualTotalTests: String = "",
    val manualTotalSleep: String = "",
    val manualTotalSocial: String = "",
    val subjectsData: String = "" // Formatted subjects data
)

@Entity(tableName = "exam_analyses")
data class ExamAnalysisEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val weekKey: String = "",        // کلید هفته مربوطه جهت تفکیک آزمون‌های هر هفته
    val examTitle: String = "آزمون آزمایشی",
    val examDate: String = "",
    val totalTaraz: String = "",     // تراز کل
    val totalRank: String = "",      // رتبه کل (جداگانه)
    val biologyPercent: String = "",  // درصد زیست‌شناسی
    val physicsPercent: String = "",  // درصد فیزیک
    val chemistryPercent: String = "",// درصد شیمی
    val mathPercent: String = "",     // درصد ریاضی
    val geologyPercent: String = "",  // درصد زمین‌شناسی
    val notes: String = ""
)

@Entity(tableName = "exam_questions")
data class ExamQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val examId: Long,
    val questionNumber: String = "",
    val subject: String = "",
    val isUnanswered: Boolean = false,
    val isWrong: Boolean = false,
    val reason: String = "", // فراموشی، بی‌دقتی در خواندن، بی‌دقتی محاسباتی، کمبود وقت، متوجه نشدن سوال، عدم حل تست مشابه، اشکال انتقال پاسخ
    val topicAndDecision: String = ""
)
