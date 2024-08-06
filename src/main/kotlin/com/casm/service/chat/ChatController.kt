package com.casm.service.chat

import com.casm.data.repository.chat.ChatRepository
import com.casm.data.websocket.WsServerMessage
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {
    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(chatSession: ChatSession, socket: WebSocketSession) {
        onlineUsers[chatSession.userId] = socket
    }

    fun onDisconnect(userId: String) {
        if (onlineUsers.containsKey(userId)) {
            onlineUsers.remove(userId)
        }
    }

    suspend fun sendMessage(frameText: String, message: WsServerMessage) {
        onlineUsers[message.fromId]?.send(Frame.Text(frameText))
        onlineUsers[message.toId]?.send(Frame.Text(frameText))
        val messageEntity = message.toMessage()
        repository.insertMessage(messageEntity)

        if (!repository.doesChatByUsersExist(message.fromId, message.toId)) {
            repository.insertChat(message.fromId, message.toId, messageEntity.id)
        } else {
            message.chatId?.let {
                repository.updateLastMessageIdForChat(message.chatId, messageEntity.id)
            }
        }

    }
}