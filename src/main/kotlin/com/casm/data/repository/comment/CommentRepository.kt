package com.casm.data.repository.comment

import com.casm.data.models.Comment
import com.casm.data.responses.CommentResponse

interface CommentRepository {

    suspend fun createComment(comment: Comment): String

    suspend fun deleteComment(commentId: String): Boolean

    suspend fun deleteCommentsFromPost(postId: String): Boolean

    suspend fun getCommentsForPost(postId: String): List<CommentResponse>

    suspend fun getComment(commentId: String): Comment?

}