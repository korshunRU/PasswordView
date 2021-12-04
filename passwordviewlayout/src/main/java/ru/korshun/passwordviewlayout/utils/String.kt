package ru.korshun.passwordviewlayout.utils

fun String.intOrString(): Int {
    return when(val v = toIntOrNull()) {
        null -> -1
        else -> v
    }
}