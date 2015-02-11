package faJa

import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
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

		//initialize all fields to null
		Integer fieldsSectionPtr = ClassAccessHelper.getFieldsSection(heap,ptr)
		Integer fieldsCount = heap.getPointer(fieldsSectionPtr) / Heap.SLOT_SIZE
		Integer nullObjectPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer mainObjectPointer = classLoader.singletonRegister.get(className)
		fieldsCount.times { field ->
			ObjectAccessHelper.setNewValue(heap, mainObjectPointer, field * Heap.SLOT_SIZE, nullObjectPointer)
		}

		def mainPtr = ClassAccessHelper.findMethod(heap, ptr, MAIN_METHOD_SIGNATURE)
		if(mainPtr == null){
			throw new InterpretException('In executed object must be main method!')
		}
		def mainBytecodePtr = mainPtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE // mainPtr + bytecodeSize + constPoolPtr
		StackFrame mainStackFrame = new StackFrame()
		def mainSize = heap.getPointer(mainPtr) - Heap.SLOT_SIZE // mehtodSize - constPoolPtrSize


		mainStackFrame.bytecode =  heap.getBytes(mainBytecodePtr, mainSize)
		mainStackFrame.bytecodePtr = 0
		mainStackFrame.locals = [classLoader.singletonRegister.get(className)] // todo size in method
//		mainStackFrame.classPtr = ptr
		mainStackFrame.methodStack = [] // todo size in method
		mainStackFrame.parent = null

		stack.add(mainStackFrame)

		interpreter.interpret()
	}
}
