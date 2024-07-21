package com.casm.routes

import com.casm.data.requests.LikeUpdateRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.data.util.ParentType
import com.casm.service.ActivityService
import com.casm.service.LikeService
import com.casm.util.ApiResponseMessages
import com.casm.util.QueryParams
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.likeParent(
    likeService: LikeService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/like") {
            val request = call.receiveOrNull<LikeUpdateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userId = call.userId
            val likeSuccessful = likeService.likeParent(userId, request.parentId, request.parentType)

            if(likeSuccessful) {
                activityService.addLikeActivity(
                   byUserId =  userId,
                   parentType = ParentType.fromType(request.parentType),
                   parentId = request.parentId
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
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }

        }
    }
}

fun Route.unlikeParent(
    likeService: LikeService
) {
    authenticate {
        delete("/api/unlike") {
            val request = call.receiveOrNull<LikeUpdateRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val unlikeSuccessful = likeService.unlikeParent(call.userId, request.parentId, request.parentType)
            if(unlikeSuccessful) {
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
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
            }
        }
    }
}



fun Route.getLikesForParent(likeService: LikeService) {
    authenticate {
        get("/api/like/parent") {
            val parentId = call.parameters[QueryParams.PARAM_PARENT_ID] ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val usersWhoLikedParent = likeService.getUsersWhoLikedParent(
                parentId = parentId,
                call.userId
            )
            call.respond(
                HttpStatusCode.OK,
                usersWhoLikedParent
            )
        }
    }

}