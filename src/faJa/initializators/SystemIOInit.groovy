package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledMethod

class SystemIOInit extends BaseInit{
	SystemIOInit(){
		classFile = new ClassFile()
		classFile.constantPool.add('SystemIO')
		classFile.constantPool.add('Object')

		classFile.fields = []

//		Method writeToFile(2) - Native
		def writeToFile = new PrecompiledMethod()
		writeToFile.signatureIndex = classFile.constantPool.add('writeToFile(2)')
		classFile.methods.add(writeToFile)

//		Method readFromFile(2) - Native
		def readFromFile = new PrecompiledMethod()
		readFromFile.signatureIndex = classFile.constantPool.add('readFromFile(1)')
		classFile.methods.add(readFromFile)

//		Method out(1) - Native
		def out = new PrecompiledMethod()
		out.signatureIndex = classFile.constantPool.add('out(1)')
		classFile.methods.add(out)

//		Method inString(0) - Native
		def inputS = new PrecompiledMethod()
		inputS.signatureIndex = classFile.constantPool.add('inString(0)')
		classFile.methods.add(inputS)

//		Method inNumber(0) - Native
		def inputN = new PrecompiledMethod()
		inputN.signatureIndex = classFile.constantPool.add('inNumber(0)')
		classFile.methods.add(inputN)

//		Method inBoolean(0) - Native
		def inputB = new PrecompiledMethod()
		inputB.signatureIndex = classFile.constantPool.add('inBool(0)')
		classFile.methods.add(inputB)

		classFile.isSingleton = true
	}
}
