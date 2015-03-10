package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

class ObjectCreation implements Expression {

	String className
	Expression memberAccess = null

	ObjectCreation(String className){
		this.className = className
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		PrecompiledInstruction inst  = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT
		inst.paramVal = classFile.constantPool.add(className)

		List<PrecompiledInstruction> result = []
		result.add(inst)
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		result
	}

	@Override
	String toString(){
		String res = className + '.new'
		if(memberAccess){
			res+= memberAccess.toString()
		}
		res
	}
}
