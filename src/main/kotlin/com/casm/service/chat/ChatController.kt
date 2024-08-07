package com.casm.service.chat

import com.casm.data.repository.chat.ChatRepository
import com.casm.data.websocket.WsClientMessage
import com.casm.data.websocket.WsServerMessage
import com.casm.util.WebSocketObject
import com.google.gson.Gson
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val repository: ChatRepository
) {
    private val onlineUsers = ConcurrentHashMap<String, WebSocketSession>()

    fun onJoin(userId: String, socket: WebSocketSession) {
        onlineUsers[userId] = socket
    }

    fun onDisconnect(userId: String) {
        if (onlineUsers.containsKey(userId)) {
            onlineUsers.remove(userId)
        }
    }

    suspend fun sendMessage(ownUserId: String, gson: Gson, message: WsClientMessage) {
        val messageEntity = message.toMessage(ownUserId)
        val wsServerMessage = WsServerMessage(
            fromId = ownUserId,
            toId = message.toId,
            text = message.text,
            timestamp = System.currentTimeMillis(),
            chatId = message.chatId
        )
        val frameText = gson.toJson(wsServerMessage)

        onlineUsers[ownUserId]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))
        onlineUsers[message.toId]?.send(Frame.Text("${WebSocketObject.MESSAGE.ordinal}#$frameText"))

        repository.insertMessage(messageEntity)

        if (!repository.doesChatByUsersExist(ownUserId, message.toId)) {
            val chatId = repository.insertChat(ownUserId, message.toId, messageEntity.id)
            repository.insertMessage(messageEntity.copy(chatId = chatId))
        } else {
            repository.insertMessage(messageEntity)
            message.chatId?.let {
                repository.updateLastMessageIdForChat(message.chatId, messageEntity.id)
            }
        }

    }
}