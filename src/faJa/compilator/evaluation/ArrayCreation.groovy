package faJa.compilator.evaluation

import faJa.ClassFile
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
		// todo array creation
		[]
	}

	@Override
	String toString(){
		String res = '[ ' + args.join(', ')+ ' ]'
		if(memberAccess){
			res+= memberAccess.toString()
		}
	}
}
