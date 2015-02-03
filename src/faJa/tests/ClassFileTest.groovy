package faJa.tests

import faJa.ClassFile
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

	def testPrefix(){
		def classFile = new ClassFile()
		assert classFile.createPrefixSum([4,3,6]) == [0,4,7,13]
	}
}
