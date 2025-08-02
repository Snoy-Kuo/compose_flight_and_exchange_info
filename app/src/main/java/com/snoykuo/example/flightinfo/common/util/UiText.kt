package com.snoykuo.example.flightinfo.common.util

import android.content.Context
import androidx.annotation.StringRes

/**
 * A wrapper class for text that can be displayed on the UI.
 * This keeps the Android Context dependency limited to the UI layer.
 */
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    data class StringResource(@param:StringRes val resId: Int) : UiText()
    class FormattedString(
        @param:StringRes val resId: Int,
        vararg val args: Any
    ) : UiText() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as FormattedString

            if (resId != other.resId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId)
            is FormattedString -> context.getString(resId, *args)
        }
    }
}
