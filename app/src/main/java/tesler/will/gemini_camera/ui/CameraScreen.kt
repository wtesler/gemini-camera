package tesler.will.gemini_camera.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private enum class FlashMode {
    OFF, ON, TORCH
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    if (cameraPermissionState.status.isGranted) {
        CameraContent(modifier = modifier)
    } else {
        Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Camera permission is required to capture images for Gemini.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
private fun CameraContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var currentZoomRatio by rememberSaveable { mutableStateOf(1f) }
    var flashMode by rememberSaveable { mutableStateOf(FlashMode.OFF) }

    LaunchedEffect(currentZoomRatio, flashMode, camera) {
        camera?.let {
            it.cameraControl.setZoomRatio(currentZoomRatio)
            when (flashMode) {
                FlashMode.OFF -> {
                    imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
                    it.cameraControl.enableTorch(false)
                }
                FlashMode.ON -> {
                    imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
                    it.cameraControl.enableTorch(false)
                }
                FlashMode.TORCH -> {
                    imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
                    it.cameraControl.enableTorch(true)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e("CameraScreen", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        FlashControls(
            flashMode = flashMode,
            onFlashModeChange = { flashMode = it },
            modifier = Modifier
                .align(if (isLandscape) Alignment.TopStart else Alignment.TopEnd)
                .statusBarsPadding()
                .navigationBarsPadding()
                .displayCutoutPadding()
                .padding(16.dp, 2.dp, 16.dp, 16.dp)
        )

        AdaptiveControlBar(
            isLandscape = isLandscape,
            modifier = Modifier
                .align(if (isLandscape) Alignment.CenterEnd else Alignment.BottomCenter)
                .navigationBarsPadding()
                .displayCutoutPadding()
                .padding(PaddingValues(
                        end = if (isLandscape) 12.dp else 0.dp,
                        bottom = if (isLandscape) 0.dp else 12.dp
                    )
                )
        ) {
            ZoomControls(
                currentZoomRatio = currentZoomRatio,
                onZoomRatioChange = { ratio ->
                    currentZoomRatio = ratio
                    camera?.cameraControl?.setZoomRatio(ratio)
                },
                isLandscape = isLandscape
            )
            CaptureButton(
                onClick = {
                    takePhoto(context, imageCapture, cameraExecutor) { file ->
                        shareImageToGemini(context, file)
                    }
                }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            clearCache(context)
        }
    }
}

@Composable
private fun AdaptiveControlBar(
    isLandscape: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isLandscape) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            content = { content() }
        )
    } else {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = { content() }
        )
    }
}

@Composable
private fun FlashControls(
    flashMode: FlashMode,
    onFlashModeChange: (FlashMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FlashMode.entries.forEach { mode ->
            val isSelected = flashMode == mode
            IconButton(
                onClick = { onFlashModeChange(mode) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = when (mode) {
                        FlashMode.OFF -> Icons.Rounded.FlashOff
                        FlashMode.ON -> Icons.Rounded.FlashOn
                        FlashMode.TORCH -> Icons.Rounded.FlashlightOn
                    },
                    contentDescription = "Flash ${mode.name}",
                    tint = if (isSelected) Color.Yellow else Color.White
                )
            }
        }
    }
}

@Composable
private fun ZoomControls(
    currentZoomRatio: Float,
    onZoomRatioChange: (Float) -> Unit,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    val arrangement = Arrangement.spacedBy(4.dp)
    val content = @Composable {
        listOf(1f, 2f, 3f, 5f, 10f).forEach { ratio ->
            val isSelected = currentZoomRatio == ratio
            TextButton(
                onClick = { onZoomRatioChange(ratio) },
                modifier = Modifier.sizeIn(minWidth = 48.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent
                )
            ) {
                Text(
                    text = "${ratio.toInt()}x",
                    color = if (isSelected) Color.Yellow else Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    if (isLandscape) {
        Column(
            modifier = modifier
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                .padding(vertical = 4.dp),
            verticalArrangement = arrangement
        ) {
            content()
        }
    } else {
        Row(
            modifier = modifier
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                .padding(horizontal = 4.dp),
            horizontalArrangement = arrangement
        ) {
            content()
        }
    }
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(80.dp)
            .background(Color.White.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.Rounded.Camera,
            contentDescription = "Capture",
            modifier = Modifier.size(48.dp),
            tint = Color.Black
        )
    }
}

private fun takePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onImageCaptured: (File) -> Unit
) {
    val outputDirectory = File(context.cacheDir, "images").apply {
        if (!exists()) {
            mkdirs()
        } else {
            clearCache(context)
        }
    }
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraScreen", "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageCaptured(photoFile)
            }
        }
    )
}

private fun shareImageToGemini(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        setPackage("com.google.android.apps.bard")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        context.startActivity(Intent.createChooser(shareIntent, "Share with Gemini"))
    } catch (e: Exception) {
        Log.e("CameraScreen", "Failed to start sharing intent", e)
    }
}

private fun clearCache(context: Context) {
    val outputDirectory = File(context.cacheDir, "images")
    outputDirectory.listFiles()?.forEach { it.delete() }
}
