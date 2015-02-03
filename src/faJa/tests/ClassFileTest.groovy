package faJa.tests

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.PrecompiledMethod
import faJa.helpers.ByteHelper

class ClassFileTest {

	def testWithoutMethods(){
		def classFile = new ClassFile()
		classFile.constantPool = ['StringClass','test', 'ahoj', 'dlouhyString']
		classFile.fields = [1, 2, 3]
		classFile.methods = []
		byte[] bytecode = classFile.toByteCode()

		// test class size
		assert ByteHelper.bytesToIntAt(bytecode, 0) == 51

		// test constant pool size
		assert ByteHelper.bytesToIntAt(bytecode, 2) == 39

		// test first constant pool value
		assert ByteHelper.bytesToIntAt(bytecode, 4) == 11
		assert bytecode.toList().subList(6, 6+11) == 'StringClass'.bytes

		// test second constant pool value
		assert ByteHelper.bytesToIntAt(bytecode, 17) == 4
		assert bytecode.toList().subList(19, 19+4) == 'test'.bytes

		// test field size
		assert ByteHelper.bytesToIntAt(bytecode, 43) == 6

		// test first field
		assert ByteHelper.bytesToIntAt(bytecode, 45) == 17
	}

	def testMethods(){
		def classFile = new ClassFile()
		classFile.constantPool = ['TestClass', 'a', 'test()faja.Number']
		classFile.fields = [1]

		def method = new PrecompiledMethod()
		method.instructions.add(new PrecompiledInstruction([instruction: Instruction.LOAD, paramVal:0]))
		method.instructions.add(new PrecompiledInstruction([instruction: Instruction.INVOKE, paramVal:2]))
		method.instructions.add(new PrecompiledInstruction([instruction: Instruction.LOAD, paramVal:  1]))
		method.instructions.add(new PrecompiledInstruction([instruction: Instruction.GETFIELD, paramVal:1]))

		classFile.methods.add(method)

		byte[] bytecode = classFile.toByteCode()

		// test method bytecode size
		assert ByteHelper.bytesToIntAt(bytecode, 43) == 10

		// test load instruction
		assert bytecode[45] == Instruction.LOAD.id

		// test invoke instruction
		assert bytecode[47] == Instruction.INVOKE.id
		assert ByteHelper.bytesToIntAt(bytecode, 48) == 18 // 18 is pointer to constant pool value 'test():faja.Number'

		// test getfield instruction
		assert bytecode[52] == Instruction.GETFIELD.id
		assert ByteHelper.bytesToIntAt(bytecode, 53) == 15 // 15 is pointer to constant pool value 'a'
	}

	def testPrefix(){
		def classFile = new ClassFile()
		assert classFile.createPrefixSum([4,3,6]) == [0,4,7,13]
	}
}
