package faJa.initializators

import faJa.compilator.representation.ClassFile

abstract class BaseInit {
	ClassFile classFile

	def toBytecode(){
		classFile.methods.each { method ->
			if(method.instructions.empty){
				method.isNative = true
			}
		}
		classFile.toByteCode()
	}
}
