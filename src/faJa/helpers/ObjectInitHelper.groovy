package faJa.helpers

class ObjectInitHelper {

	static byte [] createBool(Integer classPtr, boolean value){
		def bytes = ByteHelper.IntegerTo2Bytes(classPtr)
		if(value){
			bytes = [bytes[0], bytes[1], 1]
		}else {
			bytes = [bytes[0], bytes[1], 0]
		}
		bytes.toArray()
	}

	static byte [] createNumber(Integer classPtr, Integer value){
		def bytes = ByteHelper.IntegerTo2Bytes(classPtr)
		def number = ByteHelper.IntegerTo4Bytes(value)

		[bytes[0], bytes[1], number[0],number[1],number[2],number[3]].toArray()
	}

}
