package faJa.memory

import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.StackFrame
import faJa.interpreter.ClassLoader
import faJa.compilator.Compiler
/**
 * Created by Kamil on 18. 2. 2015.
 */
class GarbageCollector {

	ClassLoader classLoader
	Heap heap
	List stackFrames

	GarbageCollector(ClassLoader classLoader, Heap heap, List stackFrames) {
		this.classLoader = classLoader
		this.heap = heap
		this.stackFrames = stackFrames
	}

	public def run(){
		Map<String, Integer> classRegister = [:]
		Map<String,Integer> singletonRegister = [:]
		
		copyInitClasses(classRegister)
		copySingletons()
		
		// todo delete cache
		// todo update registers
		// todo update stackFrames
	}
	
	private copyInitClasses(Map<String, Integer> classRegister){
		classLoader.embeddedClassList.each { String className ->
			Integer classPtr = classLoader.classRegister.get(className)
			copyClass(classPtr,classRegister, className)
		}
	}
	
	private void copyClass(Integer classPtr, Map<String, Integer> classRegister, String className = null){
		className = className == null ? ClassAccessHelper.getName(heap, classPtr) : className
		Integer classSize = ClassAccessHelper.getClassSize(heap, classPtr)
		Integer newClassPtr = heap.load(heap.getBytes(classPtr,classSize))
		classRegister.put(className,newClassPtr)
		heap.setPointer(classPtr,newClassPtr)
	}
	
	private copySingletons(Map classRegister, Map<String, Integer> singletonRegister) {
		classLoader.singletonRegister.each { String className, Integer objectPtr ->
			Integer newPtr = copyObject(objectPtr, classRegister)
			singletonRegister.put(className, newPtr)
		}
	}
	
	private Integer copyObject(Integer objectPtr, Map classRegister){
		Integer firstPtr = heap.getPointer(objectPtr)
		if(!isOldPointer(firstPtr)){
			//firstPtr is not class pointer but pointer to object recreated on new heap
			return firstPtr
		}
		Integer classPtr = firstPtr
		Integer classFirstPtr = heap.getPointer(classPtr)
		if(!isCopiedClass(classFirstPtr)){
			copyClass(classPtr, classRegister)
		}
		
		Integer objectSize = ClassAccessHelper.getObjectSize(heap,classPtr)
		Integer newPtr = heap.load(heap.getBytes(objectPtr,objectSize))
		
		newPtr
	}

	boolean isCopiedClass(Integer classPtr){
		classPtr != 0
		
	}
	boolean isOldPointer(Integer objectPtr) {
		heap.heapStart > objectPtr || heap.heapEnd < objectPtr
	}
//	enum SpecialClass {NUMBER(Compiler.NUMBER_CLASS), STRING(Compiler.STRING_CLASS), BOOL(Compiler.BOOL_CLASS),
//	CLOSURE(Compiler.CLOSURE_CLASS), ARRAY(Compiler.ARRAY_CLASS)
//
//		private String name
//		SpecialClass(String name){
//			this.name = name
//		}
//		def getName(){
//			name
//		}
//	}
//	byte [] getClassBytes(Heap heap, Integer classPtr){
//
//	}
//
//	byte [] getSingletonBytes(Heap heap, Integer singletonPtr){
//
//	}
//
//	Heap recreateHeap(ClassLoader classLoader,StackFrame currentStackFrame, Heap heap){
//	 	def newClassRegister = [:]
//	 	def newSingletonRegister = [:]
//		Heap newHeap = new Heap()
//
//		// load classes
//		classLoader.classRegister.each{ k,v ->
//			newClassRegister.put(k, newHeap.load(getClassBytes(heap, v)))
//		}
//
//		// load singletons
//		classLoader.singletonRegister.each{ k,v ->
//			newSingletonRegister.put(k, newHeap.load(getSingletonBytes(heap, v)))
//		}
//
//		Set stackPointers = getStackPointers(currentStackFrame)
//		Map newValues = [:]
//		stackPointers.each {
//			newValues.put(it, null)
//		}
//
//		loadStackObjects(heap, newHeap, stackPointers, newValues)
//
//
//		// update pointeres in stackFrame
//		updateStackFrame(currentStackFrame, newValues)
//
//		newHeap
//	}
//
//	def loadStackObjects(Heap heap , Heap newHeap, stackPointers, newValues) {
//
//		// todo load object to new heap and add new pointers to newValue collection
//	}
//
//	Set<Integer> getStackPointers(StackFrame stackFrame){
//		Set pointers = [] as Set
//		if(!stackFrame){
//			return []
//		}
//		pointers.addAll(stackFrame.locals)
//		pointers.addAll(stackFrame.methodStack)
//		pointers.addAll(stackFrame.parent)
//		pointers.addAll(stackFrame.parent)
//		pointers
//	}
//
//	def getPointers(Heap heap, Integer objectPtr){
//
//	}
//
//	def getObjectPointers(){
//
//	}
//
//
//	def updateStackFrame(StackFrame stackFrame, Map newValues){
//		if(!stackFrame){
//			return
//		}
//		def newLocals = stackFrame.locals.collect{
//			newValues.get(it)
//		}
//		stackFrame.locals = newLocals
//		def newMethodStack = stackFrame.methodStack.collect{
//			newValues.get(it)
//		}
//		stackFrame.methodStack = newMethodStack
//		updateStackFrame(stackFrame.parent)
//		updateStackFrame(stackFrame.environment)
//	}

}
