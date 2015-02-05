package faJa.helpers

import faJa.Heap
import faJa.compilator.Compilator
import faJa.ClassLoader

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
		heap.getPointer(fieldSizePtr) + Heap.SLOT_SIZE
	}

	// returns pointer to method bytecode or null
	static Integer findMethod(Heap heap, Integer ptr, String signature) {
		def methodSectionPtr = getMethodSection(heap, ptr)
		def methodSectionSize = heap.getPointer(methodSectionPtr)
		def methodPtr = methodSectionPtr + Heap.SLOT_SIZE
		while (methodSectionSize + methodSectionPtr > methodPtr) {

			def methodConstPoolPtr = methodPtr + Heap.SLOT_SIZE
			def methodSignaturePtr = ptr + heap.getPointer(methodConstPoolPtr)
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
		def fieldsSize = heap.getPointer(fieldsPtr)
		def fieldCpPointer = fieldsPtr
		def fieldIndex = 0
		while (fieldsSize + fieldsPtr > fieldCpPointer) {
			fieldCpPointer += Heap.SLOT_SIZE

			def fieldNamePtr = ptr + heap.getPointer(fieldCpPointer)
			def fieldName = heap.getString(fieldNamePtr)

			if (name == fieldName) {
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

	static Integer skipSection(Heap heap, int ptr) {
		def sectionSize = heap.getPointer(ptr)
		ptr + Heap.SLOT_SIZE + sectionSize
	}

	static Integer skipMethod(Heap heap, int ptr) {
		def sectionSize = heap.getPointer(ptr)
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
		Integer fieldsPtr = ClassAccessHelper.getFieldsSection(heap,classPtr)
		Integer classSize = heap.getPointer(fieldsPtr)
		Integer currentFieldPtr = fieldsPtr + Heap.SLOT_SIZE
		while(fieldsPtr + classSize > currentFieldPtr){
			Integer fieldCPPtr = heap.getPointer(currentFieldPtr)
			Integer fieldNamePtr = ClassAccessHelper.getConstantPoolValue(heap, classPtr, fieldCPPtr)
			String fieldName = heap.getString(fieldNamePtr)

			result.add(fieldName)

			currentFieldPtr = fieldsPtr + Heap.SLOT_SIZE
		}

		result
	}

	static Boolean isNative(Heap heap, Integer methodPointer) {
		heap.getPointer(methodPointer) == 0
	}

	static String getConstantPoolValue(Heap heap, Integer classPtr, Integer constPoolPtr) {
		heap.getString(classPtr + constPoolPtr)
	}

	static List findMethodWithSuper(Heap heap, Integer ptr, String signature, ClassLoader classLoader) {
		def methodPtr = findMethod(heap,ptr,signature)
		while(methodPtr == null && getName(heap,ptr) != Compilator.DEFAULT_PARENT){
			String parentName = getParent(heap,ptr)
			ptr = classLoader.findClass(heap,parentName)
			methodPtr = findMethod(heap,ptr,signature)
		}
		[ptr, methodPtr]
	}
}