package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

/**
 *       NULL OBJECT
 * +--------------------+
 * |    nullClassPtr    |
 * +--------------------+
 *
 */
class NullInit extends BaseInit {
	NullInit() {
		classFile = new ClassFile()
		classFile.constantPool = [
				'Null',
				'Object',
				'==(1)',
				'isNull(0)',
				'ifTrue(1)',
				'ifFalse(1)',
				'toS(0)'
		]
		classFile.fields = []

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 2
		classFile.methods.add(equals)

//		Method isNull(0) - Native
		def isNull = new PrecompiledMethod()
		isNull.signatureIndex = 3
		classFile.methods.add(isNull)

//		Method ifTrue(1) - Native
		def ifTrue = new PrecompiledMethod()
		ifTrue.signatureIndex = 4
		classFile.methods.add(ifTrue)

//		Method ifFalse(1) - Native
		def ifFalse = new PrecompiledMethod()
		ifFalse.signatureIndex = 5
		classFile.methods.add(ifFalse)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 6
		classFile.methods.add(toS)

		classFile.isSingleton = true

	}
}
