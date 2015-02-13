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
				'innerOut(1)',
				'out(1)',
				'toS(0)'

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

//		Method innerOut(1) - Native
		def innerOut = new PrecompiledMethod()
		innerOut.signatureIndex = 4
		classFile.methods.add(innerOut)

//		Method out(1)
		def out = new PrecompiledMethod()
		out.signatureIndex = 5

		def instr = new PrecompiledInstruction()
		instr.instruction = Instruction.LOAD
		instr.paramVal = 1
		out.instructions.add(instr)

		instr = new PrecompiledInstruction()
		instr.instruction = Instruction.INVOKE
		instr.paramVal = 6
		out.instructions.add(instr)

		instr = new PrecompiledInstruction()
		instr.instruction = Instruction.LOAD
		instr.paramVal = 0
		out.instructions.add(instr)

		instr = new PrecompiledInstruction()
		instr.instruction = Instruction.INVOKE
		instr.paramVal = 4
		out.instructions.add(instr)

		classFile.methods.add(out)
	}
}
