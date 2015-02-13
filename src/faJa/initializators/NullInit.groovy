package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.ConstantPool
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
		classFile.constantPool = new ConstantPool()
		classFile.constantPool.add('Null')
		classFile.constantPool.add('Object')

		classFile.fields = []

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = classFile.constantPool.add('==(1)')
		classFile.methods.add(equals)

//		Method isNull(0) - Native
		def isNull = new PrecompiledMethod()
		isNull.signatureIndex = classFile.constantPool.add('isNull(0)')
		classFile.methods.add(isNull)

//		Method ifTrue(1) - Native
		def ifTrue = new PrecompiledMethod()
		ifTrue.signatureIndex = classFile.constantPool.add('ifTrue(1)')
		classFile.methods.add(ifTrue)

//		Method ifFalse(1) - Native
		def ifFalse = new PrecompiledMethod()
		ifFalse.signatureIndex = classFile.constantPool.add('ifFalse(1)')
		classFile.methods.add(ifFalse)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = classFile.constantPool.add('toS(1)')
		classFile.methods.add(toS)

		classFile.isSingleton = true

	}
}
