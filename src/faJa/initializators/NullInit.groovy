package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

/**
 * Created by xjacka on 6.2.15.
 */
class NullInit extends BaseInit {
	NullInit() {
		classFile = new ClassFile()
		classFile.constantPool = [
				'Number',
				'Object',
				'==(1)',
				'isNull(1)',
				'ifTrue(1)',
				'ifFalse(1)',
				'toS(0)'
		]
		classFile.fields = []

//		Method isNull(0) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 2
		classFile.methods.add(equals)

//		Method isNull(0) - Native
		def isNull = new PrecompiledMethod()
		isNull.signatureIndex = 3
		classFile.methods.add(isNull)

//		Method isNull(0) - Native
		def ifTrue = new PrecompiledMethod()
		ifTrue.signatureIndex = 4
		classFile.methods.add(ifTrue)

//		Method isNull(0) - Native
		def ifFalse = new PrecompiledMethod()
		ifFalse.signatureIndex = 5
		classFile.methods.add(ifFalse)

//		Method isNull(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 6
		classFile.methods.add(toS)

		classFile.isSingleton = true

	}
}
