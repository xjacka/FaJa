package faJa.initializators

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.PrecompiledMethod

class ObjectInit extends BaseInit {

	ObjectInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
		        'Object',
				'',
				'==(1)',
				'toS(0)',
		        'isNull(0)'
		]
		classFile.fields = []
		def equals = new PrecompiledMethod()
		equals.signatureIndex = 2
		classFile.methods.add(equals)

//		Method toS(0) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 3
		classFile.methods.add(toS)

//		Method isNull(0) - Native
		def isNull = new PrecompiledMethod()
		isNull.signatureIndex = 4
		classFile.methods.add(isNull)

	}

}
