package com.casm.routes

import com.casm.data.requests.CreateCommentRequest
import com.casm.data.requests.DeleteCommentRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.service.ActivityService
import com.casm.service.CommentService
import com.casm.service.LikeService
import com.casm.util.ApiResponseMessages
import com.casm.util.QueryParams
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post

fun Route.createComment(
    commentService: CommentService,
    activityService: ActivityService
) {
    authenticate {
        post("/api/comment/create") {
            val request = call.receiveOrNull<CreateCommentRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val userId = call.userId
            when (commentService.createComment(request, userId)) {
                is CommentService.ValidationEvent.ErrorFieldEmpty -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.FIELDS_BLANK
                        )
                    )
                }

                is CommentService.ValidationEvent.ErrorCommentTooLong -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = ApiResponseMessages.COMMENT_TOO_LONG
                        )
                    )
                }

                is CommentService.ValidationEvent.Success -> {
                    activityService.addCommentActivity(
                        byUserId = userId,
                        postId = request.postId,
                    )
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true,
                        )
                    )
                }

                is CommentService.ValidationEvent.UserNotFound -> {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = false,
                            message = "User not found"
                        )
                    )
                }
            }
        }
    }
}

fun Route.getCommentsForPost(
    commentService: CommentService
) {
    get("/api/comment/get") {
        val postId = call.parameters[QueryParams.PARAM_POST_ID] ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@get
        }
        val comments = commentService.getCommentsForPost(postId, call.userId)
        call.respond(HttpStatusCode.OK, comments)
    }
}

fun Route.deleteComment(
    commentService: CommentService,
    likeService: LikeService
) {
    authenticate {
        delete("/api/comment/delete") {
            val request = call.receiveOrNull<DeleteCommentRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val comment = commentService.getCommentById(request.commentId)
            if (comment?.userId != call.userId) {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }

            val deleted = commentService.deleteComment(request.commentId)
            if (deleted) {

                likeService.deleteLikesForParent(request.commentId)

                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = true
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    BasicApiResponse<Unit>(
                        successful = false
                    )
                )
            }
        }
    }
}