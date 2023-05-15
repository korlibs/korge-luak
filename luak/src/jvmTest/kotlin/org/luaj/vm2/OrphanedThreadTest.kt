/*******************************************************************************
 * Copyright (c) 2012 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.luaj.vm2

import org.luaj.test.suspendTest
import org.luaj.vm2.lib.*
import org.luaj.vm2.lib.jse.*
import java.lang.ref.*
import kotlin.test.*


class OrphanedThreadTest {

    internal lateinit var globals: Globals
    internal var luathread: LuaThread? = null
    internal lateinit var luathr_ref: WeakReference<*>
    internal var function: LuaValue? = null
    internal lateinit var func_ref: WeakReference<*>

    @BeforeTest
    fun setUp() {
        LuaThread.thread_orphan_check_interval = 5
        globals = JsePlatform.standardGlobals().also {
            it.thread_orphan_check_interval = 100L
        }
    }

    @AfterTest
    fun tearDown() {
        LuaThread.thread_orphan_check_interval = 30000
    }

    @Test
    //@Ignore("Check")
    fun testCollectOrphanedNormalThread() = suspendTest {
        function = NormalFunction(globals)
        doTest(LuaValue.BTRUE, LuaValue.ZERO)
    }

    @Test
    //@Ignore("Check")
    fun testCollectOrphanedEarlyCompletionThread() = suspendTest {
        function = EarlyCompletionFunction(globals)
        doTest(LuaValue.BTRUE, LuaValue.ZERO)
    }

    @Test
    //@Ignore("Check")
    fun testCollectOrphanedAbnormalThread() = suspendTest {
        function = AbnormalFunction(globals)
        doTest(LuaValue.BFALSE, LuaValue.valueOf("abnormal condition"))
    }

    @Test
    //@Ignore("Check")
    fun testCollectOrphanedClosureThread() = suspendTest {
        val script = """
            print('in closure, arg is '..(...))
            arg = coroutine.yield(1)
            print('in closure.2, arg is ',arg)
            arg = coroutine.yield(0)
            print('leakage in closure.3, arg is '..arg)
            return 'done'
        """.trimIndent()
        function = globals.load(script, "script")
        doTest(LuaValue.BTRUE, LuaValue.ZERO)
    }

    @Test
    //@Ignore("Check")
    fun testCollectOrphanedPcallClosureThread() = suspendTest {
        function = globals.load("""
            f = function(x)
              print('in pcall-closure, arg is ',(x))
              arg = coroutine.yield(1)
              print('in pcall-closure.2, arg is ',arg)
              arg = coroutine.yield(0)
              print('leakage in pcall-closure.3, arg is ',arg)
              return 'done'
            end
            print( 'pcall-closre.result:', pcall( f, ... ) )
        """.trimIndent(), "script")
        doTest(LuaValue.BTRUE, LuaValue.ZERO)
    }

    @Test
    //@Ignore("Check")
    fun testCollectOrphanedLoadClosureThread() = suspendTest {
        val script = """
            t = { "print ", "'hello, ", "world'", }
            i = 0
            arg = ...
            f = function()
                i = i + 1
               print('in load-closure, arg is', arg, 'next is', t[i])
               arg = coroutine.yield(1)
                return t[i]
            end
            load(f)()
        """.trimIndent()
        function = globals.load(script, "script")
        doTest(LuaValue.BTRUE, LuaValue.ONE)
        //assertEquals(1, 2)
    }

    private suspend fun doTest(status2: LuaValue, value2: LuaValue) {
        luathread = LuaThread(globals, function)
        luathr_ref = WeakReference(luathread)
        func_ref = WeakReference(function)
        assertNotNull(luathr_ref.get())

        // resume two times
        var a = luathread!!.resume(LuaValue.valueOf("foo"))
        assertEquals(LuaValue.ONE, a.arg(2))
        assertEquals(LuaValue.BTRUE, a.arg1())
        a = luathread!!.resume(LuaValue.valueOf("bar"))
        //println("value2=$value2, arg2=${a.arg(2)}")
        //println("status2=$status2, arg1=${a.arg1()}")
        assertEquals(value2, a.arg(2))
        assertEquals(status2, a.arg1())

        // drop strong references
        luathread = null
        function = null

        // gc
        var i = 0
        while (i < 100 && (luathr_ref.get() != null || func_ref.get() != null)) {
            Runtime.getRuntime().gc()
            Thread.sleep(5)
            i++
        }

        // check reference
        //assertNull(luathr_ref.get())
        //assertNull(func_ref.get())
    }


    internal class NormalFunction(val globals: Globals) : OneArgFunctionSuspend() {
        override suspend fun callSuspend(arg: LuaValue): LuaValue {
            var arg = arg
            println("in normal.1, arg is $arg")
            arg = globals.yield(LuaValue.ONE).arg1()
            println("in normal.2, arg is $arg")
            arg = globals.yield(LuaValue.ZERO).arg1()
            println("in normal.3, arg is $arg")
            return LuaValue.NONE
        }
    }

    internal class EarlyCompletionFunction(val globals: Globals) : OneArgFunctionSuspend() {
        override suspend fun callSuspend(arg: LuaValue): LuaValue {
            var arg = arg
            println("in early.1, arg is $arg")
            arg = globals.yield(LuaValue.ONE).arg1()
            println("in early.2, arg is $arg")
            return LuaValue.ZERO
        }
    }

    internal class AbnormalFunction(val globals: Globals) : OneArgFunctionSuspend() {
        override suspend fun callSuspend(arg: LuaValue): LuaValue {
            var arg = arg
            println("in abnormal.1, arg is $arg")
            arg = globals.yield(LuaValue.ONE).arg1()
            println("in abnormal.2, arg is $arg")
            LuaValue.error("abnormal condition")
            return LuaValue.ZERO
        }
    }
}
