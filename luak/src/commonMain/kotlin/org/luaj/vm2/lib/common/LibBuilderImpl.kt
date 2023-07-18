package org.luaj.vm2.lib.common

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaString
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.LibFunction
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.ZeroArgFunction

internal class LibBuilderImpl(
    private val moduleName: String,
    private val defaultRequire: Boolean,
): LibBuilder {
    private val functionBuilders = mutableListOf<(LuaTable,Globals)->Unit>()

    override fun add(name: String, functionBuilder: (Globals) -> LibFunction) {
        functionBuilders.add { table, globals ->
            table[name] = functionBuilder(globals)
        }
    }

    override fun addValue(name: String, luaValue: LuaValue) {
        functionBuilders.add { table, _ ->
            table[name] = luaValue
        }
    }

    fun build(): TwoArgFunction{
        return object: TwoArgFunction(){
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun call(modname: LuaValue, env: LuaValue): LuaTable {
                val globals = env as Globals
                val table = LuaTable()
                functionBuilders.forEach { functionBuilder ->
                    functionBuilder(table, globals)
                }
                if (defaultRequire) {
                    globals[moduleName] = table
                }
                globals["package"]["loaded"][moduleName] = table
                if (LuaString.s_metatable == null) {
                    val mt = tableOf(
                        arrayOf(INDEX, table)
                    )
                    LuaString.s_metatable = mt
                }
                return table
            }

        }
    }
}
