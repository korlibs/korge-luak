/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.luaj.vm2.lib

import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs

/** Abstract base class for Java function implementations that take two arguments and
 * return one value.
 *
 *
 * Subclasses need only implement [LuaValue.call] to complete this class,
 * simplifying development.
 * All other uses of [.call], [.invoke],etc,
 * are routed through this method by this class,
 * dropping or extending arguments with `nil` values as required.
 *
 *
 * If more or less than two arguments are required,
 * or variable argument or variable return values,
 * then use one of the related function
 * [ZeroArgFunction], [OneArgFunction], [ThreeArgFunction], or [VarArgFunction].
 *
 *
 * See [LibFunction] for more information on implementation libraries and library functions.
 * @see .call
 * @see LibFunction
 *
 * @see ZeroArgFunction
 *
 * @see OneArgFunction
 *
 * @see ThreeArgFunction
 *
 * @see VarArgFunction
 */
/** Default constructor  */
abstract class TwoArgFunction : LibFunction() {

    abstract override fun call(arg1: LuaValue, arg2: LuaValue): LuaValue

    override fun call(): LuaValue = call(NIL, NIL)
    override fun call(arg: LuaValue): LuaValue = call(arg, NIL)
    override fun call(arg1: LuaValue, arg2: LuaValue, arg3: LuaValue): LuaValue = call(arg1, arg2)
    override fun invoke(varargs: Varargs): Varargs = call(varargs.arg1(), varargs.arg(2))

    //override suspend fun callSuspend(): LuaValue = callSuspend(NIL, NIL)
}
