package faJa.natives

import faJa.ClassLoader
import faJa.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClosureHelper
import faJa.interpreter.StackFrame

class NullNatives {

	static isNull = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), BoolNatives.TRUE)
		stackFrame.methodStack.push(boolPtr)
		null
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer argPtr = stackFrame.methodStack.pop()
		Byte result
		if(thisPtr == argPtr){
			result = BoolNatives.TRUE
		}else{
			result = BoolNatives.FALSE
		}
		Integer boolPtr = heap.createBool(classLoader.findClass(heap, Compiler.BOOL_CLASS), result)
		stackFrame.methodStack.push(boolPtr)
		null
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		stackFrame.methodStack.pop()
		Integer strPtr = heap.createString(stringClassPtr, Compiler.NULL_KEYWORD)
		stackFrame.methodStack.push(strPtr)
		null
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		stackFrame.methodStack.pop()
		stackFrame.methodStack.pop()
		null
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisNullPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

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
	}
}
