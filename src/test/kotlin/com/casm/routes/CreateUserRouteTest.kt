package com.casm.routes

import com.casm.data.models.User
import com.casm.data.requests.CreateAccountRequest
import com.casm.data.responses.BasicApiResponse
import com.casm.di.testModule
import com.casm.plugins.configureSerialization
import com.casm.repository.user.FakeUserRepository
import com.casm.util.ApiResponseMessages
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import io.ktor.application.install
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.routing.Routing
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


internal class CreateUserRouteTest : KoinTest {

    private val userRepository by inject<FakeUserRepository>()
    private val gson = Gson()

    @BeforeTest
    fun setUp() {
        startKoin {
            modules(testModule)
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `Create user, no body attached, responds with BadRequest`() {
        withTestApplication(
            moduleFunction = {
                install(Routing) {}
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            )

            assertThat(request.response.status()).isEqualTo(HttpStatusCode.BadRequest)
        }
    }

    @Test
    fun `Create user, user already exists, responds with unsuccessful`() = runBlocking {
        val user = User(
            email = "test@test.com",
            username = "test",
            password = "test",
            profileImageUrl = "",
            bannerUrl = "",
            bio = "",
            gitHubUrl = null,
            instagramUrl = null,
            linkedInUrl = null

        )
        userRepository.createUser(user)

        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {}
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ) {
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "asdf",
                    password = "asdf"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.USER_ALREADY_EXISTS)
        }
    }

    @Test
    fun `Create user, email is empty, responds with unsuccessful`() = runBlocking {

        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {}
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ) {
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "",
                    username = "",
                    password = ""
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isFalse()
            assertThat(response.message).isEqualTo(ApiResponseMessages.FIELDS_BLANK)
        }
    }

    @Test
    fun `Create user, valid data, responds with successful`() {

        withTestApplication(
            moduleFunction = {
                configureSerialization()
                install(Routing) {}
            }
        ) {
            val request = handleRequest(
                method = HttpMethod.Post,
                uri = "/api/user/create"
            ) {
                addHeader("Content-Type", "application/json")
                val request = CreateAccountRequest(
                    email = "test@test.com",
                    username = "test",
                    password = "test"
                )
                setBody(gson.toJson(request))
            }

            val response = gson.fromJson(
                request.response.content ?: "",
                BasicApiResponse::class.java
            )
            assertThat(response.successful).isTrue()

            runBlocking {
                val isUserInDb = userRepository.getUserByEmail("test@test.com") != null
                assertThat(isUserInDb).isTrue()
            }
        }
    }
}