package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
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
		if(memberAccess){
			result.addAll(memberAccess.argEval(classFile, locals))
		}
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
	List<PrecompiledInstruction> argEval(ClassFile classFile, LocalVariables locals) {
		[] // comile args
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
