package com.casm.data.repository.chat

import com.casm.data.models.Chat
import com.casm.data.models.Message

interface ChatRepository {
    suspend fun getMessagesForChat(chatId: String, page: Int, pageSize: Int): List<Message>
    suspend fun getChatsForUser(ownUserId: String): List<Chat>
    suspend fun doesChatBelongToUser(chatId: String, userId: String): Boolean

}