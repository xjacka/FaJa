package faJa.natives

import faJa.Heap
import faJa.compilator.Compilator
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class ObjectNatives {

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer strPtr = heap.createString(classLoader.findClass(heap, Compilator.STRING_CLASS), 'Object') //
		stackFrame.methodStack.push(strPtr)
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}
}
