package faJa.natives

import faJa.Heap
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class SystemIONatives {

	static writeToFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static readFromFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static innerOut = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer stringPtr = stackFrame.methodStack.last() // pop and push
		println(heap.stringFromStringObject(stringPtr))

		stackFrame.methodStack.push(stringPtr) // always push
	}
}
