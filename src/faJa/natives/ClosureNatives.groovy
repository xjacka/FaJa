package faJa.natives

import faJa.Heap
import faJa.helpers.ClosureHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class ClosureNatives {

	static call = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisPtr = stackFrame.methodStack.pop()
		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, thisPtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeArgCount = ClosureHelper.getBytecodeArgCount(heap, bytecodePtr)
		List reversedArgs = []
		bytecodeArgCount.times{
			reversedArgs.push(stackFrame.methodStack.pop())
		}


		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		StackFrame newStackFrame = new StackFrame()
		newStackFrame.parent = stackFrame
		newStackFrame.bytecodePtr = 0
		newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
		newStackFrame.locals = [stackFrame.thisInst]
		newStackFrame.methodStack = []
		newStackFrame.locals.addAll(reversedArgs.reverse())
		newStackFrame.locals.addAll(stackFrame.locals)

		newStackFrame
	}
}
