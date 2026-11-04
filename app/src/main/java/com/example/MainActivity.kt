package com.example

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.LumiaTheme
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LauncherViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Ensure the system window is completely transparent so the system TV wallpaper shines through
    window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    enableEdgeToEdge()

    setContent {
      LumiaTheme {
        HomeScreen(viewModel = viewModel)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    // Refresh installed apps list whenever returning to the launcher
    viewModel.loadInstalledApps()
  }
}
