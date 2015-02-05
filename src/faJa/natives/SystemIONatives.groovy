package faJa.natives

import faJa.Heap
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class SystemIONatives {

	static writeToFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static readFromFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static innerOut = { StackFrame currentStackFrame, Heap heap, ClassLoader classLoader ->
		currentStackFrame.methodStack.pop()
		Integer stringPtr = currentStackFrame.methodStack.pop()
		println(heap.stringFromStringObject(stringPtr))
	}
}
