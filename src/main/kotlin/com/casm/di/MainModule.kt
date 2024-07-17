package com.casm.di

import com.casm.data.models.Post
import com.casm.data.repository.activity.ActivityRepository
import com.casm.data.repository.activity.ActivityRepositoryImpl
import com.casm.data.repository.comment.CommentRepository
import com.casm.data.repository.comment.CommentRepositoryImpl
import com.casm.data.repository.follow.FollowRepository
import com.casm.data.repository.follow.FollowRepositoryImpl
import com.casm.data.repository.likes.LikeRepository
import com.casm.data.repository.likes.LikeRepositoryImpl
import com.casm.data.repository.post.PostRepository
import com.casm.data.repository.post.PostRepositoryImpl
import com.casm.data.repository.skill.SkillRepository
import com.casm.data.repository.skill.SkillRepositoryImpl
import com.casm.data.repository.user.UserRepository
import com.casm.data.repository.user.UserRepositoryImpl
import com.casm.service.ActivityService
import com.casm.service.CommentService
import com.casm.service.FollowService
import com.casm.service.LikeService
import com.casm.service.PostService
import com.casm.service.SkillService
import com.casm.service.UserService
import com.casm.util.Constants
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo



val mainModule = module {
    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(Constants.DATABASE_NAME)
    }
    single<UserRepository> {
        UserRepositoryImpl(get())
    }
    single<FollowRepository> {
        FollowRepositoryImpl(get())
    }
    single<PostRepository> {
       PostRepositoryImpl(get())
    }
    single<LikeRepository> {
        LikeRepositoryImpl(get())
    }
    single<CommentRepository> {
        CommentRepositoryImpl(get())
    }
    single<ActivityRepository> {
        ActivityRepositoryImpl(get())
    }
    single<SkillRepository> {
        SkillRepositoryImpl(get())
    }
    single { UserService(get(), get()) }
    single { FollowService(get()) }
    single { PostService(get()) }
    single { LikeService(get(), get(), get()) }
    single { CommentService(get()) }
    single { ActivityService(get(), get(), get()) }
    single { SkillService(get()) }

    single { Gson() }

}