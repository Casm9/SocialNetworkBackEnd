package com.casm.routes

import com.casm.data.websocket.WsClientMessage
import com.casm.service.chat.ChatController
import com.casm.service.chat.ChatService
import com.casm.util.Constants
import com.casm.util.QueryParams
import com.casm.util.WebSocketObject
import com.casm.util.fromJsonOrNull
import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
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
    authenticate {
        webSocket("/api/chat/websocket") {
            chatController.onJoin(call.userId, this)
            try {
                incoming.consumeEach { frame ->
                    run {
                        when (frame) {
                            is Frame.Text -> {
                                val frameText = frame.readText()
                                val delimiterIndex = frameText.indexOf("#")
                                if (delimiterIndex == -1) {
                                    return@consumeEach
                                }
                                val type = frameText.substring(0, delimiterIndex).toIntOrNull()
                                    ?: return@consumeEach
                                val json = frameText.substring(delimiterIndex + 1, frameText.length)
                                handleWebSocket(call.userId, chatController, type, json)
                            }

                            else -> {}
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                chatController.onDisconnect(call.userId)
            }
        }
    }
}

suspend fun handleWebSocket(
    ownUserId: String,
    chatController: ChatController,
    type: Int,
    json: String
) {
    val gson by inject<Gson>(Gson::class.java)
    when (type) {
        WebSocketObject.MESSAGE.ordinal -> {
            val message = gson.fromJsonOrNull(json, WsClientMessage::class.java) ?: return
            chatController.sendMessage(ownUserId, gson, message)
        }
    }
}