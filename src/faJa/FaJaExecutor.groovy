package faJa

import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ArrayHelper
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.ClassLoader
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame
import faJa.memory.Heap
import faJa.natives.ArrayNatives

class FaJaExecutor {

	Heap heap
	ClassLoader classLoader

	static final MAIN_METHOD_SIGNATURE = 'main(1)'

	def run(String classPath,String [] args){

		String className = classPath.substring(classPath.lastIndexOf('/') + 1)
		String workingDir = classPath.substring(0,classPath.lastIndexOf('/') + 1)

		heap = new Heap()
		classLoader = new ClassLoader(heap, workingDir)
		heap.setClassLoader(classLoader)

		def ptr = classLoader.findClass(heap, className)

		//initialize all fields to null
		Integer fieldsSectionPtr = ClassAccessHelper.getFieldsSection(heap,ptr)
		Integer fieldsCount = heap.getSlot(fieldsSectionPtr) / Heap.SLOT_SIZE
		Integer nullObjectPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer mainObjectPointer = classLoader.singletonRegister.get(className)
		fieldsCount.times { field ->
			ObjectAccessHelper.setNewValue(heap, mainObjectPointer, field * Heap.SLOT_SIZE, nullObjectPointer)
		}

		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer arrayPtr = heap.createArray(arrayClassPtr, args.size(), nullObjectPointer)
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)

		args.eachWithIndex { String arg, Integer i ->
			Integer argStringPtr = heap.createString(stringClassPtr, arg)
			ArrayHelper.setNewValue(heap, arrayObjectPtr, i, argStringPtr)
		}
		ArrayHelper.setInsertIndex(heap,arrayPtr, args.size())


		def mainPtr = ClassAccessHelper.findMethod(heap, ptr, MAIN_METHOD_SIGNATURE)
		if(mainPtr == null){
			throw new InterpretException('In executed object must be main method!')
		}
		def mainBytecodePtr = mainPtr + Heap.SLOT_SIZE + Heap.SLOT_SIZE // mainPtr + bytecodeSize + constPoolPtr
		StackFrame mainStackFrame = new StackFrame()
		def mainSize = heap.getSlot(mainPtr) - Heap.SLOT_SIZE // mehtodSize - constPoolPtrSize


		mainStackFrame.bytecode =  heap.getBytes(mainBytecodePtr, mainSize)
		mainStackFrame.bytecodePtr = 0
		mainStackFrame.locals = [classLoader.singletonRegister.get(className),arrayPtr] // todo size in method
		mainStackFrame.methodStack = [] // todo size in method
		mainStackFrame.parent = null

		new Interpreter(heap, mainStackFrame, classLoader).interpret()
	}
}
