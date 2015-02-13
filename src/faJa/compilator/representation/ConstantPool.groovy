package faJa.compilator.representation

import faJa.helpers.ByteHelper

class ConstantPool {
	List<String> constants

	public ConstantPool(){
		this.constants = []
	}

	Integer add(String constant){
		Integer idx = getIdx(constant)
		if(!idx){
			idx = constants.size()
			constants.add(constant)
		}
		idx
	}
	String get(Integer idx){
		constants[idx]
	}
	Integer getIdx(String constant){
		Integer idx = null
		constants.eachWithIndex { c, i ->
			if(c == constant){
				idx = i
			}
		}
		idx
	}


	byte[] toBytecode(){
		List<Byte> bytes = []
		// constant pool
		constants.each { String it ->
			def length = it.bytes.length
			bytes.addAll(ByteHelper.IntegerTo2Bytes(length))
			bytes.addAll(it.bytes)
		}
		Integer constPoolSize = size()

		setConstantPoolSize(bytes, constPoolSize)

		bytes.toArray()
	}

	@Override
	String toString(){
		StringBuilder sb = new StringBuilder()
		sb.append('ConstantPool:\n')
		constants.eachWithIndex { c , i ->
			sb.append('\t'+i + ': '+ c + '\n')
		}
		sb.toString()
	}

	def setConstantPoolSize(List bytes, Integer size){
		byte[] byteVal = ByteHelper.IntegerTo2Bytes(size)
		bytes.addAll(0, byteVal)
	}

	def createPrefixSum(List<Integer> lengths) {
		lengths.add(0,0)
		def prefixSum = []
		lengths.inject(0) { acc, len ->
			prefixSum << (acc + len)
			acc + len
		}
		prefixSum
	}

	def constantPoolIndexes(){
		def lengths = lengths()
		createPrefixSum(lengths).collect{ it + ClassFile.CONST_POOL_START}
	}

	def lengths(){
		constants.collect { String it ->
			def length = it.bytes.length
			length + ClassFile.SLOT_SIZE
		}
	}
	def size() {
		lengths().sum(0)
	}
}
