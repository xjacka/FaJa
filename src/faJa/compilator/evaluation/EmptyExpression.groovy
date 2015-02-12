package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 12. 2. 2015.
 */
class EmptyExpression implements Expression{
	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		// returns nothing
		[]
	}
}
