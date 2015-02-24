package faJa.helpers

import faJa.memory.Heap

class ObjectAccessHelper {

	// return pointer to object class
	static Integer getClassPointer(Heap heap,Integer ptr) {
		if(ptr == null){
			return ptr
		}
		heap.getPointer(ptr)
	}

	static Integer valueOf(Heap heap,Integer objectPtr, Integer fieldIdx){
		heap.getPointer(objectPtr + ((fieldIdx/Heap.SLOT_SIZE) * Heap.HEAP_POINTER_SIZE).toInteger() + Heap.HEAP_POINTER_SIZE)
	}

	static setNewValue(Heap heap, Integer objectPtr, Integer fieldIdx, Integer newVal){
		heap.setPointer(objectPtr + ((fieldIdx/Heap.SLOT_SIZE) * Heap.HEAP_POINTER_SIZE).toInteger() + Heap.HEAP_POINTER_SIZE, newVal)
	}
}
