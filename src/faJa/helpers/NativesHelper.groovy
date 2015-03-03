package faJa.helpers

import faJa.interpreter.ClassLoader
import faJa.memory.GarbageCollector
import faJa.memory.Heap
import faJa.exceptions.InterpretException
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame
import faJa.natives.ClosureRegister
import faJa.natives.NativesRegister

class NativesHelper {

	static callMethodFromNative(Heap heap, StackFrame parentStackFrame, Integer objectPtr, String methodSignature, ClassLoader classLoader,List reversedArgList = []){
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap,objectPtr)
		def resultPair = ClassAccessHelper.findMethodWithSuper(heap,classPtr,methodSignature,classLoader)

		Integer methodPtr = resultPair[1]
		if(methodPtr == null){
			throw new InterpretException('invoked method "'+ methodSignature +'" not found on ' + ClassAccessHelper.getName(heap, classPtr))
		}
		if(ClassAccessHelper.isNative(heap,methodPtr)){
			String nativeMethodClassName = ClassAccessHelper.getName(heap, resultPair[0]) // result[0] - pointer on class with method bytecode
			Closure nativeMethod = NativesRegister.register.get(methodSignature + nativeMethodClassName)

			reversedArgList.reverse().each{
				parentStackFrame.methodStack.push(it)
			}
			parentStackFrame.methodStack.push(objectPtr)

			nativeMethod.call(parentStackFrame, heap, classLoader)
			return
		}

		StackFrame newStackFrame = new StackFrame()
		newStackFrame.parent = parentStackFrame
		newStackFrame.bytecodePtr = 0
		newStackFrame.bytecode = MethodHelper.getBytecode(heap, methodPtr)
		newStackFrame.locals = [objectPtr]
		newStackFrame.locals.addAll(reversedArgList.reverse())
		newStackFrame.methodStack = []

		parentStackFrame.methodStack.push(objectPtr)

		new Interpreter(heap, newStackFrame, classLoader).interpret()
	}

	static ifClosure(StackFrame stackFrame, Heap heap, ClassLoader classLoader, Closure closure, String methodName) {
		Integer thisObjectPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		if(closure.call(thisObjectPtr)) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 0){
				throw new InterpretException("Too much arguments for closure in method ${methodName}")
			}

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.environment = ClosureRegister.get(closurePtr)
			newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

			new Interpreter(heap, newStackFrame, classLoader).interpret()
			if(GarbageCollector.isOldPointer(heap, thisObjectPtr)){
				thisObjectPtr = heap.getPointer(thisObjectPtr)
			}
		}
		stackFrame.methodStack.push(thisObjectPtr)
	}
}
