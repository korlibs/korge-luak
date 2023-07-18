package org.luaj.vm2.lib

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue

fun Globals.loadLuaModule(script: String, moduleName: String) {
    val module = load(script).call()

    val registration = object : TwoArgFunction() {
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun call(modname: LuaValue, env: LuaValue): LuaValue {
            env[moduleName] = module
            env["package"]["loaded"][moduleName] = module
            return module
        }
    }
    this.load(registration)
}

fun Globals.unloadLuaModule(moduleName: String) {
    val registration = object : TwoArgFunction() {
        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun call(modname: LuaValue, env: LuaValue): LuaValue {
            env[moduleName] = NIL
            env["package"]["loaded"][moduleName] = NIL
            return NIL
        }
    }
    this.load(registration)
}
