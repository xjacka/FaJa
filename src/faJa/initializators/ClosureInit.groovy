package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

class ClosureInit extends BaseInit{

	ClosureInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
		        'Closure',
				'Object',
				'call(0)'
		]

		classFile.fields = []

		def call = new PrecompiledMethod()
		call.signatureIndex = 2
		classFile.methods.add(call)
	}
}
