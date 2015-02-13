package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
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
		List<PrecompiledInstruction> result = []
		if(nextMemberAccess){
			result.addAll(nextMemberAccess.argEval(classFile, locals))
		}
		result.add(inst)
		if(nextMemberAccess){
			result.addAll(nextMemberAccess.eval(classFile, locals))
		}
		result
	}
	@Override
	List<PrecompiledInstruction> argEval(ClassFile classFile, LocalVariables locals) {
		[] // comile args
	}
	@Override
	String toString(){
		String res = ':' + fieldName
		if(nextMemberAccess){
			res+= nextMemberAccess.toString()
		}
		res
	}
}
