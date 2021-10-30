package pubsub

import AmouxCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.codifysoftware.amoux.proto.message.MessageInfo
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashSet

class RedisPubSub(private val amouxCore: AmouxCore): JedisPubSub() {

    private val channelListeners = ConcurrentHashMap<String, HashSet<RedisChannelListener>>()
    private val channelRedis = ConcurrentHashMap<String, Jedis>()

    init {
        amouxCore.logger.debug { "Initiated RedisPubSub" }
    }

    private suspend fun listenOn(channelName: String) {
        if (channelRedis.containsKey(channelName)) {
            amouxCore.logger.error { "Trying to listen on a channel already register to: $channelName" }
            return
        }

        val jedis = amouxCore.getRedisPool()?.resource ?: return
        channelRedis[channelName] = jedis

        withContext(Dispatchers.IO) {
            try {
                jedis.subscribe(this@RedisPubSub, channelName)
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                jedis.close()
                channelRedis.remove(channelName)
                channelListeners.remove(channelName)
            }
        }
    }

    private suspend fun stopListeningOn(channelName: String) {
        val jedis = channelRedis[channelName] ?: return
        try {
            jedis.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        channelRedis.remove(channelName)
        channelListeners.remove(channelName)
    }

    suspend fun addChannelListener(channelName: String, listener: RedisChannelListener) {
        val listeners = channelListeners.getOrPut(channelName) {
            listenOn(channelName)
            HashSet()
        }

        listeners.add(listener)
    }

    suspend fun removeChannelListener(channelName: String, listener: RedisChannelListener) {
        val listeners = channelListeners[channelName] ?: return
        listeners.remove(listener)

        if (listeners.isEmpty()) {
            stopListeningOn(channelName)
        }
    }

    override fun onMessage(channel: String, message: String) {
        try {
            val data = Base64.getDecoder().decode(message)
            val messageInfo = MessageInfo.parseFrom(data)
            channelListeners[channel]?.forEach {
                runBlocking {
                    it.onNewMessage(channel, messageInfo)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}