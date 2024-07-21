package com.casm.routes

import com.casm.data.requests.CreatePostRequest
import com.casm.data.requests.DeletePostRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.service.CommentService
import com.casm.service.LikeService
import com.casm.service.PostService
import com.casm.util.Constants
import com.casm.util.QueryParams
import com.casm.util.save
import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.receiveMultipart
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import java.io.File

fun Route.createPost(
    postService: PostService
) {
    val gson by inject<Gson>()
    authenticate {
        post("/api/post/create") {
            val multipart = call.receiveMultipart()
            var createPostRequest: CreatePostRequest? = null
            var fileName: String? = null
            multipart.forEachPart { partData ->
               when(partData) {
                  is PartData.FormItem -> {
                     if(partData.name == "post_data") {
                       createPostRequest = gson.fromJson(
                           partData.value,
                           CreatePostRequest::class.java
                       )
                     }
                  }
                  is PartData.FileItem -> {
                      fileName = partData.save(Constants.POST_PICTURE_PATH)
                  }
                  is PartData.BinaryItem -> Unit
               }
            }

            val postPictureUrl = "${Constants.BASE_URL}post_pictures/$fileName"

            createPostRequest?.let { request ->
               val createPostAcknowledged = postService.createPost(
                  request = request,
                  userId = call.userId,
                  imageUrl = postPictureUrl
               )
               if (createPostAcknowledged) {
                 call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(successful = true)
                  )
               } else {
                  File("${Constants.POST_PICTURE_PATH}/$fileName").delete()
                  call.respond(HttpStatusCode.InternalServerError)
               }
            } ?: run {
               call.respond(HttpStatusCode.BadRequest)
               return@post
            }
        }
    }

}

fun Route.getPostsForFollows(
   postService: PostService,
) {
    authenticate {
        get("/api/post/get") {

             val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
             val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull() ?:
                     Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForFollows(call.userId, page ,pageSize)
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}




fun Route.deletePost(
    postService: PostService,
    likeService: LikeService,
    commentService: CommentService
) {
    authenticate {
        delete("/api/post/delete") {
            val request = call.receiveOrNull<DeletePostRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            val post = postService.getPost(request.postId)

            if(post == null){
                call.respond(HttpStatusCode.NotFound)
                return@delete
            }

            if(post.userId == call.userId) {
                postService.deletePost(request.postId)
                likeService.deleteLikesForParent(request.postId)
                commentService.deleteCommentsForPost(request.postId)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.Unauthorized)
            }

        }
    }

}

fun Route.getPostsForProfile(
    postService: PostService,
) {
    authenticate {
        get("/api/user/posts") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize = call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull()
                ?: Constants.DEFAULT_POST_PAGE_SIZE

            val posts = postService.getPostsForProfile(
                userId = userId ?: call.userId,
                page = page,
                pageSize = pageSize
            )
            call.respond(
                HttpStatusCode.OK,
                posts
            )
        }
    }
}

fun Route.getPostDetails(
    postService: PostService
) {
    authenticate{
        get("api/post/details") {
            val postId = call.parameters["postId"] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val post = postService.getPost(postId) ?: run {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true,
                    data = post
                )
            )
        }
    }
}