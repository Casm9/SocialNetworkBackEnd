package com.casm.data.repository.post

import com.casm.data.models.Post
import com.casm.data.responses.PostResponse
import com.casm.util.Constants
import com.casm.util.Constants.DEFAULT_POST_PAGE_SIZE


interface PostRepository {

    suspend fun createPost(post: Post): Boolean

    suspend fun deletePost(postId: String)

    suspend fun getPostsByFollows(
        ownUserId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<PostResponse>

    suspend fun getPostsForProfile(
        ownUserId: String,
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_PAGE_SIZE
    ): List<PostResponse>

    suspend fun getPost(postId: String): Post?

    suspend fun getPostDetails(userId: String, postId: String): PostResponse?
}