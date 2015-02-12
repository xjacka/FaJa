package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 12. 2. 2015.
 */
class NumberCreation implements Expression {
	Integer number
	Expression memberAccess = null

	public NumberCreation(Integer number){
		this.number = number
	}
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_NUM
		inst.paramVal = number
		List<PrecompiledInstruction> result = [inst]
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		return result
	}


	@Override
	String toString(){
		String res = number
		if(memberAccess){
			res+= memberAccess.toString()
		}
		res
	}
}
