package faJa

import faJa.helpers.ClassAccessHelper
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame

class FaJaExecutor {

	Heap heap
	ClassLoader classLoader
	List<StackFrame> stack
	Interpreter interpreter

	static final MAIN_METHOD_SIGNATURE = 'main(0)'

	def run(String classPath){

		String className = classPath.substring(classPath.lastIndexOf('/') + 1)
		String workingDir = classPath.substring(0,classPath.lastIndexOf('/') + 1)

		heap = new Heap()
		classLoader = new ClassLoader(heap, workingDir)
		stack = new ArrayList<>()
		interpreter = new Interpreter(heap,stack,classLoader)

		def ptr = classLoader.findClass(heap, className)

		def mainPtr = ClassAccessHelper.findMethod(heap, ptr, MAIN_METHOD_SIGNATURE)
		def mainBytecodePtr = mainPtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE // mainPtr + bytecodeSize + constPoolPtr
		StackFrame mainStackFrame = new StackFrame()
		def mainSize = heap.getPointer(mainPtr) - Heap.SLOT_SIZE // mehtodSize - constPoolPtrSize


		mainStackFrame.bytecode =  heap.getBytes(mainBytecodePtr, mainSize)
		mainStackFrame.bytecodePtr = 0
		mainStackFrame.locals = [classLoader.singletonRegister.get(className)] // todo size in method
		mainStackFrame.classPtr = ptr
		mainStackFrame.methodStack = [] // todo size in method
		mainStackFrame.parent = null

		stack.add(mainStackFrame)

		interpreter.interpret()
	}
}
