package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ClosureHelper
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.helpers.ObjectInitHelper
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class BoolNatives {

	static final Byte TRUE = 1
	static final Byte FALSE = 0

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a == b})
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> heap.boolFromBoolObject(a) == true},"ifTrue(1)Bool")
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> heap.boolFromBoolObject(a) == false},"ifFalse(1)Bool")
	}

	static and = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a && b})
	}

	static or = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a || b})
	}

	static not = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		def arg1ptr = stackFrame.methodStack.pop()

		def class1Ptr = ObjectAccessHelper.getClassPointer(heap,arg1ptr)

		def pointer = heap.load(ObjectInitHelper.createBool(class1Ptr,heap.getByte(arg1ptr + Heap.SLOT_SIZE) == FALSE))
		stackFrame.methodStack.push(pointer)
		null
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer numPtr = stackFrame.methodStack.pop()
		Boolean bool = heap.boolFromBoolObject(numPtr)
		Integer stringPtr = heap.createString(stringClassPtr, bool.toString())
		stackFrame.methodStack.push(stringPtr)
		null
	}

	private static binaryOperation(StackFrame stackFrame, Heap heap, ClassLoader classLoader,Closure closure){
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer bool1Ptr = stackFrame.methodStack.pop()
		Integer bool2Ptr = stackFrame.methodStack.pop()

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,bool2Ptr))

		Boolean result = false

		if(class2 == Compiler.BOOL_CLASS) {
			Boolean bool1 = heap.boolFromBoolObject(bool1Ptr)
			Boolean bool2 = heap.boolFromBoolObject(bool2Ptr)

			if (closure.call(bool1, bool2)) {
				result = true
			}
		}

		Integer resultPtr = heap.createBool(boolClassPtr, result)
		stackFrame.methodStack.push(resultPtr)
	}
}
