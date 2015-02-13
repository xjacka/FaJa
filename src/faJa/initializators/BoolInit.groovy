package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

class BoolInit extends BaseInit{

	BoolInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
		        'Bool',
				'Object',
				'==(1)',
				'ifTrue(1)',
				'ifFalse(1)',
				'and(1)',
				'or(1)',
				'not(0)',
		        'toS(0)'
		]

		classFile.fields = []

//		Method ==(1) - Native
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 2
		classFile.methods.add(equals)

//		Method ifTrue(1) - Native
		def iftrue = new PrecompiledMethod()
		iftrue.signatureIndex = 3
		classFile.methods.add(iftrue)

//		Method ifFalse(1) - Native
		def iffalse = new PrecompiledMethod()
		iffalse.signatureIndex = 4
		classFile.methods.add(iffalse)

//		Method and(1) - Native
		def and = new PrecompiledMethod()
		and.signatureIndex = 5
		classFile.methods.add(and)

//		Method or(1) - Native
		def or = new PrecompiledMethod()
		or.signatureIndex = 6
		classFile.methods.add(or)

//		Method not(0) - Native
		def not = new PrecompiledMethod()
		not.signatureIndex = 7
		classFile.methods.add(not)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 8
		classFile.methods.add(toS)
	}
}
