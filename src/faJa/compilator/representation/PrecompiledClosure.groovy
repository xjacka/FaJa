package faJa.compilator.representation

import faJa.helpers.ByteHelper

class PrecompiledClosure {

	Integer argsCount
	Integer localsSize
	List<PrecompiledInstruction> instructions

	byte [] toBytecode(List constPoolIndexes){
		List<Byte> bytes = []
		bytes.addAll(ByteHelper.IntegerTo2Bytes(argsCount))
		bytes.addAll(ByteHelper.IntegerTo2Bytes(localsSize))
		instructions.each{ pi ->
			bytes << pi.instruction.id
			bytes.addAll(pi.constructParams(constPoolIndexes))
		}

		def bytecodeSize = bytes.size()
		bytes.addAll(0, ByteHelper.IntegerTo2Bytes(bytecodeSize))
		bytes.toArray()
	}
}
