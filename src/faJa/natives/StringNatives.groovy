package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InputException
import faJa.exceptions.InterpretException
import faJa.helpers.ClosureHelper
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class StringNatives {

	static plus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer otherPtr = stackFrame.methodStack.pop()

		Integer stringClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)
		Integer otherClassPtr = ObjectAccessHelper.getClassPointer(heap, otherPtr)

		Integer otherStringPtr = otherPtr
		if(stringClassPtr != otherClassPtr){
			NativesHelper.callMethodFromNative(heap,stackFrame,otherPtr,'toS(0)',classLoader)
			otherStringPtr = stackFrame.methodStack.pop()
		}

		String result  = heap.stringFromStringObject(thisPtr) + heap.stringFromStringObject(otherStringPtr)
		Integer newStringPtr = heap.createString(stringClassPtr, result)

		stackFrame.methodStack.push(newStringPtr)
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer otherPtr = stackFrame.methodStack.pop()

		Integer stringClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)
		Integer otherClassPtr = ObjectAccessHelper.getClassPointer(heap, otherPtr)

		Boolean result = false
		if(stringClassPtr == otherClassPtr){
			if(heap.stringFromStringObject(thisPtr) == heap.stringFromStringObject(otherPtr)){
				result = true
			}
		}
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), result)

		stackFrame.methodStack.push(boolPtr)
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> heap.stringFromStringObject(a).trim() != ''},"ifTrue(1)String")
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> heap.stringFromStringObject(a).trim() == ''},"ifFalse(1)String")
	}

	static length = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer size = heap.getPointer(thisPtr + Heap.SLOT_SIZE)

		Integer resultPtr = heap.createNumber(classLoader.findClass(heap, Compiler.NUMBER_CLASS), size)

		stackFrame.methodStack.push(resultPtr)
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		// empty, leaves string arg on stack
	}

	static toNumber = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		String stringObj = heap.stringFromStringObject(thisPtr)

		try {
			Integer newNumberPtr = heap.createNumber(classLoader.findClass(heap, Compiler.NUMBER_CLASS), Integer.parseInt(stringObj))
			stackFrame.methodStack.push(newNumberPtr)
		}
		catch (NumberFormatException e){
			throw new InputException("String value can not be cast to Number")
		}
	}
}
