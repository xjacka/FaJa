package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.helpers.ClosureHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class NumberNatives {

	static plus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 + num2)
		stackFrame.methodStack.push(resultPtr)

		null
	}

	static minus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 - num2)
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static mul = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 * num2)
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static div = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, (Integer)(num1 / num2))
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static mod = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 % num2)
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createBool(boolClassPtr,(byte) ( num1 == num2 ? 1 : 0 ))
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisIntegerPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer integerValue = heap.intFromNumberObject(thisIntegerPtr)
		if(integerValue != 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			return newStackFrame
		}else{
			return null
		}
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisIntegerPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer integerValue = heap.intFromNumberObject(thisIntegerPtr)
		if(integerValue == 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			return newStackFrame
		}else{
			return null
		}
	}

	static init = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer numPtr = stackFrame.methodStack.pop()
		Integer num = heap.intFromNumberObject(numPtr)
		Integer stringPtr = heap.createString(stringClassPtr, num.toString())
		stackFrame.methodStack.push(stringPtr)
		null
	}
}
