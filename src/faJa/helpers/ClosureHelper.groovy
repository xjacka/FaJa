package faJa.helpers

import faJa.Heap
import faJa.exceptions.InterpretException

class ClosureHelper {

	// returns pointer to bytecode structure of closure [size, argcnt, bytecode ..]
	static Integer getBytecodePtr(Heap heap, Integer objectPtr){
		Integer initClass = getInitClassPtr(heap, objectPtr)
		Integer closureIdx = getClosureIndex(heap, objectPtr)
		Integer closureSectionPtr = ClassAccessHelper.getClosureSection(heap, initClass)
		Integer closureSectionSize = heap.getPointer(closureSectionPtr)
		Integer bytecodePtr = closureSectionPtr + Heap.SLOT_SIZE
		closureIdx.times{
			bytecodePtr = ClassAccessHelper.skipSection(heap, bytecodePtr)
		}
//		if(bytecodePtr > closureSectionPtr + closureSectionSize){
//			throw new InterpretException('closure index points beyond closure section')
//		}
		bytecodePtr
	}

	static Integer getBytecodeSize(Heap heap, Integer bytecodePtr){
		heap.getPointer(bytecodePtr) - Heap.SLOT_SIZE // argCountSlot
	}

	static Integer getBytecodeArgCount(Heap heap, Integer bytecodePtr){
		heap.getPointer(bytecodePtr + Heap.SLOT_SIZE)
	}

	// returns pointer to (real) start of bytecode
	static Integer getBytecodeStart(Integer bytecodePtr){
		bytecodePtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE // skip size and argcnt
	}

	// get pointer to class where was closure initialize
	static Integer getInitClassPtr(Heap heap, Integer objectPtr){
		heap.getPointer(objectPtr + Heap.SLOT_SIZE)
	}

	static Integer getClosureIndex(Heap heap, Integer objectPtr){
		Integer closureIdxPtr = objectPtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE
		heap.getUnsignedByte(closureIdxPtr)

	}
}
