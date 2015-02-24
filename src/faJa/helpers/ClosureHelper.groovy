package faJa.helpers

import faJa.interpreter.Instruction
import faJa.memory.Heap

class ClosureHelper {

	// returns pointer to bytecode structure of closure [size, argcnt, localsCnt, bytecode ..]
	static Integer getBytecodePtr(Heap heap, Integer objectPtr){
		Integer initClass = getInitClassPtr(heap, objectPtr)
		Integer closureIdx = getClosureIndex(heap, objectPtr)
		Integer closureSectionPtr = ClassAccessHelper.getClosureSection(heap, initClass)
//		Integer closureSectionSize = heap.getSlot(closureSectionPtr)
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
		heap.getPointer(bytecodePtr) - Heap.SLOT_SIZE - Heap.SLOT_SIZE // argCountSlot and localCountSlot
	}

	static Integer getBytecodeArgCount(Heap heap, Integer bytecodePtr){
		heap.getSlot(bytecodePtr + Heap.SLOT_SIZE)
	}

	// returns pointer to (real) start of bytecode
	static Integer getBytecodeStart(Integer bytecodePtr){
		bytecodePtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE + Heap.SLOT_SIZE // skip size and argcnt and localCntSlot
	}

	// get pointer to class where was closure initialize
	static Integer getInitClassPtr(Heap heap, Integer objectPtr){
		heap.getPointer(objectPtr + Heap.HEAP_POINTER_SIZE)
	}

	static Integer getClosureIndex(Heap heap, Integer objectPtr){
		Integer closureIdxPtr = objectPtr + Heap.HEAP_POINTER_SIZE + Heap.HEAP_POINTER_SIZE
		heap.getUnsignedByte(closureIdxPtr)
	}
	static Integer getClosureLocalCnt(Heap heap, Integer bytecodePtr){
		Integer closureLocalCntPtr = bytecodePtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE
		heap.getSlot(closureLocalCntPtr)
	}
}
