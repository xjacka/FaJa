package faJa.compilator.representation

import faJa.helpers.ByteHelper

class PrecompiledClosure {

	Integer argsCount
	List<PrecompiledInstruction> instructions

	byte [] toBytecode(List constPoolIndexes){
		List<Byte> bytes = []
		bytes.addAll(ByteHelper.IntegerTo2Bytes(argsCount))
		instructions.each{ pi ->
			bytes << pi.instruction.id
			bytes.addAll(pi.constructParams(constPoolIndexes))
		}

		def bytecodeSize = bytes.size()
		bytes.addAll(0, ByteHelper.IntegerTo2Bytes(bytecodeSize))
		bytes.toArray()
	}
}
