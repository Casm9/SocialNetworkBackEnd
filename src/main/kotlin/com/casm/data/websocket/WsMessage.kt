package com.casm.data.websocket

import com.casm.data.models.Message

data class WsMessage(
    val fromId: String,
    val toId: String,
    val text: String,
    val timeStamp: Long,
    val chatId: String?
) {
    fun toMessage(): Message {
        return Message(
            fromId = fromId,
            toId = toId,
            text = text,
            timeStamp = timeStamp,
            chatId = chatId
        )
    }
}
