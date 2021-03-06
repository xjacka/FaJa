package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.exceptions.CompilerException

class Assigment implements Expression{
	String assignee
	Expression assigned

	public Assigment(String assignee){
		this.assignee = assignee
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		if(!assigned){
			throw CompilerException('assigment without assigned value')
		}
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.STORE
		inst.paramVal = locals.findIndexByName(assignee)

		List result = assigned.eval(classFile, locals)
		result.add(inst)
		return result
	}

	@Override
	String toString(){
		assignee + ' <- ' + assigned.toString()
	}
}
