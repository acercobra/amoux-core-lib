package pubsub

import org.codifysoftware.amoux.proto.message.MessageInfo

interface RedisChannelListener {
    suspend fun onNewMessage(channelName: String, message: MessageInfo)
}