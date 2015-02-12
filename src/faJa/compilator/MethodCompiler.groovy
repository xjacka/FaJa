package faJa.compilator

import faJa.ClassFile
import faJa.PrecompiledMethod
import faJa.exceptions.CompilerException

/**
 * Created by Kamil on 11. 2. 2015.
 */
class MethodCompiler {
	ClassFile classFile

	def addSignitureToClassFile(String signature){
		def signitureIndex = classFile.constantPool.size()
		if(methods.contains(signature)) {
			throw new CompilerException('method "' + signature +'" already exists in class ' + classFile.constantPool[0])
		}
		methods.add(signature)
		classFile.constantPool.add(signature)
		signitureIndex
	}
	// parse method body without start and end keyword
	def compileMethod(String signature, List<String> argList, List<String> code){
		def method = new PrecompiledMethod()

		Integer signitureIndex = addSignitureToClassFile(signature)
		method.signatureIndex = signitureIndex

		LocalVariables locals = new LocalVariables()
		locals.addLocalVariables(argList)


		for(int i=0; i < code.size();){
			def result = compileLine(code[i], locals, classFile.constantPool)
			if(result){
				method.instructions.addAll(result)
				i++
			}
			else{
				result = compileClosure(code, i, classFile, createArgsForClosure(definitions , argList),  locals)
				method.instructions.addAll(result[0])
				i = result[1]

			}
		}
		classFile.methods.add(method)
	}

	def initLocals(){

	}

}
