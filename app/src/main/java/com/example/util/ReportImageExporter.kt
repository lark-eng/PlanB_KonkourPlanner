package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.DailyPlanEntity
import com.example.data.model.ExamAnalysisEntity
import com.example.data.model.ExamQuestionEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.WeeklyReportEntity
import java.io.File
import java.io.FileOutputStream

object ReportImageExporter {

    private val PURPLE_PRIMARY = Color.parseColor("#6B21A8")
    private val PURPLE_LIGHT = Color.parseColor("#F3E8FF")
    private val PURPLE_DARK = Color.parseColor("#4C1D95")
    private val TEXT_DARK = Color.parseColor("#0F172A")
    private val TEXT_MUTED = Color.parseColor("#64748B")
    private val TEXT_FOOTER = Color.parseColor("#94A3B8")
    private val BORDER_COLOR = Color.parseColor("#CBD5E1")
    private val BG_WHITE = Color.parseColor("#FFFFFF")
    private val BG_LIGHT = Color.parseColor("#F8FAFC")
    private val GREEN_ACCENT = Color.parseColor("#15803D")
    private val YELLOW_ACCENT = Color.parseColor("#B45309")
    private val RED_ACCENT = Color.parseColor("#B91C1C")

    private fun createPaint(): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isSubpixelText = true
        }
    }

    /**
     * Saves the bitmap into the user's Downloads folder and launches the Android share sheet.
     */
    private fun saveToDownloadsAndShare(context: Context, bitmap: Bitmap, fileName: String, title: String) {
        // 1. Save to user's public Downloads directory in PlanB folder
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/PlanB")
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val appDir = File(downloadsDir, "PlanB").apply { mkdirs() }
                val file = File(appDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. Also ensure a shareable copy in cache for zero-permission sharing
        val cachePath = File(context.cacheDir, "shared_reports").apply { mkdirs() }
        val shareFile = File(cachePath, fileName)
        FileOutputStream(shareFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val shareUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile
        )

        Toast.makeText(context, "گزارش در پوشه Download ذخیره و آماده اشتراک‌گذاری شد", Toast.LENGTH_LONG).show()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, shareUri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری گزارش Plan B"))
    }

    /**
     * Renders the Daily Report matching PDF Pages 1-7.
     * All columns in RTL order (Start-End Time on the right),
     * all texts perfectly centered in their cells,
     * separate Social Media card between Gratitude & Good Event,
     * and equal spacing in the top header.
     */
    fun shareDailyReport(
        context: Context,
        jalaliDate: JalaliDate,
        dayOfWeekName: String,
        plan: DailyPlanEntity?,
        sessions: List<StudySessionEntity>
    ) {
        val width = 1240
        val rowCount = maxOf(sessions.size, 8)
        val rowHeight = 62f
        val height = 1420 + (rowCount * rowHeight.toInt())
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(BG_WHITE)

        val paint = createPaint()
        val fillPaint = createPaint()
        val strokePaint = createPaint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = BORDER_COLOR
        }

        // Outer border
        val borderBottom = height - 52f
        strokePaint.strokeWidth = 3f
        strokePaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(RectF(25f, 25f, width - 25f, borderBottom), 24f, 24f, strokePaint)

        // 1. TOP HEADER (PDF style) with equal spaced badges
        val topRect = RectF(40f, 40f, width - 40f, 165f)
        strokePaint.strokeWidth = 2f
        strokePaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(topRect, 18f, 18f, strokePaint)

        // Logo circle on the right/left
        fillPaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(RectF(55f, 55f, 145f, 150f), 16f, 16f, fillPaint)
        paint.color = Color.WHITE
        paint.textSize = 36f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("B", 100f, 115f, paint)

        // 6 evenly distributed slots from x = 160f to x = width - 50f (1030px)
        val headerSlotWidth = (width - 210f) / 6f
        val dateStr = "${jalaliDate.year}/${jalaliDate.month}/${jalaliDate.day}".toPersianDigits()
        val sleepStr = plan?.sleepTime?.ifBlank { "—" } ?: "—"
        val wakeStr = plan?.wakeTime?.ifBlank { "—" } ?: "—"
        val moodStr = plan?.mood?.ifBlank { "پرانرژی" } ?: "پرانرژی"
        val countdownStr = plan?.examCountdown?.ifBlank { "—" }?.toPersianDigits() ?: "—"

        val headerBadges = listOf(
            "روز: $dayOfWeekName",
            "تاریخ: $dateStr",
            "خواب: $sleepStr",
            "بیداری: $wakeStr",
            "حال دل: $moodStr",
            "روزشمار: $countdownStr روز"
        )

        headerBadges.forEachIndexed { i, text ->
            val slotCenter = 160f + (i * headerSlotWidth) + (headerSlotWidth / 2f)
            val pillWidth = headerSlotWidth - 12f
            val pillRect = RectF(slotCenter - (pillWidth / 2f), 70f, slotCenter + (pillWidth / 2f), 135f)
            fillPaint.color = PURPLE_LIGHT
            canvas.drawRoundRect(pillRect, 12f, 12f, fillPaint)
            strokePaint.color = PURPLE_PRIMARY
            strokePaint.strokeWidth = 1f
            canvas.drawRoundRect(pillRect, 12f, 12f, strokePaint)

            paint.color = PURPLE_DARK
            paint.textSize = 19f
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(text, slotCenter, 108f, paint)
        }

        // 2. SUB-BANNER: چالش درس امروز | هدف امروز | جمله انگیزشی
        var yPos = 185f
        val goalRect = RectF(40f, yPos, width - 40f, yPos + 145f)
        strokePaint.color = BORDER_COLOR
        strokePaint.strokeWidth = 2f
        canvas.drawRoundRect(goalRect, 16f, 16f, strokePaint)

        val bannerBoxWidth = (width - 110f) / 3f

        // Right box: چالش درس امروز
        val b1Center = 40f + 15f + (bannerBoxWidth / 2f)
        fillPaint.color = Color.parseColor("#ECFDF5")
        canvas.drawRoundRect(RectF(40f + 15f, yPos + 10f, 40f + 15f + bannerBoxWidth, yPos + 135f), 12f, 12f, fillPaint)
        paint.color = GREEN_ACCENT
        paint.textSize = 23f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("🎯 چالش درس امروز", b1Center, yPos + 45f, paint)
        paint.color = TEXT_DARK
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(plan?.challenge?.ifBlank { "ثبت نشده" }?.take(22) ?: "ثبت نشده", b1Center, yPos + 92f, paint)

        // Center box: هدف امروز
        val b2Center = 40f + 30f + bannerBoxWidth + (bannerBoxWidth / 2f)
        fillPaint.color = PURPLE_LIGHT
        canvas.drawRoundRect(RectF(40f + 30f + bannerBoxWidth, yPos + 10f, 40f + 30f + (2 * bannerBoxWidth), yPos + 135f), 12f, 12f, fillPaint)
        paint.color = PURPLE_DARK
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("🎯 هـدف امـروز", b2Center, yPos + 45f, paint)
        paint.color = TEXT_DARK
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(plan?.goal?.ifBlank { "ثبت نشده" }?.take(25) ?: "ثبت نشده", b2Center, yPos + 92f, paint)

        // Left box: جمله انگیزشی
        val b3Center = 40f + 45f + (2 * bannerBoxWidth) + (bannerBoxWidth / 2f)
        fillPaint.color = Color.parseColor("#FEF3C7")
        canvas.drawRoundRect(RectF(40f + 45f + (2 * bannerBoxWidth), yPos + 10f, width - 55f, yPos + 135f), 12f, 12f, fillPaint)
        paint.color = YELLOW_ACCENT
        paint.textSize = 23f
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("✨ جمله انگیزشی", b3Center, yPos + 45f, paint)
        paint.color = TEXT_DARK
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(plan?.quote?.ifBlank { "امروز بهترین خودت باش!" }?.take(22) ?: "امروز بهترین خودت باش!", b3Center, yPos + 92f, paint)

        // 3. MAIN STUDY TABLE (RTL ORDER: Start-End time on the Right, all centered)
        yPos += 165f
        val tableLeft = 40f
        val tableWidth = width - 80f // 1160f

        // Column specifications from Left to Right:
        // Leftmost (Col 0) to Rightmost (Col 8):
        // 0: درصد آزمونک (115f)
        // 1: زمان تست (115f)
        // 2: تعداد تست (110f)
        // 3: زمان مطالعه (125f)
        // 4: حواس‌پرتی (110f)
        // 5: نوع کار (125f)
        // 6: مبحث / فصل (180f)
        // 7: نام درس (140f)
        // 8: ساعت شروع - پایان (140f) -> RIGHTMOST!
        val colWidths = listOf(115f, 115f, 110f, 125f, 110f, 125f, 180f, 140f, 140f)
        val colHeaders = listOf(
            "درصد آزمونک",
            "زمان تست",
            "تعداد تست",
            "زمان مطالعه",
            "حواس‌پرتی",
            "نوع کار",
            "مبحث / فصل",
            "نام درس",
            "ساعت شروع-پایان"
        )

        // Compute X left boundaries and centers for each column
        val colLefts = mutableListOf<Float>()
        val colCenters = mutableListOf<Float>()
        var currentX = tableLeft
        for (w in colWidths) {
            colLefts.add(currentX)
            colCenters.add(currentX + (w / 2f))
            currentX += w
        }

        // Table Header Pill
        fillPaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 60f), 14f, 14f, fillPaint)

        paint.color = Color.WHITE
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER

        colHeaders.forEachIndexed { i, header ->
            canvas.drawText(header, colCenters[i], yPos + 38f, paint)
        }

        yPos += 60f
        strokePaint.color = BORDER_COLOR
        strokePaint.strokeWidth = 1.5f

        val displaySessions = if (sessions.isNotEmpty()) sessions else List(8) { StudySessionEntity(dateKey = "") }

        displaySessions.forEachIndexed { idx, s ->
            val rowY = yPos + (idx * rowHeight)
            fillPaint.color = if (idx % 2 == 0) BG_LIGHT else BG_WHITE
            canvas.drawRect(RectF(tableLeft, rowY, tableLeft + tableWidth, rowY + rowHeight), fillPaint)
            canvas.drawRect(RectF(tableLeft, rowY, tableLeft + tableWidth, rowY + rowHeight), strokePaint)

            // Draw vertical column dividers
            for (i in 1 until colLefts.size) {
                canvas.drawLine(colLefts[i], rowY, colLefts[i], rowY + rowHeight, strokePaint)
            }

            paint.color = TEXT_DARK
            paint.textSize = 20f
            paint.typeface = Typeface.DEFAULT
            paint.textAlign = Paint.Align.CENTER

            // 0: درصد آزمونک
            val tq = if (s.testQuizPercent.isNotBlank()) "%${s.testQuizPercent.toPersianDigits()}" else ""
            canvas.drawText(tq, colCenters[0], rowY + 39f, paint)

            // 1: زمان تست
            val td = if (s.testDuration.isNotBlank()) "${s.testDuration.toPersianDigits()} د" else ""
            canvas.drawText(td, colCenters[1], rowY + 39f, paint)

            // 2: تعداد تست
            val tc = if (s.testCount.isNotBlank()) s.testCount.toPersianDigits() else ""
            canvas.drawText(tc, colCenters[2], rowY + 39f, paint)

            // 3: زمان مطالعه
            val dur = if (s.studyDuration.isNotBlank()) "${s.studyDuration.toPersianDigits()} د" else ""
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.color = PURPLE_PRIMARY
            canvas.drawText(dur, colCenters[3], rowY + 39f, paint)

            // 4: حواس‌پرتی
            paint.typeface = Typeface.DEFAULT
            paint.color = TEXT_DARK
            val dist = if (s.distractions > 0) s.distractions.toPersianDigits() else ""
            canvas.drawText(dist, colCenters[4], rowY + 39f, paint)

            // 5: نوع کار
            canvas.drawText(s.taskType.take(8), colCenters[5], rowY + 39f, paint)

            // 6: مبحث / فصل
            canvas.drawText(s.topic.take(14), colCenters[6], rowY + 39f, paint)

            // 7: نام درس
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.color = PURPLE_DARK
            canvas.drawText(s.subject.take(10), colCenters[7], rowY + 39f, paint)

            // 8: ساعت شروع-پایان (Rightmost)
            paint.typeface = Typeface.DEFAULT
            paint.color = TEXT_DARK
            val time = if (s.startTime.isNotBlank() || s.endTime.isNotBlank()) "${s.startTime}-${s.endTime}".toPersianDigits() else ""
            canvas.drawText(time, colCenters[8], rowY + 39f, paint)
        }

        yPos += (displaySessions.size * rowHeight) + 15f

        // 4. DAILY SUMMARY ROW (7 connected pill boxes perfectly centered)
        val sumBoxWidth = (tableWidth - 30f) / 7f
        val sumLabels = listOf(
            "ساعت پیش‌بینی",
            "ساعت انجام‌شده",
            "حداقل تست",
            "حل تشریحی",
            "تست زده‌شده",
            "درصد عمل",
            "درصد رضایت"
        )
        val sumValues = listOf(
            plan?.plannedStudyHours?.ifBlank { "—" }?.toPersianDigits() ?: "—",
            plan?.actualStudyHours?.ifBlank { "—" }?.toPersianDigits() ?: "—",
            plan?.minPredictedTests?.ifBlank { "—" }?.toPersianDigits() ?: "—",
            plan?.descriptiveQuestions?.ifBlank { "—" }?.toPersianDigits() ?: "—",
            plan?.testsDone?.ifBlank { "—" }?.toPersianDigits() ?: "—",
            plan?.adherencePercent?.ifBlank { "—" }?.let { "%${it.toPersianDigits()}" } ?: "—",
            plan?.satisfactionPercent?.ifBlank { "—" }?.let { "%${it.toPersianDigits()}" } ?: "—"
        )

        sumLabels.forEachIndexed { i, label ->
            val boxLeft = tableLeft + (i * (sumBoxWidth + 5f))
            val boxRect = RectF(boxLeft, yPos, boxLeft + sumBoxWidth, yPos + 95f)
            fillPaint.color = PURPLE_LIGHT
            canvas.drawRoundRect(boxRect, 12f, 12f, fillPaint)
            strokePaint.color = PURPLE_PRIMARY
            strokePaint.strokeWidth = 1.5f
            canvas.drawRoundRect(boxRect, 12f, 12f, strokePaint)

            val boxCenter = boxLeft + (sumBoxWidth / 2f)
            paint.color = PURPLE_DARK
            paint.textSize = 17f
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(label, boxCenter, yPos + 34f, paint)

            paint.color = TEXT_DARK
            paint.textSize = 23f
            canvas.drawText(sumValues[i], boxCenter, yPos + 74f, paint)
        }

        // 5. REFLECTIONS & PERFORMANCE NOTES:
        // Social Media in an independent card between Gratitude & Good Event
        yPos += 120f
        val refHeight = 120f
        val cardWidth = (tableWidth - 24f) / 3f

        // Card 1 (Right): اتفاق خوب امروز چی بود؟
        val c1Left = tableLeft + (2 * (cardWidth + 12f))
        val c1Center = c1Left + (cardWidth / 2f)
        drawCenteredRefBox(
            canvas,
            "✨ اتفاق خوب امروز",
            plan?.goodEvent?.ifBlank { "ثبت نشده" } ?: "ثبت نشده",
            c1Left,
            yPos,
            cardWidth,
            refHeight,
            Color.parseColor("#FDF2F8"),
            Color.parseColor("#DB2777")
        )

        // Card 2 (Center): فضای مجازی کاملا جدا و مستقل
        val socialHours = plan?.socialMediaSlots?.toLatinDigits()?.filter { it.isDigit() }?.toIntOrNull() ?: 0
        val c2Left = tableLeft + cardWidth + 12f
        val c2Center = c2Left + (cardWidth / 2f)
        drawCenteredRefBox(
            canvas,
            "📱 زمان فضای مجازی",
            "${socialHours.toPersianDigits()} ساعت در روز",
            c2Left,
            yPos,
            cardWidth,
            refHeight,
            Color.parseColor("#EFF6FF"),
            Color.parseColor("#2563EB")
        )

        // Card 3 (Left): حداقل ۲ شکرگزاری بابت داشته‌هات
        val c3Left = tableLeft
        drawCenteredRefBox(
            canvas,
            "🙏 حداقل ۲ شکرگزاری",
            plan?.gratitudeNotes?.ifBlank { "ثبت نشده" } ?: "ثبت نشده",
            c3Left,
            yPos,
            cardWidth,
            refHeight,
            Color.parseColor("#FEF3C7"),
            YELLOW_ACCENT
        )

        yPos += refHeight + 15f
        // توضیحاتی درمورد عملکرد امروز (کامل و وسط چین)
        val noteRect = RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 95f)
        fillPaint.color = BG_LIGHT
        canvas.drawRoundRect(noteRect, 12f, 12f, fillPaint)
        strokePaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(noteRect, 12f, 12f, strokePaint)

        paint.color = PURPLE_PRIMARY
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("📝 توضیحاتی درمورد عملکرد امروز", tableLeft + (tableWidth / 2f), yPos + 35f, paint)

        paint.color = TEXT_DARK
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(plan?.performanceNotes?.ifBlank { "ثبت نشده است" }?.take(60) ?: "ثبت نشده است", tableLeft + (tableWidth / 2f), yPos + 72f, paint)

        // 6. SUBTLE GREY WATERMARK IN THE BOTTOM MARGIN (خارج از کادر در حاشیه)
        paint.color = TEXT_FOOTER
        paint.textSize = 15f
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.CENTER
        val footerText = "دفتر برنامه‌ریزی کنکور Plan B | @COD_LARK"
        canvas.drawText(footerText, width / 2f, height - 18f, paint)

        saveToDownloadsAndShare(context, bitmap, "PlanB_Daily_${jalaliDate.toKey()}.png", "گزارش روزانه Plan B")
    }

    fun normalizeSubjectName(name: String): String {
        val trimmed = name.trim()
        return when {
            trimmed.startsWith("زیست") -> "زیست"
            trimmed.startsWith("زمین") -> "زمین"
            trimmed.startsWith("شیمی") -> "شیمی"
            trimmed.startsWith("فیزیک") -> "فیزیک"
            trimmed.startsWith("ریاضی") -> "ریاضی"
            trimmed.startsWith("ادبیات") || trimmed.startsWith("فارسی") -> "ادبیات"
            trimmed.startsWith("عربی") -> "عربی"
            trimmed.startsWith("زبان") || trimmed.startsWith("انگلیسی") -> "زبان"
            trimmed.startsWith("دینی") || trimmed.startsWith("معارف") -> "دینی"
            else -> trimmed
        }
    }

    /**
     * Renders the Weekly Report matching PDF Page 8,
     * perfectly centered, evenly distributed columns and separate cards.
     */
    fun shareWeeklyReport(
        context: Context,
        weekRangeText: String,
        report: WeeklyReportEntity?,
        weekSessions: List<StudySessionEntity>,
        weekPlans: List<DailyPlanEntity>
    ) {
        val width = 1240
        val height = 1560
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(BG_WHITE)

        val paint = createPaint()
        val fillPaint = createPaint()
        val strokePaint = createPaint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = PURPLE_PRIMARY
        }

        // Outer border
        val borderBottom = height - 52f
        canvas.drawRoundRect(RectF(25f, 25f, width - 25f, borderBottom), 24f, 24f, strokePaint)

        // 1. Header: گزارش هفته
        paint.color = PURPLE_PRIMARY
        paint.textSize = 48f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("گـزارش هـفـتـه", width / 2f, 95f, paint)

        paint.color = TEXT_MUTED
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT
        val rangeStr = "بازه زمانی: $weekRangeText"
        canvas.drawText(rangeStr, width / 2f, 138f, paint)

        // 2. Main Weekly Table matching PDF Page 8
        var yPos = 175f
        val tableLeft = 40f
        val tableWidth = width - 80f // 1160f

        val defaultSubjects = listOf("زیست", "شیمی", "فیزیک", "ریاضی", "زمین", "ادبیات", "عربی", "زبان", "دینی")
        val sessionSubjects = weekSessions
            .map { normalizeSubjectName(it.subject) }
            .filter { it.isNotBlank() }
            .distinct()
        val allSubjects = (defaultSubjects + sessionSubjects).distinct().take(9)

        val totalCols = allSubjects.size + 1
        val colWidth = tableWidth / totalCols.toFloat()

        // Table Header (Rightmost: درس / جمع, then subjects in RTL)
        fillPaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 64f), 12f, 12f, fillPaint)

        paint.color = Color.WHITE
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER

        // Rightmost column: درس / جمع
        val labelColCenter = tableLeft + tableWidth - (colWidth / 2f)
        canvas.drawText("درس / جمع", labelColCenter, yPos + 40f, paint)

        // Subject columns from right to left
        allSubjects.forEachIndexed { i, subj ->
            val colCenter = tableLeft + tableWidth - ((i + 1.5f) * colWidth)
            canvas.drawText(subj, colCenter, yPos + 40f, paint)
        }

        yPos += 64f
        strokePaint.color = BORDER_COLOR
        strokePaint.strokeWidth = 1.5f

        // Row 1: جمع ساعت مطالعه
        drawCenteredWeeklyRow(canvas, "جمع ساعت مطالعه", tableLeft, yPos, tableWidth, colWidth, allSubjects) { subj ->
            val mins = weekSessions
                .filter { normalizeSubjectName(it.subject).equals(subj, ignoreCase = true) }
                .sumOf { it.studyDuration.toLatinDigits().toIntOrNull() ?: 0 }
            val h = mins / 60
            val m = mins % 60
            if (mins > 0) "${h.toPersianDigits()}h ${m.toPersianDigits()}m" else "۰"
        }

        yPos += 62f
        // Row 2: جمع تعداد تست
        drawCenteredWeeklyRow(canvas, "جمع تعداد تست", tableLeft, yPos, tableWidth, colWidth, allSubjects) { subj ->
            val tests = weekSessions
                .filter { normalizeSubjectName(it.subject).equals(subj, ignoreCase = true) }
                .sumOf { it.testCount.toLatinDigits().toIntOrNull() ?: 0 }
            if (tests > 0) tests.toPersianDigits() else "۰"
        }

        yPos += 62f
        // Row 3: درصدهای آزمون (آزمایشی)
        drawCenteredWeeklyRow(canvas, "درصدهای آزمون", tableLeft, yPos, tableWidth, colWidth, allSubjects) { _ ->
            "—"
        }

        // 3. Summary Cards (4 equal cards across the width)
        yPos += 80f
        val sumCardWidth = (tableWidth - 36f) / 4f

        val totalStudyMinutes = weekSessions.sumOf { it.studyDuration.toLatinDigits().toIntOrNull() ?: 0 }
        val totalStudyH = totalStudyMinutes / 60
        val totalStudyM = totalStudyMinutes % 60
        val totalStudyStr = "${totalStudyH.toPersianDigits()}h ${totalStudyM.toPersianDigits()}m"

        val totalTests = weekSessions.sumOf { it.testCount.toLatinDigits().toIntOrNull() ?: 0 }
        val totalSocialHours = weekPlans.sumOf { it.socialMediaSlots.toLatinDigits().filter { c -> c.isDigit() }.toIntOrNull() ?: 0 }
        val daysSleepLogged = weekPlans.count { it.sleepTime.isNotBlank() }

        val weeklyStats = listOf(
            Pair("ساعت مطالعه کل", totalStudyStr),
            Pair("تعداد تست کل", "${totalTests.toPersianDigits()} تست"),
            Pair("ثبت خواب", "${daysSleepLogged.toPersianDigits()} روز"),
            Pair("فضای مجازی", "${totalSocialHours.toPersianDigits()} ساعت")
        )

        weeklyStats.forEachIndexed { i, stat ->
            val cLeft = tableLeft + (i * (sumCardWidth + 12f))
            val cCenter = cLeft + (sumCardWidth / 2f)
            val cRect = RectF(cLeft, yPos, cLeft + sumCardWidth, yPos + 95f)

            fillPaint.color = PURPLE_LIGHT
            canvas.drawRoundRect(cRect, 14f, 14f, fillPaint)
            strokePaint.color = PURPLE_PRIMARY
            canvas.drawRoundRect(cRect, 14f, 14f, strokePaint)

            paint.color = PURPLE_DARK
            paint.textSize = 19f
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(stat.first, cCenter, yPos + 35f, paint)

            paint.color = TEXT_DARK
            paint.textSize = 23f
            canvas.drawText(stat.second, cCenter, yPos + 74f, paint)
        }

        // 4. Bottom Reflection Cards (Conclusion & Motivation Quote)
        yPos += 125f

        // نتیجه‌گیری هفته
        fillPaint.color = Color.parseColor("#F5F3FF")
        val concRect = RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 175f)
        canvas.drawRoundRect(concRect, 16f, 16f, fillPaint)
        strokePaint.color = Color.parseColor("#8B5CF6")
        canvas.drawRoundRect(concRect, 16f, 16f, strokePaint)

        paint.color = Color.parseColor("#6D28D9")
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("📋 نتیجه‌گیری و بازخورد مشاوره‌ای این هفته", width / 2f, yPos + 45f, paint)

        paint.color = TEXT_DARK
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT
        val conclusion = report?.conclusion?.ifBlank { "نتیجه‌گیری این هفته هنوز ثبت نشده است." } ?: "نتیجه‌گیری این هفته هنوز ثبت نشده است."
        canvas.drawText(conclusion.take(65), width / 2f, yPos + 105f, paint)

        yPos += 205f
        // جمله‌ای که بهت بیشترین انگیزه رو داد این هفته
        fillPaint.color = Color.parseColor("#ECFDF5")
        val quoteRect = RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 175f)
        canvas.drawRoundRect(quoteRect, 16f, 16f, fillPaint)
        strokePaint.color = GREEN_ACCENT
        canvas.drawRoundRect(quoteRect, 16f, 16f, strokePaint)

        paint.color = GREEN_ACCENT
        paint.textSize = 24f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("💚 جمله‌ای که بهت بیشترین انگیزه رو داد این هفته", width / 2f, yPos + 45f, paint)

        paint.color = TEXT_DARK
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT
        val quote = report?.motivationQuote?.ifBlank { "به تلاش‌هایت ادامه بده، روزهای درخشان در راهند!" } ?: "به تلاش‌هایت ادامه بده، روزهای درخشان در راهند!"
        canvas.drawText(quote.take(65), width / 2f, yPos + 105f, paint)

        // SUBTLE GREY WATERMARK IN THE BOTTOM MARGIN
        paint.color = TEXT_FOOTER
        paint.textSize = 15f
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.CENTER
        val footerText = "دفتر برنامه‌ریزی کنکور Plan B | @COD_LARK"
        canvas.drawText(footerText, width / 2f, height - 18f, paint)

        saveToDownloadsAndShare(context, bitmap, "PlanB_Weekly_Report.png", "گزارش هفتگی Plan B")
    }

    /**
     * Renders the Exam Analysis table matching PDF Pages 9 & 10,
     * with perfectly centered stats and properly ordered RTL columns.
     */
    fun shareExamAnalysis(
        context: Context,
        exam: ExamAnalysisEntity?,
        questions: List<ExamQuestionEntity>
    ) {
        val width = 1240
        val rowCount = maxOf(questions.size, 10)
        val rowHeight = 62f
        val height = 520 + (rowCount * rowHeight.toInt())
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(BG_WHITE)

        val paint = createPaint()
        val fillPaint = createPaint()
        val strokePaint = createPaint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = PURPLE_PRIMARY
        }

        // Outer border
        val borderBottom = height - 52f
        canvas.drawRoundRect(RectF(25f, 25f, width - 25f, borderBottom), 24f, 24f, strokePaint)

        // 1. Header: فرم تحلیل آزمون
        paint.color = PURPLE_PRIMARY
        paint.textSize = 48f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("فـرم تـحـلـیـل آزمـون", width / 2f, 95f, paint)

        // Header Card: Clean centered 4 metric pills + percentage summary
        var yPos = 135f
        val tableLeft = 40f
        val tableWidth = width - 80f // 1160f

        fillPaint.color = PURPLE_LIGHT
        val cardRect = RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 120f)
        canvas.drawRoundRect(cardRect, 16f, 16f, fillPaint)
        strokePaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(cardRect, 16f, 16f, strokePaint)

        val examTitle = exam?.examTitle ?: "آزمون آزمایشی"
        val examDate = exam?.examDate?.toPersianDigits() ?: "—"
        val taraz = exam?.totalTaraz?.toPersianDigits() ?: "—"
        val rank = exam?.totalRank?.toPersianDigits() ?: "—"

        // Row 1: 4 centered badges
        val examPillWidth = tableWidth / 4f
        val examBadges = listOf(
            "نام آزمون: $examTitle",
            "تاریخ: $examDate",
            "تراز کل: $taraz",
            "رتبه کل: $rank"
        )
        examBadges.forEachIndexed { i, badge ->
            val center = tableLeft + (i * examPillWidth) + (examPillWidth / 2f)
            paint.color = PURPLE_DARK
            paint.textSize = 21f
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText(badge, center, yPos + 45f, paint)
        }

        // Row 2: Subject percentages centered
        val percs = "درصدها:   زیست: %${exam?.biologyPercent?.toPersianDigits() ?: "—"}   |   شیمی: %${exam?.chemistryPercent?.toPersianDigits() ?: "—"}   |   فیزیک: %${exam?.physicsPercent?.toPersianDigits() ?: "—"}   |   ریاضی: %${exam?.mathPercent?.toPersianDigits() ?: "—"}"
        paint.color = TEXT_DARK
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(percs, width / 2f, yPos + 92f, paint)

        // 2. Question Error Analysis Table
        // RTL columns from Right to Left:
        // Rightmost (Col 5): شماره (90f)
        // Col 4: نام درس (160f)
        // Col 3: نزده (90f)
        // Col 2: غلط (90f)
        // Col 1: علت اشتباه یا عدم پاسخ (360f)
        // Col 0 (Leftmost): بهترین تصمیم برای رفع اشکال (370f)
        // Total = 90 + 160 + 90 + 90 + 360 + 370 = 1160px!
        yPos += 145f

        val qColWidths = listOf(370f, 360f, 90f, 90f, 160f, 90f)
        val qColHeaders = listOf(
            "بهترین تصمیم برای رفع اشکال",
            "علت اشتباه یا عدم پاسخ",
            "غلط",
            "نزده",
            "نام درس",
            "شماره"
        )

        val qColLefts = mutableListOf<Float>()
        val qColCenters = mutableListOf<Float>()
        var qX = tableLeft
        for (w in qColWidths) {
            qColLefts.add(qX)
            qColCenters.add(qX + (w / 2f))
            qX += w
        }

        fillPaint.color = PURPLE_PRIMARY
        canvas.drawRoundRect(RectF(tableLeft, yPos, tableLeft + tableWidth, yPos + 60f), 12f, 12f, fillPaint)

        paint.color = Color.WHITE
        paint.textSize = 21f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER

        qColHeaders.forEachIndexed { i, header ->
            canvas.drawText(header, qColCenters[i], yPos + 38f, paint)
        }

        yPos += 60f
        strokePaint.color = BORDER_COLOR
        strokePaint.strokeWidth = 1.5f

        val displayQuestions = if (questions.isNotEmpty()) questions else List(10) { ExamQuestionEntity(examId = 0) }

        displayQuestions.forEachIndexed { idx, q ->
            val rowY = yPos + (idx * rowHeight)
            fillPaint.color = if (idx % 2 == 0) BG_LIGHT else BG_WHITE
            canvas.drawRect(RectF(tableLeft, rowY, tableLeft + tableWidth, rowY + rowHeight), fillPaint)
            canvas.drawRect(RectF(tableLeft, rowY, tableLeft + tableWidth, rowY + rowHeight), strokePaint)

            // Vertical column dividers
            for (i in 1 until qColLefts.size) {
                canvas.drawLine(qColLefts[i], rowY, qColLefts[i], rowY + rowHeight, strokePaint)
            }

            paint.color = TEXT_DARK
            paint.textSize = 20f
            paint.typeface = Typeface.DEFAULT
            paint.textAlign = Paint.Align.CENTER

            // Col 0: بهترین تصمیم برای رفع اشکال
            canvas.drawText(q.topicAndDecision.take(28), qColCenters[0], rowY + 39f, paint)

            // Col 1: علت اشتباه یا عدم پاسخ
            canvas.drawText(q.reason.take(26), qColCenters[1], rowY + 39f, paint)

            // Col 2: غلط
            paint.color = if (q.isWrong) RED_ACCENT else Color.TRANSPARENT
            paint.textSize = 22f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(if (q.isWrong) "✔" else "", qColCenters[2], rowY + 40f, paint)

            // Col 3: نزده
            paint.color = if (q.isUnanswered) YELLOW_ACCENT else Color.TRANSPARENT
            canvas.drawText(if (q.isUnanswered) "✔" else "", qColCenters[3], rowY + 40f, paint)

            // Col 4: نام درس
            paint.color = PURPLE_DARK
            paint.textSize = 20f
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(q.subject.take(10), qColCenters[4], rowY + 39f, paint)

            // Col 5: شماره (Rightmost)
            paint.color = TEXT_DARK
            paint.typeface = Typeface.DEFAULT_BOLD
            val qNum = if (q.questionNumber.isNotBlank()) q.questionNumber.toPersianDigits() else ""
            canvas.drawText(qNum, qColCenters[5], rowY + 39f, paint)
        }

        // SUBTLE GREY WATERMARK IN THE BOTTOM MARGIN
        paint.color = TEXT_FOOTER
        paint.textSize = 15f
        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.CENTER
        val footerText = "دفتر برنامه‌ریزی کنکور Plan B | @COD_LARK"
        canvas.drawText(footerText, width / 2f, height - 18f, paint)

        saveToDownloadsAndShare(context, bitmap, "PlanB_Exam_Analysis_${exam?.id ?: 0}.png", "تحلیل آزمون Plan B")
    }

    private fun drawCenteredRefBox(
        canvas: Canvas,
        title: String,
        content: String,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        bgColor: Int,
        borderColor: Int
    ) {
        val fillPaint = createPaint().apply { color = bgColor }
        val strokePaint = createPaint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = borderColor
        }
        val textPaint = createPaint()

        val rect = RectF(x, y, x + w, y + h)
        canvas.drawRoundRect(rect, 14f, 14f, fillPaint)
        canvas.drawRoundRect(rect, 14f, 14f, strokePaint)

        val centerX = x + (w / 2f)

        textPaint.color = borderColor
        textPaint.textSize = 21f
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textAlign = Paint.Align.CENTER
        canvas.drawText(title, centerX, y + 42f, textPaint)

        textPaint.color = TEXT_DARK
        textPaint.textSize = 20f
        textPaint.typeface = Typeface.DEFAULT
        canvas.drawText(content.take(28), centerX, y + 84f, textPaint)
    }

    private fun drawCenteredWeeklyRow(
        canvas: Canvas,
        rowTitle: String,
        tableLeft: Float,
        top: Float,
        tableWidth: Float,
        colWidth: Float,
        subjects: List<String>,
        getValue: (String) -> String
    ) {
        val fillPaint = createPaint().apply { color = BG_LIGHT }
        val strokePaint = createPaint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = BORDER_COLOR
        }
        val paint = createPaint().apply {
            color = TEXT_DARK
            textSize = 20f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawRect(RectF(tableLeft, top, tableLeft + tableWidth, top + 62f), fillPaint)
        canvas.drawRect(RectF(tableLeft, top, tableLeft + tableWidth, top + 62f), strokePaint)

        // Draw vertical dividers
        for (i in 1 until (subjects.size + 1)) {
            val divX = tableLeft + (i * colWidth)
            canvas.drawLine(divX, top, divX, top + 62f, strokePaint)
        }

        // Rightmost column: Row Title
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = PURPLE_DARK
        val labelCenter = tableLeft + tableWidth - (colWidth / 2f)
        canvas.drawText(rowTitle, labelCenter, top + 39f, paint)

        // Subjects from right to left
        paint.color = TEXT_DARK
        subjects.forEachIndexed { i, subj ->
            val colCenter = tableLeft + tableWidth - ((i + 1.5f) * colWidth)
            canvas.drawText(getValue(subj), colCenter, top + 39f, paint)
        }
    }
}
