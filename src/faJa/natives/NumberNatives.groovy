package faJa.natives

import faJa.Heap
import faJa.compilator.Compilator
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class NumberNatives {

	static plus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compilator.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 + num2)
		stackFrame.methodStack.push(resultPtr)

	}

	static minus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compilator.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 - num2)
		stackFrame.methodStack.push(resultPtr)
	}

	static mul = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compilator.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 * num2)
		stackFrame.methodStack.push(resultPtr)
	}

	static div = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compilator.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, (Integer)(num1 / num2))
		stackFrame.methodStack.push(resultPtr)
	}

	static mod = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compilator.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 % num2)
		stackFrame.methodStack.push(resultPtr)
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compilator.BOOL_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createBool(boolClassPtr,(byte) ( num1 == num2 ? 1 : 0 ))
		stackFrame.methodStack.push(resultPtr)
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static init = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compilator.STRING_CLASS)
		Integer numPtr = stackFrame.methodStack.pop()
		Integer num = heap.intFromNumberObject(numPtr)
		Integer stringPtr = heap.createString(stringClassPtr, num.toString())
		stackFrame.methodStack.push(stringPtr)
	}
}
