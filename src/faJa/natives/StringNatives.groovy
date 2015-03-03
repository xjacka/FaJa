package faJa.natives

import faJa.exceptions.InterpretException
import faJa.helpers.ArrayHelper
import faJa.helpers.ClassAccessHelper
import faJa.memory.GarbageCollector
import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InputException
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.interpreter.ClassLoader

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
		Integer size = heap.getSlot(thisPtr + Heap.HEAP_POINTER_SIZE)

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
			throw new InputException("String value ${stringObj} can not be cast to Number")
		}
	}

	static split = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer delimiterPtr = stackFrame.methodStack.pop()

		Integer stringClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)
		Integer otherClassPtr = ObjectAccessHelper.getClassPointer(heap, delimiterPtr)

		if(stringClassPtr == otherClassPtr){
			String thisString = heap.stringFromStringObject(thisPtr)
			String delimiter = heap.stringFromStringObject(delimiterPtr)
			ArrayList<String> tokens = thisString.split(delimiter)
			
			Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)
			Integer nullPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
			Integer arrayPtr = heap.createArray(arrayClassPtr, tokens.size(), nullPointer)
			Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap, arrayPtr)

			tokens.eachWithIndex { String token, Integer i ->
				stackFrame.currentVariables.addAll([arrayPtr]) // for GC
				Integer lineStringPtr = heap.createString(classLoader.findClass(heap,Compiler.STRING_CLASS),token)
				arrayPtr = stackFrame.currentVariables.pop()
				arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap, arrayPtr)
				
				ArrayHelper.setNewValue(heap,arrayObjectPtr,i,lineStringPtr)
			}
			ArrayHelper.setInsertIndex(heap,arrayPtr,tokens.size())

			stackFrame.methodStack.push(arrayPtr) // always push
		}else{
			throw new InterpretException("Can not split String using delimiter with class " + ClassAccessHelper.getName(heap,otherClassPtr))
		}
	}
}
