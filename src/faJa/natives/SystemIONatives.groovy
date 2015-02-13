package faJa.natives

import faJa.Heap
import faJa.compilator.Compiler
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class SystemIONatives {

	static writeToFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer objectPtr = stackFrame.methodStack.pop()
		Integer fileNamePtr = stackFrame.methodStack.pop()

		String fileName = heap.stringFromStringObject(fileNamePtr)

		NativesHelper.callMethodFromNative(heap,stackFrame,objectPtr,"toS(0)",classLoader,[])
		Integer stringPtr = stackFrame.methodStack.pop()

		File f = new File(fileName)
		f.append("\n" + heap.stringFromStringObject(stringPtr))

		stackFrame.methodStack.push(stringPtr) // always push
	}

	static readFromFile = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer fileNamePtr = stackFrame.methodStack.pop()

		String fileName = heap.stringFromStringObject(fileNamePtr)
		File f = new File(fileName)
		Integer lines = f.inject(0) { Integer sum, String line -> sum += 1}

		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)
		Integer nullPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer arrayPtr = heap.createArray(arrayClassPtr, lines, nullPointer)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		f.eachLine { String line, Integer i ->
			Integer lineStringPtr = heap.createString(classLoader.findClass(heap,Compiler.STRING_CLASS),line)
			ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,(i - 1) * Heap.SLOT_SIZE,lineStringPtr)
		}
		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,lines)

		stackFrame.methodStack.push(arrayPtr) // always push
	}

	static innerOut = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer stringPtr = stackFrame.methodStack.last() // pop and push
		println(heap.stringFromStringObject(stringPtr))
	}
}
