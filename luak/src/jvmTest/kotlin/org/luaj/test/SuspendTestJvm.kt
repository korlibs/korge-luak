package org.luaj.test

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

actual fun suspendTest(callback: suspend () -> Unit) {
    runBlocking {
        withTimeout(2000L) {
            callback()
        }
    }
}
