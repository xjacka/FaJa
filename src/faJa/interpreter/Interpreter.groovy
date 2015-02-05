package faJa.interpreter

import faJa.Heap
import faJa.Instruction
import faJa.ClassLoader
import faJa.compilator.Compilator
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.MethodHelper
import faJa.helpers.ObjectAccessHelper

class Interpreter {
	Heap heap
	List<StackFrame> stack
	ClassLoader classLoader

	static final String INSTRUCTION_SIZE = 1

	Interpreter(Heap heap, List<StackFrame> stack, ClassLoader classLoader){
		this.heap = heap
		this.stack = stack
		this.classLoader = classLoader
	}

	def interpret(){
		def currentStackFrame = stack.last()
		processStackFrame(currentStackFrame)
		if(!currentStackFrame.methodStack.empty) {
			currentStackFrame.methodStack.pop()
		}

	}

	def processStackFrame(StackFrame currentStackFrame){
		while(currentStackFrame.bytecode.length > currentStackFrame.bytecodePtr){
			switch(currentStackFrame.currentByte){
				case Instruction.INIT_BOOL:
					processInitBool()
					break
				case Instruction.INIT:
					processInit()
					break
				case Instruction.GETFIELD:
					processGetfield()
					break
				case Instruction.INIT_CLOSURE:
					processInitClosure()
					break
				case Instruction.INIT_NUM:
					processInitNum()
					break
				case Instruction.INIT_STRING:
					processInitString()
					break
				case Instruction.INVOKE:
					processInvoke()
					break
				case Instruction.LOAD:
					processLoad()
					break
				case Instruction.PUSH_NULL:
					processPushNull()
					break
				case Instruction.PUTFIELD:
					processPutfield()
					break
				case Instruction.STORE:
					processStore()
					break
			}
		}
		stack.pop()
	}

	def processGetfield() {
		StackFrame currentStackFrame = stack.last()
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
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get new value from stack
		Integer newValue = currentStackFrame.methodStack.pop()

		// get field name
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, currentStackFrame.thisInst)
		Integer constPoolPtr = currentStackFrame.currentPointer
		String fieldName = ClassAccessHelper.getConstantPoolValue(heap, classPtr, constPoolPtr)


		// get field index
		Integer targetObjectPtr = currentStackFrame.methodStack.pop()
		Integer targetClassPtr = ObjectAccessHelper.getClassPointer(heap, targetObjectPtr)
		Integer fieldIdx = ClassAccessHelper.findFieldIndex(heap, targetClassPtr, fieldName)

		// set new field value
		ObjectAccessHelper.setNewValue(heap,targetObjectPtr, fieldIdx, newValue)
		currentStackFrame.incrementBP(Instruction.PUTFIELD.params)

	}

	def processLoad() {
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)
		Integer localIdx = currentStackFrame.currentByte
		currentStackFrame.methodStack.add(currentStackFrame.locals[localIdx])
		currentStackFrame.incrementBP(Instruction.LOAD.params)
	}

	def processStore() {
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)
		Integer localIdx = currentStackFrame.currentByte
		currentStackFrame.locals[localIdx] = currentStackFrame.methodStack.pop()
		currentStackFrame.incrementBP(Instruction.STORE.params)
	}

	def processPushNull() {

	}

	def processInitClosure() {

	}

	def processInitNum() {
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer stringClassPtr = classLoader.findClass(heap, Compilator.NUMBER_CLASS)

		// get new number value
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		Integer newNumberValue = parseInteger(ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr))

		// number object initialization on heap
		Integer newObjectPtr = heap.createNumber(stringClassPtr, newNumberValue)
		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT_NUM.params)
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
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		String classOfNewObject = ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr)
		Integer classOfNewObjectPtr = classLoader.findClass(heap, classOfNewObject)

		// object initialization on heap
		Integer newObjectPtr = heap.createObject(classOfNewObjectPtr)
		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT.params)
	}

	def processInitBool() {
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer boolClassPtr = classLoader.findClass(heap, Compilator.BOOL_CLASS)

		// get new bool value
		Integer constPoolPtr = currentStackFrame.currentPointer
		Integer objectPtr = currentStackFrame.thisInst
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, objectPtr)
		String newStringValue = ClassAccessHelper.getConstantPoolValue(heap, classPtr,constPoolPtr)
		Byte newBoolValue = parseByte(newStringValue)

		// bool object initialization on heap
		Integer newObjectPtr = heap.createBool(boolClassPtr, newBoolValue)
		currentStackFrame.methodStack.add(newObjectPtr)

		currentStackFrame.incrementBP(Instruction.INIT_BOOL.params)
	}

	private parseByte(String toParse){
		if(toParse == Compilator.TRUE_STRING_VALUE){
			return 1
		}
		if(toParse == Compilator.FALSE_STRING_VALUE){
			return 0
		}
		throw new InterpretException('parse bool from constant pool failed')
	}

	def processInitString() {
		StackFrame currentStackFrame = stack.last()
		currentStackFrame.incrementBP(INSTRUCTION_SIZE)

		// get pointer to class of new object
		Integer stringClassPtr = classLoader.findClass(heap, Compilator.STRING_CLASS)

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
		StackFrame currentStackFrame = stack.last()
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

		// new stack frame creation
		def newStackFrame = new StackFrame()
		newStackFrame.locals.add(targetObjectPtr)
		newStackFrame.locals.addAll(reversedArgList.reverse())
		newStackFrame.parent = currentStackFrame
		newStackFrame.bytecodePtr = 0

		Integer methodPtr = ClassAccessHelper.findMethodWithSuper(heap, targetClassPtr, methodSignature)
		newStackFrame.bytecode = MethodHelper.getBytecode(methodPtr)

		stack.add(newStackFrame)
		def result = interpret()
		stack.last().methodStack.push(result)
	}
}
