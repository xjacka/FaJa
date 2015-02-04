package faJa.initializators

import faJa.ClassFile
import faJa.Heap

abstract class BaseInit {
	ClassFile classFile

	def initOnHeap(Heap heap){
		classFile.toByteCode()
	}
}
