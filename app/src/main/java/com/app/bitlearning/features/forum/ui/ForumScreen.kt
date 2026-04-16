/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 */
package com.app.bitlearning.features.forum.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.components.*
import com.app.bitlearning.core.common.theme.*
import com.app.bitlearning.domain.model.ForumPost

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ForumViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Post detail dialog
    if (uiState.selectedPost != null) {
        PostDetailDialog(
            post = uiState.selectedPost!!,
            comments = uiState.comments,
            isLoadingComments = uiState.isLoadingComments,
            newComment = uiState.newComment,
            onNewCommentChange = viewModel::onNewCommentChange,
            onSubmitComment = viewModel::submitComment,
            onLike = { viewModel.toggleLike(uiState.selectedPost!!) },
            onDismiss = viewModel::clearSelectedPost,
        )
    }

    // Create post dialog
    if (uiState.showCreateDialog) {
        CreatePostDialog(
            title = uiState.newPostTitle,
            content = uiState.newPostContent,
            isCreating = uiState.isCreatingPost,
            onTitleChange = viewModel::onNewPostTitleChange,
            onContentChange = viewModel::onNewPostContentChange,
            onCreate = viewModel::createPost,
            onDismiss = viewModel::toggleCreateDialog,
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Diễn đàn", style = MaterialTheme.typography.headlineMedium)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::toggleCreateDialog,
                containerColor = Primary,
                contentColor = OnPrimary,
                shape = CircleShape,
            ) {
                Icon(Icons.Filled.Edit, contentDescription = "Tạo bài viết")
            }
        },
        bottomBar = {
            ForumBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToCourses = onNavigateToCourses,
                onNavigateToProfile = onNavigateToProfile,
            )
        },
    ) { padding ->
        if (uiState.isLoading) {
            BLLoadingIndicator()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.posts, key = { it.id }) { post ->
                    ForumPostCard(
                        post = post,
                        onClick = { viewModel.selectPost(post) },
                        onLike = { viewModel.toggleLike(post) },
                    )
                }

                if (uiState.posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Filled.Forum,
                                    contentDescription = null,
                                    tint = OnSurfaceMuted,
                                    modifier = Modifier.size(48.dp),
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Chưa có bài viết nào",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceMuted),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


// ─────────────────────────────────────────────
//  Forum Post Card
// ─────────────────────────────────────────────
@Composable
private fun ForumPostCard(
    post: ForumPost,
    onClick: () -> Unit,
    onLike: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Author row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                BLAvatar(imageUrl = post.authorAvatar, size = 36.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = post.createdAt,
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                    )
                }
            }

            // Title
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            // Content preview
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceVariant),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            // Media preview
            if (post.mediaUrls.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(post.mediaUrls) { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariant),
                        )
                    }
                }
            }

            // Tags
            if (post.tags.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    post.tags.take(3).forEach { tag ->
                        BLCategoryChip(label = tag, color = Secondary)
                    }
                }
            }

            BLDivider()

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.clickable { onLike() },
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Thích",
                        tint = if (post.isLiked) Error else OnSurfaceMuted,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "${post.likeCount}",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Comment,
                        contentDescription = "Bình luận",
                        tint = OnSurfaceMuted,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "${post.commentCount}",
                        style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Post Detail Dialog
// ─────────────────────────────────────────────
@Composable
private fun PostDetailDialog(
    post: ForumPost,
    comments: List<com.app.bitlearning.domain.model.ForumComment>,
    isLoadingComments: Boolean,
    newComment: String,
    onNewCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
    onLike: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Chi tiết bài viết",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Đóng")
                    }
                }

                BLDivider()

                // Content
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Post content
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            BLAvatar(imageUrl = post.authorAvatar, size = 40.dp)
                            Column {
                                Text(
                                    post.authorName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                )
                                Text(
                                    post.createdAt,
                                    style = MaterialTheme.typography.bodySmall.copy(color = OnSurfaceMuted),
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            post.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        )
                    }

                    item {
                        Text(
                            post.content,
                            style = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceVariant),
                        )
                    }

                    // Media
                    if (post.mediaUrls.isNotEmpty()) {
                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(post.mediaUrls) { url ->
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurfaceVariant),
                                    )
                                }
                            }
                        }
                    }

                    // Like button
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.clickable { onLike() },
                        ) {
                            Icon(
                                imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = if (post.isLiked) Error else OnSurfaceMuted,
                            )
                            Text(
                                "${post.likeCount} lượt thích",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    item { BLDivider() }

                    // Comments header
                    item {
                        Text(
                            "Bình luận (${comments.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        )
                    }

                    if (isLoadingComments) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(
                                    color = Primary,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }
                    } else {
                        items(comments, key = { it.id }) { comment ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                BLAvatar(imageUrl = comment.authorAvatar, size = 32.dp)
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SurfaceVariant)
                                        .padding(10.dp),
                                ) {
                                    Text(
                                        comment.authorName,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    )
                                    Text(
                                        comment.content,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                    Text(
                                        comment.createdAt,
                                        style = MaterialTheme.typography.labelSmall.copy(color = OnSurfaceMuted),
                                    )
                                }
                            }
                        }
                    }
                }

                // Comment input
                BLDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = onNewCommentChange,
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Viết bình luận...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = OnSurfaceMuted),
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Divider,
                            focusedBorderColor = Primary,
                        ),
                    )
                    IconButton(
                        onClick = onSubmitComment,
                        enabled = newComment.isNotBlank(),
                    ) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Gửi",
                            tint = if (newComment.isNotBlank()) Primary else OnSurfaceMuted,
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Create Post Dialog
// ─────────────────────────────────────────────
@Composable
private fun CreatePostDialog(
    title: String,
    content: String,
    isCreating: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onCreate: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Surface),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Tạo bài viết mới",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Đóng")
                    }
                }

                BLTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    placeholder = "Tiêu đề bài viết",
                    leadingIcon = Icons.Filled.Title,
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Nội dung bài viết...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceMuted),
                        )
                    },
                    minLines = 4,
                    maxLines = 8,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Divider,
                        focusedBorderColor = Primary,
                        unfocusedContainerColor = SurfaceVariant,
                        focusedContainerColor = Surface,
                    ),
                )

                if (isCreating) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    BLPrimaryButton(
                        text = "Đăng bài →",
                        onClick = onCreate,
                        enabled = title.isNotBlank() && content.isNotBlank(),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Bottom Navigation Bar
// ─────────────────────────────────────────────
@Composable
private fun ForumBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val colors = NavigationBarItemDefaults.colors(
        selectedIconColor = Primary,
        selectedTextColor = Primary,
        unselectedIconColor = OnSurfaceMuted,
        unselectedTextColor = OnSurfaceMuted,
        indicatorColor = PrimaryContainer,
    )
    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 0.dp,
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToHome,
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("Trang chủ", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToCourses,
            icon = { Icon(Icons.Filled.School, contentDescription = null) },
            label = { Text("Khóa học", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Filled.Forum, contentDescription = null) },
            label = { Text("Diễn đàn", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
        NavigationBarItem(
            selected = false,
            onClick = onNavigateToProfile,
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
            label = { Text("Cá nhân", style = MaterialTheme.typography.labelSmall) },
            colors = colors,
        )
    }
}
