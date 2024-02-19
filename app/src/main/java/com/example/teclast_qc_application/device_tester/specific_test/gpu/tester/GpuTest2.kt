package com.example.teclast_qc_application.device_tester.specific_test.gpu.tester

import com.example.teclast_qc_application.test_result.test_results_db.AddTestResultV2
import com.example.teclast_qc_application.test_result.test_results_db.TestResultEvent
import com.example.teclast_qc_application.test_result.test_results_db.TestResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext

suspend fun gpu3DTest(state: TestResultState,
                      onEvent: (TestResultEvent) -> Unit,): String {
    return withContext(Dispatchers.Default) {
        try {
            val egl = EGLContext.getEGL() as EGL10
            val display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

            val version = IntArray(2)
            egl.eglInitialize(display, version)

            val configAttribs = intArrayOf(
                EGL10.EGL_RENDERABLE_TYPE, 4, // Request an OpenGL ES 2.0 compatible config
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT,
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                EGL10.EGL_DEPTH_SIZE, 16,
                EGL10.EGL_STENCIL_SIZE, 0,
                EGL10.EGL_NONE
            )

            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfig = IntArray(1)
            egl.eglChooseConfig(display, configAttribs, configs, 1, numConfig)

            if (numConfig[0] > 0) {
                AddTestResultV2(state = state, onEvent = onEvent, "GPU TEST 2", "Success", Date().toString())
                "GPU TEST 2 : Success"
            } else {
                AddTestResultV2(state = state, onEvent = onEvent, "GPU TEST 2", "Fail", Date().toString())
                "GPU TEST 2 : Fail : Error: OpenGL ES 2.0 not available"
            }
        } catch (error: Exception) {
            AddTestResultV2(state = state, onEvent = onEvent, "GPU TEST 2", "Fail", Date().toString())
            "GPU TEST 2 : Fail : Error: ${error.message}"
        }
    }
}