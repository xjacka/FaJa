package faJa.compilator.evaluation

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledClosure
import faJa.PrecompiledInstruction
import faJa.compilator.LocalVariables
import faJa.compilator.MethodCompiler
import faJa.compilator.parser.ClosureParser
import faJa.compilator.parser.Code
import faJa.compilator.parser.Parser

/**
 * Created by Kamil on 12. 2. 2015.
 */
class ClosureCreation implements Expression{
	List<String> args
	Code code
	public ClosureCreation(List<String> args, Code code){
		this.args = args
		this.code = code
	}

	@Override
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals) {
		List<String> closureArgList = locals.asList()
		List<PrecompiledInstruction> result = []
		// load context on stack
		closureArgList.each { localName ->
			PrecompiledInstruction load = new PrecompiledInstruction()
			load.instruction = Instruction.LOAD
			load.paramVal = locals.findIndexByName(localName)
		}
		closureArgList.addAll(1, args)
		List<PrecompiledInstruction> bytecode = new MethodCompiler().compileMethod(closureArgList,new ClosureParser(), code)
		PrecompiledClosure precompiledClosure = new PrecompiledClosure()
		precompiledClosure.argsCount = args.size()
		precompiledClosure.instructions = bytecode
		Integer closureIdx = classFile.closures.size()
		classFile.closures.add(precompiledClosure)

		def initClosureInst = new PrecompiledInstruction()
		initClosureInst.instruction = Instruction.INIT_CLOSURE
		initClosureInst.paramVal = closureIdx

		result.add(initClosureInst)

		result
	}

	@Override
	String toString(){
		String res = '{' + args.join(',') + ' | ..closurebody.. }'
		res
	}
}
