package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 12. 2. 2015.
 */
class FieldAccess implements Expression{
	String fieldName
	Expression nextMemberAccess = null
	FieldAccess(String fieldName){
		this.fieldName = fieldName
	}
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.GETFIELD
		inst.paramVal = classFile.constantPool.size()
		classFile.constantPool.add(fieldName)
		List<PrecompiledInstruction> result = [inst]
		if(nextMemberAccess){
			result.addAll(nextMemberAccess.eval(classFile, locals))
		}
		result
	}
}
