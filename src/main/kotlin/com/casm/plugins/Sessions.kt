package com.casm.plugins

import com.casm.service.chat.ChatSession
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import io.ktor.util.generateNonce

fun Application.configureSessions() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<ChatSession>() == null) {
            val userId = call.parameters["userId"] ?: return@intercept
            call.sessions.set(ChatSession(userId, generateNonce()))
        }
    }
}