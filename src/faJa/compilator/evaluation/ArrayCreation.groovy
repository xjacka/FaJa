package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 13. 2. 2015.
 */
class ArrayCreation  implements Expression{
	List<String> args
	Expression memberAccess = null

	public ArrayCreation(List<String> args){
		this.args = args
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List<PrecompiledInstruction> result = []
		if(memberAccess){
			result.addAll(memberAccess.argEval(classFile, locals))
		}
		result.addAll(initArray())
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		result
	}
	List<PrecompiledInstruction> initArray(){
		// todo load args
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_ARRAY
		[inst]
	}
	@Override
	List<PrecompiledInstruction> argEval(ClassFile classFile, LocalVariables locals) {
		[] // comile args
	}


	@Override
	String toString(){
		String res = '[ ' + args.join(', ')+ ' ]'
		if(memberAccess){
			res+= memberAccess.toString()
		}
	}
}
