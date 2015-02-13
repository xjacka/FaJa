package faJa.initializators

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.ConstantPool
import faJa.compilator.representation.PrecompiledMethod

class ArrayInit extends BaseInit {

	ArrayInit() {
		classFile = new ClassFile()
		classFile.constantPool.add('Array')
		classFile.constantPool.add('Object')

		classFile.fields = []

//		Method ifTrue(1) - Native
		def ifTrue = new PrecompiledMethod()
		ifTrue.signatureIndex = classFile.constantPool.add('ifTrue(1)')
		classFile.methods.add(ifTrue)

//		Method ifFalse(1) - Native
		def ifFalse = new PrecompiledMethod()
		ifFalse.signatureIndex = classFile.constantPool.add('ifFalse(1)')
		classFile.methods.add(ifFalse)

//		Method toS(1) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = classFile.constantPool.add('toS(0)')
		classFile.methods.add(toS)

//		Method each(1) - Native
		def each = new PrecompiledMethod()
		each.signatureIndex = classFile.constantPool.add('each(1)')
		classFile.methods.add(each)

//		Method collect(1) - Native
		def collect = new PrecompiledMethod()
		collect.signatureIndex = classFile.constantPool.add('collect(1)')
		classFile.methods.add(collect)

//		Method add(1) - Native
		def add1 = new PrecompiledMethod()
		add1.signatureIndex = classFile.constantPool.add('add(1)')
		classFile.methods.add(add1)

//		Method add(2) - Native
		def add2 = new PrecompiledMethod()
		add2.signatureIndex = classFile.constantPool.add('add(2)')
		classFile.methods.add(add2)

//		Method get(1) - Native
		def get = new PrecompiledMethod()
		get.signatureIndex = classFile.constantPool.add('get(1)')
		classFile.methods.add(get)

//		Method push(1) - Native
		def push = new PrecompiledMethod()
		push.signatureIndex = classFile.constantPool.add('push(1)')
		classFile.methods.add(push)

//		Method pop(1) - Native
		def pop = new PrecompiledMethod()
		pop.signatureIndex = classFile.constantPool.add('pop(0)')
		classFile.methods.add(pop)

//		Method pop(1) - Native
		def top = new PrecompiledMethod()
		top.signatureIndex = classFile.constantPool.add('top(0)')
		classFile.methods.add(top)

//		Method size(0) - Native
		def size = new PrecompiledMethod()
		size.signatureIndex = classFile.constantPool.add('size(0)')
		classFile.methods.add(size)

//		Method contains(1) - Native
		def contains = new PrecompiledMethod()
		contains.signatureIndex = classFile.constantPool.add('contains(1)')
		classFile.methods.add(contains)

//		Method select(1) - Native
		def select = new PrecompiledMethod()
		select.signatureIndex = classFile.constantPool.add('select(1)')
		classFile.methods.add(select)
	}
}
