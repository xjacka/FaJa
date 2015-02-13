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
		List<PrecompiledInstruction> result = []
		if(memberAccess){
			result.addAll(memberAccess.argEval(classFile, locals))
		}

		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_NUM
		inst.paramVal = classFile.constantPool.size()
		classFile.constantPool.add(number)
		result.add(inst)
		
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		return result
	}

	@Override
	List<PrecompiledInstruction> argEval(ClassFile classFile, LocalVariables locals) {
		[] // comile args
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
