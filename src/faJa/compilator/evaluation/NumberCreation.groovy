package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

class NumberCreation implements Expression {
	Integer number
	Expression memberAccess = null

	public NumberCreation(Integer number){
		this.number = number
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List<PrecompiledInstruction> result = []

		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_NUM
		inst.paramVal = classFile.constantPool.add(number.toString())

		result.add(inst)

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
