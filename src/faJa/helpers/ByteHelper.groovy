package faJa.helpers

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

	static int bytesToIntAt(byte [] bytes, int idx){
		int i = 0;
		i += unsignedValue(bytes[idx])
		i = i << 8
		i += unsignedValue(bytes[idx+1])
		i
	}

	static int bytesToInt(byte b0,byte b1){
		int i = 0;
		i += unsignedValue(b0)
		i = i << 8
		i += unsignedValue(b1)
		i
	}

	static int unsignedValue(byte b){
		b & 0xFF
	}

	static Byte [] IntegerTo2Bytes(Integer i){
		return [ (byte)(i >>> 8), (byte)i].toArray()
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