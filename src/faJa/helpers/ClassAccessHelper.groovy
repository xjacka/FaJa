package faJa.helpers

import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.interpreter.ClassLoader
import faJa.exceptions.InterpretException

class ClassAccessHelper {

	public static int CLASS_NAME_INDEX = 4

	static String getName(Heap heap, Integer ptr) {
		heap.getString(ptr + CLASS_NAME_INDEX)
	}

	static String getParent(Heap heap, Integer ptr) {
		def parentPtr = skipSection(heap, ptr + CLASS_NAME_INDEX)
		heap.getString(parentPtr)
	}

	static Integer getObjectSize(Heap heap, Integer ptr) {
		def fieldSizePtr = getFieldsSection(heap, ptr)
		((heap.getSlot(fieldSizePtr)/Heap.SLOT_SIZE)*Heap.HEAP_POINTER_SIZE)+ Heap.HEAP_POINTER_SIZE
	}

	// returns pointer to method bytecode or null
	static Integer findMethod(Heap heap, Integer ptr, String signature) {
		def methodSectionPtr = getMethodSection(heap, ptr)
		def methodSectionSize = heap.getSlot(methodSectionPtr)
		def methodPtr = methodSectionPtr + Heap.SLOT_SIZE
		while (methodSectionSize + methodSectionPtr > methodPtr) {

			def methodConstPoolPtr = methodPtr + Heap.SLOT_SIZE
			def methodSignaturePtr = ptr + heap.getSlot(methodConstPoolPtr)
			def methodSignature = heap.getString(methodSignaturePtr)

			if (signature == methodSignature) {
				return methodPtr
			}
			methodPtr = skipMethod(heap, methodPtr)
		}

		return null
	}

	// return relative pointer to data area in object
	static Integer findFieldIndex(Heap heap, Integer ptr, String name) {
		def fieldsPtr = getFieldsSection(heap, ptr)
		def fieldsSize = heap.getSlot(fieldsPtr)
		def fieldCpPointer = fieldsPtr
		def fieldIndex = 0
		while (fieldsSize + fieldsPtr > fieldCpPointer) {
			fieldCpPointer += Heap.SLOT_SIZE
			def fieldNamePtr = ptr + heap.getSlot(fieldCpPointer)
			def fieldName = heap.getString(fieldNamePtr)

			if (name == fieldName) {
				return fieldIndex
			}

			fieldIndex += Heap.SLOT_SIZE
		}
		throw new InterpretException('field "' + name + '" not found on class ' + getName(heap,ptr))
		return null
	}

	static Integer getFieldsSection(Heap heap, int ptr) {
		def constPoolPtr = ptr + Heap.SLOT_SIZE
		def fieldsPtr = skipSection(heap, constPoolPtr)

		fieldsPtr
	}

	static Integer skipSection(Heap heap, int ptr) {
		def sectionSize = heap.getSlot(ptr)
		ptr + Heap.SLOT_SIZE + sectionSize
	}

	static Integer skipMethod(Heap heap, int ptr) {
		def sectionSize = heap.getSlot(ptr)
		ptr = ptr + Heap.SLOT_SIZE + sectionSize
		if(sectionSize == 0) {
			ptr += Heap.SLOT_SIZE //* 2
		}
		ptr
	}

	static Integer getMethodSection(Heap heap, int ptr) {
		def constPoolPtr = ptr + Heap.SLOT_SIZE
		def fieldsPtr = skipSection(heap, constPoolPtr)
		def methodsPtr = skipSection(heap, fieldsPtr)

		methodsPtr
	}

	static List<String> getAllFieldNames(Heap heap, Integer classPtr){
		List result = []
		Integer fieldsPtr = getFieldsSection(heap,classPtr)
		Integer classSize = heap.getSlot(fieldsPtr)
		Integer currentFieldPtr = fieldsPtr + Heap.SLOT_SIZE
		while(fieldsPtr + classSize > currentFieldPtr){
			Integer fieldCPPtr = heap.getSlot(currentFieldPtr)
			String fieldNamePtr = getConstantPoolValue(heap, classPtr, fieldCPPtr)
			String fieldName = heap.getString(fieldNamePtr)

			result.add(fieldName)

			currentFieldPtr = fieldsPtr + Heap.SLOT_SIZE
		}

		result
	}

	static Integer getConstantPoolPointer(Heap heap, Integer classPtr, String constantPoolValue){
		Integer constPoolPtr = classPtr + Heap.SLOT_SIZE
		Integer cpSize = heap.getSlot(constPoolPtr)
		Integer cpPointer = constPoolPtr + Heap.SLOT_SIZE
		Integer counter = 0
		while(constPoolPtr + cpSize > cpPointer){
			Integer itemSize = heap.getSlot(cpPointer)
			if(heap.getString(cpPointer) == constantPoolValue){
				return counter
			}
			counter++
			cpPointer += itemSize + Heap.SLOT_SIZE
		}
	}

	static Boolean isNative(Heap heap, Integer methodPointer) {
		heap.getSlot(methodPointer) == 0
	}

	static String getConstantPoolValue(Heap heap, Integer classPtr, Integer constPoolPtr) {
		heap.getString(classPtr + constPoolPtr)
	}

	static List findMethodWithSuper(Heap heap, Integer classPtr, String signature, ClassLoader classLoader) {
		if(classPtr == null){
			throw new InterpretException("Can not invoke method on null object")
		}
		List<Integer> cachePointer = heap.methodCache.get(signature + ClassAccessHelper.getName(heap,classPtr))
		if(cachePointer != null){
			return cachePointer
		}
		Integer methodPtr = findMethod(heap,classPtr,signature)
		while(methodPtr == null && getName(heap,classPtr) != Compiler.DEFAULT_PARENT){
			String parentName = getParent(heap,classPtr)
			classPtr = classLoader.findClass(heap,parentName)
			methodPtr = findMethod(heap,classPtr,signature)
		}
		if(methodPtr != null){
			heap.methodCache.put(signature + ClassAccessHelper.getName(heap,classPtr),[classPtr, methodPtr])
		}
		[classPtr, methodPtr]
	}

	static getClosureSection(Heap heap, int ptr){
		def methodsPtr = getMethodSection(heap, ptr)
		def closuresPtr = skipSection(heap, methodsPtr)

		closuresPtr
	}
}