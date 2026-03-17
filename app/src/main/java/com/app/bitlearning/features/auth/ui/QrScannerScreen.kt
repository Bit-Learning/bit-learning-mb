package com.app.bitlearning.features.auth.ui

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.app.bitlearning.core.common.theme.Background
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

/**
 * QR scanner entry screen used from the authenticated area.
 *
 * In production you would integrate CameraX / ML Kit to detect the QR code
 * and then call [onTokenScanned] with the decoded token. For now we expose a
 * simple text field so the flow and API timing are wired correctly.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    onNavigateBack: () -> Unit,
    onTokenScanned: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var simulatedToken by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        )
        if (permissionStatus == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quét mã QR đăng nhập web") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        containerColor = Background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .background(Background),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Sử dụng điện thoại để đăng nhập tài khoản web",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )

            Text(
                text = "Hướng camera vào mã QR trên trang đăng nhập web Bit Learning. " +
                    "Nếu không quét được, bạn có thể dán giá trị token vào ô bên dưới.",
                style = MaterialTheme.typography.bodyMedium,
            )

            if (hasCameraPermission) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    factory = { androidContext ->
                        val previewView = PreviewView(androidContext).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }

                        val cameraProviderFuture =
                            ProcessCameraProvider.getInstance(androidContext)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()

                            val preview = Preview.Builder().build().also { previewUseCase ->
                                previewUseCase.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val analysisUseCase = ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                    ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST,
                                )
                                .build()

                            analysisUseCase.setAnalyzer(
                                ContextCompat.getMainExecutor(androidContext),
                                QrCodeAnalyzer { token ->
                                    Log.d("QrScannerScreen", "QR detected: $token")
                                    onTokenScanned(token)
                                },
                            )

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    analysisUseCase,
                                )
                            } catch (e: Exception) {
                                Log.e("QrScannerScreen", "Camera binding failed", e)
                            }
                        }, ContextCompat.getMainExecutor(androidContext))

                        previewView
                    },
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ứng dụng cần quyền truy cập camera để quét mã QR.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            TextField(
                value = simulatedToken,
                onValueChange = { simulatedToken = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Giá trị qrToken (mô phỏng)") },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onTokenScanned(simulatedToken.trim()) },
                enabled = simulatedToken.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Tiếp tục")
            }

            TextButton(onClick = onNavigateBack) {
                Text("Hủy")
            }
        }
    }
}

private class QrCodeAnalyzer(
    private val onTokenDetected: (String) -> Unit,
) : ImageAnalysis.Analyzer {

    @Volatile
    private var handled = false

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees,
        )

        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (handled) return@addOnSuccessListener
                val qrCode = barcodes.firstOrNull { barcode ->
                    barcode.format == Barcode.FORMAT_QR_CODE && !barcode.rawValue.isNullOrBlank()
                }
                val value = qrCode?.rawValue
                if (!value.isNullOrBlank()) {
                    handled = true
                    onTokenDetected(value)
                }
            }
            .addOnFailureListener { e ->
                Log.e("QrCodeAnalyzer", "Barcode analysis failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

