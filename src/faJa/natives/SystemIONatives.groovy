package faJa.natives

import faJa.Heap
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class SystemIONatives {

	static writeToFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static readFromFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static innerOut = { StackFrame currentStackFrame, Heap heap, ClassLoader classLoader ->
		currentStackFrame.methodStack.pop()
		Integer stringPtr = currentStackFrame.methodStack.last() // pop and push
		println(heap.stringFromStringObject(stringPtr))

		null
	}
}
