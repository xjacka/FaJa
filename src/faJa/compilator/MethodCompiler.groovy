package faJa.compilator

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.representation.PrecompiledMethod
import faJa.compilator.evaluation.Expression
import faJa.compilator.parser.Code
import faJa.compilator.parser.Parser

/**
 * Created by Kamil on 11. 2. 2015.
 */
class MethodCompiler {
	ClassFile classFile
	public  MethodCompiler(ClassFile classFile){
		this.classFile = classFile
	}
	def addSignitureToClassFile(String signature){
		def signitureIndex = classFile.constantPool.size()
//		if(methods.contains(signature)) {
//			throw new CompilerException('method "' + signature +'" already exists in class ' + classFile.constantPool[0])
//		}
//		methods.add(signature)
		classFile.constantPool.add(signature)
		signitureIndex
	}

	def compileMethod(List<String> argList,Parser parser,  Code code){
		LocalVariables locals = new LocalVariables()
		locals.addLocalVariables(argList)
		List<Expression> expressionList = parser.parseCode(code)
		List<PrecompiledInstruction> bytecode = []
		expressionList.each { Expression ex ->
			bytecode.addAll(ex.eval(classFile, locals))
		}
		return bytecode
	}
	// parse method body without start and end keyword
	def createMethod(String signature, List<String> argList, List<String> methodBody){
		def method = new PrecompiledMethod()

		Integer signitureIndex = addSignitureToClassFile(signature)
		method.signatureIndex = signitureIndex

		Code code = new Code(methodBody)
		method.instructions = compileMethod(argList, new Parser(), code)

		classFile.methods.add(method)
	}

}
