package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.ConstantPool
import faJa.compilator.representation.PrecompiledMethod

/**
 *      BOOL OBJECT
 * +--------------------+
 * |    boolClassPtr    |
 * +--------------------+
 * |        val         |  <- only 1 byte
 * +--------------------+
 *
 */
class BoolInit extends BaseInit{

	BoolInit(){
		classFile = new ClassFile()
		classFile.constantPool.add('Bool')
		classFile.constantPool.add('Object')
		classFile.fields = []

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = classFile.constantPool.add('==(1)')
		classFile.methods.add(equals)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = classFile.constantPool.add('ifTrue(1)')
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = classFile.constantPool.add('ifFalse(1)')
		classFile.methods.add(iffalse)

//		Method and(1) - Native
		def and = new PrecompiledMethod()
		and.signatureIndex = classFile.constantPool.add('and(1)')
		classFile.methods.add(and)

//		Method or(1) - Native
		def or = new PrecompiledMethod()
		or.signatureIndex = classFile.constantPool.add('or(1)')
		classFile.methods.add(or)

//		Method not(0) - Native
		def not = new PrecompiledMethod()
		not.signatureIndex = classFile.constantPool.add('not(0)')
		classFile.methods.add(not)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = classFile.constantPool.add('toS(0)')
		classFile.methods.add(toS)
	}
}
