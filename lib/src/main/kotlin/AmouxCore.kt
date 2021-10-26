import redis.clients.jedis.JedisPool

class AmouxCore {

    private var redisPool: JedisPool? = null

    fun connect() {

    }

    fun getVersion(): Long {
        return 21
    }

    fun getRedisPool(): JedisPool? {
        return redisPool
    }
}