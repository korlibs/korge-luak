package org.luaj.vm2

import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.*
import kotlin.test.Test

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
}