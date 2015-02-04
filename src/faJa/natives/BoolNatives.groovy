package faJa.natives

import faJa.Heap
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.helpers.ObjectInitHelper
import faJa.interpreter.StackFrame

class BoolNatives {

	static final Integer TRUE = 1
	static final Integer FALSE = 0

	// expect: two boolean object on stack
	static equals = { StackFrame stackFrame, Heap heap ->
		def arg1ptr = stackFrame.methodStack.pop()
		def arg2ptr = stackFrame.methodStack.pop()

		def class1Ptr = ObjectAccessHelper.getClassPointer(heap,arg1ptr)
		def class2Ptr = ObjectAccessHelper.getClassPointer(heap,arg2ptr)

		def result = false

		if(class1Ptr == class2Ptr) {
			if(heap.getByte(arg1ptr + Heap.SLOT_SIZE) == heap.getByte(arg2ptr + Heap.SLOT_SIZE)){
				result == true
			}
		}

		def pointer = heap.load(ObjectInitHelper.createBool(class1Ptr,result))
		stackFrame.methodStack.push(pointer)
	}

	static ifTrue = {

	}

	static ifFalse = {

	}

	static and = { StackFrame stackFrame, Heap heap ->
		def arg1ptr = stackFrame.methodStack.pop()
		def arg2ptr = stackFrame.methodStack.pop()

		def class1Ptr = ObjectAccessHelper.getClassPointer(heap,arg1ptr)
		def class2Ptr = ObjectAccessHelper.getClassPointer(heap,arg2ptr)

		def result = false

		if(class1Ptr == class2Ptr) {
			if(heap.getByte(arg1ptr + Heap.SLOT_SIZE) == TRUE && heap.getByte(arg2ptr + Heap.SLOT_SIZE) == TRUE){
				result == true
			}
		}

		def pointer = heap.load(ObjectInitHelper.createBool(class1Ptr,result))
		stackFrame.methodStack.push(pointer)
	}

	static or = { StackFrame stackFrame, Heap heap ->
			def arg1ptr = stackFrame.methodStack.pop()
			def arg2ptr = stackFrame.methodStack.pop()

			def class1Ptr = ObjectAccessHelper.getClassPointer(heap,arg1ptr)
			def class2Ptr = ObjectAccessHelper.getClassPointer(heap,arg2ptr)

			def result = false

			if(class1Ptr == class2Ptr) {
				if(heap.getByte(arg1ptr + Heap.SLOT_SIZE) == TRUE || heap.getByte(arg2ptr + Heap.SLOT_SIZE) == TRUE){
					result == true
				}
			}

			def pointer = heap.load(ObjectInitHelper.createBool(class1Ptr,result))
			stackFrame.methodStack.push(pointer)
	}

	static not = { StackFrame stackFrame, Heap heap ->
		def arg1ptr = stackFrame.methodStack.pop()

		def class1Ptr = ObjectAccessHelper.getClassPointer(heap,arg1ptr)

		def pointer = heap.load(ObjectInitHelper.createBool(class1Ptr,heap.getByte(arg1ptr + Heap.SLOT_SIZE) == FALSE))
		stackFrame.methodStack.push(pointer)
	}
}
