package com.teclast_korea.teclast_qc_application.device_tester.standard_test.scspro_mode

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import com.teclast_korea.teclast_qc_application.device_tester.specific_test.battery.tester.BatteryTestTestMode
import com.teclast_korea.teclast_qc_application.device_tester.specific_test.cpu.tester.test_kit.CpuBurnInTest
import com.teclast_korea.teclast_qc_application.device_tester.specific_test.gpu.tester.gpu3DTest
import com.teclast_korea.teclast_qc_application.device_tester.specific_test.rom.tester.romTest1
import com.teclast_korea.teclast_qc_application.device_tester.standard_test.api_kit.FailTestNavigator
import com.teclast_korea.teclast_qc_application.device_tester.standard_test.scspro_mode.sub_screen.SCSPROModeTestScreenScaffold
import com.teclast_korea.teclast_qc_application.home.device_report.deviceSpecReportList
import com.teclast_korea.teclast_qc_application.test_result.test_results_db.CheckTestResultbyItem
import com.teclast_korea.teclast_qc_application.test_result.test_results_db.TestResultEvent
import com.teclast_korea.teclast_qc_application.test_result.test_results_db.TestResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SCSPROModeScreen_2(
    state: TestResultState,
    onEvent: (TestResultEvent) -> Unit,
    context: Context,
    navController: NavHostController,
    nextTestRoute: MutableList<String>
) {
    var progress by remember { mutableStateOf(4 / 7f) }
    var testsCompleted by remember { mutableStateOf(false) }

    // var done is empty list
    val done =
        remember { mutableStateListOf<String>("1. CPU Buffer", "3. GPU test 1", "5. RAM test 1", "8. battery test 1") }
    val undone = remember {
        mutableStateListOf(
            "2. CPU BURNIN",
            "4. GPU test 2",
            "6. ROM test 1",
        )
    }

    val doneTests = listOf("CPU BURNIN TEST", "GPU TEST 2", "ROM TEST")
    val device_spec_pdf = deviceSpecReportList(context = context)
    val isAnyTestFailed = remember { mutableStateOf(false) }
    val hasCheckedEveryTest = remember { mutableStateOf(false) }
    // 매우 긴 nextTestRoute를 던져주고 이걸 딱딱 나눠서 보도록 하면 된다. 즉 호출 함수는 그냥 첫 함수인 거고 나머지 뒤에 따라오는 함수들은 내가 정한 nextTestRoute에 따라 따라오는 것이다.
    // nextTestRoute : "~~~~~~screen//~~~~~~~screen//~~~~~~screen//end" 이러면서 맨 앞에 처리된 라우터 새끼들은 싹 다 쳐내는 것이다. 그리고 체크해서 만약에 end면 그냥 함수 navigate.pop 써서 끝내버리면 된다.


    if (!testsCompleted) {
        LaunchedEffect(key1 = testsCompleted) {
            withContext(Dispatchers.IO) {
                Log.i("SCSPROModeScreen", "Test Started")
                onEvent(TestResultEvent.StartTest)
                Log.i("SCSPROModeScreen", "Test DataBase is Ready")

                // Cpu Test
                delay(100L)


//                val cpuTestResult2 = cpuTest1(state = state, onEvent = onEvent)
//                progress += 1f / (done.size + undone.size)
//                done.add("2. CPU TEST 1")
//                undone.remove("2. CPU TEST 1")
//                Log.i("SCSPROModeScreen", "2. cpuTest1() is called : $cpuTestResult2 : Percentage : $progress")
//                delay(100L)
//                onEvent(TestResultEvent.SaveTestResult)
//                delay(100L)

                var romTestResult1 = ""
                runBlocking {
                    romTestResult1 = romTest1(state = state, onEvent = onEvent)
                }
                progress += 1f / (done.size + undone.size)
                done.add("6. ROM test 1")
                undone.remove("6. ROM test 1")
                Log.i("SCSPROModeScreen", "6. ROM test 1 is called : $romTestResult1 : Percentage : $progress")
                delay(100L)
                onEvent(TestResultEvent.SaveTestResult)
                delay(100L)

                var cpuTestResult3 = ""
                progress += 1f / (done.size + undone.size)
                done.add("2. CPU BURNIN")
                undone.remove("2. CPU BURNIN")
                runBlocking {
                    cpuTestResult3 =
                        CpuBurnInTest(state = state, onEvent = onEvent, 500L, 16600, this) // fail when 500L/16800
                }
                Log.i("SCSPROModeScreen", "3. CpuBurnInTest() is called : $cpuTestResult3 : Percentage : $progress")
                delay(100L)
                onEvent(TestResultEvent.SaveTestResult)
                delay(100L)


                var gpuTestResult2 = ""
                runBlocking {
                    gpuTestResult2 = gpu3DTest(state = state, onEvent = onEvent)
                }
                progress += 1f / (done.size + undone.size)
                done.add("4. GPU test 2")
                undone.remove("4. GPU test 2")
                Log.i("SCSPROModeScreen", "4. GPU test 2 is called : $gpuTestResult2 : Percentage : $progress")
                delay(100L)
                onEvent(TestResultEvent.SaveTestResult)
                delay(100L)



                testsCompleted = true
            }
        }
    }

    if (testsCompleted == false) {
        SCSPROModeTestScreenScaffold(
            navController = navController,
            nextTestRoute = nextTestRoute,
            progress = progress,
            done = done,
            undone = undone,
            content = {},
            onEvent = onEvent,
            state = state,
            context = context
        )

    }
    if (testsCompleted) {

        if (!isAnyTestFailed.value && !hasCheckedEveryTest.value) {
            doneTests.forEach { doneTest ->
                run {
                    Log.i("SCSPROModeScreen", "Done Test: $doneTest")
                    if (CheckTestResultbyItem(state = state, onEvent = onEvent, itemName = doneTest) == "FAIL") {
                        Log.i("SCSPROModeScreen", "Done Test: $doneTest is Failed")
                        FailTestNavigator(
                            context = context,
                            onEvent = onEvent,
                            state = state,
                            testMode = "SCSPROMode",
                            navController = navController,
                            navigateToNextTest = false,
                            nextTestRoute = nextTestRoute,
                            currentTestItem = doneTest,
                            deviceSpec = device_spec_pdf
                        )
                        isAnyTestFailed.value = true
                    } else {
                        Log.i("SCSPROModeScreen", "Done Test: $doneTest is Passed")

                    }
                }
            }
            hasCheckedEveryTest.value = true
        }
        if (!isAnyTestFailed.value) {
            BatteryTestTestMode(
                state = state,
                onEvent = onEvent,
                context = context,
                navController = navController,
                navigateToNextTest = true,
                nextTestRoute = nextTestRoute,
                testMode = "SCSPROMode"
            )
        }
    }
}


