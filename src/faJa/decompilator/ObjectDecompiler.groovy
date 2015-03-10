package faJa.decompilator

import faJa.helpers.ArrayHelper
import faJa.helpers.ClassAccessHelper
import faJa.helpers.ClosureHelper
import faJa.helpers.ObjectAccessHelper
import faJa.memory.Heap


class ObjectDecompiler {
	
	def decompile(Heap heap, Integer ptr){
	
		String className
		List<String> fields = []
//		if(oldPtr != null){
//			Integer oldClassPtr = ObjectAccessHelper.getClassPointer(heap, oldPtr)
//			if(oldClassPtr != ptr) {
//				throw new RuntimeException()
//			}
//				oldClassName = ClassAccessHelper.getName(heap, oldClassPtr) + '(' + oldClassPtr + ')'
//				// oldClassPtr points to new
//
//
//		}

		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, ptr)

		if(classPtr == 0){
			println('decompilor error '  + ptr)
			return
		}
		
		className = ClassAccessHelper.getName(heap, classPtr) + '(' + ptr + ':' + classPtr + ')'
		Integer objectSize = ClassAccessHelper.getObjectSize(heap, classPtr)
		Integer objectEnd = ptr + objectSize
		processSpecialObjects(ptr, heap, fields)
		ptr += Heap.HEAP_POINTER_SIZE
		
		while(objectEnd > ptr){
			fields.add(getFieldClassName(ptr,heap))
			ptr += Heap.HEAP_POINTER_SIZE
		}

		// printing
		println('object with class ' + className)
		fields.eachWithIndex {name, i ->
			println('\t'+i+': ' + name)
		}
		fields.size()?println('object end\n'):println()
	}

	def processSpecialObjects(int ptr,  Heap heap, List fields) {
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap, ptr)
		String className = ClassAccessHelper.getName(heap, classPtr)
		
		switch(className){
			case faJa.compilator.Compiler.ARRAY_CLASS:
				fields.add(ArrayHelper.getInsertIndex(heap, ptr))
				Integer arrayObjectPtr = ArrayHelper.getArrayObjectPtr(heap, ptr)
				Integer size = heap.getSlot(arrayObjectPtr)
				fields.add('----items-----')
//				fields.add(size)
				size.times{
					Integer itemPtrPtr = arrayObjectPtr + Heap.SLOT_SIZE + Heap.HEAP_POINTER_SIZE * it
					fields.add(getFieldClassName(itemPtrPtr, heap))
				}
				return
			case faJa.compilator.Compiler.BOOL_CLASS:
				fields.add(heap.boolFromBoolObject(ptr))
				return
			case faJa.compilator.Compiler.CLOSURE_CLASS:
				int initClassPtr = ClosureHelper.getInitClassPtr(heap, ptr)
				fields.add(heap.getSlot(ptr + Heap.HEAP_POINTER_SIZE))
//				fields.add(ClassAccessHelper.getName(heap, initClassPtr) + '(' + initClassPtr + ')')
				return
			case faJa.compilator.Compiler.NUMBER_CLASS:
				fields.add(heap.intFromNumberObject(ptr))
				return
			case faJa.compilator.Compiler.STRING_CLASS:
				fields.add(heap.stringFromStringObject(ptr))
				return
		}
		return
	}

	def getFieldClassName(Integer ptr,Heap heap){
		Integer fieldPtr = heap.getPointer(ptr)
		Integer classPtr = ObjectAccessHelper.getClassPointer(heap,fieldPtr )
		if(isOldPointer(heap, fieldPtr) && !isOldPointer(heap, classPtr)){
			// object class pointer was rewritten to pointer to recreated object
			fieldPtr = classPtr
			classPtr = heap.getPointer(fieldPtr)
		}
		ClassAccessHelper.getName(heap, classPtr)  + '(' + fieldPtr + ')'
	}

	boolean isOldPointer(heap, ptr){
		heap.heapStart > ptr || heap.heapEnd < ptr
	}
}
