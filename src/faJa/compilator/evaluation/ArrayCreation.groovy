package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.exceptions.CompilerException

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
		if(!args.empty){
			throw CompilerException('current supported only empty array initialization')
		}
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
