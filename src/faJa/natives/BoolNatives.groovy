package faJa.natives

import faJa.Heap
import faJa.compilator.Compilator
import faJa.helpers.ObjectAccessHelper
import faJa.helpers.ObjectInitHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class BoolNatives {

	static final Integer TRUE = 1
	static final Integer FALSE = 0

	// expect: two boolean object on stack
	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compilator.BOOL_CLASS)
		Integer bool1Ptr = stackFrame.methodStack.pop()
		Integer bool2Ptr = stackFrame.methodStack.pop()

		Boolean bool1 = heap.boolFromBoolObject(bool1Ptr)
		Boolean bool2 = heap.boolFromBoolObject(bool2Ptr)

		Integer resultPtr = heap.createBool(boolClassPtr,(byte) ( bool1 == bool2 ? 1 : 0 ))
		stackFrame.methodStack.push(resultPtr)
	}

	static ifTrue = {

	}

	static ifFalse = {

	}

	static and = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compilator.BOOL_CLASS)
		Integer bool1Ptr = stackFrame.methodStack.pop()
		Integer bool2Ptr = stackFrame.methodStack.pop()

		Boolean bool1 = heap.boolFromBoolObject(bool1Ptr)
		Boolean bool2 = heap.boolFromBoolObject(bool2Ptr)

		Byte result = (byte) 0

		if(bool1 == true && bool2 == true){
			result = (byte) 1
		}

		Integer resultPtr = heap.createBool(boolClassPtr, result)
		stackFrame.methodStack.push(resultPtr)
	}

	static or = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compilator.BOOL_CLASS)
		Integer bool1Ptr = stackFrame.methodStack.pop()
		Integer bool2Ptr = stackFrame.methodStack.pop()

		Boolean bool1 = heap.boolFromBoolObject(bool1Ptr)
		Boolean bool2 = heap.boolFromBoolObject(bool2Ptr)

		Byte result = (byte) 0

		if(bool1 == true || bool2 == true){
			result = (byte) 1
		}

		Integer resultPtr = heap.createBool(boolClassPtr, result)
		stackFrame.methodStack.push(resultPtr)
	}

	static not = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		def arg1ptr = stackFrame.methodStack.pop()

		def class1Ptr = ObjectAccessHelper.getClassPointer(heap,arg1ptr)

		def pointer = heap.load(ObjectInitHelper.createBool(class1Ptr,heap.getByte(arg1ptr + Heap.SLOT_SIZE) == FALSE))
		stackFrame.methodStack.push(pointer)
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compilator.STRING_CLASS)
		Integer numPtr = stackFrame.methodStack.pop()
		Boolean bool = heap.boolFromBoolObject(numPtr)
		Integer stringPtr = heap.createString(stringClassPtr, bool.toString())
		stackFrame.methodStack.push(stringPtr)
	}
}
