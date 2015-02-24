package faJa.memory

import faJa.helpers.ByteHelper
import faJa.helpers.ClassAccessHelper

class Heap {

	byte [] heap = new byte[10000000]
	Integer insertIndex = 0
	public static final SLOT_SIZE = 2
	public static final HEAP_POINTER_SIZE = 4

	// nahraje na volne misto pole bytu
	synchronized Integer load(byte [] bytes){
		Integer pointer = insertIndex
		bytes.each { b ->
			heap[insertIndex++] = b
		}
		pointer
	}

	def getByte(Integer ptr){
		heap[ptr] // should return unsigned ??
	}

	Integer getUnsignedByte(Integer ptr){
		ByteHelper.unsignedValue(heap[ptr])
	}

	synchronized def setPointer(Integer ptr, Integer newVal){
		def bytes = ByteHelper.IntegerTo4Bytes(newVal)
		heap[ptr] = bytes[0]
		heap[ptr+1] = bytes[1]
		heap[ptr+2] = bytes[2]
		heap[ptr+3] = bytes[3]
	}

	Integer getPointer(Integer ptr){
		ByteHelper.bytesToNumber(heap, ptr)
	}

	Integer setSlot(Integer ptr, Integer newVal){
		def bytes = ByteHelper.IntegerTo2Bytes(newVal)
		heap[ptr] = bytes[0]
		heap[ptr+1] = bytes[1]
	}

	Integer getSlot(Integer ptr){
		ByteHelper.bytesToIntAt(heap, ptr)
	}

	String getString(Integer ptr){
		ByteHelper.bytesToString(heap, ptr)
	}

	String stringFromStringObject(Integer ptr){
		getString(ptr + Heap.HEAP_POINTER_SIZE)
	}

	def getNumber(Integer ptr){
		ByteHelper.bytesToNumber(heap, ptr)
	}

	def intFromNumberObject(Integer ptr){
		getNumber(ptr + Heap.HEAP_POINTER_SIZE)
	}

	synchronized def createObject(Integer classPtr){
		Integer objectSize = ClassAccessHelper.getObjectSize(this, classPtr)
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo4Bytes(classPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex] = bytesOfClassPtr[0]
		heap[insertIndex+1] = bytesOfClassPtr[1]
		heap[insertIndex+2] = bytesOfClassPtr[2]
		heap[insertIndex+3] = bytesOfClassPtr[3]
		insertIndex += objectSize

		objectPtr
	}

	synchronized Integer createString(Integer stringClassPtr, String value) {
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo4Bytes(stringClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]
		heap[insertIndex++] = bytesOfClassPtr[2]
		heap[insertIndex++] = bytesOfClassPtr[3]
		byte [] bytes = value.bytes
		byte [] stringLength = ByteHelper.IntegerTo2Bytes(bytes.length)
		heap[insertIndex++] = stringLength[0]
		heap[insertIndex++] = stringLength[1]

		bytes.length.times { i->
			heap[insertIndex++] = bytes[i]
		}

		objectPtr
	}

	synchronized Integer createNumber(Integer numberClassPtr, Integer newVal){
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo4Bytes(numberClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]
		heap[insertIndex++] = bytesOfClassPtr[2]
		heap[insertIndex++] = bytesOfClassPtr[3]

		def bytes = ByteHelper.IntegerTo4Bytes(newVal)
		heap[insertIndex++] = bytes[0]
		heap[insertIndex++] = bytes[1]
		heap[insertIndex++] = bytes[2]
		heap[insertIndex++] = bytes[3]

		objectPtr
	}

	synchronized Integer createBool(Integer boolClassPtr, Boolean newVal) {
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo4Bytes(boolClassPtr)
		Integer objectPtr = insertIndex
		Byte newByteVal = (byte) newVal ? 1 : 0
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]
		heap[insertIndex++] = bytesOfClassPtr[2]
		heap[insertIndex++] = bytesOfClassPtr[3]
		heap[insertIndex++] = newByteVal

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
		heap[ptr + Heap.HEAP_POINTER_SIZE] == 0 ? false : true
	}

	synchronized Integer createClosure(Integer closureClassPtr, Integer initClassPtr, Integer closureIdx) {
		byte [] bytesOfClosureClass = ByteHelper.IntegerTo4Bytes(closureClassPtr)
		byte [] bytesOfInitClass = ByteHelper.IntegerTo4Bytes(initClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClosureClass[0]
		heap[insertIndex++] = bytesOfClosureClass[1]
		heap[insertIndex++] = bytesOfClosureClass[2]
		heap[insertIndex++] = bytesOfClosureClass[3]
		heap[insertIndex++] = bytesOfInitClass[0]
		heap[insertIndex++] = bytesOfInitClass[1]
		heap[insertIndex++] = bytesOfInitClass[2]
		heap[insertIndex++] = bytesOfInitClass[3]
		heap[insertIndex++] = closureIdx.byteValue()

		objectPtr
	}

	synchronized Integer createArray(Integer arrayClassPtr, Integer size, Integer initializeObjectPointer) {
		// creates pointer to Array class
		byte [] bytesOfClassPtr = ByteHelper.IntegerTo4Bytes(arrayClassPtr)
		Integer objectPtr = insertIndex
		heap[insertIndex++] = bytesOfClassPtr[0]
		heap[insertIndex++] = bytesOfClassPtr[1]
		heap[insertIndex++] = bytesOfClassPtr[2]
		heap[insertIndex++] = bytesOfClassPtr[3]

		// creates index of last array value
		byte [] lastInsertedIndex = ByteHelper.IntegerTo2Bytes(0)
		heap[insertIndex++] = lastInsertedIndex[0]
		heap[insertIndex++] = lastInsertedIndex[1]

		// save pointer to pointer to array
		Integer saveInsertIndex = insertIndex
		insertIndex += Heap.HEAP_POINTER_SIZE
		byte [] arrayPointer = ByteHelper.IntegerTo4Bytes(createArrayObject(size,initializeObjectPointer))
		heap[saveInsertIndex] = arrayPointer[0]
		heap[saveInsertIndex+1] = arrayPointer[1]
		heap[saveInsertIndex+2] = arrayPointer[2]
		heap[saveInsertIndex+3] = arrayPointer[3]

		objectPtr
	}

	synchronized Integer createArrayObject(Integer size, Integer initializeObjectPointer) {
		Integer objectPtr = insertIndex

		// insert length of initialized array
		byte [] arraySize = ByteHelper.IntegerTo2Bytes(size)
		heap[insertIndex++] = arraySize[0]
		heap[insertIndex++] = arraySize[1]

		// insert pointers to null object into array
		size.times {
			setPointer(insertIndex,initializeObjectPointer)
			insertIndex += Heap.HEAP_POINTER_SIZE
		}

		objectPtr
	}
}
