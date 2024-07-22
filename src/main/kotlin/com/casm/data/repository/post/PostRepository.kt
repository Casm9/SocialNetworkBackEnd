package com.casm.data.repository.post

import com.casm.data.models.Post
import com.casm.data.responses.PostResponse
import com.casm.util.Constants.DEFAULT_POST_PAGE_SIZE

interface PostRepository {
    suspend fun createPost(post: Post): Boolean

    suspend fun deletePost(postId: String)

    suspend fun getPostByFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = DEFAULT_POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPostForProfile(
        userId: String,
        page: Int = 0,
        pageSize: Int = DEFAULT_POST_PAGE_SIZE
    ): List<Post>

    suspend fun getPost(postId: String): Post?

    suspend fun getPostDetails(userId: String, postId: String): PostResponse?


}