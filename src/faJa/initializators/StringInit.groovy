package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

class StringInit extends BaseInit{
	StringInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
				'String',
				'Object',
				'value',
				'length(0)',
				'==(1)',
				'+(1)',
				'ifTrue(1)',
				'ifFalse(1)',
				'toS(0)'
		]
		classFile.fields = [2]

//		Method length(0) - Native
		def length = new PrecompiledMethod()
		length.signatureIndex = 3
		classFile.methods.add(length)

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 4
		classFile.methods.add(equals)

//		Method +(1) - Native
		def plus = new PrecompiledMethod()
		plus.signatureIndex = 5
		classFile.methods.add(plus)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = 6
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = 7
		classFile.methods.add(iffalse)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 8
		classFile.methods.add(toS)
	}
}
