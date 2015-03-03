package faJa.natives

import faJa.helpers.ArrayHelper
import faJa.interpreter.ClassLoader
import faJa.memory.GarbageCollector
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

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> ArrayHelper.getInsertIndex(heap,a) != 0 },"ifTrue(1)Array")
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		NativesHelper.ifClosure(stackFrame,heap,classLoader,{Integer a -> ArrayHelper.getInsertIndex(heap,a) == 0 },"ifFalse(1)Array")
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer arrayPtr = stackFrame.methodStack.pop()

		List<String> arrayStr = []
		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
		index.times{
			Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,it)
			
			stackFrame.currentVariables.addAll([stringClassPtr,arrayPtr]) // for GC
			NativesHelper.callMethodFromNative(heap,stackFrame,resultPtr,'toS(0)',classLoader)
			arrayPtr = stackFrame.currentVariables.pop()
			arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
			stringClassPtr = stackFrame.currentVariables.pop()
			
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

		Integer index = ArrayHelper.getInsertIndex(heap,thisArrayPtr)
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		index.times{
			Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,it)

			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []

			if(arguments == 1){
				newStackFrame.locals.add(resultPtr)
			}
			if(arguments == 2){
				newStackFrame.locals.add(resultPtr)
				Integer numberClassPtr = classLoader.findClass(heap, Compiler.NUMBER_CLASS)
				Integer itemIndex = heap.createNumber(numberClassPtr, it)
				newStackFrame.locals.add(itemIndex)
			}
			if(arguments > 2){
				throw new InterpretException('Too much arguments for closure in method each(1)Array')
			}
			newStackFrame.environment = ClosureRegister.get(closurePtr) // insert current context
			newStackFrame.parentLocalCnt = ClosureHelper.getClosureLocalCnt(heap, bytecodePtr)

			stackFrame.currentVariables.addAll([closurePtr,thisArrayPtr]) // for GC
			new Interpreter(heap, newStackFrame, classLoader).interpret()
			thisArrayPtr = stackFrame.currentVariables.pop()
			closurePtr = stackFrame.currentVariables.pop()
			bytecodePtr = ClosureHelper.getBytecodePtr(heap,closurePtr)
			bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)
			arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)

			stackFrame.methodStack.pop()
		}

		stackFrame.methodStack.push(thisArrayPtr)
	}

	static collect = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)
		Integer sizeOfInitializedArry = heap.getSlot(arrayObjectPtr)

		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)

		stackFrame.currentVariables.addAll([closurePtr,thisArrayPtr]) // for GC
		Integer newArrayPtr = heap.createArray(arrayClassPtr,sizeOfInitializedArry,nullPtr)
		arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)

		Integer newArrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,newArrayPtr)

		Integer index = ArrayHelper.getInsertIndex(heap,thisArrayPtr)
		ArrayHelper.setInsertIndex(heap,newArrayPtr,index)

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		index.times{
			Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,it)

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

			stackFrame.currentVariables.addAll([newArrayPtr,closurePtr,thisArrayPtr]) // for GC
			new Interpreter(heap, newStackFrame, classLoader).interpret()
			thisArrayPtr = stackFrame.currentVariables.pop()
			closurePtr = stackFrame.currentVariables.pop()
			newArrayPtr = stackFrame.currentVariables.pop()
			bytecodePtr = ClosureHelper.getBytecodePtr(heap,closurePtr)
			bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)
			arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)
			newArrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,newArrayPtr)

			Integer closureResult = stackFrame.methodStack.pop()

			ArrayHelper.setNewValue(heap,newArrayObjectPtr,it,closureResult)
		}

		stackFrame.methodStack.push(newArrayPtr)
	}

	// for select return value must be true/false
	static select = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)
		Integer sizeOfInitializedArry = heap.getSlot(arrayObjectPtr)

		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)

		stackFrame.currentVariables.addAll([closurePtr,thisArrayPtr]) // for GC
		Integer newArrayPtr = heap.createArray(arrayClassPtr,sizeOfInitializedArry,nullPtr)
		thisArrayPtr = stackFrame.currentVariables.pop()
		closurePtr = stackFrame.currentVariables.pop()
		arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)

		Integer newArrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,newArrayPtr)

		Integer index = ArrayHelper.getInsertIndex(heap,thisArrayPtr)

		Integer selectedItems = 0

		Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
		Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
		Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)
		Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

		index.times{
			Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,it)

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

			stackFrame.currentVariables.addAll([newArrayPtr,closurePtr,thisArrayPtr]) // for GC
			new Interpreter(heap, newStackFrame, classLoader).interpret()
			thisArrayPtr = stackFrame.currentVariables.pop()
			closurePtr = stackFrame.currentVariables.pop()
			newArrayPtr = stackFrame.currentVariables.pop()
			bytecodePtr = ClosureHelper.getBytecodePtr(heap,closurePtr)
			bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)
			arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,thisArrayPtr)
			newArrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,newArrayPtr)

			Integer closureResult = stackFrame.methodStack.pop()
			String resultClass = ClassAccessHelper.getName(heap,ObjectAccessHelper.getClassPointer(heap,closureResult))

			if(resultClass == Compiler.BOOL_CLASS) {
				if (heap.boolFromBoolObject(closureResult) == true) {
					ArrayHelper.setNewValue(heap,newArrayObjectPtr,selectedItems,resultPtr)
					selectedItems++
				}
			}
		}
		ArrayHelper.setInsertIndex(heap,newArrayPtr,selectedItems)

		stackFrame.methodStack.push(newArrayPtr)
	}

	static add1 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()

		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)

		Integer sizeOfInitializedArry = heap.getSlot(arrayObjectPtr)

		// resize array
		if(index >= sizeOfInitializedArry){
			stackFrame.currentVariables.addAll([addingItemPtr,arrayPtr]) // for GC
			arrayObjectPtr = resizeArray(heap,classLoader, index, index + 2, arrayPtr, arrayObjectPtr)
			arrayPtr = stackFrame.currentVariables.pop()
			addingItemPtr = stackFrame.currentVariables.pop()
		}

		ArrayHelper.setNewValue(heap,arrayObjectPtr,index,addingItemPtr)

		index += 1
		ArrayHelper.setInsertIndex(heap,arrayPtr,index)

		stackFrame.methodStack.push(arrayPtr)
	}

	static add2 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()
		Integer itemIndexPtr = stackFrame.methodStack.pop()
		Integer itemIndex = heap.intFromNumberObject(itemIndexPtr)

		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)

		Integer sizeOfInitializedArry = heap.getSlot(arrayObjectPtr)

		// resize array
		if(index >= sizeOfInitializedArry){
			stackFrame.currentVariables.addAll([addingItemPtr,arrayPtr]) // for GC
			arrayObjectPtr = resizeArray(heap,classLoader, index, itemIndex + (int)(itemIndex / 10), arrayPtr, arrayObjectPtr)
			arrayPtr = stackFrame.currentVariables.pop()
			addingItemPtr = stackFrame.currentVariables.pop()
		}

		ArrayHelper.setNewValue(heap,arrayObjectPtr,itemIndex,addingItemPtr)

		ArrayHelper.setInsertIndex(heap,arrayPtr,[itemIndex+1,index].max())

		stackFrame.methodStack.push(arrayPtr)
	}

	static get = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = stackFrame.methodStack.pop()
		Integer indexValue = heap.intFromNumberObject(index)

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
		Integer sizeOfInitializedArry = heap.getSlot(arrayObjectPtr)

		if(sizeOfInitializedArry == 0){
			Integer nullObjectPointer = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
			stackFrame.methodStack.push(nullObjectPointer)
			return
		}

		Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,indexValue)

		stackFrame.methodStack.push(resultPtr)
	}

	static push = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		this.add1.call(stackFrame,heap,classLoader)
	}

	static pop = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)

		if(index <= 0){
			throw new InterpretException("Array out of bound")
		}

		index -= 1
		Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,index)
		ArrayHelper.setNewValue(heap,arrayObjectPtr,index ,classLoader.singletonRegister.get(Compiler.NULL_CLASS))
		ArrayHelper.setInsertIndex(heap,arrayPtr,index)

		stackFrame.methodStack.push(resultPtr)
	}

	static top = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
		Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,(index - 1) )

		stackFrame.methodStack.push(resultPtr)
	}

	static size = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)

		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer items = 0
		index.times{
			Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,it)
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
		Integer index = ArrayHelper.getInsertIndex(heap,arrayPtr)
		Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
		index.times{
			Integer resultPtr = ArrayHelper.valueAt(heap,arrayObjectPtr,it)

			stackFrame.currentVariables.addAll([objectPtr,arrayPtr]) // for GC
			NativesHelper.callMethodFromNative(heap,stackFrame,resultPtr,'==(1)',classLoader,[objectPtr])
			arrayPtr = stackFrame.currentVariables.pop()
			objectPtr = stackFrame.currentVariables.pop()
			arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap,arrayPtr)
			
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

	static Integer resizeArray(Heap heap,ClassLoader classLoader, Integer size, Integer newSize, Integer arrayPtr, Integer arrayObjectPtr){
		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer newArrayObjectPtr = heap.createArrayObject(newSize,nullPtr) // resize 2x
		ArrayHelper.setArrayObjectPtr(heap,arrayPtr,newArrayObjectPtr)
		size.times {
			Integer oldPtr = heap.getPointer(arrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.HEAP_POINTER_SIZE))
			if(GarbageCollector.isOldPointer(heap,oldPtr)){
				oldPtr = heap.getPointer(oldPtr)
			}
			heap.setPointer(newArrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.HEAP_POINTER_SIZE),oldPtr)
		}
		newArrayObjectPtr
	}
}
