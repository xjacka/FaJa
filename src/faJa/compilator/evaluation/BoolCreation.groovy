package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

class BoolCreation implements Expression{
	String bool
	Expression memberAccess = null

	public BoolCreation(String  bool){
		this.bool = bool
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_BOOL
		inst.paramVal = classFile.constantPool.add(bool)

		List<PrecompiledInstruction> result = []
		result.add(inst)
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		return result
	}

	@Override
	String toString(){
		String res = bool
		if(memberAccess){
			res+= memberAccess.toString()
		}
		res
	}
}
