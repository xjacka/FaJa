package faJa.helpers

class ByteHelper {

	static byte [] IntegerTo4Bytes(Integer i){
		return [ (byte)(i >>> 24),(byte)(i >>> 16),(byte)(i >>> 8), (byte)i].toArray()
	}

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

	static String bytesToString(byte [] bytes,Integer ptr){
		def length = ByteHelper.bytesToIntAt(bytes, ptr)
		if(length == 0)
			return  ""
		def start = ptr+2
		def end = start + length - 1
		def stringBytes = bytes[ start..end ].toArray() as byte[]
		new String(stringBytes)
	}

	static Integer bytesToNumber(byte[] bytes, int ptr) {
		int i = 0;
		i += unsignedValue(bytes[ptr])
		i = i << 8
		i += unsignedValue(bytes[ptr+1])
		i = i << 8
		i += unsignedValue(bytes[ptr+2])
		i = i << 8
		i += unsignedValue(bytes[ptr+3])
		i
	}
}