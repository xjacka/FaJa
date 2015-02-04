package faJa.helpers

import faJa.Heap

class ClassAccessHelper {

	public static int CLASS_NAME_INDEX = 4

	static String getName(Heap heap, Integer ptr){
		heap.getString(ptr + CLASS_NAME_INDEX)
	}

	static String getParent(Heap heap, Integer ptr){
		def parentPtr = skipSection(heap, ptr + CLASS_NAME_INDEX)
		heap.getString(parentPtr)
	}

	static Integer getObjectSize(Heap heap, Integer ptr){
		def fieldSizePtr = getFieldsSection(heap, ptr)
		heap.getPointer(fieldSizePtr)
	}

	// returns pointer to method bytecode or null
	static Integer findMethod(Heap heap, Integer ptr, String signature){
		def methodSectionPtr = getMethodSection(heap,ptr)
		def methodSectionSize = heap.getPointer(methodSectionPtr)
		def methodPtr = methodSectionPtr + Heap.SLOT_SIZE
		while(methodSectionSize + methodSectionPtr > methodPtr){

			def methodConstPoolPtr = methodPtr + Heap.SLOT_SIZE
			def methodSignaturePtr = ptr + heap.getPointer(methodConstPoolPtr)
			def methodSignature = heap.getString(methodSignaturePtr)

			if(signature == methodSignature){
				return methodPtr
			}
			methodPtr = skipSection(heap, methodPtr)
		}

		return null
	}

	// return relative pointer to date area in object
	static Integer findFieldIndex(Heap heap, Integer ptr, String name) {
		def fieldsPtr = getFieldsSection(heap, ptr)
		def fieldsSize = heap.getPointer(fieldsPtr)
		def fieldCpPointer = fieldsPtr
		def fieldIndex = 0
		while(fieldsSize + fieldsPtr > fieldCpPointer){
			fieldCpPointer += Heap.SLOT_SIZE

			def fieldNamePtr = ptr + heap.getPointer(fieldCpPointer)
			def fieldName = heap.getString(fieldNamePtr)

			if(name == fieldName){
				return fieldIndex
			}

			fieldIndex += Heap.SLOT_SIZE
		}
		return null
	}

	static Integer getFieldsSection(Heap heap, int ptr) {
		def constPoolPtr = ptr + Heap.SLOT_SIZE
		def fieldsPtr = skipSection(heap, constPoolPtr)

		fieldsPtr
	}

	static Integer skipSection(Heap heap, int ptr){
		def sectionSize = heap.getPointer(ptr)
		ptr + Heap.SLOT_SIZE + sectionSize
	}

	static Integer getMethodSection(Heap heap, int ptr) {
		def constPoolPtr = ptr + Heap.SLOT_SIZE
		def fieldsPtr = skipSection(heap, constPoolPtr)
		def methodsPtr = skipSection(heap, fieldsPtr)

		methodsPtr
	}

	static Boolean isNative(Heap heap, Integer methodPointer) {
		heap.getPointer(methodPointer) == Heap.SLOT_SIZE
	}

}
