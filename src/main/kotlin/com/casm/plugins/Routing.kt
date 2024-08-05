package com.casm.plugins

import com.casm.routes.authenticate
import com.casm.routes.chatWebSocket
import com.casm.routes.createComment
import com.casm.routes.createPost
import com.casm.routes.createUser
import com.casm.routes.deleteComment
import com.casm.routes.deletePost
import com.casm.routes.followUser
import com.casm.routes.getActivities
import com.casm.routes.getChatsForUser
import com.casm.routes.getCommentsForPost
import com.casm.routes.getLikesForParent
import com.casm.routes.getMessagesForChat
import com.casm.routes.getPostDetails
import com.casm.routes.getPostsForFollows
import com.casm.routes.getPostsForProfile
import com.casm.routes.getSkills
import com.casm.routes.getUserProfile
import com.casm.routes.likeParent
import com.casm.routes.loginUser
import com.casm.routes.searchUser
import com.casm.routes.unfollowUser
import com.casm.routes.unlikeParent
import com.casm.routes.updateUserProfile
import com.casm.service.ActivityService
import com.casm.service.CommentService
import com.casm.service.FollowService
import com.casm.service.LikeService
import com.casm.service.PostService
import com.casm.service.SkillService
import com.casm.service.UserService
import com.casm.service.chat.ChatController
import com.casm.service.chat.ChatService
import io.ktor.application.Application
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val userService: UserService by inject()
    val followService: FollowService by inject()
    val postService: PostService by inject()
    val likeService: LikeService by inject()
    val commentService: CommentService by inject()
    val activityService: ActivityService by inject()
    val skillService: SkillService by inject()
    val chatService: ChatService by inject()
    val chatController: ChatController by inject()

    val jwtIssuer = environment.config.property("jwt.domain").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtSecret = environment.config.property("jwt.secret").getString()

    routing {
        // User routes
        authenticate()
        createUser(userService)
        loginUser(
            userService = userService,
            jwtIssuer = jwtIssuer,
            jwtAudience = jwtAudience,
            jwtSecret = jwtSecret

        )
        searchUser(userService)
        getUserProfile(userService)
        getPostsForProfile(postService)
        updateUserProfile(userService)

        // Following routes
        followUser(followService, activityService)
        unfollowUser(followService)

        //Post routes
        createPost(postService)
        getPostsForFollows(postService)
        deletePost(postService, likeService, commentService)
        getPostDetails(postService)

        //Like routes
        likeParent(likeService, activityService)
        unlikeParent(likeService)
        getLikesForParent(likeService)

        //comment routes
        createComment(commentService, activityService)
        deleteComment(commentService, likeService)
        getCommentsForPost(commentService)

        //activity routes
        getActivities(activityService)

        //skill routes
        getSkills(skillService)

        //chat routes
        getChatsForUser(chatService)
        getMessagesForChat(chatService)
        chatWebSocket(chatController)

        static {
            resources("static")
        }
    }
}
