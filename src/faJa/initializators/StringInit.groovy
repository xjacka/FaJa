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

		classFile.constantPool.add('String')
		classFile.constantPool.add('Object')

		classFile.fields = []

//		Method length(0) - Native
		def length = new PrecompiledMethod()
		length.signatureIndex = classFile.constantPool.add('length(0)')
		classFile.methods.add(length)

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = classFile.constantPool.add('==(1)')
		classFile.methods.add(equals)

//		Method +(1) - Native
		def plus = new PrecompiledMethod()
		plus.signatureIndex = classFile.constantPool.add('+(1)')
		classFile.methods.add(plus)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = classFile.constantPool.add('ifTrue(1)')
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = classFile.constantPool.add('ifFalse(1)')
		classFile.methods.add(iffalse)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = classFile.constantPool.add('toS(0)')
		classFile.methods.add(toS)

//		Method toNumber(0) - Native
		def toNumber = new PrecompiledMethod()
		toNumber.signatureIndex = classFile.constantPool.add('toNumber(0)')
		classFile.methods.add(toNumber)
	}
}
