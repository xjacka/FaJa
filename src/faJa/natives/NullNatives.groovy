package faJa.natives

import faJa.interpreter.ClassLoader
import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.helpers.NativesHelper
import faJa.interpreter.StackFrame

class NullNatives {

	static isNull = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), true)

		stackFrame.methodStack.push(boolPtr)
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer argPtr = stackFrame.methodStack.pop()
		Boolean result
		if(thisPtr == argPtr){
			result = true
		}else{
			result = false
		}
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), result)

		stackFrame.methodStack.push(boolPtr)
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		stackFrame.methodStack.pop()
		Integer strPtr = heap.createString(stringClassPtr, Compiler.NULL_KEYWORD)

		stackFrame.methodStack.push(strPtr)
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer objectPtr = stackFrame.methodStack.pop()
		stackFrame.methodStack.pop()

		stackFrame.methodStack.push(objectPtr) // always push
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{a -> true},'ifFalse(1)Null')
	}
}
