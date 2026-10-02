package com.example.data.repository

import com.example.data.dao.PlannerDao
import com.example.data.model.DailyPlanEntity
import com.example.data.model.ExamAnalysisEntity
import com.example.data.model.ExamQuestionEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.WeeklyReportEntity
import kotlinx.coroutines.flow.Flow

class PlannerRepository(private val dao: PlannerDao) {

    fun getDailyPlanFlow(dateKey: String): Flow<DailyPlanEntity?> =
        dao.getDailyPlanFlow(dateKey)

    suspend fun getDailyPlan(dateKey: String): DailyPlanEntity? =
        dao.getDailyPlan(dateKey)

    fun getDailyPlansForDates(dateKeys: List<String>): Flow<List<DailyPlanEntity>> =
        dao.getDailyPlansForDates(dateKeys)

    suspend fun upsertDailyPlan(plan: DailyPlanEntity) =
        dao.upsertDailyPlan(plan)

    fun getStudySessionsFlow(dateKey: String, dayOfWeekIndex: Int): Flow<List<StudySessionEntity>> =
        dao.getStudySessionsFlow(dateKey, dayOfWeekIndex)

    fun getStudySessionsForWeek(dateKeys: List<String>): Flow<List<StudySessionEntity>> =
        dao.getStudySessionsForWeek(dateKeys)

    suspend fun insertStudySession(session: StudySessionEntity): Long =
        dao.insertStudySession(session)

    suspend fun updateStudySession(session: StudySessionEntity) =
        dao.updateStudySession(session)

    suspend fun deleteStudySession(id: Long) =
        dao.deleteStudySession(id)

    fun getWeeklyReportFlow(weekKey: String): Flow<WeeklyReportEntity?> =
        dao.getWeeklyReportFlow(weekKey)

    suspend fun upsertWeeklyReport(report: WeeklyReportEntity) =
        dao.upsertWeeklyReport(report)

    fun getExamAnalysesForWeekFlow(weekKey: String): Flow<List<ExamAnalysisEntity>> =
        dao.getExamAnalysesForWeekFlow(weekKey)

    fun getAllExamAnalysesFlow(): Flow<List<ExamAnalysisEntity>> =
        dao.getAllExamAnalysesFlow()

    suspend fun getExamAnalysis(id: Long): ExamAnalysisEntity? =
        dao.getExamAnalysis(id)

    suspend fun insertExamAnalysis(exam: ExamAnalysisEntity): Long =
        dao.insertExamAnalysis(exam)

    suspend fun updateExamAnalysis(exam: ExamAnalysisEntity) =
        dao.updateExamAnalysis(exam)

    suspend fun deleteExamAnalysis(id: Long) {
        dao.deleteExamQuestionsForExam(id)
        dao.deleteExamAnalysis(id)
    }

    fun getExamQuestionsFlow(examId: Long): Flow<List<ExamQuestionEntity>> =
        dao.getExamQuestionsFlow(examId)

    suspend fun insertExamQuestion(question: ExamQuestionEntity): Long =
        dao.insertExamQuestion(question)

    suspend fun updateExamQuestion(question: ExamQuestionEntity) =
        dao.updateExamQuestion(question)

    suspend fun deleteExamQuestion(id: Long) =
        dao.deleteExamQuestion(id)
}
