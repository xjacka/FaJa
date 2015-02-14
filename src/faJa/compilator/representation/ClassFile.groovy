package faJa.compilator.representation

import faJa.helpers.ByteHelper
import faJa.interpreter.Instruction

/**
 *                         SERIALIZED CLASS FILE TO BYTES (each space = 2 bytes)
 * ++------------------------------++---------------------------------------------------++----------------------------------------++--------------------------------------------------------------------------------------------------------------------------++--------------------------------------------------------------------------------------------------------------++
 * ||          CLASS HEAD          ||                CONSTANT POOL     . value .        ||                FIELDS                  ||                             METHODS           .    bytecode   .                                                          ||                                                    CLOSURES                            .   bytecode  .       ||
 * ++------------------------------++---------------------------------------------------++----------------------------------------++--------------------------------------------------------------------------------------------------------------------------++--------------------------------------------------------------------------------------------------------------++
 * || classSize | ConstantPoolSize || itemSize_1 | xxxxxx | itemSize_2 | xxxxxx | ..... || fieldsSize | field_1 | field_2 | ..... || methodsSize | methodSize_1 | signatureIndex_1 | xxxxxxxxxxx_1 | methodSize_2 | signatureIndex_2 | xxxxxxxxxxxx_2 | ..... || closuresSize | closureSize_1 | argsCount_1 | xxxxxxxxx_1 | closureSize_2 | argsCount_2 | xxxxxxxxx_2 | ..... ||
 * ++------------------------------++---------------------------------------------------++----------------------------------------++--------------------------------------------------------------------------------------------------------------------------++--------------------------------------------------------------------------------------------------------------++
 *
 */

/**
 *   SERIALIZED CLASS FILE TO BYTES
 *       (each space = 2 bytes)
 * ++------------------------------++........................
 * ||          classSize           ||                       :
 * ++==============================++     CLASS HEAD        :
 * ||       ConstantPoolSize       ||                       :
 * ++//////////////////////////////++.......................:
 * ++==============================++                       :
 * ||          itemSize_1          ||                       :
 * ++------------------------------++                       :
 * ||           xxxxxx             || <- value              :
 * ++==============================++                       :
 * ||          itemSize_2          ||                       :
 * ++------------------------------++     CONSTANT POOL     :                      :
 * ||           xxxxxx             ||                       :
 * ++==============================++                       :
 * ||           ......             ||                       :
 * ++------------------------------++                       :
 * ||           ......             ||                       :
 * ++==============================++                       :
 * ++//////////////////////////////++.......................:
 * ++==============================++                       :
 * ||        fieldsSize            ||                       :
 * ++==============================++                       :
 * ||         field_1              ||                       :
 * ++------------------------------++       FIELDS          :
 * ||         field_2              ||                       :
 * ++------------------------------++                       :
 * ||          ......              ||                       :
 * ++------------------------------++                       :
 * ++//////////////////////////////++.......................:
 * ++==============================++                       :
 * ||         methods_size         ||                       :
 * ++==============================++                       :
 * ||        method_size_1         ||                       :
 * ++------------------------------++                       :
 * ||       signatureIndex_1       ||                       :
 * ++------------------------------++                       :
 * ||          xxxxxxx             ||  <- bytecode          :
 * ++==============================++                       :
 * ||        method_size_2         ||                       :
 * ++------------------------------++                       :
 * ||       signatureIndex_2       ||                       :
 * ++------------------------------++       METHODS         :
 * ||          xxxxxxx             ||                       :
 * ++==============================++                       :
 * ||          ......              ||                       :
 * ++------------------------------++                       :
 * ||          ......              ||                       :
 * ++------------------------------++                       :
 * ||          ......              ||                       :
 * ++==============================++                       :
 * ++//////////////////////////////++.......................:
 * ++==============================++                       :
 * ||        closures_size         ||                       :
 * ++==============================++                       :
 * ||        closure_size_1        ||                       :
 * ++------------------------------++                       :
 * ||       arguments_count_1      ||                       :
 * ++------------------------------++                       :
 * ||         locals_count_1       ||                       :
 * ++------------------------------++                       :
 * ||          xxxxxxx_1           ||  <- bytecode          :
 * ++==============================++                       :
 * ||        closure_size_2        ||                       :
 * ++------------------------------++                       :
 * ||       arguments_count_2      ||                       :
 * ++------------------------------++       CLOSURES        :
 * ||        locals_count_2        ||                       :
 * ++==============================++                       :
 * ||           xxxxxx             ||                       :
 * ++------------------------------++                       :
 * ||           ......             ||                       :
 * ++------------------------------++                       :
 * ||           ......             ||                       :
 * ++==============================++.......................:
 */
class ClassFile {

	static CONST_POOL_START = 4
	static SLOT_SIZE = 2

	def constantPool = new ConstantPool()
	def fields = []
	def isSingleton = false
	List<PrecompiledClosure> closures = []
	List<PrecompiledMethod> methods = []

	byte [] toByteCode(){

		List<Byte> bytes = []

		// class size init
		setClassSize(bytes, 0)

		bytes.addAll(constantPool.toBytecode())

		def constPoolSize = constantPool.size()
		def constPoolIndexes = constantPool.constantPoolIndexes()

		// fields size
		def fieldsSize = fields.size() * SLOT_SIZE
		setBytes(bytes, CONST_POOL_START + constPoolSize, fieldsSize)

		// fields
		fields.each{ Integer f ->
			def ptr = constPoolIndexes[f]
			bytes.addAll(ByteHelper.IntegerTo2Bytes(ptr))
		}

		// method size init (constPoolStart + constPoolSize + fieldsSizeSlot + fieldsSize)
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE, 0 )

		// method
		def methodLengths = methods.collect{ m ->
			byte[] methodBytecode = m.toBytecode(constPoolIndexes)
			bytes.addAll(methodBytecode)
			methodBytecode.length
		}
		def methodsSize = methodLengths.sum(0)

		// method size
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE, methodsSize )

		// closure size init (constPoolStart + constPoolSize + fieldsSizeSlot + fieldsSize + methodsSizeSlot + methodsSize)
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE + methodsSize + SLOT_SIZE, 0 )

		// closure
		def closureLengths = closures.collect{ c ->
			byte[] closureBytecode = c.toBytecode(constPoolIndexes)
			bytes.addAll(closureBytecode)
			closureBytecode.length
		}
		def closuresSize = closureLengths.sum(0)

		// closure size
		setBytes(bytes, CONST_POOL_START + constPoolSize + fieldsSize + SLOT_SIZE + methodsSize + SLOT_SIZE, closuresSize )

		// class size ( constPoolSizeSlot + constPoolSize + fieldSizeSlot + fieldSize + methodsSizeSlot + methodsSize  + closuresSizeSlot + closuresSize)
		setClassSize(bytes, 4 * SLOT_SIZE + constPoolSize + fieldsSize + methodsSize + closuresSize)

		bytes.toArray() as byte []
	}

	@Override
	String toString(){
		StringBuilder sb = new StringBuilder()
		sb.append(isSingleton ? 'objectName: ' : 'className: ')
		sb.append(constantPool.get(0))
		sb.append('\nparentName: ' + constantPool.get(1))
		sb.append('\n')
		sb.append(constantPool.toString())
		sb.append('Fields:\n')
		fields.each{ Integer f ->
			sb.append('\t' +constantPool.get(f) + '\n')
		}
		sb.append('Methods:\n')
		methods.each{ method ->
			sb.append(" " + constantPool.get(method.signatureIndex)+':\n')
			method.instructions.each{ inst ->
				sb.append('\t'+ inst.instruction.toString().padRight(12) + ' ' + (inst.paramVal==null?'':inst.paramVal).toString().padRight(3))
				sb.append(getComment(inst) +'\n' )
			}
		}
		if(closures.size() > 0) {
			sb.append("Closures:\n")
			closures.eachWithIndex { closure, i ->
				sb.append('[' + i + '] (arguments: ' + closure.argsCount + '):\n')
				closure.instructions.each { inst ->
					sb.append('\t'+ inst.instruction.toString().padRight(12) + ' ' + (inst.paramVal==null?'':inst.paramVal).toString().padRight(3))
					sb.append(getComment(inst) +'\n' )
				}
			}
		}

		sb.toString()
	}

	private String getComment(PrecompiledInstruction instruction){
		String comment = ""
		List pointingToCP = [Instruction.INVOKE, Instruction.GETFIELD, Instruction.PUTFIELD,
							Instruction.INIT, Instruction.INIT_STRING, Instruction.INIT_BOOL,
							Instruction.INIT_NUM]
		List pointingToLocals = [Instruction.LOAD, Instruction.STORE]
		if(pointingToCP.collect{it.id}.contains(instruction.instruction.id)){
			comment += "\t// " + constantPool.get(instruction.paramVal)
		}
		comment
	}

	String getClassName(){
		constantPool.get(0)
	}

	String getParentName(){
		constantPool.get(1)
	}

	// -------------------- PRIVATE -----------------------------------

	def createPrefixSum(List<Integer> lengths) {
		constantPool.createPrefixSum(lengths)
	}

	def setClassSize(List bytes, int size){
		setBytes(bytes, 0, size)
	}

	def setBytes(List bytes,int start,int value){
		byte[] byteVal = ByteHelper.IntegerTo2Bytes(value)
		bytes[start] = byteVal[0]
		bytes[start+1] = byteVal[1]
	}
}
