package org.luaj.vm2

import org.luaj.test.suspendTest
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.io.ByteArrayLuaBinOutput
import org.luaj.vm2.io.LuaWriterBinOutput
import org.luaj.vm2.lib.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CoroutineTest {
    @Test
    fun test() {
        val globals = Globals().apply {
            load(BaseLib())
            load(PackageLib())
            load(Bit32Lib())
            load(TableLib())
            load(StringLib())
            load(CoroutineLib())
            LoadState.install(this)
            LuaC.install(this)
        }
        globals.load("""
            co = coroutine.create(function ()
                print("hi")
            end)
            
            print(co)
        """.trimIndent()).call()
    }


    @Test
    fun testCoroutines() = suspendTest {
        val stdout = ByteArrayLuaBinOutput()
        val globals = Globals().apply {
            STDOUT = LuaWriterBinOutput(stdout)
            load(BaseLib())
            load(PackageLib())
            load(Bit32Lib())
            load(TableLib())
            load(StringLib())
            load(CoroutineLib())
            LoadState.install(this)
            LuaC.install(this)
            this.thread_orphan_check_interval = 200L
        }
        val code = globals.load(
            // language=lua
            """
            co = coroutine.create(function ()
               for i=1,4 do
                 print("co", i)
                 coroutine.yield(i, i + 1)
               end
               return "completed"
             end)
            
            for i=1,6 do
                local code, res = coroutine.resume(co)
                print(code, res)
            end
            print("ENDED!")
        """.trimIndent())
        //println("code=$code")
        code.callSuspend()

        assertEquals(
            """
                co	1
                true	1
                co	2
                true	2
                co	3
                true	3
                co	4
                true	4
                true	completed
                false	cannot resume dead(4) coroutine
                ENDED!
            """.trimIndent().trim(),
            stdout.toByteArray().decodeToString().trim()
        )
    }
}