package faJa.natives

import faJa.ClassLoader
import faJa.Heap
import faJa.compilator.Compiler
import faJa.interpreter.StackFrame

class NullNatives {

	static isNull = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), BoolNatives.TRUE)
		stackFrame.methodStack.push(boolPtr)
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

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		stackFrame.methodStack.pop()
		Integer strPtr = heap.createString(stringClassPtr, Compiler.NULL_KEYWORD)
		stackFrame.methodStack.push(strPtr)
		null
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		stackFrame.methodStack.pop()
		null
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		// call closure

		null
	}
}
