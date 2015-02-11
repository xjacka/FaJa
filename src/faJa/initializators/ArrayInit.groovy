package faJa.initializators

import faJa.ClassFile
import faJa.PrecompiledMethod

class ArrayInit extends BaseInit {

	ArrayInit() {
		classFile = new ClassFile()
		classFile.constantPool = [
				'Array',
				'Object',
				'ifTrue(1)',
				'ifFalse(1)',
				'toS(0)',
				'each(1)',
				'collect(1)',
				'add(1)',
				'add(2)',
				'get(1)',
				'push(1)',
				'pop(0)',
				'top(0)'
		]

		classFile.fields = []

//		Method ifTrue(1) - Native
		def ifTrue = new PrecompiledMethod()
		ifTrue.signatureIndex = 2
		classFile.methods.add(ifTrue)

//		Method ifFalse(1) - Native
		def ifFalse = new PrecompiledMethod()
		ifFalse.signatureIndex = 3
		classFile.methods.add(ifFalse)

//		Method toS(1) - Native
		def toS = new PrecompiledMethod()
		toS.signatureIndex = 4
		classFile.methods.add(toS)

//		Method each(1) - Native
		def each = new PrecompiledMethod()
		each.signatureIndex = 5
		classFile.methods.add(each)

//		Method collect(1) - Native
		def collect = new PrecompiledMethod()
		collect.signatureIndex = 6
		classFile.methods.add(collect)

//		Method add(1) - Native
		def add1 = new PrecompiledMethod()
		add1.signatureIndex = 7
		classFile.methods.add(add1)

//		Method add(2) - Native
		def add2 = new PrecompiledMethod()
		add2.signatureIndex = 8
		classFile.methods.add(add2)

//		Method get(1) - Native
		def get = new PrecompiledMethod()
		get.signatureIndex = 9
		classFile.methods.add(get)

//		Method push(1) - Native
		def push = new PrecompiledMethod()
		push.signatureIndex = 10
		classFile.methods.add(push)

//		Method pop(1) - Native
		def pop = new PrecompiledMethod()
		pop.signatureIndex = 11
		classFile.methods.add(pop)

//		Method pop(1) - Native
		def top = new PrecompiledMethod()
		top.signatureIndex = 12
		classFile.methods.add(top)

	}
}
