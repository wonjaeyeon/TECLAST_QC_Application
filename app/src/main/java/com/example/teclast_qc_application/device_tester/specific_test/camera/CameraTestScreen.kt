package com.example.teclast_qc_application.device_tester.specific_test.camera

//make a screen for cpu test
//import com.example.teclast_qc_application.device_tester.testFunction.cpu.tester.getCurrentCpuUsage
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.teclast_qc_application.test_result.test_results_db.TestResultEvent
import com.example.teclast_qc_application.test_result.test_results_db.TestResultState


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CameraTestScreen(
    state: TestResultState,
    onEvent: (TestResultEvent) -> Unit, context: Context, navController: NavController,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Camera Screen Test") },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primaryVariant)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                // Camera Test t1 Button
                Button(onClick = {
                    navController.navigate("camera_test_t1_screen/notNextTest") {
//                        popUpTo("touch_panel_test_screen"){
//                            inclusive = true
//
//                        }
                    }
                }) {
                    Text(text = "Back Camera Test")
                }

                // Camera Test t1 Button
                Button(onClick = {
                    navController.navigate("camera_test_t2_screen") {
//                        popUpTo("touch_panel_test_screen"){
//                            inclusive = true
//
//                        }
                    }
                }) {
                    Text(text = "Front Camera Test")
                }

            }
        }
    }
}