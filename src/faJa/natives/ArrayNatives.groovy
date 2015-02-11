package faJa.natives

import faJa.ClassLoader
import faJa.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ByteHelper
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame

class ArrayNatives {

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static each = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static collect = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static add1 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()

		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		if(index >= sizeOfInitializedArry){
			throw new InterpretException("Array out of bound")
		}

		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,index * Heap.SLOT_SIZE,addingItemPtr)

		index += 1
		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,index)

		stackFrame.methodStack.push(arrayPtr)

		null
	}

	static add2 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()
		Integer itemIndexPtr = stackFrame.methodStack.pop()
		Integer itemIndex = heap.intFromNumberObject(itemIndexPtr)

		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		if(index >= sizeOfInitializedArry){
			throw new InterpretException("Array out of bound")
		}

		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,itemIndex * Heap.SLOT_SIZE,addingItemPtr)

		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,itemIndex+1)

		stackFrame.methodStack.push(arrayPtr)
		null
	}

	static get = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = stackFrame.methodStack.pop()
		Integer indexValue = heap.intFromNumberObject(index)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,indexValue * Heap.SLOT_SIZE)

		stackFrame.methodStack.push(resultPtr)

		null
	}

	static push = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		this.add1.call(stackFrame,heap,classLoader)
	}

	static pop = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		if(index == 0){
			throw new InterpretException("Array out of bound")
		}
		index -= 1
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,index * Heap.SLOT_SIZE)
		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,index * Heap.SLOT_SIZE,classLoader.singletonRegister.get(Compiler.NULL_CLASS))
		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,index)

		stackFrame.methodStack.push(resultPtr)

		null
	}

	static top = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,(index - 1) * Heap.SLOT_SIZE)

		stackFrame.methodStack.push(resultPtr)

		null
	}
}
