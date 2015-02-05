package faJa.initializators

import faJa.ClassFile

abstract class BaseInit {
	ClassFile classFile

	def toBytecode(){
		classFile.toByteCode()
	}
}
