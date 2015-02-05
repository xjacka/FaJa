package faJa

import faJa.helpers.ByteHelper
import faJa.helpers.ClassAccessHelper

class Heap {

	byte [] heap = new byte[100000]
	Integer insertIndex = 0
	public static final SLOT_SIZE = 2

	// nahraje na volne misto pole bytu
	def load(byte [] bytes){
		def pointer = insertIndex
		bytes.each { b ->
			heap[insertIndex++] = b
		}
		pointer
	}


//	def setByte(Integer ptr, Integer newVal){
//		heap[ptr] = newVal.byteValue()
//	}

	def getByte(Integer ptr){
		heap[ptr] // should return unsigned ??
	}

	def setPointer(Integer ptr, Integer newVal){
		def bytes = ByteHelper.IntegerTo2Bytes(newVal)
		heap[ptr] = bytes[0]
		heap[ptr+1] = bytes[1]
	}

	Integer getPointer(Integer ptr){
		ByteHelper.bytesToIntAt(heap, ptr)
	}

	String getString(Integer ptr){
		ByteHelper.bytesToString(heap, ptr)
	}

	def getNumber(Integer ptr){
		ByteHelper.bytesToNumber(heap, ptr)
	}

	def createObject(Integer classPtr){
		Integer objectSize = ClassAccessHelper.getObjectSize(heap, classPtr)
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo2Bytes(classPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex] = bytesOfClassPtr[0]
		heap[insertIndex+1] = bytesOfClassPtr[1]
		insertIndex += objectSize

		objectPtr
	}

	Integer createString(Integer stringClassPtr, String value) {
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo2Bytes(stringClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]
		byte [] bytes = value.bytes
		bytes.length.times { i->
			heap[insertIndex++] = bytes[i]
		}

		objectPtr
	}

	def createNumber(Integer numberClassPtr, Integer newVal){
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo2Bytes(numberClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]

		def bytes = ByteHelper.IntegerTo4Bytes(newVal)
		heap[insertIndex++] = bytes[0]
		heap[insertIndex++] = bytes[1]
		heap[insertIndex++] = bytes[2]
		heap[insertIndex++] = bytes[3]

		objectPtr
	}

	Integer createBool(Integer boolClassPtr, Byte newVal) {
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo2Bytes(boolClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]
		heap[insertIndex++] = bytesOfClassPtr[2]

		objectPtr
	}

	byte[] getBytes(Integer bytecodePtr, Integer bytecodeSize) {
		byte [] bytecode = new byte[bytecodeSize]
		bytecodeSize.times{ i ->
			bytecode[i] = heap[bytecodePtr + i]
		}
		bytecode
	}
}
