package faJa

import faJa.helpers.ByteHelper

class PrecompiledMethod {
	List<PrecompiledInstruction> instructions = []

	def toBytecode(List<Integer> constPoolIndexes){
		List<Byte> bytes = []
		instructions.each{ pi ->
			bytes << pi.instruction.id
			bytes.addAll(pi.constructParams(constPoolIndexes))
		}
		def bytecodeSize = bytes.size()
		bytes.addAll(0, ByteHelper.IntegerTo2Bytes(bytecodeSize))
		bytes.toArray()
	}
}
