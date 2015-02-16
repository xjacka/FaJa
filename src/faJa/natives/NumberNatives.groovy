package faJa.natives

import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ClosureHelper
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame
import faJa.interpreter.ClassLoader

class NumberNatives {

	static plus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a + b},".+(1)")
	}

	static minus = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a - b},".-(1)")
	}

	static mul = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a * b},".*(1)")
	}

	static div = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a / b},"./(1)")
	}

	static mod = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		binaryOperation(stackFrame,heap,classLoader,{a,b -> a % b},".%(1)")
	}

	static equals = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		compareWith(stackFrame,heap,classLoader,{a,b -> a == b})
	}

	static lessThen = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		compareWith(stackFrame,heap,classLoader,{a,b -> a < b})
	}

	static greaterThen = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		compareWith(stackFrame,heap,classLoader,{a,b -> a > b})
	}

	static greaterOrEqualThen = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		compareWith(stackFrame,heap,classLoader,{a,b -> a >= b})
	}

	static lessOrEqualThen = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		compareWith(stackFrame,heap,classLoader,{a,b -> a <= b})
	}

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> heap.intFromNumberObject(a) != 0},"ifTrue(1)Number")
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> heap.intFromNumberObject(a) > 0},"ifFalse(1)Number")
	}

	static init = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static toS ={ StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer numPtr = stackFrame.methodStack.pop()
		Integer num = heap.intFromNumberObject(numPtr)
		Integer stringPtr = heap.createString(stringClassPtr, num.toString())

		stackFrame.methodStack.push(stringPtr)
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

				if(arguments == 1){
					Integer iterationCounter = heap.createNumber(classLoader.findClass(heap,Compiler.NUMBER_CLASS),it)
					newStackFrame.locals.add(iterationCounter)
				}
				if(arguments > 1){
					throw new InterpretException('Too much arguments for closure in method times(1)Number')
				}
				newStackFrame.environment = ClosureRegister.get(closurePtr) // insert current context
				newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

				returnFrames.add(newStackFrame)
			}
			returnFrames.each { StackFrame sf ->
				new Interpreter(heap, sf, classLoader).interpret()
			}
		}

		stackFrame.methodStack.push(thisIntegerPtr) // always push
	}

	private static compareWith(StackFrame stackFrame, Heap heap, ClassLoader classLoader,Closure closure) {
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		Integer resultPtr
		if(Compiler.NUMBER_CLASS != class2){
			resultPtr = heap.createBool(boolClassPtr,false)
		}
		else{
			resultPtr = heap.createBool(boolClassPtr, closure.call(num1,num2))
		}

		stackFrame.methodStack.push(resultPtr)
	}

	private static binaryOperation(StackFrame stackFrame, Heap heap, ClassLoader classLoader,Closure closure,String methodName){
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer num1Ptr = stackFrame.methodStack.pop()
		Integer num2Ptr = stackFrame.methodStack.pop()

		String class2 = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,num2Ptr))

		if(Compiler.NUMBER_CLASS != class2){
			throw new InterpretException("Can not invoke method ${methodName} on " + Compiler.NUMBER_CLASS + " with argument of type " + class2)
		}

		Integer num1 = heap.intFromNumberObject(num1Ptr)
		Integer num2 = heap.intFromNumberObject(num2Ptr)

		Integer resultPtr = heap.createNumber(numberClassPtr, closure.call(num1, num2))

		stackFrame.methodStack.push(resultPtr)
	}
}
