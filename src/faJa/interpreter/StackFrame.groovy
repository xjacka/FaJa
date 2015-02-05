package faJa.interpreter

import faJa.exceptions.InterpretException
import faJa.helpers.ByteHelper

class StackFrame {

	//Integer thisInst
	List locals
	// List args
	Integer parent
	byte [] bytecode
	Integer bytecodePtr
// def method

	List<Integer> methodStack

	def incrementBP(int i){
		bytecodePtr += i
	}

	def getThisInst(){
		locals[0]
	}

	Integer getCurrentByte(){
		if(bytecode.length > bytecodePtr ){
			throw new InterpretException('byte out of bound')
		}
		ByteHelper.unsignedValue(bytecode[bytecodePtr])
	}

	Integer getCurrentPointer(){
		if(bytecode.length > bytecodePtr + 1 ){
			throw new InterpretException('pointer out of bound')
		}
		ByteHelper.bytesToIntAt(bytecode, bytecodePtr)
	}
}
