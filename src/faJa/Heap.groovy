package faJa

import faJa.compilator.Compiler
import faJa.helpers.ByteHelper
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper

class Heap {

	byte [] heap = new byte[65536]
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

	Integer getUnsignedByte(Integer ptr){
		ByteHelper.unsignedValue(heap[ptr])
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

	String stringFromStringObject(Integer ptr){
		getString(ptr + Heap.SLOT_SIZE)
	}

	def getNumber(Integer ptr){
		ByteHelper.bytesToNumber(heap, ptr)
	}

	def intFromNumberObject(Integer ptr){
		getNumber(ptr + Heap.SLOT_SIZE)
	}

	def createObject(Integer classPtr){
		Integer objectSize = ClassAccessHelper.getObjectSize(this, classPtr)
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
		byte [] stringLength = ByteHelper.IntegerTo2Bytes(bytes.length)
		heap[insertIndex++] = stringLength[0]
		heap[insertIndex++] = stringLength[1]

		bytes.length.times { i->
			heap[insertIndex++] = bytes[i]
		}

		objectPtr
	}

	Integer createNumber(Integer numberClassPtr, Integer newVal){
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
		heap[insertIndex++] = newVal

		objectPtr
	}

	byte[] getBytes(Integer bytecodePtr, Integer bytecodeSize) {
		byte [] bytecode = new byte[bytecodeSize]
		bytecodeSize.times{ i ->
			bytecode[i] = heap[bytecodePtr + i]
		}
		bytecode
	}

	Boolean boolFromBoolObject(Integer ptr) {
		heap[ptr + Heap.SLOT_SIZE] == 0 ? false : true
	}

	Integer createClosure(Integer closureClassPtr, Integer initClassPtr, Integer closureIdx) {
		byte [] bytesOfClosureClass = ByteHelper.IntegerTo2Bytes(closureClassPtr)
		byte [] bytesOfInitClass = ByteHelper.IntegerTo2Bytes(initClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClosureClass[0]
		heap[insertIndex++] = bytesOfClosureClass[1]
		heap[insertIndex++] = bytesOfInitClass[0]
		heap[insertIndex++] = bytesOfInitClass[1]
		heap[insertIndex++] = closureIdx.byteValue()

		objectPtr
	}

	Integer createArray(Integer arrayClassPtr, Integer size, Integer initializeObjectPointer) {
		// creates pointer to Array class
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo2Bytes(arrayClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]

		// creates index of last array value
		byte [] lastInsertedIndex = ByteHelper.IntegerTo2Bytes(0)
		heap[insertIndex++] = lastInsertedIndex[0]
		heap[insertIndex++] = lastInsertedIndex[1]

		// save pointer to pointer to array
		Integer saveInsertIndex = insertIndex
		insertIndex += Heap.SLOT_SIZE
		byte [] arrayPointer = ByteHelper.IntegerTo2Bytes(createArrayObject(size,initializeObjectPointer))
		heap[saveInsertIndex] = arrayPointer[0]
		heap[saveInsertIndex+1] = arrayPointer[1]

		objectPtr
	}

	Integer createArrayObject(Integer size, Integer initializeObjectPointer) {
		Integer objectPtr = insertIndex

		// insert length of initialized array
		byte [] arraySize = ByteHelper.IntegerTo2Bytes(size)
		heap[insertIndex++] = arraySize[0]
		heap[insertIndex++] = arraySize[1]

		// insert pointers to null object into array
		size.times {
			setPointer(insertIndex,initializeObjectPointer)
			insertIndex += Heap.SLOT_SIZE
		}

		objectPtr
	}
}
