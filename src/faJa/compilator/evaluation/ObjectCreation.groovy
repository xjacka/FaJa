package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

class ObjectCreation implements Expression {

//	static Map<String , Instruction> buildingClassInit
//	static {
//		buildingClassInit = [:]
//		buildingClassInit.put(Compiler.NUMBER_CLASS, Instruction.INIT_NUM)
//		buildingClassInit.put(Compiler.CLOSURE_CLASS, Instruction.INIT_CLOSURE)
//		buildingClassInit.put(Compiler.BOOL_CLASS, Instruction.INIT_BOOL)
//		buildingClassInit.put(Compiler.STRING_CLASS, Instruction.INIT_STRING)
//	}

	String className
	Expression memberAccess = null

	ObjectCreation(String className){
		this.className = className
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
//		Instruction buildinClassInitInst = buildingClassInit.get(className)
//		if(buildingClassInit){}
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
