package com.casm.data.repository.follow

import com.casm.data.models.Following

interface FollowRepository {

    suspend fun followUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun unfollowUserIfExists(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun doesUserFollow(
        followingUserId: String,
        followedUserId: String
    ): Boolean

    suspend fun getFollowsByUser(userId: String): List<Following>
}