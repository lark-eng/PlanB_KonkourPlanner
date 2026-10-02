package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.DailyPlanEntity
import com.example.data.model.StudySessionEntity
import com.example.ui.components.DailyReflectionSection
import com.example.ui.components.DailySummarySection
import com.example.ui.components.EditGoalsDialog
import com.example.ui.components.EditHeaderDialog
import com.example.ui.components.EditReflectionDialog
import com.example.ui.components.EditSessionDialog
import com.example.ui.components.EditSummaryDialog
import com.example.ui.components.GoalBanner
import com.example.ui.components.PersianHeader
import com.example.ui.components.SocialMediaTracker
import com.example.ui.components.StudyTable
import com.example.ui.viewmodel.PlannerViewModel
import com.example.util.JalaliDate

@Composable
fun DailyPlannerScreen(
    jalaliDate: JalaliDate,
    dayOfWeekName: String,
    plan: DailyPlanEntity?,
    sessions: List<StudySessionEntity>,
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    var showHeaderDialog by remember { mutableStateOf(false) }
    var showGoalsDialog by remember { mutableStateOf(false) }
    var showSummaryDialog by remember { mutableStateOf(false) }
    var showReflectionDialog by remember { mutableStateOf(false) }
    var editingSession by remember { mutableStateOf<StudySessionEntity?>(null) }
    var showAddSessionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header pills row matching PDF
        PersianHeader(
            jalaliDate = jalaliDate,
            dayOfWeekName = dayOfWeekName,
            plan = plan,
            onEditHeaderClick = { showHeaderDialog = true }
        )

        // 2. Goal & Motivation banner matching PDF top cards
        GoalBanner(
            plan = plan,
            onEditClick = { showGoalsDialog = true }
        )

        // 3. Main study sessions table matching PDF
        StudyTable(
            sessions = sessions,
            onAddSessionClick = { showAddSessionDialog = true },
            onEditSessionClick = { editingSession = it },
            onDeleteSessionClick = { viewModel.deleteStudySession(it) }
        )

        // 4. Social media hours tracker (2-digit hours input)
        SocialMediaTracker(
            hoursStr = plan?.socialMediaSlots ?: "",
            onHoursChanged = { viewModel.setSocialMediaHours(it) }
        )

        // 5. Daily statistics summary boxes matching PDF
        DailySummarySection(
            plan = plan,
            sessions = sessions,
            onEditSummaryClick = { showSummaryDialog = true }
        )

        // 6. Reflection, good event & gratitude cards matching PDF bottom
        DailyReflectionSection(
            plan = plan,
            onEditReflectionClick = { showReflectionDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialogs
    if (showHeaderDialog) {
        EditHeaderDialog(
            plan = plan,
            onDismiss = { showHeaderDialog = false },
            onSave = { sleep, wake, mood, countdown ->
                viewModel.saveDailyPlan {
                    it.copy(
                        sleepTime = sleep,
                        wakeTime = wake,
                        mood = mood,
                        examCountdown = countdown
                    )
                }
                showHeaderDialog = false
            }
        )
    }

    if (showGoalsDialog) {
        EditGoalsDialog(
            plan = plan,
            onDismiss = { showGoalsDialog = false },
            onSave = { challenge, goal, quote ->
                viewModel.saveDailyPlan {
                    it.copy(
                        challenge = challenge,
                        goal = goal,
                        quote = quote
                    )
                }
                showGoalsDialog = false
            }
        )
    }

    if (showAddSessionDialog) {
        EditSessionDialog(
            session = null,
            onDismiss = { showAddSessionDialog = false },
            onSave = { newSession ->
                viewModel.addStudySession(newSession)
                showAddSessionDialog = false
            }
        )
    }

    editingSession?.let { session ->
        EditSessionDialog(
            session = session,
            onDismiss = { editingSession = null },
            onSave = { updatedSession ->
                viewModel.updateStudySession(updatedSession)
                editingSession = null
            }
        )
    }

    if (showSummaryDialog) {
        EditSummaryDialog(
            plan = plan,
            onDismiss = { showSummaryDialog = false },
            onSave = { plannedStudy, actualStudy, plannedTests, testsDone, descriptive, adherence, satisfaction ->
                viewModel.saveDailyPlan {
                    it.copy(
                        plannedStudyHours = plannedStudy,
                        actualStudyHours = actualStudy,
                        minPredictedTests = plannedTests,
                        testsDone = testsDone,
                        descriptiveQuestions = descriptive,
                        adherencePercent = adherence,
                        satisfactionPercent = satisfaction
                    )
                }
                showSummaryDialog = false
            }
        )
    }

    if (showReflectionDialog) {
        EditReflectionDialog(
            plan = plan,
            onDismiss = { showReflectionDialog = false },
            onSave = { goodEvent, notes, gratitude ->
                viewModel.saveDailyPlan {
                    it.copy(
                        goodEvent = goodEvent,
                        performanceNotes = notes,
                        gratitudeNotes = gratitude
                    )
                }
                showReflectionDialog = false
            }
        )
    }
}
