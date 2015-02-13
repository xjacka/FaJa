package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

/**
 *      OBJECT OBJECT
 * +--------------------+
 * |   objectClassPtr   |
 * +--------------------+
 *
 */
class ObjectInit extends BaseInit {

	ObjectInit(){
		classFile = new ClassFile()

		classFile.constantPool.add('Object')
		classFile.constantPool.add('')

		classFile.fields = []
		def equals = new PrecompiledMethod()
		equals.signatureIndex = classFile.constantPool.add('==(1)')
		classFile.methods.add(equals)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = classFile.constantPool.add('toS(0)')
		classFile.methods.add(toS)

//		Method isNull(0) - Native
		def isNull = new PrecompiledMethod()
		isNull.signatureIndex = classFile.constantPool.add('isNull(0)')
		classFile.methods.add(isNull)

	}

}
