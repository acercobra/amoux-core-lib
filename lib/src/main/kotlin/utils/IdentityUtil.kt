package utils

import java.time.Instant
import kotlin.math.pow

class IdentityUtil(nodeId: Int) {
    companion object {
        private const val EPOCH_BITS = 42
        private const val NODE_BITS = 10
        private const val SEQ_BITS = 12
        private const val START_EPOCH = 1420070400000L // Jan 1, 2015 Midnight UTC

        private val MAX_NODE_ID = 2.0.pow(NODE_BITS.toDouble()).minus(1).toInt()
        private val MAX_SEQ = 2.0.pow(SEQ_BITS.toDouble()).minus(1).toInt()
    }

    private var localNodeId = nodeId
    private var lastTimestamp = 0L
    private var currentSequence = 0

    init {
        if (nodeId < 0 || nodeId >= MAX_NODE_ID) {
            throw RuntimeException("Node Id needs to between 0 and $MAX_NODE_ID. Current value used: $nodeId")
        }
    }

    fun generateId(): String {
        var currentTime = timestamp()

        if (currentTime < lastTimestamp) {
            throw RuntimeException("Timestamp somehow was reverted!!!")
        }

        if (currentTime == lastTimestamp) {
            currentSequence = currentSequence.plus(1).takeIf { it < MAX_SEQ } ?: 0

            if (currentSequence == 0) {
                // Current Sequence all been used up so let wait a bit
                Thread.sleep(1)
                currentTime = timestamp()
            }
        } else {
            currentSequence = 0
        }

        lastTimestamp = currentTime

        var id: Long = currentTime shl NODE_BITS + SEQ_BITS
        id = id or (localNodeId shl SEQ_BITS).toLong()
        id = id or currentSequence.toLong()
        return id.toString()
    }

    private fun timestamp(): Long {
        return Instant.now().toEpochMilli() - START_EPOCH
    }
}