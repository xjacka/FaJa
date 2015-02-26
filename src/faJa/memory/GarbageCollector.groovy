package faJa.memory

import faJa.interpreter.StackFrame
import faJa.interpreter.ClassLoader
import faJa.compilator.Compiler
/**
 * Created by Kamil on 18. 2. 2015.
 */
class GarbageCollector {

	enum SpecialClass {NUMBER(Compiler.NUMBER_CLASS), STRING(Compiler.STRING_CLASS), BOOL(Compiler.BOOL_CLASS),
	CLOSURE(Compiler.CLOSURE_CLASS), ARRAY(Compiler.ARRAY_CLASS)

		private String name
		SpecialClass(String name){
			this.name = name
		}
		def getName(){
			name
		}
	}
	byte [] getClassBytes(Heap heap, Integer classPtr){

	}

	byte [] getSingletonBytes(Heap heap, Integer singletonPtr){

	}

	Heap recreateHeap(ClassLoader classLoader,StackFrame currentStackFrame, Heap heap){
	 	def newClassRegister = [:]
	 	def newSingletonRegister = [:]
		Heap newHeap = new Heap()

		// load classes
		classLoader.classRegister.each{ k,v ->
			newClassRegister.put(k, newHeap.load(getClassBytes(heap, v)))
		}

		// load singletons
		classLoader.singletonRegister.each{ k,v ->
			newSingletonRegister.put(k, newHeap.load(getSingletonBytes(heap, v)))
		}

		Set stackPointers = getStackPointers(currentStackFrame)
		Map newValues = [:]
		stackPointers.each {
			newValues.put(it, null)
		}

		loadStackObjects(heap, newHeap, stackPointers, newValues)


		// update pointeres in stackFrame
		updateStackFrame(currentStackFrame, newValues)

		newHeap
	}

	def loadStackObjects(Heap heap , Heap newHeap, stackPointers, newValues) {

		// todo load object to new heap and add new pointers to newValue collection
	}

	Set<Integer> getStackPointers(StackFrame stackFrame){
		Set pointers = [] as Set
		if(!stackFrame){
			return []
		}
		pointers.addAll(stackFrame.locals)
		pointers.addAll(stackFrame.methodStack)
		pointers.addAll(stackFrame.parent)
		pointers.addAll(stackFrame.parent)
		pointers
	}

	def getPointers(Heap heap, Integer objectPtr){

	}

	def getObjectPointers(){

	}


	def updateStackFrame(StackFrame stackFrame, Map newValues){
		if(!stackFrame){
			return
		}
		def newLocals = stackFrame.locals.collect{
			newValues.get(it)
		}
		stackFrame.locals = newLocals
		def newMethodStack = stackFrame.methodStack.collect{
			newValues.get(it)
		}
		stackFrame.methodStack = newMethodStack
		updateStackFrame(stackFrame.parent)
		updateStackFrame(stackFrame.environment)
	}

}
