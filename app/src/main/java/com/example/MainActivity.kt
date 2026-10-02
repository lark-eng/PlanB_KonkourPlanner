package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainScreen
import com.example.ui.screens.WelcomeSplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.PlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val plannerViewModel: PlannerViewModel = viewModel()
            val themeMode by plannerViewModel.themeMode.collectAsStateWithLifecycle()
            val accentColor by plannerViewModel.accentColor.collectAsStateWithLifecycle()

            // Request Notification Permission on first launch if on Android 13+ (API 33+)
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { /* Permission response handled gracefully */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val isGranted = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!isGranted) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            var showWelcome by remember { mutableStateOf(true) }

            MyApplicationTheme(
                themeMode = themeMode,
                accentColor = accentColor
            ) {
                if (showWelcome) {
                    WelcomeSplashScreen(
                        onFinished = { showWelcome = false },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    MainScreen(
                        viewModel = plannerViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
