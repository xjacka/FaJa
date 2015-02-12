package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/** todo
 * Created by Kamil on 11. 2. 2015.
 */
class Assigment implements Expression{
	String assignee
	Expression assigned

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List result = assigned.eval(classFile)
		result.addAll(createAssignmentInstructions(classFile))
		return result
	}

	private List<PrecompiledInstruction> createAssignmentInstructions(ClassFile classFile){

	}
}
