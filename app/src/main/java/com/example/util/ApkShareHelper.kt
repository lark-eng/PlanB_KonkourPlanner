package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ApkShareHelper {

    fun exportAndShareApk(context: Context) {
        try {
            val appInfo = context.applicationInfo
            val sourceApkFile = File(appInfo.sourceDir)

            if (!sourceApkFile.exists()) {
                Toast.makeText(context, "فایل نصبی یافت نشد!", Toast.LENGTH_SHORT).show()
                return
            }

            val fileName = "KonkurPlanner_Universal.apk"

            // 1. Copy to public Downloads or external files
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }
            val targetApkInDownloads = File(downloadsDir, fileName)

            copyFile(sourceApkFile, targetApkInDownloads)

            // Also keep a copy in cache for clean FileProvider sharing
            val cacheApk = File(context.cacheDir, fileName)
            copyFile(sourceApkFile, cacheApk)

            Toast.makeText(
                context,
                "فایل APK یونیورسال با موفقیت در پوشه Download ذخیره شد",
                Toast.LENGTH_LONG
            ).show()

            // 2. Trigger Share Intent
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                cacheApk
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.android.package-archive"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "پلنر کنکور - فایل نصبی یونیورسال")
                putExtra(Intent.EXTRA_TEXT, "فایل نصبی یونیورسال اپلیکیشن پلنر کنکور (App by @COD_LARK)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "اشتراک‌گذاری نسخه نصبی APK").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "خطا در استخراج APK: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun copyFile(src: File, dst: File) {
        FileInputStream(src).use { inStream ->
            FileOutputStream(dst).use { outStream ->
                inStream.copyTo(outStream)
            }
        }
    }
}
