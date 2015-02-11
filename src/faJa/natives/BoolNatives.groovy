package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ClosureHelper
import faJa.helpers.ObjectAccessHelper
import faJa.helpers.ObjectInitHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class BoolNatives {

	static final Byte TRUE = 1
	static final Byte FALSE = 0

	// expect: two boolean object on stack
	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer bool1Ptr = stackFrame.methodStack.pop()
		Integer bool2Ptr = stackFrame.methodStack.pop()

		Boolean bool1 = heap.boolFromBoolObject(bool1Ptr)
		Boolean bool2 = heap.boolFromBoolObject(bool2Ptr)

		Integer resultPtr = heap.createBool(boolClassPtr,(byte) ( bool1 == bool2 ? 1 : 0 ))
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisBoolPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Boolean boolValue = heap.boolFromBoolObject(thisBoolPtr)
		if(boolValue) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method times(1)Number')
			}
			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			return [newStackFrame]
		}else{
			return null
		}
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisBoolPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Boolean boolValue = heap.boolFromBoolObject(thisBoolPtr)
		if(!boolValue) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method times(1)Number')
			}
			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			return [newStackFrame]
		}else{
			return null
		}
	}

	static and = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
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
		null
	}

	static or = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
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
		null
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
}
