package com.casm.routes

import com.casm.data.models.Activity
import com.casm.data.requests.FollowUpdateRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.data.util.ActivityType
import com.casm.service.ActivityService
import com.casm.service.FollowService
import com.casm.util.ApiResponseMessages.USER_NOT_FOUND
import com.casm.util.QueryParams
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.post

fun Route.followUser(
    followService: FollowService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/following/follow") {
            val request = call.receiveOrNull<FollowUpdateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val didUserExist = followService.followUserIfExists(request, call.userId)

            if (didUserExist) {
                activityService.createActivity(
                    Activity(
                        timestamp = System.currentTimeMillis(),
                        byUserId = call.userId,
                        toUserId = request.followedUserId,
                        type = ActivityType.FollowedUser.type,
                        parentId = ""
                    )
                )
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}

fun Route.unfollowUser(followService: FollowService) {
    authenticate {
        delete("/api/following/unfollow") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val didUserExist = followService.unfollowUserIfExists(userId, call.userId)

            if (didUserExist) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = USER_NOT_FOUND
                    )
                )
            }
        }
    }
}