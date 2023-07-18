package org.luaj.vm2

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.luaj.vm2.LuaString.Companion.valueOf
import org.luaj.vm2.lib.common.CommonPlatform
import org.luaj.vm2.lib.common.LibBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class WrappedSuspendedFunctionsTest {
    @Test
    fun ifZeroArgIsWorking() = runTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["zeroArg"].callSuspend()
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifZeroArgIsWorkingFromScript() = runTest {
        val globals = createGlobals()
        val result = globals.load("""
            local testLibrary = require("testLibrary")
            return testLibrary.zeroArg()
            """.trimIndent()).callSuspend()
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifOneArgIsWorking() = runTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["oneArg"].callSuspend(valueOf("World"))
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifOneArgIsWorkingFromScript() = runTest {
        val globals = createGlobals()
        val result = globals.load("""
            local testLibrary = require("testLibrary")
            return testLibrary.oneArg("World")
            """.trimIndent()).callSuspend()
        assertEquals( "Hello World", result.tojstring())
    }

    @Test
    fun ifTwoArgIsWorking() = runTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["twoArg"].callSuspend(valueOf("World"), valueOf("Universe"))
        assertEquals( "Hello World and Universe", result.tojstring())
    }

    @Test
    fun ifTwoArgIsWorkingFromScript() = runTest {
        val globals = createGlobals()
        val result = globals.load("""
            local testLibrary = require("testLibrary")
            return testLibrary.twoArg("World", "Universe")
            """.trimIndent()).callSuspend()
        assertEquals( "Hello World and Universe", result.tojstring())
    }

    @Test
    fun ifThreeArgIsWorking() = runTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["threeArg"].callSuspend(valueOf("World"), valueOf("Universe"), valueOf("Galaxy"))
        assertEquals( "Hello World, Universe and Galaxy", result.tojstring())
    }

    @Test
    fun ifThreeArgIsWorkingFromScript() = runTest {
        val globals = createGlobals()
        val result = globals.load("""
            local testLibrary = require("testLibrary")
            return testLibrary.threeArg("World", "Universe", "Galaxy")
            """.trimIndent()).callSuspend()
        assertEquals( "Hello World, Universe and Galaxy", result.tojstring())
    }

    @Test
    fun ifVarArgIsWorking() = runTest {
        val globals = createGlobals()
        val result = globals["testLibrary"]["varArg"].callSuspend(valueOf("World"), valueOf("Universe"), valueOf("Galaxy"))
        assertEquals( "Hello World, Universe and Galaxy", result.tojstring())
    }

    @Test
    fun ifVarArgIsWorkingFromScript() = runTest {
        val globals = createGlobals()
        val result = globals.load("""
            local testLibrary = require("testLibrary")
            return testLibrary.varArg("World", "Universe", "Galaxy")
            """.trimIndent()).callSuspend()
        assertEquals( "Hello World, Universe and Galaxy", result.tojstring())
    }

    companion object {
        fun createGlobals(): Globals{
            val globals = CommonPlatform.standardGlobals()
            globals.load(testLibrary)
            return globals
        }

        private val testLibrary = LibBuilder.create("testLibrary") {
            addSuspendedZeroArgWrapped("zeroArg") {
                delay(500)
                LuaValue.valueOf("Hello World")
            }
            addSuspendedOneArgWrapped("oneArg") { arg, _ ->
                delay(500)
                LuaValue.valueOf("Hello $arg")
            }
            addSuspendedTwoArgWrapped("twoArg") { arg1, arg2, _ ->
                delay(500)
                LuaValue.valueOf("Hello $arg1 and $arg2")
            }
            addSuspendedThreeArgWrapped("threeArg") { arg1, arg2, arg3, _ ->
                delay(500)
                LuaValue.valueOf("Hello $arg1, $arg2 and $arg3")
            }
            addSuspendedVarArgWrapped("varArg") { args, _ ->
                delay(500)
                val values = (1 ..args.narg()).map { args.arg(it).tojstring() }
                LuaValue.valueOf("Hello ${values.joinToString(", ")}")
            }
        }
    }
}
