package faJa

import faJa.helpers.ByteHelper

class PrecompiledMethod {
	List<PrecompiledInstruction> instructions = []
	Integer signatureIndex

	def toBytecode(List<Integer> constPoolIndexes){
		List<Byte> bytes = []
		bytes.addAll(ByteHelper.IntegerTo2Bytes(constPoolIndexes.get(signatureIndex)))
		instructions.each{ pi ->
			bytes << pi.instruction.id
			bytes.addAll(pi.constructParams(constPoolIndexes))
		}
		def bytecodeSize = bytes.size()
		bytes.addAll(0, ByteHelper.IntegerTo2Bytes(bytecodeSize))
		bytes.toArray()
	}
}
