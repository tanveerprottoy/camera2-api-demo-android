package com.tanveershafeeprottoy.camera2apidemo

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.util.Log

object CameraUtils {
    private const val TAG = "CameraUtils"
    private lateinit var cameraManager: CameraManager
    const val MAX_WIDTH = 1280
    const val MAX_HEIGHT = 720

    fun initCameraManager(context: Context) {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    fun <T> getCameraCharacteristics(cameraId: String, key: CameraCharacteristics.Key<T>): T {
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val characteristic = characteristics.get(key)!!
        return when(key) {
            CameraCharacteristics.LENS_FACING -> characteristic
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP -> characteristic
            else -> throw IllegalArgumentException("Type of key is not supported")
        }
    }

    fun getCameraId(type: Int): String {
        return try {
            cameraManager.cameraIdList.filter {
                type == getCameraCharacteristics(it, CameraCharacteristics.LENS_FACING)
            }[0]
        } catch(c: CameraAccessException) {
            ""
        }
    }

    @SuppressLint("MissingPermission")
    fun connectCamera(
        cameraDeviceStateCallback: CameraDevice.StateCallback,
        backgroundHandler: Handler
    ) {
        try {
            Log.d(TAG, getCameraId(CameraCharacteristics.LENS_FACING_BACK))
            cameraManager.openCamera(
                getCameraId(CameraCharacteristics.LENS_FACING_BACK),
                cameraDeviceStateCallback, backgroundHandler
            )
        } catch(e: Exception) {
            Log.d(TAG, e.message)
        }
    }
}