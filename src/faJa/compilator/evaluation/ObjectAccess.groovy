package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 11. 2. 2015.
 */
class ObjectAccess implements Expression {
	String varName
	Expression memberAccess = null

	public ObjectAccess(String varName){
		this.varName = varName
	}
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List<PrecompiledInstruction> result = []
		result.addAll(loadObject(locals))
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile,locals))
		}
		result
	}

	private List<PrecompiledInstruction> loadObject(LocalVariables locals){
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.LOAD
		inst.paramVal = locals.findIndexByName(varName)
		[inst]
	}

	@Override
	String toString(){
		String res = varName
		if(memberAccess){
			res+= memberAccess.toString()
		}
		res
	}
}
