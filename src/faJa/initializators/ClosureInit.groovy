package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.ConstantPool
import faJa.compilator.representation.PrecompiledMethod

/**
 *          CLOSURE OBJECT
 *   size         |
 * +-----------------------+
 * |  4 | closureClassPtr  |
 * +-----------------------+
 * |  4 |  initClassPtr    |
 * +-----------------------+
 * |  1 |  closureIndex    |   <- index of closure in initClass
 * +-----------------------+
 *
 */
class ClosureInit extends BaseInit{

	ClosureInit(){
		classFile = new ClassFile()
		classFile.constantPool.add('Closure')
		classFile.constantPool.add('Object')
		classFile.fields = []

		def call = new PrecompiledMethod()
		call.signatureIndex = classFile.constantPool.add('call(0)')
		classFile.methods.add(call)


	}
}
