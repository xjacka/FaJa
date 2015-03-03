package faJa.helpers

import faJa.memory.Heap

class MethodHelper {

	static getArgsCount(String signature){
		String wrappedArgsCount = signature.find(~/\(.*\)/)
		String argsCount = wrappedArgsCount.substring(1, wrappedArgsCount.length() - 1)
		argsCount.toInteger()
	}

	static byte[] getBytecode(Heap heap, Integer methodPtr) {
		Integer methodSize = heap.getSlot(methodPtr)
		Integer bytecodePtr = methodPtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE // move pointer over methodSize and pointer to signature in constant pool
		Integer bytecodeSize = methodSize - Heap.SLOT_SIZE // method size - pointer to signature in constant pool

		heap.getBytes(bytecodePtr, bytecodeSize)
	}
}
