package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class ObjectNatives {

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr  =stackFrame.methodStack.pop()
		Integer strPtr = heap.createString(classLoader.findClass(heap, Compiler.STRING_CLASS), 'Object@' + thisPtr) //
		stackFrame.methodStack.push(strPtr)

		null
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer argPtr = stackFrame.methodStack.pop()
		Byte result
		if(thisPtr == argPtr){
			result = BoolNatives.TRUE
		}else{
			result = BoolNatives.FALSE
		}
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), result)
		stackFrame.methodStack.push(boolPtr)

		null
	}

	static isNull = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), BoolNatives.FALSE)
		stackFrame.methodStack.push(boolPtr)

		null
	}
}
