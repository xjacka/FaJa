package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

/**
 *         NUMBER OBJECT
 * +----------------------+
 * | 4 |  numberClassPtr  |
 * +----------------------+
 * |   |                  |
 * + 4 |--    value    ---+
 * |   |                  |
 * +----------------------+
 *
 */
class NumberInit extends BaseInit {

	NumberInit(){
		classFile = new ClassFile()
		classFile.constantPool.add('Number')
		classFile.constantPool.add('Object')

		classFile.fields = []

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = classFile.constantPool.add('==(1)')
		classFile.methods.add(equals)

//		Method +(1) - Native
		def plus = new PrecompiledMethod()
		plus.signatureIndex = classFile.constantPool.add('+(1)')
		classFile.methods.add(plus)

//		Method -(1) - Native
		def minus = new PrecompiledMethod()
		minus.signatureIndex = classFile.constantPool.add('-(1)')
		classFile.methods.add(minus)

//		Method *(1) - Native
		def mul = new PrecompiledMethod()
		mul.signatureIndex = classFile.constantPool.add('*(1)')
		classFile.methods.add(mul)

//		Method *(1) - Native
		def pow = new PrecompiledMethod()
		pow.signatureIndex = classFile.constantPool.add('**(1)')
		classFile.methods.add(pow)

//		Method /(1) - Native
		def div = new PrecompiledMethod()
		div.signatureIndex = classFile.constantPool.add('/(1)')
		classFile.methods.add(div)

//		Method %(1) - Native
		def mod = new PrecompiledMethod()
		mod.signatureIndex = classFile.constantPool.add('%(1)')
		classFile.methods.add(mod)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = classFile.constantPool.add('ifTrue(1)')
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = classFile.constantPool.add('ifFalse(1)')
		classFile.methods.add(iffalse)

//		Method init(1) - Native
		def init = new PrecompiledMethod()
		init.signatureIndex = classFile.constantPool.add('init(1)')
		classFile.methods.add(init)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = classFile.constantPool.add('toS(0)')
		classFile.methods.add(toS)

//		Method times(1) - Native
		def times = new PrecompiledMethod()
		times.signatureIndex = classFile.constantPool.add('times(1)')
		classFile.methods.add(times)

//		Method <(1) - Native
		def lessThen = new PrecompiledMethod()
		lessThen.signatureIndex = classFile.constantPool.add('<(1)')
		classFile.methods.add(lessThen)

//		Method >(1) - Native
		def greaterThen = new PrecompiledMethod()
		greaterThen.signatureIndex = classFile.constantPool.add('>(1)')
		classFile.methods.add(greaterThen)

//		Method >=(1) - Native
		def greaterOrEqualThen = new PrecompiledMethod()
		greaterOrEqualThen.signatureIndex = classFile.constantPool.add('>=(1)')
		classFile.methods.add(greaterOrEqualThen)

//		Method <=(1) - Native
		def lessOrEqualThen = new PrecompiledMethod()
		lessOrEqualThen.signatureIndex = classFile.constantPool.add('<=(1)')
		classFile.methods.add(lessOrEqualThen)

//		Method abs() - Native
		def abs = new PrecompiledMethod()
		abs.signatureIndex = classFile.constantPool.add('abs(0)')
		classFile.methods.add(abs)
	}
}
