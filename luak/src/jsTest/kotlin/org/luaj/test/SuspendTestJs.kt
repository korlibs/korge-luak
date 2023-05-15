package org.luaj.test

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun suspendTest(callback: suspend () -> Unit): dynamic = GlobalScope.promise { callback() }
