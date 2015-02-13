package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 11. 2. 2015.
 */
class MethodCall implements Expression{
	List<Expression> args = []
	String methodName
	Expression nextMemberAccess = null

	public MethodCall(String methodName){
		this.methodName = methodName
	}
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List<PrecompiledInstruction> result = []
		PrecompiledInstruction inst = new PrecompiledInstruction()
		inst.instruction = Instruction.INVOKE
		inst.paramVal = classFile.constantPool.add(signature)

		args.each{
			result.addAll(it.eval(classFile, locals))
		}

		result.add(inst)

		if(nextMemberAccess){
			result.addAll(nextMemberAccess.eval(classFile, locals))
		}

		return result
	}

	String getSignature(){
		methodName + '(' + args.size() + ')'
	}


	@Override
	String toString(){
		String res = '.' + methodName + '(' + args.join(',') + ')'
		if(nextMemberAccess){
			res+= nextMemberAccess.toString()
		}
		res
	}
}
