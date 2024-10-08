package com.teclast_korea.teclast_qc_application.ui.device_tester.sub.specific_test.battery.tester

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.teclast_korea.teclast_qc_application.domain.qc_result.AddTestResult
import com.teclast_korea.teclast_qc_application.ui.router.api_kit.DialogAPIInterface
import com.teclast_korea.teclast_qc_application.ui.router.api_kit.TestAPIDialog
import com.teclast_korea.teclast_qc_application.ui.test_result.TestResultEvent
import com.teclast_korea.teclast_qc_application.ui.test_result.TestResultState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BatteryTestTestMode(
    state: TestResultState,
    onEvent: (TestResultEvent) -> Unit,
    context: Context,
    navController: NavController,
    testMode: String = "",
    navigateToNextTest: Boolean = false,
    nextTestRoute: MutableList<String> = mutableListOf<String>()
) {

    val coroutineScope = rememberCoroutineScope()
    val batteryStatus = remember { mutableStateOf(BatteryManager.BATTERY_STATUS_UNKNOWN) }
    val hasNavigated = remember { mutableStateOf(false) }  // State to track navigation status
    val hasAddedTestResult = remember { mutableStateOf(false) }  // State to track test result status
    val showDialog = remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val job = coroutineScope.launch {
            while (isActive) {
                val batteryIntent: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                    context.registerReceiver(null, filter)
                }
                batteryStatus.value = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                delay(100) // Check battery status every second
            }
        }

        onDispose {
            job.cancel()
        }
    }

    val batteryMessage = when (batteryStatus.value) {
        BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL -> {
            if (!hasNavigated.value) {
                Log.i(testMode, "9. batteryTest2() is called : Battery TEST2 : Success : Charging")
                if (navigateToNextTest && nextTestRoute.isNotEmpty()) {
                    val pastRoute = nextTestRoute.removeAt(0) // pastRoute = LCDTest1
                    Log.i("MyTag:BatteryTest", "pastRoute: $pastRoute")
                    Log.i("MyTag:BatteryTest", "nextTestRoute: $nextTestRoute")
                    val nextRoute = nextTestRoute[0] // nextRoute = LCDTest2
                    val nextPath = nextTestRoute.drop(1)
                    val nextPathString = nextPath.joinToString(separator = "->")
                    Log.i("MyTag:BatteryTest", "nextPath: $nextPath")
                    Log.i("MyTag:BatteryTest", "nextPathString: $nextPathString")

                    var nextRouteWithArguments: String
                    if (nextPathString.isNotEmpty()) {
                        nextRouteWithArguments = "$nextRoute/$nextPathString/$testMode"
                        Log.i("MyTag:BatteryTest", "nextRouteWithArguments: $nextRouteWithArguments")
                    } else {
                        nextRouteWithArguments = nextRoute
                        Log.i("MyTag:BatteryTest", "nextRouteWithArguments: $nextRouteWithArguments")
                    }
                    if (!hasNavigated.value) {
                        onEvent(TestResultEvent.SaveTestResult)
                        AddTestResult(
                            state = state,
                            onEvent = onEvent,
                            "Battery Test 2",
                            "Success",
                            Date().toString()
                        )
                        onEvent(TestResultEvent.SaveTestResult)
                    }


                    navController.navigate(nextRouteWithArguments)
                    hasNavigated.value = true
                }  else {

                    if (!hasNavigated.value) {
                        onEvent(TestResultEvent.SaveTestResult)
                        AddTestResult(
                            state = state,
                            onEvent = onEvent,
                            "Battery Test 2",
                            "Success",
                            Date().toString()
                        )
                        onEvent(TestResultEvent.SaveTestResult)
                    }
                    navController.popBackStack()
                }
                hasNavigated.value = true


                "Success : Charging"
            } else {
                "Success : Charging"
            }
        }

        BatteryManager.BATTERY_STATUS_DISCHARGING -> {
            if (!hasAddedTestResult.value) {
                onEvent(TestResultEvent.SaveTestResult)
                AddTestResult(
                    state = state,
                    onEvent = onEvent,
                    "Battery Test 2",
                    "Fail",
                    Date().toString()
                )
                onEvent(TestResultEvent.SaveTestResult)
                hasAddedTestResult.value = true
            }
            "Please charge the battery"
        }

        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> {

            if (!hasAddedTestResult.value) {
                onEvent(TestResultEvent.SaveTestResult)
                AddTestResult(
                    state = state,
                    onEvent = onEvent,
                    "Battery Test 2",
                    "Fail",
                    Date().toString()
                )
                onEvent(TestResultEvent.SaveTestResult)
                hasAddedTestResult.value = true
            }
            "Not charging"
        }

        BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
        else -> "Error: Unable to determine battery status"
    }

    //Screen UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Battery Charging Test") },

                actions = {
                    DialogAPIInterface(
                        testMode = testMode,
                        showDialog = showDialog
                    )
                },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary,
            )
        }
    ) {

        TestAPIDialog(
            testMode = testMode,
            onEvent = onEvent,
            context = context,
            navController = navController,
            nextTestRoute = nextTestRoute,
            showDialog = showDialog
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.BatteryAlert, contentDescription = "Battery Status")
                Text(text = batteryMessage)
            }
        }
    }
}
