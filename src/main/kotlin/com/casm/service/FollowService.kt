package com.casm.service

import com.casm.data.requests.FollowUpdateRequest
import com.casm.data.repository.follow.FollowRepository

class FollowService(
    private val followRepository: FollowRepository
) {
    suspend fun followUserIfExists(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.followUserIfExists(
            followingUserId,
            request.followedUserId
        )
    }

    suspend fun unfollowUserIfExists(request: FollowUpdateRequest, followingUserId: String): Boolean {
        return followRepository.unfollowUserIfExists(
            followingUserId,
            request.followedUserId
        )
    }
}