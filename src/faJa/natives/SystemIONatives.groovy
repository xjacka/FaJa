package faJa.natives

import faJa.helpers.ArrayHelper
import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InputException
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.interpreter.ClassLoader

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
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)

		f.eachLine { String line, Integer i ->
			stackFrame.currentVariables.add(arrayPtr) // for GC
			Integer lineStringPtr = heap.createString(classLoader.findClass(heap,Compiler.STRING_CLASS),line)
			arrayPtr = stackFrame.currentVariables.pop()
			arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
			
			ArrayHelper.setNewValue(heap,arrayObjectPtr,(i - 1),lineStringPtr)
		}
		ArrayHelper.setInsertIndex(heap,arrayPtr,lines)

		stackFrame.methodStack.push(arrayPtr) // always push
	}

	static out = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer objectPtr = stackFrame.methodStack.last() // pop and push

		NativesHelper.callMethodFromNative(heap,stackFrame,objectPtr,"toS(0)",classLoader,[])
		Integer stringPtr = stackFrame.methodStack.pop()

		println(heap.stringFromStringObject(stringPtr))
	}

	static inputString = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
		String input = br.readLine()

		Integer newStringPtr = heap.createString(classLoader.findClass(heap,Compiler.STRING_CLASS),input)

		stackFrame.methodStack.push(newStringPtr) // always push
	}

	static inputNumber = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
		String input = br.readLine()

		try {
			Integer newNumberPtr = heap.createNumber(classLoader.findClass(heap, Compiler.NUMBER_CLASS), Integer.parseInt(input))
			stackFrame.methodStack.push(newNumberPtr) // always push
		}
		catch (NumberFormatException e){
			throw new InputException("Input value is not of type Number")
		}
	}

	static inputBool = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in))
		String input = br.readLine()

		if(input.trim() == Compiler.TRUE_STRING_VALUE){
			Integer newBoolPtr = heap.createBool(classLoader.findClass(heap,Compiler.BOOL_CLASS),true)
			stackFrame.methodStack.push(newBoolPtr)
		}
		else if(input.trim() == Compiler.FALSE_STRING_VALUE){
			Integer newBoolPtr = heap.createBool(classLoader.findClass(heap,Compiler.BOOL_CLASS),false)
			stackFrame.methodStack.push(newBoolPtr)
		}
		else {
			throw new InputException("Input value is not of type Bool")
		}
	}
}
