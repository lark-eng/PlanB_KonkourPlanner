package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.PlannerDao
import com.example.data.model.DailyPlanEntity
import com.example.data.model.ExamAnalysisEntity
import com.example.data.model.ExamQuestionEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.WeeklyReportEntity

@Database(
    entities = [
        DailyPlanEntity::class,
        StudySessionEntity::class,
        WeeklyReportEntity::class,
        ExamAnalysisEntity::class,
        ExamQuestionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class PlannerDatabase : RoomDatabase() {
    abstract fun plannerDao(): PlannerDao

    companion object {
        @Volatile
        private var INSTANCE: PlannerDatabase? = null

        fun getDatabase(context: Context): PlannerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlannerDatabase::class.java,
                    "konkur_planner_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
