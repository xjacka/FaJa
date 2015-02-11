package faJa.natives

import faJa.Heap
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ByteHelper
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ClosureHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.ClassLoader

class NumberNatives {

	static plus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		// todo controll all object parents
		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		if(Compiler.NUMBER_CLASS != class2){
			throw new InterpretException("Can not invoke method .+(1) on " + Compiler.NUMBER_CLASS + " with argument of type " + class2)
		}

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 + num2)
		stackFrame.methodStack.push(resultPtr)

		null
	}

	static minus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		if(Compiler.NUMBER_CLASS != class2){
			throw new InterpretException("Can not invoke method .-(1) on " + Compiler.NUMBER_CLASS + " with argument of type " + class2)
		}


		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 - num2)
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static mul = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		if(Compiler.NUMBER_CLASS != class2){
			throw new InterpretException("Can not invoke method .*(1) on " + Compiler.NUMBER_CLASS + " with argument of type " + class2)
		}

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 * num2)
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static div = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		if(Compiler.NUMBER_CLASS != class2){
			throw new InterpretException("Can not invoke method ./(1) on " + Compiler.NUMBER_CLASS + " with argument of type " + class2)
		}

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, (Integer)(num1 / num2))
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static mod = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		if(Compiler.NUMBER_CLASS != class2){
			throw new InterpretException("Can not invoke method .%(1) on " + Compiler.NUMBER_CLASS + " with argument of type " + class2)
		}

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, num1 % num2)
		stackFrame.methodStack.push(resultPtr)
		null
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		Integer resultPtr
		if(Compiler.NUMBER_CLASS != class2){
			resultPtr = heap.createBool(boolClassPtr,(byte) 0)
		}
		else{
			resultPtr = heap.createBool(boolClassPtr,(byte) ( num1 == num2 ? 1 : 0 ))
		}

		stackFrame.methodStack.push(resultPtr)
		null
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisIntegerPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer integerValue = heap.intFromNumberObject(thisIntegerPtr)
		if(integerValue != 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 0){
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
		}else{
			return null
		}
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisIntegerPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer integerValue = heap.intFromNumberObject(thisIntegerPtr)
		if(integerValue == 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 0){
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
		}else{
			return null
		}
	}

	static init = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

		null
	}

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer numPtr = stackFrame.methodStack.pop()
		Integer num = heap.intFromNumberObject(numPtr)
		Integer stringPtr = heap.createString(stringClassPtr, num.toString())
		stackFrame.methodStack.push(stringPtr)
		null
	}

	static times = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisIntegerPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer integerValue = heap.intFromNumberObject(thisIntegerPtr)
		if(integerValue != 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			def returnFrames = []
			integerValue.times {
				StackFrame newStackFrame = new StackFrame()
				newStackFrame.parent = stackFrame
				newStackFrame.bytecodePtr = 0
				newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
				newStackFrame.locals = []
				newStackFrame.methodStack = []
				newStackFrame.locals.addAll(stackFrame.locals) // insert current context
				if(arguments == 1){
					Integer iterationCounter = heap.createNumber(classLoader.findClass(heap,Compiler.NUMBER_CLASS),it)
					newStackFrame.locals.add(1,iterationCounter)
				}
				if(arguments > 1){
					throw new InterpretException('Too much arguments for closure in method times(1)Number')
				}
				returnFrames.add(newStackFrame)
			}
			return returnFrames
		}else{
			return null
		}
	}
}
