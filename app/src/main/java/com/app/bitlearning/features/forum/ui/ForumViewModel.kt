/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 */
package com.app.bitlearning.features.forum.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.bitlearning.domain.model.ForumComment
import com.app.bitlearning.domain.model.ForumPost
import com.app.bitlearning.domain.repository.ForumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForumUiState(
    val posts: List<ForumPost> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val selectedPost: ForumPost? = null,
    val comments: List<ForumComment> = emptyList(),
    val isLoadingComments: Boolean = false,
    val newPostTitle: String = "",
    val newPostContent: String = "",
    val newComment: String = "",
    val isCreatingPost: Boolean = false,
    val showCreateDialog: Boolean = false,
)

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val forumRepository: ForumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForumUiState())
    val uiState: StateFlow<ForumUiState> = _uiState

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = it.posts.isEmpty(), isRefreshing = it.posts.isNotEmpty()) }
            forumRepository.getPosts()
                .onSuccess { posts ->
                    _uiState.update { it.copy(posts = posts, isLoading = false, isRefreshing = false) }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = e.message,
                            posts = if (it.posts.isEmpty()) mockPosts() else it.posts,
                        )
                    }
                }
        }
    }

    fun selectPost(post: ForumPost) {
        _uiState.update { it.copy(selectedPost = post, comments = emptyList()) }
        loadComments(post.id)
    }

    fun clearSelectedPost() {
        _uiState.update { it.copy(selectedPost = null, comments = emptyList()) }
    }

    private fun loadComments(postId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingComments = true) }
            forumRepository.getComments(postId)
                .onSuccess { comments ->
                    _uiState.update { it.copy(comments = comments, isLoadingComments = false) }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoadingComments = false) }
                }
        }
    }

    fun onNewPostTitleChange(value: String) = _uiState.update { it.copy(newPostTitle = value) }
    fun onNewPostContentChange(value: String) = _uiState.update { it.copy(newPostContent = value) }
    fun onNewCommentChange(value: String) = _uiState.update { it.copy(newComment = value) }
    fun toggleCreateDialog() = _uiState.update { it.copy(showCreateDialog = !it.showCreateDialog) }

    fun createPost() {
        val state = _uiState.value
        if (state.newPostTitle.isBlank() || state.newPostContent.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingPost = true) }
            forumRepository.createPost(state.newPostTitle, state.newPostContent)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isCreatingPost = false,
                            showCreateDialog = false,
                            newPostTitle = "",
                            newPostContent = "",
                        )
                    }
                    loadPosts()
                }
                .onFailure {
                    _uiState.update { it.copy(isCreatingPost = false, error = "Tạo bài viết thất bại") }
                }
        }
    }

    fun submitComment() {
        val state = _uiState.value
        val postId = state.selectedPost?.id ?: return
        if (state.newComment.isBlank()) return
        viewModelScope.launch {
            forumRepository.createComment(postId, state.newComment)
                .onSuccess {
                    _uiState.update { it.copy(newComment = "") }
                    loadComments(postId)
                }
        }
    }

    fun toggleLike(post: ForumPost) {
        viewModelScope.launch {
            if (post.isLiked) {
                forumRepository.unlikePost(post.id)
            } else {
                forumRepository.likePost(post.id)
            }
            // Optimistic update
            _uiState.update { state ->
                state.copy(
                    posts = state.posts.map {
                        if (it.id == post.id) it.copy(
                            isLiked = !it.isLiked,
                            likeCount = if (it.isLiked) it.likeCount - 1 else it.likeCount + 1,
                        ) else it
                    },
                    selectedPost = state.selectedPost?.let {
                        if (it.id == post.id) it.copy(
                            isLiked = !it.isLiked,
                            likeCount = if (it.isLiked) it.likeCount - 1 else it.likeCount + 1,
                        ) else it
                    },
                )
            }
        }
    }

    private fun mockPosts() = listOf(
        ForumPost(
            id = 1, authorId = 1, authorName = "Nguyễn Văn A",
            authorAvatar = null, title = "Hỏi về Kotlin Coroutines",
            content = "Mọi người cho mình hỏi cách sử dụng Flow trong Kotlin Coroutines hiệu quả nhất?",
            likeCount = 12, commentCount = 5, createdAt = "2026-03-27", tags = listOf("Kotlin", "Android"),
        ),
        ForumPost(
            id = 2, authorId = 2, authorName = "Trần Thị B",
            authorAvatar = null, title = "Chia sẻ kinh nghiệm học Jetpack Compose",
            content = "Sau 3 tháng học Compose, mình muốn chia sẻ một số tips hữu ích cho người mới bắt đầu.",
            likeCount = 25, commentCount = 8, createdAt = "2026-03-26", tags = listOf("Compose", "UI"),
        ),
        ForumPost(
            id = 3, authorId = 3, authorName = "Lê Văn C",
            authorAvatar = null, title = "Clean Architecture trong Android",
            content = "Bàn luận về cách áp dụng Clean Architecture cho dự án Android thực tế.",
            likeCount = 18, commentCount = 12, createdAt = "2026-03-25", tags = listOf("Architecture"),
        ),
    )
}
