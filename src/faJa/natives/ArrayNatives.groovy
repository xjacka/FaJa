package faJa.natives

import faJa.ClassLoader
import faJa.Heap
import faJa.compilator.Compiler
import faJa.exceptions.InterpretException
import faJa.helpers.ClosureHelper
import faJa.helpers.NativesHelper
import faJa.helpers.ObjectAccessHelper
import faJa.interpreter.Interpreter
import faJa.interpreter.StackFrame

class ArrayNatives {

	static ifTrue = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arraySize = ObjectAccessHelper.valueOf(heap,thisArrayPtr,0)
		if(arraySize != 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 0){
				throw new InterpretException('Too much arguments for closure in method ifTrue(1)Array')
			}
			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			new Interpreter(heap, newStackFrame, classLoader).interpret()
		}
		stackFrame.methodStack.push(thisArrayPtr)
	}

	static ifFalse = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arraySize = ObjectAccessHelper.valueOf(heap,thisArrayPtr,0)
		if(arraySize == 0) {
			Integer bytecodePtr = ClosureHelper.getBytecodePtr(heap, closurePtr)
			Integer arguments = ClosureHelper.getBytecodeArgCount(heap,bytecodePtr)
			Integer bytecodeSize = ClosureHelper.getBytecodeSize(heap, bytecodePtr)

			Integer bytecodeStart = ClosureHelper.getBytecodeStart(bytecodePtr)

			if(arguments > 0){
				throw new InterpretException('Too much arguments for closure in method ifTrue(1)Array')
			}
			StackFrame newStackFrame = new StackFrame()
			newStackFrame.parent = stackFrame
			newStackFrame.bytecodePtr = 0
			newStackFrame.bytecode = heap.getBytes(bytecodeStart, bytecodeSize)
			newStackFrame.locals = []
			newStackFrame.methodStack = []
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			new Interpreter(heap, newStackFrame, classLoader).interpret()
		}
		stackFrame.methodStack.push(thisArrayPtr)
	}

	static toS = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer stringClassPtr = classLoader.findClass(heap, Compiler.STRING_CLASS)
		Integer arrayPtr = stackFrame.methodStack.pop()

		List<String> arrayStr = []
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
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

		Integer index = ObjectAccessHelper.valueOf(heap,thisArrayPtr,0)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,thisArrayPtr,Heap.SLOT_SIZE)

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
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			if(arguments == 1){
				newStackFrame.locals.add(1,resultPtr)
			}
			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method each(1)Array')
			}

			new Interpreter(heap, newStackFrame, classLoader).interpret()
		}
		stackFrame.methodStack.push(thisArrayPtr)
	}

	static collect = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer thisArrayPtr = stackFrame.methodStack.pop()
		Integer closurePtr = stackFrame.methodStack.pop()

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,thisArrayPtr,Heap.SLOT_SIZE)
		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
		Integer arrayClassPtr = classLoader.findClass(heap, Compiler.ARRAY_CLASS)

		Integer newArrayPtr = heap.createArray(arrayClassPtr,sizeOfInitializedArry,nullPtr)
		Integer newArrayObjectPtr = ObjectAccessHelper.valueOf(heap,newArrayPtr,Heap.SLOT_SIZE)

		Integer index = ObjectAccessHelper.valueOf(heap,thisArrayPtr,0)

		ObjectAccessHelper.setNewValue(heap,newArrayPtr,0,index)

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
			newStackFrame.locals.addAll(stackFrame.locals) // insert current context

			if(arguments == 1){
				newStackFrame.locals.add(1,resultPtr)
			}
			if(arguments > 1){
				throw new InterpretException('Too much arguments for closure in method collect(1)Array')
			}

			new Interpreter(heap, newStackFrame, classLoader).interpret()

			Integer closureResult = stackFrame.methodStack.pop()

			ObjectAccessHelper.setNewValue(heap,newArrayObjectPtr,it * Heap.SLOT_SIZE,closureResult)
		}
		stackFrame.methodStack.push(newArrayPtr)
	}

	static select = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->

	}

	static add1 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()

		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		if(index >= sizeOfInitializedArry){
			Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
			Integer newArrayObjectPtr = heap.createArrayObject(index * 2,nullPtr) // resize 2x
			ObjectAccessHelper.setNewValue(heap, arrayPtr, Heap.SLOT_SIZE, newArrayObjectPtr)
			index.times {
				heap.setPointer(newArrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.SLOT_SIZE),heap.getPointer(arrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.SLOT_SIZE)))
			}
			arrayObjectPtr = newArrayObjectPtr
		}

		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,index * Heap.SLOT_SIZE,addingItemPtr)

		index += 1
		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,index)

		stackFrame.methodStack.push(arrayPtr)
	}

	static add2 = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer addingItemPtr = stackFrame.methodStack.pop()
		Integer itemIndexPtr = stackFrame.methodStack.pop()
		Integer itemIndex = heap.intFromNumberObject(itemIndexPtr)

		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		Integer sizeOfInitializedArry = heap.getPointer(arrayObjectPtr)

		if(index >= sizeOfInitializedArry){
			Integer nullPtr = classLoader.singletonRegister.get(Compiler.NULL_CLASS)
			Integer newArrayObjectPtr = heap.createArrayObject(itemIndex + 10,nullPtr) // resize to insert index + 10
			ObjectAccessHelper.setNewValue(heap, arrayPtr, Heap.SLOT_SIZE, newArrayObjectPtr)
			index.times {
				heap.setPointer(newArrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.SLOT_SIZE),heap.getPointer(arrayObjectPtr + Heap.SLOT_SIZE + (it * Heap.SLOT_SIZE)))
			}
			arrayObjectPtr = newArrayObjectPtr
		}

		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,itemIndex * Heap.SLOT_SIZE,addingItemPtr)

		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,itemIndex+1)

		stackFrame.methodStack.push(arrayPtr)
	}

	static get = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = stackFrame.methodStack.pop()
		Integer indexValue = heap.intFromNumberObject(index)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,indexValue * Heap.SLOT_SIZE)

		stackFrame.methodStack.push(resultPtr)
	}

	static push = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		this.add1.call(stackFrame,heap,classLoader)
	}

	static pop = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)

		if(index <= 0){
			throw new InterpretException("Array out of bound")
		}

		index -= 1
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,index * Heap.SLOT_SIZE)
		ObjectAccessHelper.setNewValue(heap,arrayObjectPtr,index * Heap.SLOT_SIZE,classLoader.singletonRegister.get(Compiler.NULL_CLASS))
		ObjectAccessHelper.setNewValue(heap,arrayPtr,0,index)

		stackFrame.methodStack.push(resultPtr)
	}

	static top = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
		Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,(index - 1) * Heap.SLOT_SIZE)

		stackFrame.methodStack.push(resultPtr)
	}

	static size = { StackFrame stackFrame, Heap heap, ClassLoader classLoader ->
		Integer arrayPtr = stackFrame.methodStack.pop()
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)

		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
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

		Byte result = 0
		Integer index = ObjectAccessHelper.valueOf(heap,arrayPtr,0)
		Integer arrayObjectPtr = ObjectAccessHelper.valueOf(heap,arrayPtr,Heap.SLOT_SIZE)
		index.times{
			Integer resultPtr = ObjectAccessHelper.valueOf(heap,arrayObjectPtr,it * Heap.SLOT_SIZE)

			NativesHelper.callMethodFromNative(heap,stackFrame,resultPtr,'==(1)',classLoader,[objectPtr])
			Integer compareResult = stackFrame.methodStack.pop()

			if(heap.boolFromBoolObject(compareResult) == true){
				result = 1
				return
			}
		}

		Integer boolClassPtr = classLoader.findClass(heap, Compiler.BOOL_CLASS)
		Integer resultPtr = heap.createBool(boolClassPtr, result)
		stackFrame.methodStack.push(resultPtr)
	}
}
