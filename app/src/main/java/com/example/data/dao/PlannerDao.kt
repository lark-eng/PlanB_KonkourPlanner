package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyPlanEntity
import com.example.data.model.ExamAnalysisEntity
import com.example.data.model.ExamQuestionEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.WeeklyReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {

    // --- Daily Plans ---
    @Query("SELECT * FROM daily_plans WHERE dateKey = :dateKey LIMIT 1")
    fun getDailyPlanFlow(dateKey: String): Flow<DailyPlanEntity?>

    @Query("SELECT * FROM daily_plans WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getDailyPlan(dateKey: String): DailyPlanEntity?

    @Query("SELECT * FROM daily_plans WHERE dateKey IN (:dateKeys)")
    fun getDailyPlansForDates(dateKeys: List<String>): Flow<List<DailyPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyPlan(dailyPlan: DailyPlanEntity)

    // --- Study Sessions ---
    @Query("SELECT * FROM study_sessions WHERE dateKey = :dateKey OR (isWeeklyRepeat = 1 AND dayOfWeekIndex = :dayOfWeekIndex) ORDER BY id ASC")
    fun getStudySessionsFlow(dateKey: String, dayOfWeekIndex: Int): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE dateKey IN (:dateKeys) OR isWeeklyRepeat = 1 ORDER BY id ASC")
    fun getStudySessionsForWeek(dateKeys: List<String>): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity): Long

    @Update
    suspend fun updateStudySession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteStudySession(id: Long)

    // --- Weekly Reports ---
    @Query("SELECT * FROM weekly_reports WHERE weekKey = :weekKey LIMIT 1")
    fun getWeeklyReportFlow(weekKey: String): Flow<WeeklyReportEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWeeklyReport(report: WeeklyReportEntity)

    // --- Exam Analyses ---
    @Query("SELECT * FROM exam_analyses WHERE weekKey = :weekKey ORDER BY id DESC")
    fun getExamAnalysesForWeekFlow(weekKey: String): Flow<List<ExamAnalysisEntity>>

    @Query("SELECT * FROM exam_analyses ORDER BY id DESC")
    fun getAllExamAnalysesFlow(): Flow<List<ExamAnalysisEntity>>

    @Query("SELECT * FROM exam_analyses WHERE id = :id LIMIT 1")
    suspend fun getExamAnalysis(id: Long): ExamAnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamAnalysis(exam: ExamAnalysisEntity): Long

    @Update
    suspend fun updateExamAnalysis(exam: ExamAnalysisEntity)

    @Query("DELETE FROM exam_analyses WHERE id = :id")
    suspend fun deleteExamAnalysis(id: Long)

    @Query("DELETE FROM exam_questions WHERE examId = :examId")
    suspend fun deleteExamQuestionsForExam(examId: Long)

    // --- Exam Questions ---
    @Query("SELECT * FROM exam_questions WHERE examId = :examId ORDER BY id ASC")
    fun getExamQuestionsFlow(examId: Long): Flow<List<ExamQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamQuestion(question: ExamQuestionEntity): Long

    @Update
    suspend fun updateExamQuestion(question: ExamQuestionEntity)

    @Query("DELETE FROM exam_questions WHERE id = :id")
    suspend fun deleteExamQuestion(id: Long)
}
