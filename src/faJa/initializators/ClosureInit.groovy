package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

class ClosureInit extends BaseInit{

	ClosureInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
		        'Closure',
				'Object',
				'call(1)'
		]

		classFile.fields = []

		def call = new PrecompiledMethod()
		call.signatureIndex = 2
		classFile.methods.add(call)
	}
}
