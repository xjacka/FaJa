package faJa.compilator.representation

import faJa.exceptions.InstructionException
import faJa.helpers.ByteHelper
import faJa.interpreter.Instruction

/**
 *              CLOSURE
 * ++==============================++
 * ||        closure_size_2        ||
 * ++------------------------------++
 * ||      arguments_count_2       ||
 * ++------------------------------++
 * ||          xxxxxxx             || <- bytecode
 * ++==============================++
 */
class PrecompiledInstruction {
	Instruction instruction
	Integer paramVal

	List<Byte> constructParams(List<Integer> constPoolIndexes) {
		if(instruction.params != 0 && paramVal == null){
			throw new InstructionException("Instruction " + instruction.name() + " must have some parametr")
		}
		if(instruction.params == 0){
			return []
		}
		if(instruction.params == 1){
			return [paramVal.byteValue()]
		}
		if(instruction.params == 2){
			if(paramVal >= constPoolIndexes.size()){
				throw new InstructionException('parameter value out of constant pool range')
			}
			return ByteHelper.IntegerTo2Bytes(constPoolIndexes.get(paramVal))
		}
		throw new InstructionException('unsupported number of parameters')
	}
}
