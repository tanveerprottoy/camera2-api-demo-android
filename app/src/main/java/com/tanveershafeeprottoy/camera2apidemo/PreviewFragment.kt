package com.tanveershafeeprottoy.camera2apidemo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_preview.*
import java.util.*

class PreviewFragment : Fragment() {
    private lateinit var handlerThread: HandlerThread
    private lateinit var handler: Handler
    private lateinit var cameraDevice: CameraDevice
    private var permissionGrant = false
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var captureRequestBuilder: CaptureRequest.Builder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preview, container, false)
    }

    override fun onStart() {
        super.onStart()
        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        if(permissionGrant) {
            startBackgroundThread()
            if(fragmentPreviewTextureView.isAvailable) {
                openCamera()
            }
            else {
                fragmentPreviewTextureView.surfaceTextureListener = surfaceTextureListener
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if(permissionGrant) {
            closeCamera()
            stopBackgroundThread()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionGrant = true
                    openCamera()
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun requestPermission() {
        if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            }
            else {
                // No explanation needed, we can request the permission.
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            permissionGrant = true
            openCamera()
        }
    }

    private fun previewSession() {
        val surfaceTexture = fragmentPreviewTextureView.surfaceTexture
        surfaceTexture.setDefaultBufferSize(CameraUtils.MAX_WIDTH, CameraUtils.MAX_HEIGHT)
        val surface = Surface(surfaceTexture)
        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequestBuilder.addTarget(surface)
        cameraDevice.createCaptureSession(
            Arrays.asList(surface),
            object : CameraCaptureSession.StateCallback() {

                override fun onConfigureFailed(session: CameraCaptureSession) {
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    captureSession = session
                    captureRequestBuilder.set(
                        CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )
                    captureSession.setRepeatingRequest(
                        captureRequestBuilder.build(),
                        null,
                        null
                    )
                }
            },
            null
        )
    }

    private fun closeCamera() {
        if(this::captureSession.isInitialized) {
            captureSession.close()
        }
        if(this::cameraDevice.isInitialized) {
            cameraDevice.close()
        }
    }

    private fun startBackgroundThread() {
        handlerThread = HandlerThread("CameraPreview").also {
            it.start()
        }
        handler = Handler(handlerThread.looper)
    }

    private fun stopBackgroundThread() {
        handlerThread.quitSafely()
        try {
            handlerThread.join()
        }
        catch(i: InterruptedException) {

        }
    }

    private fun openCamera() {
        startBackgroundThread()
        CameraUtils.initCameraManager(activity?.applicationContext!!)
        CameraUtils.connectCamera(cameraDeviceStateCallback, handler)
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureSizeChanged(
            surface: SurfaceTexture?,
            width: Int,
            height: Int
        ) {

        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = false

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
            //open camera here
            Toast.makeText(
                activity?.applicationContext,
                "Width: $width Height: $height",
                Toast.LENGTH_LONG
            ).show()
            openCamera()
        }
    }

    private val cameraDeviceStateCallback = object : CameraDevice.StateCallback() {

        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            previewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            //Kill the Activity
        }
    }

    companion object {
        const val CAMERA_PERMISSION_CODE = 17

        @JvmStatic
        fun newInstance() = PreviewFragment()
    }
}
