package com.mtprorun.ui.utils

fun String.toFlagEmoji(): String {
    if (this == "??" || length != 2) return "\uD83C\uDF10"
    return this.uppercase()
        .map { char -> 0x1F1E6 + (char.code - 'A'.code) }
        .map { codePoint -> Character.toChars(codePoint) }
        .joinToString("") { String(it) }
}
