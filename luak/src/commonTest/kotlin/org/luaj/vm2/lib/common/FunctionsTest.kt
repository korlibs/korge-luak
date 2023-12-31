package org.luaj.vm2.lib.common

import org.luaj.test.suspendTest
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaString.Companion.valueOf
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.execute
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionsTest {
    @Test
    fun ifZeroArgIsWorking() = suspendTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["zeroArg"].callSuspend()
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifMultipleCallsAreWorking() = suspendTest {
        val globals = SuspendedFunctionsTest.createGlobals()
        val result1 = globals["testLibrary"]["zeroArg"].callSuspend()
        val result2 = globals["testLibrary"]["zeroArg"].callSuspend()
        assertEquals( "Hello World", result1.tojstring())
        assertEquals( "Hello World", result2.tojstring())
    }

    @Test
    fun ifZeroArgIsWorkingFromScript() = suspendTest {
        val globals = createGlobals()
        val result = globals.execute("""
            local testLibrary = require("testLibrary")
            return testLibrary.zeroArg()
        """.trimIndent())
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifOneArgIsWorking() = suspendTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["oneArg"].callSuspend(valueOf("World"))
        assertEquals( "Hello World", result.tojstring())
    }
    @Test
    fun ifOneArgIsWorkingFromScript() = suspendTest {
        val globals = createGlobals()
        val result = globals.execute("""
            local testLibrary = require("testLibrary")
            return testLibrary.oneArg("World")
        """.trimIndent())
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifTwoArgIsWorking() = suspendTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["twoArg"].callSuspend(valueOf("World"), valueOf("Universe"))
        assertEquals( "Hello World and Universe", result.tojstring())
    }
    @Test
    fun ifTwoArgIsWorkingFromScript() = suspendTest {
        val globals = createGlobals()
        val result = globals.execute("""
            local testLibrary = require("testLibrary")
            return testLibrary.twoArg("World","Universe")
        """.trimIndent())
        assertEquals( "Hello World and Universe", result.tojstring())
    }
    @Test
    fun ifThreeArgIsWorking() = suspendTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["threeArg"].callSuspend(valueOf("World"), valueOf("Universe"), valueOf("Galaxy"))
        assertEquals( "Hello World, Universe and Galaxy", result.tojstring())
    }
    @Test
    fun ifThreeArgIsWorkingFromScript() = suspendTest {
        val globals = createGlobals()
        val result = globals.execute("""
            local testLibrary = require("testLibrary")
            return testLibrary.threeArg("World","Universe","Galaxy")
        """.trimIndent())
        assertEquals( "Hello World, Universe and Galaxy", result.tojstring())
    }
    @Test
    fun ifVarArgIsWorking() = suspendTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["varArg"].callSuspend(valueOf("World"), valueOf("Universe"), valueOf("Galaxy"))
        assertEquals( "Hello World, Universe, Galaxy", result.tojstring())
    }

    @Test
    fun ifVarArgIsWorkingFromScript() = suspendTest {
        val globals = createGlobals()
        val result = globals.execute("""
            local testLibrary = require("testLibrary")
            return testLibrary.varArg("World","Universe","Galaxy")
        """.trimIndent())
        assertEquals("Hello World, Universe, Galaxy", result.tojstring())
    }

    @Test
    fun ifErrorIsWorking() = suspendTest {
        val globals = createGlobals()
        try {
            globals["testLibrary"]["error"].callSuspend()
        } catch (e: Exception) {
            assertEquals("Hello World", e.message)
        }
    }

    @Test
    fun ifErrorIsWorkingFromScript() = suspendTest {
        val globals = createGlobals()
        try {
            globals.execute("""
                local testLibrary = require("testLibrary")
                return testLibrary.error()
            """.trimIndent())
        } catch (e: Exception) {
            assertEquals("Hello World", e.message)
        }
    }

    companion object {
        fun createGlobals(): Globals {
            val globals = CommonPlatform.standardGlobals()
            globals.load(testLibrary)
            return globals
        }

        private val testLibrary = LibBuilder.create("testLibrary", defaultRequire = true) {
            addZeroArg("zeroArg") {
                LuaValue.valueOf("Hello World")
            }
            addOneArg("oneArg") { arg, _ ->
                LuaValue.valueOf("Hello $arg")
            }
            addTwoArg("twoArg") { arg1, arg2, _ ->
                LuaValue.valueOf("Hello $arg1 and $arg2")
            }
            addThreeArg("threeArg") { arg1, arg2, arg3, _ ->
                LuaValue.valueOf("Hello $arg1, $arg2 and $arg3")
            }
            addVarArg("varArg") { args, _ ->
                val values = (1 ..args.narg()).map { args.arg(it).tojstring() }
                LuaValue.valueOf("Hello ${values.joinToString(", ")}")
            }
            addZeroArg("error") {
                throw RuntimeException("Hello World")
            }
        }
    }
}
