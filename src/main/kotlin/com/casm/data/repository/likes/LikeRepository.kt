package com.casm.data.repository.likes

import com.casm.data.models.Like
import com.casm.util.Constants


interface LikeRepository {

    suspend fun likeParent(userId: String, parentId: String, parentType: Int): Boolean

    suspend fun unlikeParent(userId: String, parentId: String): Boolean

    suspend fun deleteLikesForParent(parentId: String)

    suspend fun getLikesForParent(parentId: String, page: Int = 0, pageSize: Int = Constants.DEFAULT_ACTIVITY_PAGE_SIZE): List<Like>

}