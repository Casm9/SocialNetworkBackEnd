package com.casm.routes

import com.casm.service.ActivityService
import com.casm.util.Constants
import com.casm.util.QueryParams
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.getActivities(
    activityService: ActivityService,
) {
    authenticate {
        get("/api/activity/get") {

            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull()
                ?: Constants.DEFAULT_POST_PAGE_SIZE

            val activities = activityService.getActivitiesForUser(call.userId, page, pageSize)
            call.respond(
                HttpStatusCode.OK,
                activities
            )
        }
    }
}