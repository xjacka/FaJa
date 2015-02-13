package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class ObjectNatives {

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		String objectClassName = ClassAccessHelper.getName(heap, ObjectAccessHelper.getClassPointer(heap, thisPtr))
		Integer strPtr = heap.createString(classLoader.findClass(heap, Compiler.STRING_CLASS), objectClassName + '@' + thisPtr)

		stackFrame.methodStack.push(strPtr)
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

	static isNull = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), false)

		stackFrame.methodStack.push(boolPtr)
	}
}
