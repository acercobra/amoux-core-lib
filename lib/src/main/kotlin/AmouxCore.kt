import cache.RedisCache
import mu.KotlinLogging
import org.apache.log4j.BasicConfigurator
import pubsub.RedisPubSub
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class AmouxCore {

    private var redisPool: JedisPool? = null

    val pubsub: RedisPubSub by lazy {
        RedisPubSub(this)
    }

    val cache: RedisCache by lazy {
        RedisCache(this)
    }

    val logger by lazy {
        KotlinLogging.logger("AmouxCoreLib")
    }

    init {
        BasicConfigurator.configure()
        logger.info { "Initiated Amoux Core Lib" }
    }

    private fun connectRedis(host: String, password: String): Boolean {

        try {
            redisPool = JedisPool(JedisPoolConfig(), host, 34752, 1000, password, true)

            val result = redisPool?.resource?.use { it.ping() }
            if (result?.equals("PONG", true) == true) {
                logger.info { "Redis connected successfully!!" }
                return true
            }

            logger.error("Failed to get pong response from redis!!!")
        } catch (ex: Exception) {
            logger.error("Failed to connect redis!!!", ex)
        }

        redisPool = null
        return false
    }

    fun connect(config: AmouxConfig): Boolean {
        return connectRedis(config.redisHost, config.redisPassword)
    }

    fun getVersion(): Long {
        return 21
    }

    fun getRedisPool(): JedisPool? {
        return redisPool
    }
}