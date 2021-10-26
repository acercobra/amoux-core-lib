package cache

import AmouxCore
import com.google.gson.GsonBuilder

class RedisCache(private val amouxCore: AmouxCore) {

    private val gson = GsonBuilder().serializeNulls().setLenient().create()

    fun saveModel(key: String, model: Any, expire: Long = 0): Boolean {
        val data = gson.toJson(model)
        return save(key, data, expire)
    }

    fun save(key: String, data: String, expire: Long = 0): Boolean {
        val result = amouxCore.getRedisPool()?.resource?.use {
            if (expire == 0L) {
                it.set(key, data)
            } else {
                it.setex(key, expire, data)
            }
        }

        if (result.isNullOrBlank()) {
            return false
        }

        return result.equals("ok", true)
    }

    fun <T:Any> getModel(key: String, modelType: Class<T>): T? {
        val data = get(key)?.takeIf { it.isNotBlank() } ?: return null
        return gson.fromJson(data, modelType)
    }

    fun get(key: String): String? {
        val result = amouxCore.getRedisPool()?.resource?.use {
            it.get(key)
        }

        return result
    }

    fun delete(key: String): Boolean {
        val result = amouxCore.getRedisPool()?.resource?.use {
            it.del(key)
        } ?: -1
        return result >= 0
    }
}