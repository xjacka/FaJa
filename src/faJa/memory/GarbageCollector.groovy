package faJa.memory

import faJa.decompilator.ClassDecompilator
import faJa.decompilator.ObjectDecompiler
import faJa.helpers.ArrayHelper
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.ClassLoader
import faJa.compilator.Compiler
import faJa.interpreter.StackFrame
import faJa.natives.ClosureRegister

class GarbageCollector {

	ClassLoader classLoader
	Heap heap
	List stackFrames
	Map<String, Integer> classRegister = [:]
	Map<String, Integer> singletonRegister = [:]
	Map<Integer, StackFrame> closureEnvironments = [:]
	Map<Integer, Thread> thread = [:]
	Deque copyQueue = new ArrayDeque<Integer>()
	Deque updatePointersQueue = new ArrayDeque<Integer>()


	GarbageCollector(ClassLoader classLoader, Heap heap) {
		this.classLoader = classLoader
		this.heap = heap
		this.stackFrames = classLoader.stackFrameRegister.values().findAll()
	}

	public def run(){
		copyInitClasses()
		
		copySingletons()

		fillQueue()

		while(!copyQueue.isEmpty()){
			Integer objectPtr = copyQueue.poll()
			copyObject(objectPtr)
		}

		while(!updatePointersQueue.isEmpty()){
			Integer oldPointer = updatePointersQueue.poll()
			Integer objectPtr = heap.getPointer(oldPointer)
			updateObjectPointer(objectPtr)
		}
		
		classLoader.classRegister = classRegister
		classLoader.singletonRegister = singletonRegister
		updateClosureEnvironments()
		stackFrames.each { sf ->
			updateStackFramePointers(sf)
		}
		classLoader.threads = thread
		heap.methodCache = [:]
	}

	private updateClosureEnvironments(){
		
		closureEnvironments.each{ ptr, sf ->
			updateStackFramePointers(sf)	
		}
		ClosureRegister.closureEnvironments = closureEnvironments
	}
	
	private updateStackFramePointers(StackFrame stackFrame){
		if(!stackFrame){
			return
		}
		stackFrame.locals = stackFrame.locals.collect{ valPtr ->
			if(isOldPointer(valPtr)){
				return heap.getPointer(valPtr)
			}
			else{
				return valPtr
			}
		}
		stackFrame.methodStack = stackFrame.methodStack.collect{ valPtr ->
			if(isOldPointer(valPtr)){
				return heap.getPointer(valPtr)
			}
			else{
				return valPtr
			}

		}
		updateStackFramePointers(stackFrame.environment)
		updateStackFramePointers(stackFrame.parent)

	}
	
	private void updateObjectPointer(Integer objectPtr){
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)

		Integer objectSize = ClassAccessHelper.getObjectSize(heap,classPtr)
		
		Integer objectPointersCnt = objectSize / Heap.HEAP_POINTER_SIZE

		objectPointersCnt.times{ i ->
			Integer pointerPtr = objectPtr + Heap.HEAP_POINTER_SIZE * i
			Integer oldPtr = heap.getPointer(pointerPtr)
			if(isOldPointer(oldPtr)){
				Integer newPtr = heap.getPointer(oldPtr)
				heap.setPointer(pointerPtr, newPtr)
			}
		}

		new ObjectDecompiler().decompile(heap, objectPtr)

		if(isArrayObject(classPtr)){
			updateArrayPointers(objectPtr)
		}
	}

	private isArrayObject(Integer classPtr){
		String className = ClassAccessHelper.getName(heap, classPtr)
		className == Compiler.ARRAY_CLASS
	}

	private updateArrayPointers(Integer arrayPtr){
		Integer arrayObjectPtr= ArrayHelper.getArrayObjectPtr(heap, arrayPtr)
		Integer size = heap.getSlot(arrayObjectPtr)

		size.times{ i ->
			Integer pointerPtr = arrayObjectPtr + Heap.SLOT_SIZE + Heap.HEAP_POINTER_SIZE * i
			Integer oldPtr = heap.getPointer(pointerPtr)
			Integer newPtr = heap.getPointer(oldPtr)
			heap.setPointer(pointerPtr, newPtr)
		}

	}

	private fillQueue() {
		Set pointers = []
		stackFrames.each{
			pointers.addAll(getStackPointers(it))
		}
		ClosureRegister.closureEnvironments.values().each {
			pointers.addAll(getStackPointers(it))
		}
		pointers.each {
			copyQueue.add(it)
		}
	}
	
	Set<Integer> getStackPointers(StackFrame stackFrame){
		Set pointers = [] as Set
		if(!stackFrame){
			return []
		}
		pointers.addAll(stackFrame.locals)
		pointers.addAll(stackFrame.methodStack)
		pointers.addAll(getStackPointers(stackFrame.environment))
		pointers.addAll(getStackPointers(stackFrame.parent))
		pointers
	}
	
	private copyInitClasses(){
		classLoader.embeddedClassList.each { String className ->
			Integer classPtr = classLoader.classRegister.get(className)
			copyClass(classPtr)
		}
	}
	
	private Integer copyClass(Integer classPtr){
		Integer classFirstPtr = heap.getPointer(classPtr)
		String className = ClassAccessHelper.getName(heap, classPtr)
		
		if(!isCopiedClass(classFirstPtr)){
			Integer classSize = ClassAccessHelper.getClassSize(heap, classPtr)
			Integer newClassPtr = heap.load(heap.getBytes(classPtr,classSize))
			classRegister.put(className,newClassPtr)
			heap.setPointer(classPtr,newClassPtr)
//			new ClassDecompilator().decompileHeap(heap, newClassPtr)
			return newClassPtr
		}
		
		return classFirstPtr
	}
	
	private copySingletons() {
		classLoader.singletonRegister.each { String className, Integer objectPtr ->
			Integer newPtr = copyObject(objectPtr)
			singletonRegister.put(className, newPtr)
		}
	}
	
	private Integer copyObject(Integer objectPtr){
		Integer firstPtr = heap.getPointer(objectPtr)
		if(!isOldPointer(firstPtr)){
			//firstPtr is not class pointer but pointer to object recreated on new heap
			return firstPtr
		}
		updatePointersQueue.add(objectPtr)
		Integer classPtr = firstPtr
		copyClass(classPtr)

		Integer newPtr = processObject(objectPtr, classPtr)
//		new ObjectDecompiler().decompile(heap, newPtr)
		newPtr
	}

	Integer processObject(Integer objectPtr, Integer classPtr){
		String className = ClassAccessHelper.getName(heap, classPtr)
		switch(className){
			case Compiler.ARRAY_CLASS:
				return processArray(objectPtr)
			case Compiler.BOOL_CLASS:
				return processBool(objectPtr)
			case Compiler.CLOSURE_CLASS:
				return processClosure(objectPtr)
			case Compiler.NUMBER_CLASS:
				return processNumber(objectPtr)
			case Compiler.STRING_CLASS:
				return processString(objectPtr)
			case Compiler.THREAD_CLASS:
				return processThread(objectPtr)
		}
		// Ordinary object
		Integer objectSize = ClassAccessHelper.getObjectSize(heap,classPtr)
		Integer newPtr = shallowCopy(objectPtr, objectSize)
		Integer objectFields = (objectSize - Heap.HEAP_POINTER_SIZE) / Heap.HEAP_POINTER_SIZE
		
		objectFields.times{ i ->
			Integer fieldPtr = objectPtr + Heap.HEAP_POINTER_SIZE + Heap.HEAP_POINTER_SIZE * i
			copyQueue.add(heap.getPointer(fieldPtr))
		}
		newPtr
	}
	
	private processThread(int objectPtr){
		Integer newPtr = shallowCopy(objectPtr, Heap.THREAD_SIZE)
		thread.put(newPtr, classLoader.threads.get(objectPtr))
		newPtr
	}
	
	private processArray(int objectPtr){
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap, objectPtr)
		Integer arraySize = heap.getSlot(arrayObjectPtr)
		arraySize.times{
			copyQueue.add(heap.getPointer(arrayObjectPtr + Heap.SLOT_SIZE + Heap.HEAP_POINTER_SIZE * it))
		}
		Integer newArrayObjectPtr = shallowCopy(arrayObjectPtr, arraySize * Heap.HEAP_POINTER_SIZE + Heap.SLOT_SIZE)
		
		Integer newArrayPtr = shallowCopy(objectPtr, Heap.ARRAY_SIZE)
		
		ArrayHelper.setArrayObjectPtr(heap, newArrayPtr, newArrayObjectPtr) // reset array object ptr
		
		newArrayPtr
	}
	
	private shallowCopy(int objectPtr, int size){
		Integer newPtr = heap.load(heap.getBytes(objectPtr, size))
		heap.setPointer(objectPtr, newPtr)
		newPtr
	}
	
	private processBool(int objectPtr){
		shallowCopy(objectPtr, Heap.BOOL_SIZE)
	}
	
	private processNumber(int objectPtr){
		shallowCopy(objectPtr, Heap.NUMBER_SIZE)
	}
	
	private processString(int objectPtr){
		Integer length = heap.getSlot(objectPtr + Heap.HEAP_POINTER_SIZE)
		shallowCopy(objectPtr, Heap.STRING_HEAD_SIZE + length)
	}
	
	private processClosure(int objectPtr){
		Integer initClassPtr = heap.getPointer(objectPtr + Heap.HEAP_POINTER_SIZE)
		Integer newInitClassPtr = copyClass(initClassPtr)
		
		Integer newPtr = shallowCopy(objectPtr, Heap.CLOSURE_SIZE)
		// FIXME bylo tady  + Heap.SLOT_SIZE (to je podle me spatne, odstranil jsem)
		heap.setPointer(newPtr + Heap.HEAP_POINTER_SIZE, newInitClassPtr) // set new initClassPtr to recreated object
		closureEnvironments.put(newPtr, ClosureRegister.get(objectPtr))
		
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
//		def getName(){ == 0
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
