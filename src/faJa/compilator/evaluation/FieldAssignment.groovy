package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.exceptions.CompilerException

/**
 * Created by Kamil on 12. 2. 2015.
 */
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
		inst.paramVal = classFile.constantPool.size()
		classFile.constantPool.add(field)
		List<PrecompiledInstruction> result = assigned.eval(classFile, locals)
		result.add(inst)
	}
}
