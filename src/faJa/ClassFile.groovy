package faJa

import faJa.helpers.ByteHelper

class ClassFile {

	static CONST_POOL_START = 4
	static SLOT_SIZE = 2

	def constantPool = []
	def fields = []
	List<PrecompiledMethod> methods = []

	byte [] toByteCode(){
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
		def constPoolIndexes = createPrefixSum(lengths).collect{ it + CONST_POOL_START}

		// constant pool size set
		setConstantPoolSize(bytes, constPoolSize)

		// fields size
		def fieldsSize = fields.size() * SLOT_SIZE
		setBytes(bytes, CONST_POOL_START + constPoolSize, fieldsSize)

		// fields
		fields.each{ Integer f ->
			def ptr = constPoolIndexes[f]
			bytes.addAll(ByteHelper.IntegerTo2Bytes(ptr))
		}

		// method size init (constPoolStart + constPoolSize + fieldsSizeSlot + fieldsSize)
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE, 0 )

		// method
		def methodLengths = methods.collect{ m ->
			byte[] methodBytecode = m.toBytecode(constPoolIndexes)
			bytes.addAll(methodBytecode)
			methodBytecode.length
		}
		def methodsSize = methodLengths.sum(0)

		// method size
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE, methodsSize )

		// class size ( constPoolSizeSlot + constPoolSize + fieldSizeSlot + fieldSize + methodsSizeSlot + methodsSize)
		setClassSize(bytes, 3*SLOT_SIZE + constPoolSize + fieldsSize + methodsSize)

		bytes.toArray() as byte []
	}

	@Override
	String toString(){
		StringBuilder sb = new StringBuilder()
		sb.append('className: ' + constantPool[0])
		sb.append('\n')
		sb.append('ConstantPool:\n')
		constantPool.eachWithIndex { c , i ->
			sb.append('\t'+i + ': '+ c + '\n')
		}
		sb.append('Fields:\n')
		fields.each{ Integer f ->
			sb.append('\t' +constantPool[f] + '\n')
		}
		sb.append('Methods:\n')
		methods.each{ method ->
			sb.append(constantPool[method.signatureIndex]+'\n')
			sb.append('\tbytecode:\n')
			method.instructions.each{ inst ->
				sb.append('\t\t'+ inst.instruction.toString() + ' ' + inst.paramVal +'\n')
			}
		}

		sb.toString()
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
