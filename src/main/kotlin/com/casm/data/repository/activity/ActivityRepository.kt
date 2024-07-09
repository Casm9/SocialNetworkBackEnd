package com.casm.data.repository.activity

import com.casm.data.models.Activity
import com.casm.util.Constants.DEFAULT_ACTIVITY_PAGE_SIZE

interface ActivityRepository {

    suspend fun getActivitiesForUser(
        userId: String,
        page: Int = 0,
        pageSize: Int = DEFAULT_ACTIVITY_PAGE_SIZE
    ): List<Activity>

    suspend fun createActivity(activity: Activity)

    suspend fun deleteActivity(activityId: String): Boolean
}