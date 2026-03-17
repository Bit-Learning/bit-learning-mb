package com.app.bitlearning.features.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.theme.Background
import com.app.bitlearning.core.common.theme.Error
import com.app.bitlearning.core.common.theme.Primary
import com.app.bitlearning.core.common.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrLoginConfirmScreen(
	qrToken: String,
	onDone: () -> Unit,
	viewModel: AuthViewModel = hiltViewModel(),
) {
	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	// Immediately notify backend that this QR token has been scanned.
	LaunchedEffect(qrToken) {
		viewModel.scanQrToken(qrToken)
	}

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Xác nhận đăng nhập web") },
				navigationIcon = {
					IconButton(onClick = onDone) {
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
		) {
			Text(
				text = "Yêu cầu đăng nhập được phát hiện",
				style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
			)

			Text(
				text = "Thiết bị: Trình duyệt web (Chrome hoặc tương tự)\n" +
					"Phiên: Đăng nhập Bit Learning trên web.",
				style = MaterialTheme.typography.bodyMedium,
			)

			Surface(
				modifier = Modifier.fillMaxWidth(),
				color = Surface,
				shape = MaterialTheme.shapes.medium,
			) {
				Column(
					modifier = Modifier.padding(16.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
				) {
					Text(
						text = "Mã QR đã được quét từ ứng dụng web. " +
							"Nhấn \"Xác nhận đăng nhập\" để cho phép đăng nhập trên trình duyệt.",
						style = MaterialTheme.typography.bodySmall,
					)
				}
			}

			if (uiState.error != null) {
				Text(
					text = uiState.error!!,
					style = MaterialTheme.typography.bodySmall.copy(color = Error),
				)
			}

			uiState.qrMessage?.let { message ->
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.primaryContainer)
						.padding(12.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(8.dp),
				) {
					Icon(
						imageVector = Icons.Filled.CheckCircle,
						contentDescription = null,
						tint = Primary,
					)
					Text(
						text = message,
						style = MaterialTheme.typography.bodySmall,
					)
				}
			}

			Spacer(modifier = Modifier.height(8.dp))

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.spacedBy(12.dp),
			) {
				Button(
					onClick = onDone,
					modifier = Modifier.weight(1f),
					enabled = !uiState.isLoading,
				) {
					Text("Hủy")
				}

				Button(
					onClick = { viewModel.confirmQrLogin(qrToken) },
					modifier = Modifier.weight(1f),
					enabled = !uiState.isLoading,
				) {
					Text("Xác nhận đăng nhập")
				}
			}

			if (uiState.isLoading) {
				Spacer(modifier = Modifier.height(8.dp))
				LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
			}
		}
	}
}
