import korlibs.korge.Korge
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
import korlibs.korge.ui.uiVerticalStack
import korlibs.korge.view.SContainer
import korlibs.korge.view.text
import korlibs.korge.view.xy
import org.luaj.vm2.Globals
import org.luaj.vm2.LoadState
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.compiler.LuaC
import org.luaj.vm2.lib.*

suspend fun main() = Korge {
    sceneContainer().changeTo({ MainLuaScene() })
}

class MainLuaScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val globals = createLuaGlobals()

        val textStack = uiVerticalStack(padding = 8f, adjustSize = false).xy(10, 10)

        fun luaprintln(str: String) {
            println("LUA_PRINTLN: $str")
            textStack.text(str)
            //kotlin.io.println()
        }

        //globals.STDOUT = LuaWriterBinOutput(object : LuaBinOutput {
        //})

        // Overwrite print function
        globals["print"] = object : VarArgFunction() {
            override fun invoke(args: Varargs): Varargs {
                val tostring = globals["tostring"]
                val out = (1 .. args.narg())
                    .map { tostring.call(args.arg(it)).strvalue()!!.tojstring() }
                luaprintln(out.joinToString("\t"))
                return LuaValue.NONE
            }
        }
        val result = globals.load(
            //language=lua
            """
            function max(a, b)
                if (a > b) then
                    return a
                else
                    return b
                end
            end
            a = 10
            res = 1 + 2 + a + max(20, 30)
            print(res - 1)
            b = {}
            b[1] = 10
            print(b)
            for i=4,1,-1 do print(i) end
            
            
            co = coroutine.create(function ()
               for i=1,5 do
                 print("co", i)
                 coroutine.yield(i, i + 1)
               end
               return "completed"
             end)
            
            for i=1,12 do
                local code, res = coroutine.resume(co)
                print(code, res)
            end
            print("ENDED!")

            return res
        """).callSuspend()

        luaprintln(result.toString())
    }

    fun createLuaGlobals(): Globals = Globals().apply {
        load(BaseLib())
        load(PackageLib())
        load(Bit32Lib())
        load(TableLib())
        load(StringLib())
        load(CoroutineLib())
        LoadState.install(this)
        LuaC.install(this)
    }
}
