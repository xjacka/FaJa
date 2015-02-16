package faJa.natives

import faJa.interpreter.ClassLoader
import faJa.memory.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ClosureHelper
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame

class ArrayNatives {

	public static final Integer ARRAY_INSERT_INDEX_PROPERTY = 0
	public static final Integer ARRAY_OBJECT_POINTER_PROPERTY = Heap.SLOT_SIZE

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> ObjectAccessHelper.valueOf(heap,a,ARRAY_INSERT_INDEX_PROPERTY) != 0 },"ifTrue(1)Array")
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> ObjectAccessHelper.valueOf(heap,a,ARRAY_INSERT_INDEX_PROPERTY) == 0 },"ifFalse(1)Array")
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer arrayPtr = stackFrame.methodStack.pop()

		List<String> arrayStr = []
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)
			NativesHelper.callMethodFromNative(heap,stackFrame,resultPtr,'toS(0)',classLoader)
			Integer itemStrPtr = stackFrame.methodStack.pop()
			String itemStr = heap.stringFromStringObject(itemStrPtr)
			arrayStr += itemStr
		}

		Integer stringPtr = heap.createString(stringClassPtr, "[" + arrayStr.join(", ") + "]")

		stackFrame.methodStack.push(stringPtr)
	}

	static each = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer index = ObjectAccessHelper.valueOf(heap,thisArrayPtr,ARRAY_INSERT_INDEX_PROPERTY)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,thisArrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []

			if(arguments == 1){
				newStackFrame.locals.add(resultPtr)
			}
			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method each(1)Array')
			}
			newStackFrame.environment = ClosureRegister.get(closurePtr) // insert current context
			newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

			new Interpreter(heap, newStackFrame, classLoader).interpret()
			stackFrame.methodStack.pop()
		}

		stackFrame.methodStack.push(thisArrayPtr)
	}

	static collect = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,thisArrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)

		Integer newArrayPtr = heap.createArray(arrayClassPtr,sizeOfInitializedArry,nullPtr)
		Integer newArrayObjectPtr = ObjectAccessHelper.valueOf(heap,newArrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)

		Integer index = ObjectAccessHelper.valueOf(heap,thisArrayPtr,ARRAY_INSERT_INDEX_PROPERTY)

		ObjectAccessHelper.setNewValue(heap,newArrayPtr,ARRAY_INSERT_INDEX_PROPERTY,index)

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []

			if(arguments == 1){
				newStackFrame.locals.add(resultPtr)
			}
			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method collect(1)Array')
			}
			newStackFrame.environment = ClosureRegister.get(closurePtr) // insert current context
			newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

			new Interpreter(heap, newStackFrame, classLoader).interpret()

			Integer closureResult = stackFrame.methodStack.pop()

			ObjectAccessHelper.setNewValue(heap,newArrayObjectPtr,it * Heap.SLOT_SIZE,closureResult)
		}

		stackFrame.methodStack.push(newArrayPtr)
	}

	// for select return value must be true/false
	static select = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,thisArrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)

		Integer newArrayPtr = heap.createArray(arrayClassPtr,sizeOfInitializedArry,nullPtr)
		Integer newArrayObjectPtr = ObjectAccessHelper.valueOf(heap,newArrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)

		Integer index = ObjectAccessHelper.valueOf(heap,thisArrayPtr,ARRAY_INSERT_INDEX_PROPERTY)

		Integer selectedItems = 0

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []

			if(arguments == 1){
				newStackFrame.locals.add(resultPtr)
			}
			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method collect(1)Array')
			}
			newStackFrame.environment = ClosureRegister.get(closurePtr) // insert current context
			newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

			new Interpreter(heap, newStackFrame, classLoader).interpret()

			Integer closureResult = stackFrame.methodStack.pop()
			String resultClass = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,closureResult))

			if(resultClass == Compiler.BOOL_CLASS) {
				if (heap.boolFromBoolObject(closureResult) == true) {
					ObjectAccessHelper.setNewValue(heap,newArrayObjectPtr,selectedItems * Heap.SLOT_SIZE,resultPtr)
					selectedItems++
				}
			}
		}
		ObjectAccessHelper.setNewValue(heap,newArrayPtr,ARRAY_INSERT_INDEX_PROPERTY,selectedItems)

		stackFrame.methodStack.push(newArrayPtr)
	}

	static add1 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()

		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)

		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		// resize array
		if(index >= sizeOfInitializedArry){
			arrayObjectPtr = resizeArray(heap,classLoader, index, index + 2, arrayPtr)
		}

		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,index * Heap.SLOT_SIZE,addingItemPtr)

		index += 1
		ObjectAccessHelper.setNewValue(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY,index)

		stackFrame.methodStack.push(arrayPtr)
	}

	static add2 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()
		Integer itemIndexPtr = stackFrame.methodStack.pop()
		Integer itemIndex = heap.intFromNumberObject(itemIndexPtr)

		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)

		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		// resize array
		if(index >= sizeOfInitializedArry){
			arrayObjectPtr = arrayObjectPtr = resizeArray(heap,classLoader, index, itemIndex + (int)(itemIndex / 10), arrayPtr)
		}

		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,itemIndex * Heap.SLOT_SIZE,addingItemPtr)

		ObjectAccessHelper.setNewValue(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY,itemIndex+1)

		stackFrame.methodStack.push(arrayPtr)
	}

	static get = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = stackFrame.methodStack.pop()
		Integer indexValue = heap.intFromNumberObject(index)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		if(sizeOfInitializedArry == 0){
			Integer nullObjectPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
			stackFrame.methodStack.push(nullObjectPointer)
			return
		}

		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,indexValue * Heap.SLOT_SIZE)

		stackFrame.methodStack.push(resultPtr)
	}

	static push = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		this.add1.call(stackFrame,heap,classLoader)
	}

	static pop = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)

		if(index <= 0){
			throw new InterpretException("Array out of bound")
		}

		index -= 1
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,index * Heap.SLOT_SIZE)
		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,index * Heap.SLOT_SIZE,classLoader.singletonRegister.get(Compiler.NULL_CLASS))
		ObjectAccessHelper.setNewValue(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY,index)

		stackFrame.methodStack.push(resultPtr)
	}

	static top = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,(index - 1) * Heap.SLOT_SIZE)

		stackFrame.methodStack.push(resultPtr)
	}

	static size = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer items = 0
		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)
			if(resultPtr != nullPtr){
				items++
			}
		}
		Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
		Integer resultPtr = heap.createNumber(numberClassPtr, items)

		stackFrame.methodStack.push(resultPtr)
	}

	static contains = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer objectPtr = stackFrame.methodStack.pop()

		Boolean result = false
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_INSERT_INDEX_PROPERTY)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,ARRAY_OBJECT_POINTER_PROPERTY)
		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)

			NativesHelper.callMethodFromNative(heap,stackFrame,resultPtr,'==(1)',classLoader,[objectPtr])
			Integer compareResult = stackFrame.methodStack.pop()

			if(heap.boolFromBoolObject(compareResult) == true){
				result = true
				return
			}
		}

		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer resultPtr = heap.createBool(boolClassPtr, result)

		stackFrame.methodStack.push(resultPtr)
	}

	static Integer resizeArray(Heap heap,ClassLoader classLoader, Integer size, Integer newSize, Integer arrayPtr){
		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer newArrayObjectPtr = heap.createArrayObject(newSize,nullPtr) // resize 2x
		ObjectAccessHelper.setNewValue(heap, arrayPtr, ARRAY_OBJECT_POINTER_PROPERTY, newArrayObjectPtr)
		size.times {
			heap.setPointer(newArrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.SLOT_SIZE),heap.getPointer(arrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.SLOT_SIZE)))
		}
		newArrayObjectPtr
	}
}
