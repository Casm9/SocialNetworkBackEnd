package com.casm.service

import com.casm.data.models.Post
import com.casm.data.requests.CreatePostRequest
import com.casm.data.repository.post.PostRepository
import com.casm.data.responses.PostResponse
import com.casm.util.Constants

class PostService(
    private val repository: PostRepository
) {

    suspend fun createPost(request: CreatePostRequest, userId: String, imageUrl: String): Boolean {
       return repository.createPost(
            Post(
                imageUrl = imageUrl,
                userId = userId,
                timestamp = System.currentTimeMillis(),
                description = request.description
            )
        )
    }

    suspend fun getPostsForFollows(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostByFollows(userId, page, pageSize)
    }

    suspend fun getPost(postId: String): Post? {
        return repository.getPost(postId)
    }

    suspend fun getPostDetails(ownUserId: String, postId: String): PostResponse? {
        return repository.getPostDetails(ownUserId, postId)
    }

    suspend fun getPostsForProfile(
        userId: String,
        page: Int = 0,
        pageSize: Int = Constants.DEFAULT_POST_PAGE_SIZE
    ): List<Post> {
        return repository.getPostForProfile(userId, page, pageSize)
    }

    suspend fun deletePost(postId: String) {
        repository.deletePost(postId)
    }
}