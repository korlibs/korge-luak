package org.luaj.test

import kotlinx.coroutines.runBlocking

actual fun suspendTest(callback: suspend () -> Unit) {
    runBlocking { callback() }
}
