package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.exceptions.CompilerException

class FieldAssignment implements Expression{
	String field
	Expression assigned = null

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		if(!assigned){
			throw CompilerException('assigment without assigned value')
		}
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.PUTFIELD
		inst.paramVal = classFile.constantPool.add(field)

		List<PrecompiledInstruction> result = assigned.eval(classFile, locals)
		result.add(inst)
		result
	}

	@Override
	String toString(){
		String res = ':' + field + ' <- '
		if(assigned){
			res+= assigned.toString()
		}
		res
	}
}
