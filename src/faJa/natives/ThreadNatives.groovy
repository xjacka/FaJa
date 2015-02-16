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
			throw new InterpretException("Too much arguments for closure in method run(1)")
		}

		StackFrame newStackFrame = new StackFrame()
		newStackFrame.parent = null
		newStackFrame.bytecodePtr = 0
		newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
		newStackFrame.locals = []
		newStackFrame.methodStack = []
		newStackFrame.environment = ClosureRegister.get(closurePtr)
		newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

		Thread thread = classLoader.threads.get(threadPtr)
		if(thread != null) {
			thread.join()
		}

		thread = Thread.start {
			new Interpreter(heap, newStackFrame, classLoader).interpret()
		}

		synchronized (classLoader) {
			classLoader.threads.put(threadPtr, thread)
		}

		stackFrame.methodStack.push(threadPtr)
	}

	static wait = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer threadPtr = stackFrame.methodStack.last()
		Thread thread = classLoader.threads.get(threadPtr)
		if(thread == null){
			throw new InterpretException("There is no thread in current context")
		}
		thread.join()
	}
}
