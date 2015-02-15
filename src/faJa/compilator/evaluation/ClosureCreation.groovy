package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledClosure
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.compilator.parser.Code
import faJa.compilator.parser.Parser

/**
 * Created by Kamil on 12. 2. 2015.
 */
class ClosureCreation implements Expression{
	List<String> args
	List<Expression> body

	public ClosureCreation(List<String> args, List<String> body){
		this.args = args
		Code code = new Code(body)
		this.body = new Parser().parseCode(code)

	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		LocalVariables closureArgList = new LocalVariables()

		// load locals from environment to closure locals
		locals.asList().each {
			closureArgList.addLocalVariable(it)
		}
		// load closure arguments to locals
		args.each {
			closureArgList.addLocalVariable(it)
		}


		PrecompiledClosure precompiledClosure = new PrecompiledClosure()
		precompiledClosure.argsCount = args.size()
		precompiledClosure.parentLocalsSize = locals.count()
		precompiledClosure.instructions = []
		body.each {
			precompiledClosure.instructions.addAll(it.eval(classFile, closureArgList))
		}


		Integer closureIdx = classFile.closures.size()
		classFile.closures.add(precompiledClosure)

		def initClosureInst = new PrecompiledInstruction()
		initClosureInst.instruction = Instruction.INIT_CLOSURE
		initClosureInst.paramVal = closureIdx

		[initClosureInst]
	}

	@Override
	String toString(){
		String res = '{' + args.join(',') + ' | \n'
		body.each {
			res+= it.toString() + '\n'
		}

		res += '}'
		res
	}
}
