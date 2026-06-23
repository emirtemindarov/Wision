package app.emirtemindarov.p1.utils

import android.util.Log

/**
 * Работает - не трожь!
 */
object LogUtils {

    fun logLong(tag: String, message: String) {
        val maxLogSize = 2000

        for (i in 0..message.length / maxLogSize) {
            val start = i * maxLogSize
            val end = (start + maxLogSize).coerceAtMost(message.length)

            if (start < end) {
                Log.i(tag, message.substring(start, end))
            }
        }
    }

    fun logShort(
        tag: String,
        message: String,
        maxChars: Int = 100
    ) {
        require(maxChars > 0) { "maxChars must be > 0" }  // TODO полезно

        val text =
            if (message.length > maxChars)
                message.take(maxChars) + "…"
            else
                message

        Log.i(tag, text)
    }


}