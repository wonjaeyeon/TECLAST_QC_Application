package com.example.teclast_qc_application.device_tester.specific_test.vibration.tester

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "RememberReturnType")
@Composable
fun VibrationTestT1(context: Context, navController: NavController) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var vibrationResult = remember { mutableStateOf("Ready for Test") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Vibration Test") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    if (vibrator.hasVibrator()) { // Check if device has vibrator
                        if (vibrator.hasAmplitudeControl()) { // Check if the device can control the vibration amplitude
                            // If yes, vibrate with specified pattern and amplitude through VibrationEffect
                            val effect = VibrationEffect.createWaveform(longArrayOf(0, 200), intArrayOf(0, 255), -1)
                            vibrator.vibrate(effect)
                            vibrationResult.value = "Vibration with amplitude control"
                        } else {
                            // If no amplitude control, vibrate normally without specifying amplitude
                            val effect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                            vibrator.vibrate(effect)
                            vibrationResult.value = "Vibration without amplitude control"
                        }
                    } else {
                        // Log or show on UI that the device has no vibrator hardware
                        Log.e("Vibration Test", "This device does not support vibration")
                        vibrationResult.value = "This device does not support vibration"
                    }
                }) {
                    Text(text = "Vibrate")
                }

                Text(text = vibrationResult.value)
            }
        }
    )
}
