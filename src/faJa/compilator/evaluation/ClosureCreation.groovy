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
		List<PrecompiledInstruction> result = []
		List<String> closureArgList
		// load context on stack
		closureArgList.each { localName ->
			PrecompiledInstruction load = new PrecompiledInstruction()
			load.instruction = Instruction.LOAD
			load.paramVal = locals.findIndexByName(localName)
		}
		closureArgList.addAll(1, args)

		PrecompiledClosure precompiledClosure = new PrecompiledClosure()
		precompiledClosure.argsCount = args.size()

		precompiledClosure.instructions = []
		body.each {
			precompiledClosure.instructions.add(it.eval(classFile, locals))
		}


		Integer closureIdx = classFile.closures.size()
		classFile.closures.add(precompiledClosure)

		def initClosureInst = new PrecompiledInstruction()
		initClosureInst.instruction = Instruction.INIT_CLOSURE
		initClosureInst.paramVal = closureIdx

		result.add(initClosureInst)

		result
	}

	@Override
	List<PrecompiledInstruction> argEval(ClassFile classFile, LocalVariables locals) {
		[] // comile args
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
