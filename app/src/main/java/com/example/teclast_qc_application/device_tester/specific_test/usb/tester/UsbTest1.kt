package com.example.teclast_qc_application.device_tester.specific_test.usb.tester

import android.content.Context
import android.hardware.usb.UsbManager
import com.example.teclast_qc_application.test_result.test_results_db.AddTestResultV2
import com.example.teclast_qc_application.test_result.test_results_db.TestResultEvent
import com.example.teclast_qc_application.test_result.test_results_db.TestResultState
import java.util.*


fun getUsbConnectionState(context: Context): String {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val usbDevices = usbManager.deviceList

    return if (usbDevices.isNotEmpty()) {
        "USB device connected"
    } else {
        "No USB device connected"
    }
}

fun usbTest1(state: TestResultState,
             onEvent: (TestResultEvent) -> Unit, context: Context): String {
    val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val deviceList = usbManager.deviceList

    if (deviceList.isEmpty()) {
        onEvent(TestResultEvent.SaveTestResult)
        AddTestResultV2(state = state, onEvent = onEvent, "USB TEST 1", "Fail", Date().toString())
        onEvent(TestResultEvent.SaveTestResult)
        return "No USB devices connected."
    }

    val result = StringBuilder()
    //result.append("Number of connected USB devices: ${deviceList.size}\n")
    for (device in deviceList.values) {
        val deviceId = device.deviceId
        val manufacturerName = device.manufacturerName ?: "Unknown"
        val productName = device.productName ?: "Unknown"
        onEvent(TestResultEvent.SaveTestResult)
        AddTestResultV2(state = state, onEvent = onEvent, "USB TEST 1", "Success", Date().toString())
        onEvent(TestResultEvent.SaveTestResult)
        result.append("Usb TEST : Success\nDevice ID: $deviceId \nManufacturer: $manufacturerName \nProduct: $productName\n")
    }

    return result.toString()
}
