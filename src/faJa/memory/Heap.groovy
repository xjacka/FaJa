package faJa.memory

import faJa.helpers.ByteHelper
import faJa.helpers.ClassAccessHelper
import faJa.interpreter.ClassLoader

class Heap {

	public static final Integer HEAP_SIZE = 5500000
	public static final SLOT_SIZE = 2
	public static final HEAP_POINTER_SIZE = 4
	
	public static final ARRAY_SIZE = HEAP_POINTER_SIZE + SLOT_SIZE + HEAP_POINTER_SIZE
	public static final BOOL_SIZE = HEAP_POINTER_SIZE + 1
	public static final CLOSURE_SIZE = HEAP_POINTER_SIZE + HEAP_POINTER_SIZE + 1
	public static final NUMBER_SIZE = HEAP_POINTER_SIZE + 4
	public static final STRING_HEAD_SIZE = HEAP_POINTER_SIZE + SLOT_SIZE
	public static final THREAD_SIZE = HEAP_POINTER_SIZE

	public Integer heapStart = 1
	public Integer heapEnd = HEAP_SIZE / 2
	
	byte [] heap = new byte[HEAP_SIZE]
	Integer insertIndex = 1
	Map methodCache = [:]
	ClassLoader classLoader
	
	// nahraje na volne misto pole bytu
	synchronized Integer load(byte [] bytes){
		heapOverflown(bytes.size())
		Integer pointer = insertIndex
		bytes.each { b ->
			heap[insertIndex++] = b
		}
		pointer
	}

	def getByte(Integer ptr){
		heap[ptr]
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
		if(heapOverflown(objectSize)){
			classPtr = getPointer(classPtr)
		}
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
		if(heapOverflown(STRING_HEAD_SIZE + value.length())){
			stringClassPtr = getPointer(stringClassPtr)
		}
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
		if(heapOverflown(Heap.NUMBER_SIZE)){
			numberClassPtr = getPointer(numberClassPtr)
		}
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
		if(heapOverflown(Heap.BOOL_SIZE)){
			boolClassPtr = getPointer(boolClassPtr)
		}
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
		if(heapOverflown(Heap.CLOSURE_SIZE)){
			closureClassPtr = getPointer(closureClassPtr)
			initClassPtr = getPointer(initClassPtr)
		}
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
		if(heapOverflown(Heap.ARRAY_SIZE + (Heap.SLOT_SIZE + Heap.HEAP_POINTER_SIZE * size))){
			initializeObjectPointer = getPointer(initializeObjectPointer)
			arrayClassPtr = getPointer(arrayClassPtr)
		}
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
		if(heapOverflown(Heap.SLOT_SIZE + Heap.HEAP_POINTER_SIZE * size)) {
			initializeObjectPointer = getPointer(initializeObjectPointer)
		}
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
	
	synchronized private Boolean heapOverflown(Integer newObjectSize){
		if(insertIndex + newObjectSize < heapEnd){
			return false
		}

		heapStart = heapStart == 1 ? heapEnd + 1 : 1
		heapEnd = heapStart > heapEnd ? HEAP_SIZE - 1 : HEAP_SIZE / 2
		insertIndex = heapStart

		new GarbageCollector(classLoader,this).run()
		return true
	}
}
