package faJa.helpers

import faJa.Heap
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.exceptions.InterpretException
import faJa.interpreter.StackFrame
import faJa.natives.NativesRegister

class NativesHelper {

	static callMethodFromNative(Heap heap, StackFrame parentStackFrame, Integer objectPtr, String methodSignature, faJa.ClassLoader classLoader){
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap,objectPtr)
		def resultPair = ClassAccessHelper.findMethodWithSuper(heap,classPtr,methodSignature,classLoader)

		Integer methodPtr = resultPair[1]
		if(methodPtr == null){
			throw new InterpretException('invoked method "'+ methodSignature +'" not found on ' + ClassAccessHelper.getName(heap, classPtr))
		}
		if(ClassAccessHelper.isNative(heap,methodPtr)){
			String nativeMethodClassName = ClassAccessHelper.getName(heap, resultPair[0]) // result[0] - pointer on class with method bytecode
			Closure nativeMethod = NativesRegister.register.get(methodSignature + nativeMethodClassName)

			parentStackFrame.methodStack.push(objectPtr)

			nativeMethod.call(parentStackFrame, heap, classLoader)
			return
		}

//		byte [] instructions = new byte[2]
//
//		PrecompiledInstruction invokeInst = new PrecompiledInstruction()
//		invokeInst.instruction = Instruction.INVOKE
//		invokeInst.paramVal = 0
//
//		instructions[0] = invokeInst.instruction.id
//		instructions[1] = invokeInst.paramVal.byteValue()
//
//		StackFrame newStackFrame = new StackFrame()
//		newStackFrame.parent = parentStackFrame
//		newStackFrame.bytecodePtr = 0
//		newStackFrame.bytecode = instructions
//		newStackFrame.locals = []
//		newStackFrame.methodStack = [objectPtr]
//		newStackFrame.locals.addAll(parentStackFrame.locals) // insert current context

	}
}
