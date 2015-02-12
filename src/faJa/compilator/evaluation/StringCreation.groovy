package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 12. 2. 2015.
 */
class StringCreation {
	String value
	Expression memberAccess = null
	public StringCreation(String value){
		this.value = value
	}
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_NUM
		inst.paramVal = classFile.constantPool.size()
		classFile.constantPool.add(value)
		List<PrecompiledInstruction> result = [inst]
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
