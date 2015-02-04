package faJa.helpers

import faJa.Heap

/**
 * Created by xjacka on 4.2.15.
 */
class ClassAccessHelper {

	public static int CLASS_NAME_INDEX = 4
	public static int PARRENT_CLASS_NAME_INDEX = 6

	static String getName(Heap heap, Integer ptr){
		heap.getString(ptr + CLASS_NAME_INDEX)
	}

	static String getParrent(Heap heap, Integer ptr){
		heap.getString(ptr + PARRENT_CLASS_NAME_INDEX)
	}

	// returns pointer to method bytecode or null
	static Integer findMethod(Heap heap, Integer ptr, String signature){
		def methodSectionPtr = getMethodSection(heap,ptr)
		def methodSectionSize = ByteHelper.bytesToIntAt(heap, methodSectionPtr)
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
		def fieldsSize = ByteHelper.bytesToIntAt(heap, fieldsPtr)
		def fieldCpPointer = fieldsPtr + Heap.SLOT_SIZE
		def fieldIndex = 0
		while(fieldsSize + fieldsPtr > fieldCpPointer){

			def fieldNamePtr = ptr + heap.getPointer(fieldCpPointer)
			def fieldName = heap.getString(fieldNamePtr)

			if(name == fieldName){
				return fieldIndex
			}

			fieldIndex += Heap.SLOT_SIZE
			fieldCpPointer += Heap.SLOT_SIZE
		}

	}

	static Integer getFieldsSection(Heap heap, int ptr) {
		def constPoolPtr = ptr + Heap.SLOT_SIZE
		def fieldsPtr = skipSection(heap, constPoolPtr)

		fieldsPtr
	}

	static Integer skipSection(Heap heap, int ptr){
		def sectionSize = ByteHelper.bytesToIntAt(heap, ptr)
		ptr + Heap.SLOT_SIZE + sectionSize
	}

	static Integer getMethodSection(Heap heap, int ptr) {
		def constPoolPtr = ptr + Heap.SLOT_SIZE
		def fieldsPtr = skipSection(heap, constPoolPtr)
		def methodsPtr = skipSection(heap, fieldsPtr)

		methodsPtr
	}
}
