package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 12. 2. 2015.
 */
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
		inst.paramVal = classFile.constantPool.size()
		classFile.constantPool.add(bool)
		List<PrecompiledInstruction> result = [inst]
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
