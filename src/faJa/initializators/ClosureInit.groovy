package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.ConstantPool
import faJa.compilator.representation.PrecompiledMethod

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
