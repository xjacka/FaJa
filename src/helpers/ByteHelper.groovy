class ByteHelper {


//	static int bytesToInt(byte [] bytes){
//		int i = 0;
//		i += bytes[0]
//		i << 8
//		i += bytes[1]
//		i << 8
//		i += bytes[2]
//		i << 8
//		i += bytes[3]
//		i
//	}
//	static int bytesToInt(byte b0,byte b1, byte b2, byte b3){
//		int i = 0;
//		i += b0
//		i << 8
//		i += b1
//		i << 8
//		i += b2
//		i << 8
//		i += b3
//		i
//	}

	static int bytesToPointer(byte [] bytes){
		int i = 0;
		i += bytes[0]
		i << 8
		i += bytes[1]
		i
	}

	static int bytesToInt(byte b0,byte b1){
		int i = 0;
		i += b0
		i << 8
		i += b1
		i
	}

	static Byte [] IntegerTo2Bytes(Integer i){
		Byte [] bytes
		bytes[0] = (i / 256).byteValue()
		bytes[1] = (i % 256).byteValue()
	}

//	static storeIntToHeap(byte [] heap, int pos, int val ){
//		if(pos + 4 >= heap.length){
//			throw new HeapOverflowException()
//		}
//		byte [] bytes = [0,0,0,0]
//		byte [] byteVal = val.byteValue()
//		for(int i=byteVal.length; i; i--){
//			bytes[i] = byteVal[i]
//		}
//		heap[pos++] = bytes[0]
//		heap[pos++] = bytes[1]
//		heap[pos++] = bytes[2]
//		heap[pos] = bytes[3]
//	}
//	static String stringToBytes(){
//
//	}

}