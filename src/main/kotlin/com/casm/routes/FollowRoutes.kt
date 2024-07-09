package com.casm.routes


import com.casm.data.models.Activity
import com.casm.data.requests.FollowUpdateRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.data.repository.follow.FollowRepository
import com.casm.data.util.ActivityType
import com.casm.service.ActivityService
import com.casm.service.FollowService
import com.casm.util.ApiResponseMessages.USER_NOT_FOUND
import io.ktor.application.*
import io.ktor.auth.authenticate
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

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

            if(didUserExist) {
                activityService.createActivity(
                    Activity(
                        timestamp = System.currentTimeMillis(),
                        byUserId = call.userId,
                        toUserId =  request.followedUserId,
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
    delete("/api/following/unfollow") {
        val request = call.receiveOrNull<FollowUpdateRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

       val didUserExist = followService.unfollowUserIfExists(request, call.userId)

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