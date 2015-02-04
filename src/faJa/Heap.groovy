package faJa

import faJa.helpers.ByteHelper

class Heap {

	byte [] heap = new byte[1000]
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

	def getByte(Integer ptr){
		heap[ptr] // should return unsigned ??
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
}
