package faJa.decompilator

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.representation.PrecompiledMethod
import faJa.helpers.ByteHelper
import faJa.interpreter.Instruction
import faJa.memory.Heap

class ClassDecompilator {
	
	ClassFile classFile = new ClassFile()
	Map<Integer, Integer> reverseCP = [:]
	Integer CP_START = 6

	def decompileHeap(Heap heap, Integer ptr){
		int classSize = heap.getSlot(ptr + Heap.HEAP_POINTER_SIZE)
		decompile(heap.getBytes(ptr, classSize))
	}

	def decompile(byte [] bytes){
		
		int cpSize = ByteHelper.bytesToIntAt(bytes, CP_START)
		int cpPtr = CP_START + Heap.SLOT_SIZE
		while( CP_START + cpSize > cpPtr){
			String val = ByteHelper.bytesToString(bytes, cpPtr)
			reverseCP.put(cpPtr, classFile.constantPool.add(val))
			cpPtr = cpPtr + val.length() + Heap.SLOT_SIZE
		}
		int fieldSize = ByteHelper.bytesToIntAt(bytes, cpPtr)
		
		cpPtr += Heap.SLOT_SIZE
		int fieldsEnd = cpPtr + fieldSize
		while(fieldsEnd > cpPtr){
			classFile.fields.add(getConstPoolIdx(bytes, cpPtr))
			cpPtr += Heap.SLOT_SIZE
		}

		int methodsSize = ByteHelper.bytesToIntAt(bytes, cpPtr)
		Integer bpPtr = cpPtr + Heap.SLOT_SIZE
		int methodsEnd = methodsSize + bpPtr
		while(methodsEnd > bpPtr){
			bpPtr = decompileMethod(bytes, bpPtr)
		}

		// todo closures
		
		println(classFile.toString())
	}
	
	Integer decompileMethod(byte [] bytes, Integer methodPtr){
		int methodSize = ByteHelper.bytesToIntAt(bytes, methodPtr)

		PrecompiledMethod precompiledMethod = new PrecompiledMethod()

		Integer cpPtr = methodPtr + Heap.SLOT_SIZE
		precompiledMethod.signatureIndex = getConstPoolIdx(bytes, cpPtr)

		if(methodSize == 0){
			precompiledMethod.isNative = true
			classFile.methods.add(precompiledMethod)
			return methodPtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE
		}
		
		int bytecodeSize = methodSize - Heap.SLOT_SIZE
		int bytecodePtr = cpPtr + Heap.SLOT_SIZE
		int bytecodeEnd = bytecodePtr + bytecodeSize
		while(bytecodeEnd > bytecodePtr){
			Instruction inst = Instruction.getById(bytes[bytecodePtr++])
			PrecompiledInstruction preInst = new PrecompiledInstruction()
			preInst.instruction = inst
			switch(inst.params){
				case 0:
					break
				case 1:
					preInst.paramVal = ByteHelper.unsignedValue(bytes[bytecodePtr++]) 
					break
				case 2:
					preInst.paramVal = reverseCP.get(ByteHelper.bytesToIntAt(bytes, bytecodePtr))
					bytecodePtr += 2
					break
			}
			precompiledMethod.instructions.add(preInst)
		}
		classFile.methods.add(precompiledMethod)
		bytecodePtr
	}
	
	def getConstPoolIdx(byte [] bytes, int cpPtrPtr){
		int cpPtr = ByteHelper.bytesToIntAt(bytes, cpPtrPtr)
		String name = ByteHelper.bytesToString(bytes, cpPtr)
		Integer cpIdx = classFile.constantPool.add(name)
		cpIdx
	}
}
