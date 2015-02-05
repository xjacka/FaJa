package faJa

import faJa.compilator.Compilator
import faJa.helpers.ClassAccessHelper
import faJa.initializators.BoolInit
import faJa.initializators.ClosureInit
import faJa.initializators.NumberInit
import faJa.initializators.ObjectInit
import faJa.initializators.StringInit
import faJa.initializators.SystemIOInit

class ClassLoader {

	private String workDir = ''
	Map classRegister = [:]
	final static FAJA_EXTENSION = '.faja'
	Compilator compilator = new Compilator()

	ClassLoader(Heap heap, String workingDir){
		this.workDir = workingDir
		init(heap)
	}

	def init(Heap heap){
		Integer pointer = heap.load(new ObjectInit().toBytecode())
		classRegister.put(Compilator.DEFAULT_PARENT,pointer)
		pointer = heap.load(new BoolInit().toBytecode())
		classRegister.put(Compilator.BOOL_CLASS,pointer)
		pointer = heap.load(new NumberInit().toBytecode())
		classRegister.put(Compilator.NUMBER_CLASS,pointer)
		pointer = heap.load(new StringInit().toBytecode())
		classRegister.put(Compilator.STRING_CLASS,pointer)
		pointer = heap.load(new ClosureInit().toBytecode())
		classRegister.put(Compilator.CLOSURE_CLASS,pointer)
		pointer = heap.load(new SystemIOInit().toBytecode())
		classRegister.put(Compilator.SYSTEMIO_CLASS,pointer)
	}

	def findClass(Heap heap, String className){
		def classPtr = classRegister.get(className)
		if(classPtr != null){
			return classPtr
		}

		// load parent class
		ClassFile classFile = compilator.compile(workDir + className + FAJA_EXTENSION)
		String parent = classFile.getParentName()
		Integer parentPtr = findClass(heap, parent)

		// adds parent field in current class classFile
		List parentFields = ClassAccessHelper.getAllFieldNames(heap, parentPtr)
		parentFields.each{ fieldName ->
			classFile.fields.add(classFile.constantPool.size())
			classFile.constantPool.add(fieldName)
		}

		load(heap, classFile)
	}

	// nahraje reprezentaci pomoci faJa.ClassFile na heapu
	def load(Heap heap,ClassFile classFile){
		def pointer = heap.load(classFile.toByteCode())
		classRegister.put(classFile.className, pointer)
		pointer
	}
}
