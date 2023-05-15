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
        val code = globals.load("""
            co = coroutine.create(function ()
               for i=1,10 do
                 print("co", i)
                 coroutine.yield()
               end
               return "completed"
             end)
            
            print(co)
            for i=1,4 do
                print(coroutine.resume(co))
            end
            print("ENDED!")
        """.trimIndent())
        //println("code=$code")
        code.callSuspend()

        assertEquals(
            """
                thread: 4dc8caa7
                co	1
                true
                co	2
                true
                co	3
                true
                co	4
                true
                ENDED!
            """.trimIndent().trim(),
            stdout.toByteArray().toString(Charsets.UTF8).trim()
        )
    }
}
