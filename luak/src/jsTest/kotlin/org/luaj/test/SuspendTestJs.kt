package org.luaj.test

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.coroutines.withTimeout

actual fun suspendTest(callback: suspend () -> Unit): dynamic = GlobalScope.promise {
    withTimeout(2000L) {
        callback()
    }
}
