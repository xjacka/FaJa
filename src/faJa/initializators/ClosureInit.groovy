package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

class ClosureInit extends BaseInit{

	ClosureInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
		        'Closure',
				'Object',
				'stackFrame',
				'bytecode',
				'call(0)'
		]

		classFile.fields = [2,3]

		def call = new PrecompiledMethod()
		call.signatureIndex = 4
		classFile.methods.add(call)
	}
}
