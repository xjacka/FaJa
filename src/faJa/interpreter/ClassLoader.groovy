package faJa.interpreter

import faJa.initializators.ThreadInit
import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.compilator.representation.ClassFile
import faJa.helpers.ClassAccessHelper
import faJa.initializators.ArrayInit
import faJa.initializators.BoolInit
import faJa.initializators.ClosureInit
import faJa.initializators.NullInit
import faJa.initializators.NumberInit
import faJa.initializators.ObjectInit
import faJa.initializators.StringInit
import faJa.initializators.SystemIOInit

class ClassLoader {

	private String workDir = ''
	Map<String, Integer> classRegister = [:]
	Map<String,Integer> singletonRegister = [:]
	Map<Integer,Thread> threads = [:].asSynchronized()
	Map<Thread, StackFrame> stackFrameRegister = [:].asSynchronized()
	List embeddedClassList = [Compiler.DEFAULT_PARENT,
	                        Compiler.BOOL_CLASS,
	                        Compiler.NUMBER_CLASS,
	                        Compiler.STRING_CLASS,
	                        Compiler.CLOSURE_CLASS,
	                        Compiler.SYSTEMIO_CLASS,
	                        Compiler.ARRAY_CLASS,
	                        Compiler.THREAD_CLASS,
	                        Compiler.NULL_CLASS]

	final static FAJA_EXTENSION = '.faja'
	Compiler compiler

	ClassLoader(Heap heap, String workingDir){
		this.workDir = workingDir
		init(heap)
	}

	def init(Heap heap){
		Integer pointer = heap.load(new ObjectInit().toBytecode())
		classRegister.put(Compiler.DEFAULT_PARENT,pointer)

		pointer = heap.load(new BoolInit().toBytecode())
		classRegister.put(Compiler.BOOL_CLASS,pointer)

		pointer = heap.load(new NumberInit().toBytecode())
		classRegister.put(Compiler.NUMBER_CLASS,pointer)

		pointer = heap.load(new StringInit().toBytecode())
		classRegister.put(Compiler.STRING_CLASS,pointer)

		pointer = heap.load(new ClosureInit().toBytecode())
		classRegister.put(Compiler.CLOSURE_CLASS,pointer)

		pointer = heap.load(new SystemIOInit().toBytecode())
		classRegister.put(Compiler.SYSTEMIO_CLASS,pointer)
		Integer objPtr = heap.createObject(pointer)
		singletonRegister.put(Compiler.SYSTEMIO_CLASS, objPtr)

		pointer = heap.load(new ArrayInit().toBytecode())
		classRegister.put(Compiler.ARRAY_CLASS,pointer)

		pointer = heap.load(new ThreadInit().toBytecode())
		classRegister.put(Compiler.THREAD_CLASS,pointer)

		pointer = heap.load(new NullInit().toBytecode())
		classRegister.put(Compiler.NULL_CLASS,pointer)
		objPtr = heap.createObject(pointer)
		singletonRegister.put(Compiler.NULL_CLASS, objPtr)
	}

	synchronized Integer findClass(Heap heap, String className){
		Integer classPtr = classRegister.get(className)
		if(classPtr != null){
			return classPtr
		}

		// load parent class
		compiler = new Compiler()
		ClassFile classFile = compiler.compile(workDir + className + FAJA_EXTENSION)
		String parent = classFile.getParentName()
		Integer parentPtr = findClass(heap, parent)

		// adds parent field in current class classFile
		List parentFields = ClassAccessHelper.getAllFieldNames(heap, parentPtr)
		parentFields.each{ fieldName ->
			classFile.fields.add(classFile.constantPool.add(fieldName))
		}

		classPtr = load(heap, classFile)
		// creates singletons
		if(classPtr != null && classFile.isSingleton){
			Integer objPtr = heap.createObject(classPtr)
			singletonRegister.put(classFile.className, objPtr)
		}
		classPtr
	}

	// nahraje reprezentaci pomoci faJa.compilator.representation.ClassFile na heapu
	synchronized Integer load(Heap heap,ClassFile classFile){
		Integer pointer = heap.load(classFile.toByteCode())
		classRegister.put(classFile.className, pointer)
		pointer
	}
}
