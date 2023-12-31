package org.luaj.vm2.lib.common

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaThread
import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import org.luaj.vm2.lib.LibFunction
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.OneArgFunctionSuspend
import org.luaj.vm2.lib.ThreeArgFunction
import org.luaj.vm2.lib.ThreeArgFunctionSuspend
import org.luaj.vm2.lib.TwoArgFunction
import org.luaj.vm2.lib.TwoArgFunctionSuspend
import org.luaj.vm2.lib.VarArgFunction
import org.luaj.vm2.lib.VarArgFunctionSuspend
import org.luaj.vm2.lib.ZeroArgFunction
import org.luaj.vm2.lib.ZeroArgFunctionSuspend

interface LibBuilder {

    fun add(name: String, functionBuilder: (Globals) -> LibFunction)

    fun add(name: String, function: LibFunction){
        add(name) { function }
    }

    fun addValue(name: String, luaValue: LuaValue)

    fun addZeroArg(name: String, function: (Globals)->LuaValue){
        add(name){ globals: Globals ->
            object : ZeroArgFunction() {
                override fun call(): LuaValue {
                    return function(globals)
                }
            }
        }
    }

    fun addSuspendedZeroArg(name: String, function: suspend (Globals)->LuaValue){
        add(name){ globals: Globals ->
            wrap(globals,object : ZeroArgFunctionSuspend() {
                override suspend fun callSuspend(): LuaValue {
                    return function(globals)
                }
            })
        }
    }
    fun addSuspendedOneArg(name: String, function: suspend (LuaValue, Globals)->LuaValue){
        add(name){ globals: Globals ->
            wrap(globals,
                object : OneArgFunctionSuspend() {
                    override suspend fun callSuspend(arg: LuaValue): LuaValue {
                        return function(arg,globals)
                    }
                }
            )
        }
    }
    fun addSuspendedTwoArg(name: String, function: suspend (LuaValue, LuaValue, Globals)->LuaValue){
        add(name){ globals: Globals ->
            wrap(globals,
                object : TwoArgFunctionSuspend() {
                    override suspend fun callSuspend(arg1: LuaValue, arg2: LuaValue): LuaValue {
                        return function(arg1,arg2,globals)
                    }
                }
            )
        }
    }
    fun addSuspendedThreeArg(name: String, function: suspend (LuaValue, LuaValue, LuaValue, Globals)->LuaValue){
        add(name){ globals: Globals ->
            wrap(globals,
                object : ThreeArgFunctionSuspend() {
                    override suspend fun callSuspend(arg1: LuaValue, arg2: LuaValue, arg3: LuaValue): LuaValue {
                        return function(arg1,arg2,arg3,globals)
                    }
                }
            )
        }
    }
    fun addSuspendedVarArg(name: String, function: suspend (Varargs, Globals)->Varargs){
        add(name){ globals: Globals ->
            wrap(globals,
                object : VarArgFunctionSuspend() {
                    override suspend fun invokeSuspend(args: Varargs): Varargs {
                        return function(args,globals)
                    }
                }
            )
        }
    }
    fun addOneArg(name: String, function: (LuaValue,Globals)->LuaValue){
        add(name){ globals: Globals ->
            object : OneArgFunction() {
                override fun call(arg: LuaValue): LuaValue {
                    return function(arg,globals)
                }
            }
        }
    }

    fun addTwoArg(name: String, function: (LuaValue,LuaValue,Globals)->LuaValue){
        add(name){ globals: Globals ->
            object : TwoArgFunction() {
                override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue {
                    return function(arg1,arg2,globals)
                }
            }
        }
    }

    fun addThreeArg(name: String, function: (LuaValue,LuaValue,LuaValue,Globals)->LuaValue){
        add(name){ globals: Globals ->
            object : ThreeArgFunction() {
                override fun call(arg1: LuaValue, arg2: LuaValue, arg3: LuaValue): LuaValue {
                    return function(arg1,arg2,arg3,globals)
                }
            }
        }
    }

    fun addVarArg(name: String, function: (Varargs,Globals)->Varargs){
        add(name){ globals: Globals ->
            object : VarArgFunction() {
                override fun invoke(args: Varargs): Varargs {
                    return function(args,globals)
                }
            }
        }
    }

    companion object {
        fun create(
            name: String,
            defaultRequire: Boolean = false,
            callBuilder: LibBuilder.() -> Unit
        ): TwoArgFunction {
            val builder = LibBuilderImpl(
                name,
                defaultRequire
            )
            builder.callBuilder()
            return builder.build()
        }
    }

    private fun wrap(globals: Globals, function: LibFunction): LibFunction {
        return Wrapper(function,globals)
    }

    class Wrapper(
        private val luaFunction: LuaFunction,
        private val globals: Globals
    ): VarArgFunctionSuspend() {
        override suspend fun invokeSuspend(args: Varargs): Varargs {
            val luaThread = LuaThread(globals, luaFunction)
            val result = luaThread.resume(args)
            return if (result.arg1().toboolean()) {
                result.subargs(2)
            } else {
                error(result.arg(2).tojstring())
            }
        }
    }
}
