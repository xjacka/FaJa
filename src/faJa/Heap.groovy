package faJa

import faJa.helpers.ByteHelper

class Heap {
	byte [] heap
	public static final SLOT_SIZE = 2
	// nahraje na volne misto pole bytu
	def load(Byte [] bytes){

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
