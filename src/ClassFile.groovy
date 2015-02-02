class ClassFile {

	def constantPool = []
	def fields = []
	def methods = []

	def toByteCode(){
		Byte [] bytes

		bytes[0] = 0
		bytes[1] = 0

		constantPool.collect { String it ->
			def length = it.bytes.length
			bytes << ByteHelper.IntegerTo2Bytes(length)
			bytes << it.bytes
			length + 1
		}

	}

}
