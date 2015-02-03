package faJa

import faJa.helpers.ByteHelper

class ClassFile {

	static CONST_POOL_START = 4
	static SLOT_SIZE = 2

	def constantPool = []
	def fields = []
	List<PrecompiledMethod> methods = []

	def toByteCode(){
		List<Byte> bytes = []

		// class size init
		setClassSize(bytes, 0)

		// constant pool size init
		setConstantPoolSize(bytes, 0)

		// constant pool
		def lengths = constantPool.collect { String it ->
			def length = it.bytes.length
			bytes.addAll(ByteHelper.IntegerTo2Bytes(length))
			bytes.addAll(it.bytes)
			length + SLOT_SIZE
		}
		def constPoolSize = lengths.sum(0)
		def prefixSum = createPrefixSum(lengths)

		// constant pool size set
		setConstantPoolSize(bytes, constPoolSize)

		// fields size
		def fieldsSize = fields.size() * SLOT_SIZE
		setBytes(bytes, CONST_POOL_START + constPoolSize, fieldsSize)


		// fields
		fields.each{ Integer f ->
			def ptr = prefixSum[f]
			bytes.addAll(ByteHelper.IntegerTo2Bytes(ptr + CONST_POOL_START))
		}

		// method size init (constPoolStart + constPoolSize + fieldsSizeSlot + fieldsSize)
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE, 0 )

		// method
		def methodLengths = methods.collect{ m ->
			byte[] methodBytecode = m.toBytecode()
			bytes.addAll(methodBytecode)
			methodBytecode.length
		}
		def methodsSize = methodLengths.sum(0)

		// method size
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize, methodsSize )

		// class size ( constPoolSizeSlot + constPoolSize + fieldSizeSlot + fieldSize + methodsSizeSlot + methodsSize)
		setClassSize(bytes, 3*SLOT_SIZE + constPoolSize + fieldsSize + methodsSize)

		bytes.toArray() as byte []
	}

	// -------------------- PRIVATE -----------------------------------

	def createPrefixSum(List<Integer> lengths) {
		lengths.add(0,0)
		def prefixSum = []
		lengths.inject(0) { acc, len ->
			prefixSum << (acc + len)
			acc + len
		}
		prefixSum
	}

	def setClassSize(List bytes, int size){
		setBytes(bytes, 0, size)
	}

	def setConstantPoolSize(List bytes, int size){
		setBytes(bytes, 2, size)
	}

	def setBytes(List bytes,int start,int value){
		byte[] byteVal = ByteHelper.IntegerTo2Bytes(value)
		bytes[start] = byteVal[0]
		bytes[start+1] = byteVal[1]
	}
}
