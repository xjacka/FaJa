package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

/**
 *      STRING OBJECT
 * +--------------------+
 * |   stringClassPtr   |
 * +--------------------+
 * |     stringSize     |
 * +====================+
 * |      char_1        |
 * +--------------------+
 * |      char_2        |
 * +--------------------+
 * |       ....         |
 * +--------------------+
 *
 */
class StringInit extends BaseInit{
	StringInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
				'String',
				'Object',
				'length(0)',
				'==(1)',
				'+(1)',
				'ifTrue(1)',
				'ifFalse(1)',
				'toS(0)',
				'toNumber(0)'
		]
		classFile.fields = []

//		Method length(0) - Native
		def length = new PrecompiledMethod()
		length.signatureIndex = 2
		classFile.methods.add(length)

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 3
		classFile.methods.add(equals)

//		Method +(1) - Native
		def plus = new PrecompiledMethod()
		plus.signatureIndex = 4
		classFile.methods.add(plus)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = 5
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = 6
		classFile.methods.add(iffalse)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 7
		classFile.methods.add(toS)

//		Method toNumber(0) - Native
		def toNumber = new PrecompiledMethod()
		toNumber.signatureIndex = 8
		classFile.methods.add(toNumber)
	}
}
