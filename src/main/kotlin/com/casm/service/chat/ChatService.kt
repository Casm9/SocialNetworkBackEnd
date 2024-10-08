package com.casm.service.chat

import com.casm.data.models.Message
import com.casm.data.repository.chat.ChatRepository
import com.casm.data.responses.ChatDto

class ChatService(
    private val chatRepository: ChatRepository
) {
    suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean {
        return chatRepository.doesChatBelongToUser(chatId, userId)
    }

    suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message> {
        return chatRepository.getMessagesForChat(chatId, page, pageSize)
    }

    suspend fun getChatsForUser(ownUserId: String): List<ChatDto> {
        return chatRepository.getChatsForUser(ownUserId)
    }
}