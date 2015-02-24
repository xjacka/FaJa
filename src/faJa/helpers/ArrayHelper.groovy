package faJa.helpers

import faJa.memory.Heap

class ArrayHelper {
	
	static setNewValue(Heap heap, Integer arrayObjectPtr ,Integer index, Integer value){
		heap.setPointer(arrayObjectPtr+Heap.SLOT_SIZE + index * Heap.HEAP_POINTER_SIZE, value)
	}
	
	static Integer valueAt(Heap heap, Integer arrayObjectPtr, Integer index){
		heap.getPointer(arrayObjectPtr+Heap.SLOT_SIZE + index * Heap.HEAP_POINTER_SIZE)
	}
	
	static setInsertIndex(Heap heap, Integer arrayPtr, Integer insertIndex){
		heap.setSlot(arrayPtr + Heap.HEAP_POINTER_SIZE,insertIndex)
	}
	
	static getInsertIndex(Heap heap, Integer arrayPtr){
		heap.getSlot(arrayPtr + Heap.HEAP_POINTER_SIZE)
	}
	
	static getArrayObjectPtr(Heap heap, Integer arrayPtr){
		heap.getPointer(arrayPtr + Heap.HEAP_POINTER_SIZE + Heap.SLOT_SIZE)
		
	}
	
	static setValues(Heap heap, Integer arrayObjectPtr ,List vals){
		vals.eachWithIndex{ val, i ->
			heap.setPointer(arrayObjectPtr+Heap.SLOT_SIZE + i * Heap.HEAP_POINTER_SIZE, val)
		}
	}
}
