package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

class NumberInit extends BaseInit {

	NumberInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
				'Number',
				'Object',
				'==(1)',
				'+(1)',
				'-(1)',
				'*(1)',
				'/(1)',
				'%(1)',
				'ifTrue(1)',
				'ifFalse(1)',
				'init(1)',
				'toS(0)',
				'times(1)'
		]
		classFile.fields = []

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 2
		classFile.methods.add(equals)

//		Method +(1) - Native
		def plus = new PrecompiledMethod()
		plus.signatureIndex = 3
		classFile.methods.add(plus)

//		Method -(1) - Native
		def minus = new PrecompiledMethod()
		minus.signatureIndex = 4
		classFile.methods.add(minus)

//		Method *(1) - Native
		def mul = new PrecompiledMethod()
		mul.signatureIndex = 5
		classFile.methods.add(mul)

//		Method /(1) - Native
		def div = new PrecompiledMethod()
		div.signatureIndex = 6
		classFile.methods.add(div)

//		Method %(1) - Native
		def mod = new PrecompiledMethod()
		mod.signatureIndex = 7
		classFile.methods.add(mod)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = 8
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = 9
		classFile.methods.add(iffalse)

//		Method init(1) - Native
		def init = new PrecompiledMethod()
		init.signatureIndex = 10
		classFile.methods.add(init)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 11
		classFile.methods.add(toS)

//		Method times(1) - Native
		def times = new PrecompiledMethod()
		times.signatureIndex = 12
		classFile.methods.add(times)
	}
}
