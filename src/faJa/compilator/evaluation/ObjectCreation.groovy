package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.compilator.Compiler
/**
 * Created by Kamil on 12. 2. 2015.
 */
class ObjectCreation implements Expression{
//	static Map<String , Instruction> buildingClassInit
//	static {
//		buildingClassInit = [:]
//		buildingClassInit.put(Compiler.NUMBER_CLASS, Instruction.INIT_NUM)
//		buildingClassInit.put(Compiler.CLOSURE_CLASS, Instruction.INIT_CLOSURE)
//		buildingClassInit.put(Compiler.BOOL_CLASS, Instruction.INIT_BOOL)
//		buildingClassInit.put(Compiler.STRING_CLASS, Instruction.INIT_STRING)
//	}

	String className

	MethodCall methodCall = null

	ObjectCreation(String className){
		this.className = className
	}
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
//		Instruction buildinClassInitInst = buildingClassInit.get(className)
//		if(buildingClassInit){}
		PrecompiledInstruction inst  = new PrecompiledInstruction()
		inst.instruction = Instruction.INIT
		inst.paramVal = classFile.constantPool.size()
		classFile.constantPool.add(className)
		List<PrecompiledInstruction> result = [inst]
		if(methodCall){
			result.addAll(methodCall.eval(classFile, locals))
		}
		result
	}

}
