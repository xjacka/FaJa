package faJa.natives

import faJa.Heap
import faJa.compilator.Compilator
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader
import org.omg.CORBA.ObjectHelper

class StringNatives {

	static plus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer otherPtr = stackFrame.methodStack.pop()

		Integer stringClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)
		Integer otherClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)

		Integer otherStringPtr = otherPtr
		if(stringClassPtr != otherClassPtr){
			otherStringPtr = null // todo call to string  on other object
			throw new InterpretException('argument of String::plus must be string')
		}

		String result  = heap.stringFromStringObject(thisPtr) + heap.stringFromStringObject(otherStringPtr)
		Integer newStringPtr = heap.createString(stringClassPtr, result)
		stackFrame.methodStack.push(newStringPtr)

		null
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer otherPtr = stackFrame.methodStack.pop()

		Integer stringClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)
		Integer otherClassPtr = ObjectAccessHelper.getClassPointer(heap, thisPtr)

		Byte result = BoolNatives.FALSE
		if(stringClassPtr == otherClassPtr){
			if(heap.stringFromStringObject(thisPtr) == heap.stringFromStringObject(otherClassPtr)){
				result = BoolNatives.TRUE
			}
		}
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compilator.BOOL_CLASS), result)
		stackFrame.methodStack.push(boolPtr)

		null
	}
// todo
	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		if(heap.stringFromStringObject(thisPtr) != ''){
			// call closure
		}

		null
	}
// todo
	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()



		if(heap.stringFromStringObject(thisPtr) == ''){

			// call closure
		}


		null
	}

	static length = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer size = heap.getPointer(thisPtr + Heap.SLOT_SIZE)

		Integer resultPtr = heap.createNumber(classLoader.findClass(heap, Compilator.NUMBER_CLASS), size)
		stackFrame.methodStack.push(resultPtr)

		null
	}

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		// empty, leaves string arg on stack
		null
	}
}
