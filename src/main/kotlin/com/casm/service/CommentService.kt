package com.casm.service

import com.casm.data.models.Comment
import com.casm.data.repository.comment.CommentRepository
import com.casm.data.requests.CreateCommentRequest
import com.casm.util.Constants

class CommentService(
    private val repository: CommentRepository
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
         repository.createComment(
            Comment(
                comment = createCommentRequest.comment,
                userId = userId,
                postId = createCommentRequest.postId,
                timestamp = System.currentTimeMillis(),
                likeCount = 0,
                profileImageUrl = "",
                username = ""
            )
        )
        return ValidationEvent.Success
    }

    suspend fun deleteComment(commentId: String): Boolean {
        return repository.deleteComment(commentId)
    }

    suspend fun deleteCommentsForPost(postId: String) {
        repository.deleteCommentsFromPost(postId)
    }

    suspend fun getCommentsForPost(postId: String): List<Comment> {
        return repository.getCommentsForPost(postId)
    }

    suspend fun getCommentById(commentId: String): Comment? {
        return repository.getComment(commentId)
    }

    sealed class ValidationEvent {
        data object ErrorFieldEmpty: ValidationEvent()
        data object ErrorCommentTooLong: ValidationEvent()
        data object Success : ValidationEvent()
    }
}