package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables
import groovy.json.StringEscapeUtils

class StringCreation implements Expression {
	String value
	Expression memberAccess = null

	public StringCreation(String value){
		this.value = StringEscapeUtils.unescapeJava(value)
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_STRING
		inst.paramVal = classFile.constantPool.add(value)

		List<PrecompiledInstruction> result = []
		result.add(inst)
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		return result
	}

	@Override
	String toString(){
		String res = '"' + value +'"'
		if(memberAccess){
			res+= memberAccess.toString()
		}
		res
	}
}
