package com.teclast_korea.teclast_qc_application.ui.device_tester.sub.specific_test.camera.tester

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.view.ScaleGestureDetector
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.teclast_korea.teclast_qc_application.domain.qc_result.AddTestResult
import com.teclast_korea.teclast_qc_application.ui.router.api_kit.FailTestNavigator
import com.teclast_korea.teclast_qc_application.ui.router.api_kit.NavigationPopButton
import com.teclast_korea.teclast_qc_application.ui.test_result.TestResultEvent
import com.teclast_korea.teclast_qc_application.ui.test_result.TestResultState
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CameraTest2(
    state: TestResultState,
    onEvent: (TestResultEvent) -> Unit,
    context: Context,
    navController: NavController,
    testMode: String = "",
    navigateToNextTest: Boolean = false,
    nextTestRoute: MutableList<String> = mutableListOf<String>()
) {
    val cameraPermission = Manifest.permission.CAMERA
    val cameraRequestCode = 1234
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector = remember { CameraSelector.DEFAULT_FRONT_CAMERA }
    var camera: Camera? by remember { mutableStateOf(null) }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val previewView = remember { PreviewView(context) }
    val currentTestItem = "Camera Test 2"


    var zoomRatio by remember { mutableStateOf(1f) }
    val zoomState = camera?.cameraInfo?.zoomState?.value
    val zoomRatioRange = zoomState?.let { state ->
        state.minZoomRatio..state.maxZoomRatio
    } ?: 1f..4f
    Log.i("CameraTest2", "zoomRatioRange: $zoomRatioRange")


    // Setup Camera after permissions are granted
    fun setupCamera() {
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }

    // Check and request camera permissions
    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(context, cameraPermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(cameraPermission), cameraRequestCode)
        } else {
            setupCamera()
        }
    }


    val scaleGestureDetector =
        ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            //            override fun onScale(detector: ScaleGestureDetector): Boolean {
//                val scale = detector.scaleFactor
//                zoomLevel = (zoomLevel * scale).coerceIn(minZoomRatio, maxZoomRatio)
//                camera?.cameraControl?.setZoomRatio(zoomLevel)
//                return true
//            }
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                camera?.cameraInfo?.zoomState?.value?.zoomRatio?.let { currentZoomRatio ->
                    val scale = detector.scaleFactor
                    val newZoomRatio = (currentZoomRatio * scale).coerceIn(zoomRatioRange)
                    zoomRatio = newZoomRatio
                    camera?.cameraControl?.setZoomRatio(newZoomRatio)
                }
                return true
            }
        })


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Front Camera Test") },
                navigationIcon = {
                    NavigationPopButton(
                        navController = navController, testMode = testMode
                    )
                },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = MaterialTheme.colors.onPrimary,
            )
        },
        floatingActionButton = {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(start = 16.dp),
                    // add color to the background
                    backgroundColor = Color(0xFF00FF00),

                    onClick = { /* Handle success result */
                        onEvent(TestResultEvent.SaveTestResult)
                        AddTestResult(
                            state = state,
                            onEvent = onEvent,
                            "Camera Test 2",
                            "Success",
                            Date().toString()
                        )
                        onEvent(TestResultEvent.SaveTestResult)
                        if (navigateToNextTest && nextTestRoute.isNotEmpty()) {
                            val pastRoute = nextTestRoute.removeAt(0) // pastRoute = LCDTest1
                            Log.i("MyTag:CameraTest2", "pastRoute: $pastRoute")
                            Log.i("MyTag:CameraTest2", "nextTestRoute: $nextTestRoute")
                            val nextRoute = nextTestRoute[0] // nextRoute = LCDTest2
                            val nextPath = nextTestRoute.drop(1)
                            val nextPathString = nextPath.joinToString(separator = "->")
                            Log.i("MyTag:CameraTest2", "nextPath: $nextPath")
                            Log.i("MyTag:CameraTest2", "nextPathString: $nextPathString")

                            val nextRouteWithArguments = if (nextPathString.isNotEmpty()) {
                                "$nextRoute/$nextPathString/$testMode"
                            } else {
                                nextRoute
                            }

                            navController.navigate(nextRouteWithArguments)
                        } else
                            navController.popBackStack()
                    }) {
                    Text("Good")
                }
                FloatingActionButton(
                    backgroundColor = Color(0xFFFF0000),
                    onClick = { /* Handle fail result */
                        onEvent(TestResultEvent.SaveTestResult)
                        AddTestResult(
                            state = state,
                            onEvent = onEvent,
                            "Camera Test 2",
                            "Fail",
                            Date().toString()
                        )
                        onEvent(TestResultEvent.SaveTestResult)
                        FailTestNavigator(
                            onEvent = onEvent,
                            testMode = testMode,
                            navController = navController,
                            navigateToNextTest = navigateToNextTest,
                            nextTestRoute = nextTestRoute,
                            currentTestItem = currentTestItem
                        )
                    }) {
                    Text("Fail")
                }
            }
        }
    ) {
        Box {
            AndroidView(
                modifier = Modifier.fillMaxSize().pointerInteropFilter { event ->
                    scaleGestureDetector.onTouchEvent(event)
                    true
                },
                factory = { _ -> previewView }
            )
            // Example for a capture button
            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Top
            ) {
                Button(onClick = {
                    // Handle image capture
                    imageCapture?.let { capture ->
                        val fileName = System.currentTimeMillis().toString() + ".jpg"
                        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                            context.contentResolver,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                            }
                        ).build()

                        capture.takePicture(
                            outputFileOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onError(exc: ImageCaptureException) {
                                    Log.e("CameraTest", "Photo capture failed: ${exc.message}", exc)
                                }

                                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                    val msg = "Photo capture succeeded: ${outputFileResults.savedUri}"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    Log.d("CameraTest", msg)
                                }
                            }
                        )
                    }
                }) {
                    Icon(Icons.Filled.CameraAlt, "capture")
                }

                Spacer(modifier = Modifier.width(40.dp))

                // Minus button for zoom out
                Button(
                    onClick = {
//                        val currentZoomRatio = zoomRatio
//                        val step = 0.1f // Adjust the step size as needed
//                        val newZoomRatio = (currentZoomRatio - step).coerceIn(zoomRatioRange)
//                        zoomRatio = newZoomRatio
                        camera?.cameraControl?.setZoomRatio(zoomRatioRange.start)
                        zoomRatio = zoomRatioRange.start
                    }
                ) {
                    Text("MIN")
                }

                // Display current zoom level (optional)
                Text(
                    text = String.format("%.1fx", zoomRatio),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // Plus button for zoom in
                Button(
                    onClick = {
//                        val currentZoomRatio = zoomRatio
//                        val step = 0.1f // Adjust the step size as needed
//                        val newZoomRatio = (currentZoomRatio + step).coerceIn(zoomRatioRange)
//                        zoomRatio = newZoomRatio
                        camera?.cameraControl?.setZoomRatio(zoomRatioRange.endInclusive)
                        zoomRatio = zoomRatioRange.endInclusive
                    }
                ) {
                    Text("MAX")
                }
            }
        }
    }
}
