package faJa.helpers

import faJa.Heap

class ObjectAccessHelper {

	// return pointer to object class
	static Integer getClassPointer(Heap heap,Integer ptr) {
		heap.getPointer(ptr)
	}
}
