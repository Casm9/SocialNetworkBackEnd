package com.casm.service

import com.casm.data.models.Comment
import com.casm.data.repository.comment.CommentRepository
import com.casm.data.repository.user.UserRepository
import com.casm.data.requests.CreateCommentRequest
import com.casm.data.responses.CommentResponse
import com.casm.util.Constants

class CommentService(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {

    suspend fun createComment(createCommentRequest: CreateCommentRequest, userId: String): ValidationEvent {
        createCommentRequest.apply {
            if (comment.isBlank() || postId.isBlank()) {
                return ValidationEvent.ErrorFieldEmpty
            }
            if (comment.length > Constants.MAX_COMMENT_LENGTH) {
                return ValidationEvent.ErrorCommentTooLong
            }
        }
        val user = userRepository.getUserById(userId) ?: return ValidationEvent.UserNotFound
         commentRepository.createComment(
            Comment(
                username = user.username,
                profileImageUrl = user.profileImageUrl,
                likeCount = 0,
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis(),
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return commentRepository.deleteComment(commentId)
    }

    suspend fun deleteCommentsForPost(postId: String) {
        commentRepository.deleteCommentsFromPost(postId)
    }

    suspend fun getCommentsForPost(postId: String, ownUserId: String): List<CommentResponse> {
        return commentRepository.getCommentsForPost(postId, ownUserId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return commentRepository.getComment(commentId)
    }

    sealed class ValidationEvent {
        data object ErrorFieldEmpty: ValidationEvent()
        data object ErrorCommentTooLong: ValidationEvent()
        data object UserNotFound: ValidationEvent()
        data object Success : ValidationEvent()
    }
}