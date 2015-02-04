package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

class SystemIOInit extends BaseInit{
	SystemIOInit(){
		classFile = new ClassFile()
		classFile.constantPool = [
				'SystemIO',
				'Object',
				'writeToFile(2)',
				'readFromFile(2)',
				'out(1)'
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

	}
}
