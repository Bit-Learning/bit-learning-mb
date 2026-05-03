/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.player.ui

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.bitlearning.core.common.components.BLLoadingIndicator
import com.app.bitlearning.core.common.components.BLPrimaryButton
import com.app.bitlearning.core.common.components.BLProgressBar
import com.app.bitlearning.core.common.components.BLDivider
import com.app.bitlearning.core.common.theme.Background
import com.app.bitlearning.core.common.theme.CardSurface
import com.app.bitlearning.core.common.theme.OnPrimary
import com.app.bitlearning.core.common.theme.OnSurface
import com.app.bitlearning.core.common.theme.OnSurfaceMuted
import com.app.bitlearning.core.common.theme.OnSurfaceVariant
import com.app.bitlearning.core.common.theme.Primary
import com.app.bitlearning.core.common.theme.PrimaryContainer
import com.app.bitlearning.core.common.theme.Surface
import com.app.bitlearning.core.common.theme.SurfaceVariant
import com.app.bitlearning.domain.model.Lecture
import com.app.bitlearning.domain.model.LectureQuizContent
import com.app.bitlearning.domain.model.LectureType
import com.app.bitlearning.features.player.components.BLVideoPlayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    courseId: String,
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tabs = listOf("Bài học", "Giới thiệu", "Nội dung")
    var isFullscreen by remember { mutableStateOf(false) }
    val showBottomCompletionAction =
        uiState.currentLesson != null &&
            uiState.canAccessCurrentLesson &&
            uiState.currentLesson?.type == LectureType.TEXT

    Scaffold(
        containerColor = Background,
        topBar = {
            if (!isFullscreen) {
                PlayerTopBar(
                    title = uiState.course?.title ?: "",
                    onBack = onNavigateBack,
                )
            }
        },
        bottomBar = {
            if (!isFullscreen && showBottomCompletionAction) {
                Box(modifier = Modifier.padding(16.dp)) {
                    BLPrimaryButton(
                        text = if (uiState.currentLesson?.isCompleted == true) "Đã hoàn thành" else "Đánh dấu đã hoàn thành",
                        onClick = { viewModel.markCurrentCompleted() },
                        leadingIcon = Icons.Filled.CheckCircle,
                        enabled = uiState.currentLesson?.isCompleted != true,
                    )
                }
            }
        },
    ) { padding ->
        if (uiState.isLoading) {
            BLLoadingIndicator()
            return@Scaffold
        }

        if (isFullscreen) {
            // Fullscreen: video fills entire screen
            LessonContentHeader(
                uiState = uiState,
                onSync = viewModel::syncCurrentProgress,
                isFullscreen = true,
                onFullscreenChange = { isFullscreen = it },
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background),
        ) {
            LessonContentHeader(
                uiState = uiState,
                onSync = viewModel::syncCurrentProgress,
                isFullscreen = false,
                onFullscreenChange = { isFullscreen = it },
            )
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = Primary,
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = uiState.selectedTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (uiState.selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                ),
                            )
                        },
                    )
                }
            }

            when (uiState.selectedTab) {
                0 -> LessonListTab(
                    uiState = uiState,
                    onSelectLesson = viewModel::selectLesson,
                    onToggleAutoPlay = viewModel::toggleAutoPlay,
                )
                1 -> CourseIntroTab(uiState = uiState)
                else -> LessonContentTab(uiState = uiState, onQuizPassed = viewModel::markCurrentCompleted)
            }
        }
    }
}

@Composable
private fun LessonContentHeader(
    uiState: PlayerUiState,
    onSync: (Int, Int) -> Unit,
    isFullscreen: Boolean = false,
    onFullscreenChange: ((Boolean) -> Unit)? = null,
) {
    when {
        !uiState.canAccessCurrentLesson -> LockedLessonBanner()
        uiState.currentLesson?.type == LectureType.VIDEO -> {
            BLVideoPlayer(
                videoUrl = uiState.currentVideoUrl,
                authToken = uiState.authToken,
                modifier = if (isFullscreen) Modifier.fillMaxSize() else Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                autoPlay = uiState.isAutoPlay,
                startPositionSeconds = uiState.lastWatchedSecond,
                onProgressSync = onSync,
                isFullscreen = isFullscreen,
                onFullscreenChange = onFullscreenChange,
            )
        }

        uiState.currentLesson?.type == LectureType.TEXT -> TextLessonPreview(uiState)
        uiState.currentLesson?.type == LectureType.QUIZ -> QuizLessonPreview(uiState.currentQuizContent)
    }
}

@Composable
private fun LessonListTab(
    uiState: PlayerUiState,
    onSelectLesson: (Lecture) -> Unit,
    onToggleAutoPlay: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "TIẾN ĐỘ KHÓA HỌC",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = OnSurfaceMuted,
                    letterSpacing = 0.8.sp,
                ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val progress = uiState.course?.progress ?: 0f
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(color = Primary),
                )
                TextButton(onClick = onToggleAutoPlay, contentPadding = PaddingValues(0.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Autorenew,
                        contentDescription = null,
                        tint = if (uiState.isAutoPlay) Primary else OnSurfaceMuted,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "TỰ ĐỘNG PHÁT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (uiState.isAutoPlay) Primary else OnSurfaceMuted,
                        ),
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            BLProgressBar(progress = uiState.course?.progress ?: 0f, showLabel = false)
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.lessons) { lecture ->
                PlayerLectureItem(
                    lecture = lecture,
                    isCurrentLecture = lecture.id == uiState.currentLesson?.id,
                    onClick = { onSelectLesson(lecture) },
                )
                BLDivider(Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun CourseIntroTab(uiState: PlayerUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(uiState.course?.description.orEmpty(), style = MaterialTheme.typography.bodyLarge)
        }
        if (!uiState.course?.outcome.isNullOrBlank()) {
            item {
                Text("Kết quả đạt được", style = MaterialTheme.typography.titleMedium)
                Text(uiState.course?.outcome.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (!uiState.course?.requirement.isNullOrBlank()) {
            item {
                Text("Yêu cầu", style = MaterialTheme.typography.titleMedium)
                Text(uiState.course?.requirement.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun LessonContentTab(
    uiState: PlayerUiState,
    onQuizPassed: () -> Unit,
) {
    when {
        !uiState.canAccessCurrentLesson -> LockedLessonBanner()
        uiState.currentLesson?.type == LectureType.TEXT -> TextLessonContent(uiState)
        uiState.currentLesson?.type == LectureType.QUIZ -> QuizLessonContent(uiState.currentQuizContent, onQuizPassed)
        else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Video đang phát ở đầu trang", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun LockedLessonBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(SurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Lock, null, tint = OnSurfaceMuted, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(8.dp))
            Text("Bài học bị khóa", style = MaterialTheme.typography.titleMedium)
            Text("Hãy đăng ký khóa học trên web để học đầy đủ", style = MaterialTheme.typography.bodyMedium, color = OnSurfaceMuted)
        }
    }
}

@Composable
private fun TextLessonPreview(uiState: PlayerUiState) {
    val previewText = uiState.currentTextContent?.content
        .orEmpty()
        .replace(Regex("<[^>]*>"), " ")
        .replace("&nbsp;", " ")
        .replace(Regex("\\s+"), " ")
        .trim()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(uiState.currentLesson?.title.orEmpty(), style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                previewText.take(180).ifBlank { "Bài học văn bản" },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun TextLessonContent(uiState: PlayerUiState) {
    val html = uiState.currentTextContent?.content.orEmpty()
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(AndroidColor.TRANSPARENT)
                overScrollMode = WebView.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
                settings.apply {
                    javaScriptEnabled = false
                    domStorageEnabled = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                    defaultTextEncodingName = "utf-8"
                }
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean = false
                }
            }
        },
        update = { view ->
            view.loadDataWithBaseURL(
                null,
                wrapLectureHtml(html),
                "text/html",
                "utf-8",
                null,
            )
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun QuizLessonPreview(quiz: LectureQuizContent?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(quiz?.lecture?.title.orEmpty(), style = MaterialTheme.typography.titleLarge)
            Text("Số câu hỏi: ${quiz?.quizzes?.size ?: 0}", style = MaterialTheme.typography.bodyMedium)
            Text("Điểm đạt: ${((quiz?.passPercent ?: 0f) * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun QuizLessonContent(
    quiz: LectureQuizContent?,
    onQuizPassed: () -> Unit,
) {
    if (quiz == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có dữ liệu bài tập", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    var currentIndex by remember(quiz.lecture.id) { mutableIntStateOf(0) }
    val selectedAnswers = remember(quiz.lecture.id) { mutableStateMapOf<Int, Int>() }
    var submitted by remember(quiz.lecture.id) { mutableStateOf(false) }
    var score by remember(quiz.lecture.id) { mutableStateOf(0f) }
    val isAlreadyCompleted = quiz.lecture.isCompleted

    val currentQuiz = quiz.quizzes.getOrNull(currentIndex)
    val allAnswered = selectedAnswers.size == quiz.quizzes.size && quiz.quizzes.isNotEmpty()

    if (currentQuiz == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không có câu hỏi", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            if (isAlreadyCompleted) {
                Text(
                    text = "Bài quiz này đã được hoàn thành trước đó.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            Text(currentQuiz.questionText, style = MaterialTheme.typography.titleMedium)
            Text(
                "Câu ${currentIndex + 1}/${quiz.quizzes.size} • Điểm đạt ${((quiz.passPercent) * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
            )
        }

        items(currentQuiz.answers, key = { it.id }) { answer ->
            val isSelected = selectedAnswers[currentQuiz.id] == answer.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelected,
                        enabled = !submitted,
                        onClick = { selectedAnswers[currentQuiz.id] = answer.id },
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        submitted && answer.isCorrect -> PrimaryContainer
                        submitted && isSelected && !answer.isCorrect -> SurfaceVariant
                        isSelected -> PrimaryContainer.copy(alpha = 0.5f)
                        else -> CardSurface
                    },
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    RadioButton(selected = isSelected, onClick = null, enabled = !submitted)
                    Text(answer.answerText, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                androidx.compose.material3.Button(
                    onClick = { if (currentIndex > 0) currentIndex-- },
                    enabled = currentIndex > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariant, contentColor = OnSurface),
                ) {
                    Text("Câu trước")
                }

                androidx.compose.material3.Button(
                    onClick = {
                        if (currentIndex < quiz.quizzes.lastIndex) {
                            currentIndex++
                        } else if (!submitted && allAnswered) {
                            val correctCount = quiz.quizzes.count { question ->
                                val selectedId = selectedAnswers[question.id]
                                question.answers.firstOrNull { it.isCorrect }?.id == selectedId
                            }
                            score = correctCount.toFloat() / quiz.quizzes.size.toFloat()
                            submitted = true
                            if (score >= quiz.passPercent && !isAlreadyCompleted) onQuizPassed()
                        }
                    },
                    enabled = currentIndex < quiz.quizzes.lastIndex || (!submitted && allAnswered),
                ) {
                    Text(if (currentIndex < quiz.quizzes.lastIndex) "Câu tiếp" else "Nộp bài")
                }
            }
        }

        if (submitted) {
            item {
                Text(
                    text = if (score >= quiz.passPercent) {
                        "Đạt ${((score) * 100).toInt()}% - bài học đã được đánh dấu hoàn thành."
                    } else {
                        "Bạn đạt ${((score) * 100).toInt()}%. Cần tối thiểu ${((quiz.passPercent) * 100).toInt()}%."
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (score >= quiz.passPercent) Primary else OnSurface,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            if (score < quiz.passPercent) {
                item {
                    androidx.compose.material3.Button(
                        onClick = {
                            selectedAnswers.clear()
                            submitted = false
                            score = 0f
                            currentIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SurfaceVariant,
                            contentColor = OnSurface,
                        ),
                    ) {
                        Text("Làm lại")
                    }
                }
            }
        }
    }
}

private fun wrapLectureHtml(content: String): String = """
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
  <style>
    :root {
      color-scheme: light;
      --text: #0f172a;
      --muted: #475569;
      --line: #e2e8f0;
      --surface: #ffffff;
      --code-bg: #f8fafc;
      --pre-bg: #0f172a;
      --pre-text: #e2e8f0;
      --link: #2563eb;
    }
    * { box-sizing: border-box; }
    html, body {
      margin: 0;
      padding: 0;
      background: var(--surface);
      color: var(--text);
      font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
      line-height: 1.7;
      font-size: 16px;
      word-break: break-word;
    }
    body {
      padding: 20px 18px 28px;
    }
    h1, h2, h3, h4, h5, h6 {
      color: var(--text);
      line-height: 1.3;
      margin: 1.25em 0 0.6em;
    }
    h1 { font-size: 1.8rem; }
    h2 {
      font-size: 1.45rem;
      border-bottom: 2px solid var(--line);
      padding-bottom: 0.35rem;
    }
    h3 { font-size: 1.2rem; }
    p, ul, ol, blockquote, table, pre {
      margin: 0 0 1rem;
    }
    ul, ol { padding-left: 1.4rem; }
    li { margin: 0.45rem 0; }
    a {
      color: var(--link);
      text-decoration: underline;
    }
    img {
      max-width: 100%;
      height: auto;
      border-radius: 10px;
      margin: 0.75rem 0;
    }
    code {
      background: var(--code-bg);
      padding: 0.15rem 0.35rem;
      border-radius: 6px;
      font-family: "JetBrains Mono", "SFMono-Regular", monospace;
      font-size: 0.92em;
    }
    pre {
      background: var(--pre-bg);
      color: var(--pre-text);
      padding: 0.95rem;
      border-radius: 12px;
      overflow-x: auto;
      white-space: pre-wrap;
    }
    pre code {
      background: transparent;
      padding: 0;
      color: inherit;
    }
    blockquote {
      margin-left: 0;
      padding: 0.9rem 1rem;
      border-left: 4px solid var(--link);
      background: #f8fafc;
      color: var(--muted);
      border-radius: 8px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      display: block;
      overflow-x: auto;
    }
    th, td {
      border: 1px solid var(--line);
      padding: 0.7rem;
      text-align: left;
      min-width: 120px;
    }
    th {
      background: #f8fafc;
      font-weight: 700;
    }
  </style>
</head>
<body>
  $content
</body>
</html>
""".trimIndent()

@Composable
private fun PlayerLectureItem(
    lecture: Lecture,
    isCurrentLecture: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isCurrentLecture) PrimaryContainer.copy(alpha = 0.4f) else Background)
            .clickable(enabled = !lecture.isLocked) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    when {
                        isCurrentLecture -> Primary
                        lecture.isCompleted -> PrimaryContainer
                        lecture.isLocked -> SurfaceVariant
                        else -> SurfaceVariant
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            when {
                lecture.isCompleted && !isCurrentLecture -> Icon(Icons.Filled.CheckCircle, null, tint = Primary, modifier = Modifier.size(20.dp))
                lecture.isLocked -> Icon(Icons.Filled.Lock, null, tint = OnSurfaceMuted, modifier = Modifier.size(18.dp))
                else -> Icon(
                    Icons.Filled.PlayArrow,
                    null,
                    tint = if (isCurrentLecture) OnPrimary else OnSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${lecture.orderIndex.toString().padStart(2, '0')}. ${lecture.title}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = when {
                        lecture.isLocked -> OnSurfaceMuted
                        isCurrentLecture -> Primary
                        else -> OnSurface
                    },
                    fontWeight = if (isCurrentLecture) FontWeight.SemiBold else FontWeight.Normal,
                    textDecoration = if (lecture.isCompleted && !isCurrentLecture) TextDecoration.LineThrough else TextDecoration.None,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = lecture.type.name,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isCurrentLecture) Primary else OnSurfaceMuted,
                    ),
                )
                if (lecture.isPreviewable) {
                    Text("• Preview", style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted))
                }
            }
        }

        if (isCurrentLecture) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Primary),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerTopBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.MoreVert, null)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
    )
}

private fun Modifier.tabIndicatorOffset(tabPosition: TabPosition): Modifier = this.fillMaxWidth(1f / 3)
    .wrapContentSize(Alignment.BottomStart)
    .offset(x = tabPosition.left)
