package faJa.interpreter

import faJa.exceptions.InterpretException
import faJa.helpers.ByteHelper

class StackFrame {

	List locals
	Integer localCnt = null
	StackFrame parent
	StackFrame environment = null
	byte [] bytecode
	Integer bytecodePtr

	List<Integer> methodStack

	def incrementBP(int i){
		bytecodePtr += i
	}

	def getThisInst(){
		locals[0]
	}
	Integer loadLocal(Integer idx){
		// for test purpose only
		if(environment && localCnt == null){
			throw new InterpretException('stackFrame with parent environment doesn\'t have localCnt set')
		}
		if(environment && idx >= localCnt){
			return environment.loadLocal(idx - localCnt)
		}

		locals[idx]
	}

	Integer storeLocal(Integer idx, Integer value){
		// for test purpose only
		if(environment && localCnt == null){
			throw new InterpretException('stackFrame with parent environment doesn\'t have localCnt set')
		}
		if(environment && idx >= localCnt){
				return environment.storeLocal(idx - localCnt, value)
		}
		locals[idx] = value
	}

	Integer getCurrentByte(){
		if(bytecode.length <= bytecodePtr ){
			throw new InterpretException('byte out of bound')
		}
		ByteHelper.unsignedValue(bytecode[bytecodePtr])
	}

	Integer getCurrentPointer(){
		if(bytecode.length <= bytecodePtr + 1 ){
			throw new InterpretException('pointer out of bound')
		}
		ByteHelper.bytesToIntAt(bytecode, bytecodePtr)
	}

}
