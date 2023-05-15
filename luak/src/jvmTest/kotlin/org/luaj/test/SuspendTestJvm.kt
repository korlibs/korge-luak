package org.luaj.test

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

actual fun suspendTest(callback: suspend () -> Unit) {
    runBlocking {
        //withTimeout(2000L) {
        withTimeout(20_000L) {
            callback()
            //println("completed withTimeout! : ${coroutineContext.job}")
        }
        //println("completed suspendTest!")
    }

}
