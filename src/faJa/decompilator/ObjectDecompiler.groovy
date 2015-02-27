package faJa.decompilator

import faJa.helpers.ClassAccessHelper
import faJa.helpers.ObjectAccessHelper
import faJa.memory.Heap


class ObjectDecompiler {
	def decompile(Heap heap, Integer ptr, Integer oldPtr = null){
		String className
		String oldClassName = null
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
		className = ClassAccessHelper.getName(heap, classPtr) + '(' + classPtr + ')'
		Integer objectSize = ClassAccessHelper.getObjectSize(heap, classPtr)
		Integer objectEnd = ptr + objectSize
		ptr += Heap.HEAP_POINTER_SIZE
		while(objectEnd > ptr){
			classPtr = ObjectAccessHelper.getClassPointer(heap, heap.getPointer(ptr))
			fields.add(ClassAccessHelper.getName(heap, classPtr)  + '(' + classPtr + ')')

			ptr += Heap.HEAP_POINTER_SIZE	
		}
		
		println('object class ' + className + ':' + ptr)
		if(oldClassName){
			println('\told class ' + oldClassName + ':' + oldPtr)
		}
		fields.eachWithIndex { name, i ->
			println('\t'+i+': ' + name)
		}
		println('object end')
	}
	
}
