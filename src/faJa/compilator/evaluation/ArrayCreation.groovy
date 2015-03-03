package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.exceptions.CompilerException

class ArrayCreation  implements Expression{
	List<String> args
	Expression memberAccess = null

	public ArrayCreation(List<String> args){
		this.args = args.findAll{ it.trim() != ''}
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List<PrecompiledInstruction> result = []
		result.addAll(initArray())
		if(memberAccess){
			result.addAll(memberAccess.eval(classFile, locals))
		}
		result
	}

	List<PrecompiledInstruction> initArray(){
		if(!args.empty){
			throw new CompilerException('current supported only empty array initialization')
		}
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT_ARRAY
		[inst]
	}

	@Override
	String toString(){
		String res = '[ ' + args.join(', ')+ ' ]'
		if(memberAccess){
			res+= memberAccess.toString()
		}
	}
}
