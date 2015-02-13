package faJa.initializators

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.PrecompiledMethod

class SystemIOInit extends BaseInit{
	SystemIOInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
				'SystemIO',
				'Object',
				'writeToFile(2)',
				'readFromFile(1)',
				'out(1)',
				'inString(0)',
				'inNumber(0)',
				'inBool(0)'

		]
		classFile.fields = []

//		Method writeToFile(2) - Native
		def writeToFile = new PrecompiledMethod()
		writeToFile.signatureIndex = 2
		classFile.methods.add(writeToFile)

//		Method readFromFile(2) - Native
		def readFromFile = new PrecompiledMethod()
		readFromFile.signatureIndex = 3
		classFile.methods.add(readFromFile)

//		Method out(1) - Native
		def out = new PrecompiledMethod()
		out.signatureIndex = 4
		classFile.methods.add(out)

//		Method inString(0) - Native
		def inputS = new PrecompiledMethod()
		inputS.signatureIndex = 5
		classFile.methods.add(inputS)

//		Method inNumber(0) - Native
		def inputN = new PrecompiledMethod()
		inputN.signatureIndex = 6
		classFile.methods.add(inputN)

//		Method inBoolean(0) - Native
		def inputB = new PrecompiledMethod()
		inputB.signatureIndex = 7
		classFile.methods.add(inputB)
	}
}
