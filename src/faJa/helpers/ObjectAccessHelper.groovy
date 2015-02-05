package faJa.helpers

import faJa.Heap

class ObjectAccessHelper {

	// return pointer to object class
	static Integer getClassPointer(Heap heap,Integer ptr) {
		if(ptr == null){
			ptr
		}
		heap.getPointer(ptr)
	}

	static Integer valueOf(Heap heap,Integer objectPtr, Integer fieldIdx){
		heap.getPointer(objectPtr +  fieldIdx + Heap.SLOT_SIZE)
	}

	static setNewValue(Heap heap, Integer objectPtr, Integer fieldIdx, Integer newVal){
		heap.setPointer(objectPtr +  fieldIdx + Heap.SLOT_SIZE, newVal)
	}
}
