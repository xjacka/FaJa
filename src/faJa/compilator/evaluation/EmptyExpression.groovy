package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledInstruction
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


	@Override
	String toString(){
		''
	}
}
