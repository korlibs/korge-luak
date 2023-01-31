import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import org.luaj.vm2.*
import org.luaj.vm2.compiler.*
import org.luaj.vm2.lib.*

suspend fun main() = Korge {
    sceneContainer().changeTo({ MainLuaScene() })
}

class MainLuaScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        val globals = createLuaGlobals()

        val textStack = uiVerticalStack(padding = 8.0, adjustSize = false).xy(10, 10)

        fun luaprintln(str: String) {
            println("LUA_PRINTLN: $str")
            textStack.text(str)
            //kotlin.io.println()
        }

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
            return res
        """).call()

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
