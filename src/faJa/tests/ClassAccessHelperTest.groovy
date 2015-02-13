package faJa.tests

import faJa.compilator.representation.ClassFile
import faJa.memory.Heap
import faJa.interpreter.Instruction
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.representation.PrecompiledMethod
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper

class ClassAccessHelperTest {

	def test(Integer offset = 0){
		def classFile = new ClassFile()

		classFile.constantPool.add('TestClass')
		classFile.constantPool.add('Object')
		classFile.fields = [
				classFile.constantPool.add('a'),
				classFile.constantPool.add('b')
		]

		def method1 = new PrecompiledMethod()
		method1.signatureIndex = classFile.constantPool.add('test(0)')
		method1.instructions.add(new PrecompiledInstruction([instruction: Instruction.LOAD, paramVal:0]))
		method1.instructions.add(new PrecompiledInstruction([instruction: Instruction.INVOKE, paramVal:4]))
		method1.instructions.add(new PrecompiledInstruction([instruction: Instruction.LOAD, paramVal:  1]))
		method1.instructions.add(new PrecompiledInstruction([instruction: Instruction.GETFIELD, paramVal:1]))

		def method2 = new PrecompiledMethod()
		method2.signatureIndex = classFile.constantPool.add('foo(1)')
		method2.instructions.add(new PrecompiledInstruction([instruction: Instruction.LOAD, paramVal:0]))
		method2.instructions.add(new PrecompiledInstruction([instruction: Instruction.INVOKE, paramVal:4]))

		def method3 = new PrecompiledMethod()
		method3.signatureIndex = classFile.constantPool.add('native(0)')
		method3.isNative = true

		classFile.methods.add(method1)
		classFile.methods.add(method2)
		classFile.methods.add(method3)

		Heap heap = new Heap()
		heap.load(new byte[offset])
		heap.load(classFile.toByteCode())

		def constPoolSize = classFile.constantPool.size()
		def fieldsSize = classFile.fields.size() * Heap.SLOT_SIZE + Heap.SLOT_SIZE
		def firstMethod = Heap.SLOT_SIZE + offset + constPoolSize + fieldsSize + ClassFile.CONST_POOL_START
		assert ClassAccessHelper.findMethod(heap,offset,'test(0)') == firstMethod

		def fakeConstPoolIndexes = [0,0,0,0,0]
		def firstMethodSize = classFile.methods[0].toBytecode(fakeConstPoolIndexes).length
		def secondMethod = firstMethod + firstMethodSize
		assert ClassAccessHelper.findMethod(heap,offset,'foo(1)') == secondMethod


		assert ClassAccessHelper.findMethod(heap,offset,'foo(0)') == null


		assert ClassAccessHelper.findFieldIndex(heap, offset, 'a' ) == 0
		assert ClassAccessHelper.findFieldIndex(heap, offset, 'b' ) == 2
boolean  testSuccess = false
try {
	ClassAccessHelper.findFieldIndex(heap, offset, 'unkown') == null
}catch(InterpretException e){
	testSuccess = true
}
		assert testSuccess == true
		assert ClassAccessHelper.getName(heap, offset) == 'TestClass'

		assert ClassAccessHelper.getParent(heap, offset) == 'Object'

		assert ClassAccessHelper.getObjectSize(heap, offset) == classFile.fields.size() * Heap.SLOT_SIZE + Heap.SLOT_SIZE

		assert ClassAccessHelper.isNative(heap, secondMethod) == false

		assert ClassAccessHelper.isNative(heap, ClassAccessHelper.findMethod(heap,offset,'native(0)')) == true

	}

}
