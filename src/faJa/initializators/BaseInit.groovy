package faJa.initializators

import faJa.ClassFile

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
