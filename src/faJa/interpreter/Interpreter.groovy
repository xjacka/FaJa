package faJa.interpreter

import faJa.Heap
import faJa.Instruction
import faJa.ClassLoader
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.MethodHelper
import faJa.helpers.ObjectAccessHelper
import faJa.natives.NativesRegister

class Interpreter {
	Heap heap
	ClassLoader classLoader
	StackFrame currentStackFrame
	static final Integer INSTRUCTION_SIZE = 1
	static final Integer DEFAULT_ARRAY_SIZE = 50

	Interpreter(Heap heap, StackFrame stackFrame, ClassLoader classLoader){
		this.heap = heap
		this.currentStackFrame = stackFrame
		this.classLoader = classLoader
	}

	def interpret(){
		while(currentStackFrame.bytecode.length > currentStackFrame.bytecodePtr){
			switch(currentStackFrame.currentByte){
				case Instruction.INIT_BOOL.id:
					processInitBool()
					break
				case Instruction.INIT.id:
					processInit()
					break
				case Instruction.GETFIELD.id:
					processGetfield()
					break
				case Instruction.INIT_CLOSURE.id:
					processInitClosure()
					break
				case Instruction.INIT_NUM.id:
					processInitNum()
					break
				case Instruction.INIT_STRING.id:
					processInitString()
					break
				case Instruction.INVOKE.id:
					processInvoke()
					break
				case Instruction.LOAD.id:
					processLoad()
					break
				case Instruction.PUSH_NULL.id:
					processPushNull()
					break
				case Instruction.PUTFIELD.id:
					processPutfield()
					break
				case Instruction.STORE.id:
					processStore()
					break
				case Instruction.INIT_ARRAY.id:
					processInitArray()
					break
				default: throw new InterpretException("Unexpected instruction found")
			}
		}
		if(currentStackFrame.parent){
			try {
				currentStackFrame.parent.methodStack.push(currentStackFrame.methodStack.pop())
			}catch (Exception e){
				e.printStackTrace()
				throw InterpretException('return value exception')
			}
		}
	}

	def processGetfield() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get field name
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, currentStackFrame.thisInst)
		Integer constPoolPtr = currentStackFrame.currentPointer
		String fieldName = ClassAccessHelper.getConstantPoolValue(heap, classPtr, constPoolPtr)

		// get field index
		Integer targetObjectPtr = currentStackFrame.methodStack.pop()
		Integer targetClassPtr = ObjectAccessHelper.getClassPointer(heap, targetObjectPtr)
		Integer fieldIdx = ClassAccessHelper.findFieldIndex(heap, targetClassPtr, fieldName)

		// get field value
		Integer result = ObjectAccessHelper.valueOf(heap,targetObjectPtr, fieldIdx)

		// push value to stack
		currentStackFrame.methodStack.push(result)
		currentStackFrame.incrementBP(Instruction.GETFIELD.params)
	}

	def processPutfield() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get field name
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, currentStackFrame.thisInst)
		Integer constPoolPtr = currentStackFrame.currentPointer
		String fieldName = ClassAccessHelper.getConstantPoolValue(heap, classPtr, constPoolPtr)

		// get field index
		Integer targetObjectPtr = currentStackFrame.methodStack.pop()
		Integer targetClassPtr = ObjectAccessHelper.getClassPointer(heap, targetObjectPtr)
		Integer fieldIdx = ClassAccessHelper.findFieldIndex(heap, targetClassPtr, fieldName)

		// get new value from stack
		Integer newValue = currentStackFrame.methodStack.pop()

		// set new field value
		ObjectAccessHelper.setNewValue(heap,targetObjectPtr, fieldIdx, newValue)
		currentStackFrame.incrementBP(Instruction.PUTFIELD.params)

	}

	def processLoad() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)
		Integer localIdx = currentStackFrame.currentByte
		currentStackFrame.methodStack.add(currentStackFrame.locals[localIdx])
		currentStackFrame.incrementBP(Instruction.LOAD.params)
	}

	def processStore() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)
		Integer localIdx = currentStackFrame.currentByte
		currentStackFrame.locals[localIdx] = currentStackFrame.methodStack.pop()
		currentStackFrame.incrementBP(Instruction.STORE.params)
	}

	def processPushNull() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)
		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		currentStackFrame.methodStack.push(nullPtr)
	}

	def processInitClosure() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		Integer initClassPtr = ObjectAccessHelper.getClassPointer(heap, currentStackFrame.thisInst)
		Integer closureClassPtr = classLoader.findClass(heap, Compiler.CLOSURE_CLASS)
		Integer closureIdx = currentStackFrame.currentByte
		currentStackFrame.incrementBP(Instruction.INIT_CLOSURE.params)
		Integer newClosurePtr = heap.createClosure(closureClassPtr, initClassPtr, closureIdx)

		currentStackFrame.methodStack.push(newClosurePtr)

	}

	def processInitNum() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)

		// get new number value
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		Integer newNumberValue = parseInteger(ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr))

		// number object initialization on heap
		Integer newObjectPtr = heap.createNumber(numberClassPtr, newNumberValue)
		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT_NUM.params)
	}

	def processInitArray(){
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to array class
		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)

		// array object initialization on heap
		Integer nullPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer newObjectPtr = heap.createArray(arrayClassPtr, DEFAULT_ARRAY_SIZE, nullPointer)
		currentStackFrame.methodStack.add(newObjectPtr)
	}

	private parseInteger(String toParse){
		def result = 0
		try{
			result = toParse.toInteger()
		}catch (NumberFormatException e){
			throw new InterpretException('parse int from constant pool failed')
		}
		result
	}

	def processInit() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		String classOfNewObject = ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr)
		Integer classOfNewObjectPtr = classLoader.findClass(heap, classOfNewObject)

		Integer newObjectPtr
		if(classLoader.singletonRegister.containsKey(classOfNewObject)){
			newObjectPtr = classLoader.singletonRegister.get(classOfNewObject)
		} else{
			// object initialization on heap
			newObjectPtr = heap.createObject(classOfNewObjectPtr)

			//initialize all fields to null
			Integer fieldsSectionPtr = ClassAccessHelper.getFieldsSection(heap,classPtr)
			Integer fieldsCount = heap.getPointer(fieldsSectionPtr) / Heap.SLOT_SIZE
			Integer nullPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
			fieldsCount.times { field ->
				ObjectAccessHelper.setNewValue(heap,newObjectPtr,field * Heap.SLOT_SIZE,nullPointer)
			}
		}

		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT.params)
	}

	def processInitBool() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)

		// get new bool value
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		String newStringValue = ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr)
		Boolean newBoolValue = parseByte(newStringValue)

		// bool object initialization on heap
		Integer newObjectPtr = heap.createBool(boolClassPtr, newBoolValue)
		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT_BOOL.params)
	}

	private parseByte(String toParse){
		if(toParse == Compiler.TRUE_STRING_VALUE){
			return true
		}
		if(toParse == Compiler.FALSE_STRING_VALUE){
			return false
		}
		throw new InterpretException('parse bool from constant pool failed')
	}

	def processInitString() {
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)

		// get new string value
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		String newStringValue = ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr)

		// string object initialization on heap
		Integer newObjectPtr = heap.createString(stringClassPtr, newStringValue)
		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT_STRING.params)
	}

	// method invoke: first loads arguments on stack and then target object pointer
	def processInvoke(){
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get target object class
		Integer targetObjectPtr = currentStackFrame.methodStack.pop()
		Integer targetClassPtr = ObjectAccessHelper.getClassPointer(heap, targetObjectPtr)

		// get method signature
		Integer constPoolPtr = currentStackFrame.currentPointer
		currentStackFrame.incrementBP(Instruction.INVOKE.params)
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		String methodSignature = ClassAccessHelper.getConstantPoolValue(heap, classPtr, constPoolPtr)

		Integer argsCount = MethodHelper.getArgsCount(methodSignature)
		// args will be in same order in both stacks
		def reversedArgList = []
		argsCount.times{
			reversedArgList.push(currentStackFrame.methodStack.pop())
		}

		if(targetClassPtr == null){
			throw new InterpretException("Can not invoke method '"+methodSignature+"' on object with " + targetClassPtr + " class")
		}
		List resultPair = ClassAccessHelper.findMethodWithSuper(heap, targetClassPtr, methodSignature,classLoader)
		Integer methodPtr = resultPair[1]
		if(methodPtr == null){
			throw new InterpretException('invoked method "'+ methodSignature +'" not found on ' + ClassAccessHelper.getName(heap, targetClassPtr))
		}
		if(ClassAccessHelper.isNative(heap,methodPtr)){
			String nativeMethodClassName = ClassAccessHelper.getName(heap, resultPair[0]) // result[0] - pointer on class with method bytecode
			Closure nativeMethod = NativesRegister.register.get(methodSignature + nativeMethodClassName)
			reversedArgList.reverse().each {
				currentStackFrame.methodStack.push(it)
			}
			currentStackFrame.methodStack.push(targetObjectPtr)

			nativeMethod.call(currentStackFrame, heap, classLoader)
			return
		}

		// new stack frame creation
		def newStackFrame = new StackFrame()
		newStackFrame.locals = []
		newStackFrame.locals.add(targetObjectPtr)
		newStackFrame.locals.addAll(reversedArgList.reverse())
		newStackFrame.parent = currentStackFrame
		newStackFrame.bytecodePtr = 0
		newStackFrame.methodStack = []
		newStackFrame.bytecode = MethodHelper.getBytecode(heap, methodPtr)

		new Interpreter(heap, newStackFrame, classLoader).interpret()
	}
}
