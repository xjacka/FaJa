package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.exceptions.CompilerException

/**
 * Created by Kamil on 11. 2. 2015.
 */
class Assigment implements Expression{
	String assignee
	Expression assigned

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		if(!assigned){
			throw CompilerException('assigment without assigned value')
		}
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.STORE
		inst.paramVal = locals.findIndexByName(assignee)

		List result = assigned.eval(classFile)
		result.add(inst)
		return result
	}
}
