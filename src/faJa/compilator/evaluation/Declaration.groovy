package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 11. 2. 2015.
 */
class Declaration implements Expression{
	String varName
	Assigment definition = null
	Declaration nextDeclaration = null

	public Declaration(String varName){
		this.varName = varName
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		locals.addLocalVariable(varName)
		List<PrecompiledInstruction> result = []
		if(definition){
			result.addAll(definition.eval(classFile, locals))
		}
		if(nextDeclaration){
			result.addAll(nextDeclaration.eval(classFile, locals))
		}
		result
	}



	@Override
	String toString(){
		'var ' + toStringWithoutVar()
	}

	String toStringWithoutVar(){
		String res = varName
		if(definition){
			res = definition.toString()
		}
		if(nextDeclaration){
			res+= ', ' + nextDeclaration.toStringWithoutVar()
		}
		res
	}
}
