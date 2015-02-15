package faJa.natives

import faJa.exceptions.InterpretException
import faJa.helpers.ClosureHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.Interpreter
import faJa.memory.Heap
import faJa.interpreter.StackFrame
import faJa.interpreter.ClassLoader

class ThreadNatives {

	static run = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer threadPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		if(arguments > 0){
			throw new InterpretException("Too much arguments for closure in method call(1)")
		}

		StackFrame newStackFrame = new StackFrame()
		newStackFrame.parent = stackFrame
		newStackFrame.bytecodePtr = 0
		newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
		newStackFrame.locals = []
		newStackFrame.methodStack = []
		newStackFrame.environment = ClosureRegister.get(closurePtr)
		newStackFrame.localCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

		Thread thread = new Thread()

		Integer index = stackFrame.threads.size()
		stackFrame.threads.add(index,thread)
		ObjectAccessHelper.setNewValue(heap,threadPtr,0,index)

//		thread.start {
			new Interpreter(heap, newStackFrame, classLoader).interpret()
//		}
	}

	static wait = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer threadPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,threadPtr,0)
		Thread thread = stackFrame.threads.get(index)
		thread.join()
	}
}
