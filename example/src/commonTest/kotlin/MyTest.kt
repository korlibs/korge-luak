import korlibs.io.async.suspendTest
import korlibs.io.lang.Charsets
import korlibs.io.lang.toString
import org.luaj.vm2.Globals
import org.luaj.vm2.LoadState
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.io.ByteArrayLuaBinOutput
import org.luaj.vm2.io.LuaWriterBinOutput
import org.luaj.vm2.lib.*
import kotlin.test.*

class MyTest {
    @Test
    fun test() {
        assertEquals(1, 1)
    }

    @Test
    fun testCoroutines() = suspendTest {
        var stdout = ByteArrayLuaBinOutput()
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
            
            for i=1,10 do
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
                ENDED!
            """.trimIndent().trim(),
            stdout.toByteArray().toString(Charsets.UTF8).trim()
        )
    }
}
