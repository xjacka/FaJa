class ClassLoader {

	Heap heap

	// nahraje reprezentaci pomoci ClassFile na heapu
	def load(ClassFile classFile){
		heap.load(classFile.toByteCode())
	}

}
