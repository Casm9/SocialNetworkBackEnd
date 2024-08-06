package com.casm.data.websocket

import com.casm.data.models.Message

data class WsClientMessage(
    val toId: String,
    val text: String,
    val chatId: String?
) {
    fun toMessage(fromId: String): Message {
        return Message(
            fromId = fromId,
            toId = toId,
            text = text,
            timeStamp = System.currentTimeMillis(),
            chatId = chatId
        )
    }
}
