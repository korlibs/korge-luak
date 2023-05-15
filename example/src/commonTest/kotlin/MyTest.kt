import korlibs.io.async.suspendTest
import org.luaj.vm2.Globals
import org.luaj.vm2.LoadState
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.*
import kotlin.test.*

class MyTest {
    @Test
    fun test() {
        assertEquals(1, 1)
    }

    @Test
    fun test2() = suspendTest {
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
        val code = globals.load("""
            co = coroutine.create(function ()
               for i=1,10 do
                 print("co", i)
                 coroutine.yield()
               end
             end)
            
            print(co)
            print(coroutine.resume(co))
            print(coroutine.resume(co))
        """.trimIndent())
        println("code=$code")
        code.callSuspend()
    }
}
