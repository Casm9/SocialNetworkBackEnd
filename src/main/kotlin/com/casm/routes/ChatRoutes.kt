package com.casm.routes

import com.casm.data.websocket.WsServerMessage
import com.casm.service.chat.ChatController
import com.casm.service.chat.ChatService
import com.casm.service.chat.ChatSession
import com.casm.util.Constants
import com.casm.util.QueryParams
import com.casm.util.WebSocketObject
import com.casm.util.fromJsonOrNull
import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import org.koin.java.KoinJavaComponent.inject

fun Route.getMessagesForChat(chatService: ChatService) {
    authenticate {
        get("/api/chat/messages") {
            val chatId = call.parameters[QueryParams.PARAM_CHAT_ID] ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val page = call.parameters[QueryParams.PARAM_PAGE]?.toIntOrNull() ?: 0
            val pageSize =
                call.parameters[QueryParams.PARAM_PAGE_SIZE]?.toIntOrNull()
                    ?: Constants.DEFAULT_PAGE_SIZE

            if (!chatService.doesChatBelongToUser(chatId, call.userId)) {
                call.respond(HttpStatusCode.Forbidden)
                return@get
            }

            val messages = chatService.getMessagesForChat(chatId, page, pageSize)
            call.respond(HttpStatusCode.OK, messages)
        }
    }
}

fun Route.getChatsForUser(chatService: ChatService) {
    authenticate {
        get("/api/chats") {
            val chats = chatService.getChatsForUser(call.userId)
            call.respond(HttpStatusCode.OK, chats)
        }
    }
}

fun Route.chatWebSocket(chatController: ChatController) {
    webSocket("/api/chat/websocket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        chatController.onJoin(session, this)
        try {
            incoming.consumeEach { frame ->
                when (frame) {
                    is Frame.Text -> {
                        val frameText = frame.readText()
                        val delimiterIndex = frameText.indexOf("#")
                        if (delimiterIndex == -1) {
                            return@consumeEach
                        }
                        val type = frameText.substring(0, delimiterIndex).toIntOrNull() ?: return@consumeEach
                        val json = frameText.substring(delimiterIndex + 1, frameText.length)
                        handleWebSocket(this, session, chatController, type, frameText, json)

                    }

                    else -> {}
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            chatController.onDisconnect(session.userId)
        }
    }
}

suspend fun handleWebSocket(
    webSocketSession: WebSocketSession,
    session: ChatSession,
    chatController: ChatController,
    type: Int,
    frameText: String,
    json: String
) {
    val gson by inject<Gson>(Gson::class.java)
    when (type) {
        WebSocketObject.MESSAGE.ordinal -> {
            val message = gson.fromJsonOrNull(json, WsServerMessage::class.java) ?: return
            chatController.sendMessage(frameText, message)
        }
    }
}