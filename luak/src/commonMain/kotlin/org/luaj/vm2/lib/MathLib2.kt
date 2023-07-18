package org.luaj.vm2.lib

import org.luaj.vm2.Globals
import org.luaj.vm2.LuaDouble
import org.luaj.vm2.LuaDouble.Companion.valueOf
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.common.LibBuilder
import kotlin.math.pow
import kotlin.random.Random

val MathLib2 = LibBuilder.create("math",defaultRequire = true){
    addUnary("abs") { arg, _ -> kotlin.math.abs(arg) }

    addUnary("ceil") { arg, _ -> kotlin.math.ceil(arg) }

    addUnary("cos") { arg, _ -> kotlin.math.cos(arg) }

    addUnary("deg") { arg, _ -> arg * 180.0 / kotlin.math.PI }

    addUnary("exp") { arg, _ -> kotlin.math.exp(arg) }

    addUnary("floor") { arg, _ -> kotlin.math.floor(arg) }

    addBinary("fmod") { x, y, _ ->
        val q = x / y
        return@addBinary x - y * if (q >= 0)
            kotlin.math.floor(q)
        else
            kotlin.math.ceil(q)
    }

    addVarArg("frexp") { args, _ ->
        val x = args.checkdouble(1)
        if (x == 0.0) return@addVarArg LuaValue.varargsOf(LuaValue.ZERO, LuaValue.ZERO)
        val bits = (x).toRawBits()
        val m =
            ((bits and (-1L shl 52).inv()) + (1L shl 52)) * if (bits >= 0) .5 / (1L shl 52) else -.5 / (1L shl 52)
        val e = (((bits shr 52).toInt() and 0x7ff) - 1022).toDouble()
        return@addVarArg LuaValue.varargsOf(LuaValue.valueOf(m), LuaValue.valueOf(e))
    }

    addValue("huge", LuaDouble.POSINF)

    addBinary("ldexp") { x, y, _ ->
        x * Double.fromBits(y.toLong() + 1023 shl 52)
    }

    addVarArg("max"){ args, _ ->
        var m = args.checkdouble(1)
        var i = 2
        val n = args.narg()
        while (i <= n) {
            m = kotlin.math.max(m, args.checkdouble(i))
            ++i
        }
        return@addVarArg LuaValue.valueOf(m)
    }

    addVarArg("min"){ args, _ ->
        var m = args.checkdouble(1)
        var i = 2
        val n = args.narg()
        while (i <= n) {
            m = kotlin.math.min(m, args.checkdouble(i))
            ++i
        }
        return@addVarArg LuaValue.valueOf(m)
    }

    addVarArg("modf"){ args, _ ->
        val x = args.checkdouble(1)
        val intPart = if (x >= 0) kotlin.math.floor(x) else kotlin.math.ceil(x)
        val fracPart = x - intPart
        return@addVarArg LuaValue.varargsOf(LuaValue.valueOf(intPart), LuaValue.valueOf(fracPart))
    }

    addValue("pi", valueOf(kotlin.math.PI))

    addBinary("pow") { x, y, _ -> x.pow(y) }

    val random = random()
    add("random", random)
    add("randomseed", randomseed(random))

    addUnary("rad") { arg, _ -> arg * kotlin.math.PI / 180.0 }

    addUnary("sin") { arg, _ -> kotlin.math.sin(arg) }


    addUnary("sqrt") { arg, _ -> kotlin.math.sqrt(arg) }

    addUnary("tan") { arg, _ -> kotlin.math.tan(arg) }
}

private fun LibBuilder.addUnary(name: String, unaryFunction: (Double, Globals) -> Double) {
    addOneArg(name) { arg, globals ->
        val doubleArg = arg.checkdouble()
        val result = unaryFunction(doubleArg, globals)
        LuaValue.valueOf(result)
    }
}

private fun LibBuilder.addBinary(
    name: String,
    binaryFunction: (Double, Double, Globals) -> Double
) {
    addTwoArg(name) { arg1, arg2, globals ->
        val doubleArg1 = arg1.checkdouble()
        val doubleArg2 = arg2.checkdouble()
        val result = binaryFunction(doubleArg1, doubleArg2, globals)
        LuaValue.valueOf(result)
    }
}

internal class random : LibFunction() {
    var random: Random = Random.Default
    override fun call(): LuaValue {
        return valueOf(random.nextDouble())
    }

    override fun call(a: LuaValue): LuaValue {
        val m = a.checkint()
        if (m < 1) argerror(1, "interval is empty")
        return valueOf(1 + random.nextInt(m))
    }

    override fun call(a: LuaValue, b: LuaValue): LuaValue {
        val m = a.checkint()
        val n = b.checkint()
        if (n < m) argerror(2, "interval is empty")
        return valueOf(m + random.nextInt(n + 1 - m))
    }

}

internal class randomseed(val random: random) : OneArgFunction() {
    override fun call(arg: LuaValue): LuaValue {
        val seed = arg.checklong()
        random.random = Random(seed)
        return NONE
    }
}
