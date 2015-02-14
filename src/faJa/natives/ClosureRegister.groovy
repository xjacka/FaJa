package faJa.natives

import faJa.interpreter.StackFrame

/**
 * Created by Kamil on 13. 2. 2015.
 */
class ClosureRegister {
	static Map<Integer, StackFrame> closureEnvironments = [:]

	static register(Integer closurePtr, StackFrame environment){
		closureEnvironments.put(closurePtr, environment)
	}
	static StackFrame get(Integer closurePtr){
		closureEnvironments.get(closurePtr)
	}
	static unregister(Integer closurePtr){
		closureEnvironments.remove(closurePtr)
	}

}
