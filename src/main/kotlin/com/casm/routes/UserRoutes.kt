package com.casm.routes

import com.casm.data.requests.UpdateProfileRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.data.responses.UserResponseItem
import com.casm.service.UserService
import com.casm.util.ApiResponseMessages
import com.casm.util.Constants.BANNER_IMAGE_PATH
import com.casm.util.Constants.BASE_URL
import com.casm.util.Constants.PROFILE_PICTURE_PATH
import com.casm.util.QueryParams
import com.casm.util.save
import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.put
import org.koin.ktor.ext.inject
import java.io.File

fun Route.searchUser(userService: UserService) {
    authenticate {
        get("/api/user/search") {
            val query = call.parameters[QueryParams.PARAM_QUERY]
            if (query.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.OK,
                    listOf<UserResponseItem>()
                )
                return@get
            }
            val searchResults = userService.searchForUsers(query, call.userId)
            call.respond(
                HttpStatusCode.OK,
                searchResults
            )
        }
    }
}

fun Route.getUserProfile(userService: UserService) {
    authenticate {
        get("/api/user/profile") {
            val userId = call.parameters[QueryParams.PARAM_USER_ID]
            if (userId.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val profileResponse = userService.getUserProfile(userId, call.userId)
            if (profileResponse == null) {
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse<Unit>(
                        successful = false,
                        message = ApiResponseMessages.USER_NOT_FOUND
                    )
                )
                return@get
            }
            call.respond(
                HttpStatusCode.OK,
                BasicApiResponse(
                    successful = true,
                    data = profileResponse
                )
            )
        }
    }
}

fun Route.updateUserProfile(userService: UserService) {
    val gson: Gson by inject()
    authenticate {
        put("/api/user/update") {
            val multipart = call.receiveMultipart()
            var updateProfileRequest: UpdateProfileRequest? = null
            var profilePictureFileName: String? = null
            var bannerImageFileName: String? = null
            multipart.forEachPart { partData ->
                when (partData) {
                    is PartData.FormItem -> {
                        if (partData.name == "update_profile_data") {
                            updateProfileRequest = gson.fromJson(
                                partData.value,
                                UpdateProfileRequest::class.java
                            )
                        }
                    }

                    is PartData.FileItem -> {
                        if (partData.name == "profile_picture") {
                            profilePictureFileName = partData.save(PROFILE_PICTURE_PATH)
                        } else if (partData.name == "banner_image") {
                            bannerImageFileName = partData.save(BANNER_IMAGE_PATH)
                        }
                    }

                    is PartData.BinaryItem -> Unit
                }
            }

            val profilePictureUrl = "${BASE_URL}profile_pictures/$profilePictureFileName"
            val bannerImageUrl = "${BASE_URL}banner_images/$bannerImageFileName"

            updateProfileRequest?.let { request ->
                val updateAcknowledged = userService.updateUser(
                    userId = call.userId,
                    profileImageUrl = if (profilePictureFileName == null) {
                        null
                    } else {
                        profilePictureUrl
                    },
                    bannerUrl = if (bannerImageFileName == null) {
                        null
                    } else {
                        bannerImageUrl
                    },
                    updateProfileRequest = request
                )
                if (updateAcknowledged) {
                    call.respond(
                        HttpStatusCode.OK,
                        BasicApiResponse<Unit>(
                            successful = true
                        )
                    )
                } else {
                    File("${PROFILE_PICTURE_PATH}/$profilePictureFileName").delete()
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
        }
    }
}