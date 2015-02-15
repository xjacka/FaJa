package faJa.interpreter

import faJa.exceptions.InterpretException
import faJa.helpers.ByteHelper

class StackFrame {

	List locals
	Integer parentLocalCnt = null
	StackFrame parent
	StackFrame environment = null
	byte [] bytecode
	Integer bytecodePtr

	List<Integer> methodStack

	def incrementBP(int i){
		bytecodePtr += i
	}

	def getThisInst(){
		if(parentLocalCnt == null){
			return locals[0]
		}
		environment.thisInst
	}
	Integer loadLocal(Integer idx){
		// for test purpose only
		if(environment && parentLocalCnt == null){
			throw new InterpretException('stackFrame with parent environment doesn\'t have parentLocalCnt set')
		}
		if(environment){
			if( idx < parentLocalCnt ){
				return environment.loadLocal(idx)
			}
			return locals[idx - parentLocalCnt]
		}

		locals[idx]
	}

	Integer storeLocal(Integer idx, Integer value){
		// for test purpose only
		if(environment && parentLocalCnt == null){
			throw new InterpretException('stackFrame with parent environment doesn\'t have parentLocalCnt set')
		}
		if(environment){
			if(idx < parentLocalCnt){
				return environment.storeLocal(idx, value)
			}
			return locals[idx - parentLocalCnt] = value
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
