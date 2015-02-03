package faJa

class ClassLoader {

	Heap heap

	// nahraje reprezentaci pomoci faJa.ClassFile na heapu
	def load(ClassFile classFile){
		heap.load(classFile.toByteCode())
	}

}
