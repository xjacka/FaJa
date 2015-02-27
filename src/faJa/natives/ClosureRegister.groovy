package faJa.natives

import faJa.interpreter.StackFrame

class ClosureRegister {
	static Map<Integer, StackFrame> closureEnvironments = [:]

	static register(Integer closurePtr, StackFrame environment){
		closureEnvironments.put(closurePtr, environment)
	}
	static StackFrame get(Integer closurePtr){
		closureEnvironments.get(closurePtr)
	}
}
