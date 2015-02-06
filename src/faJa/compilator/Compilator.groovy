package faJa.compilator

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledInstruction
import faJa.PrecompiledMethod
import faJa.exceptions.CompilerException
import sun.reflect.ConstantPool

class Compilator {

	public static final String START_FIELDS = 'fields'
	public static final String END_FIELDS = 'endFields'
	public static final String START_METHODS = 'methods'
	public static final String END_METHODS = 'endMethods'
	public static final String END_CLASS = 'endClass'
	public static final String METHOD_START = 'def'
	public static final String METHOD_END = 'end'
	public static final String LOCAL_DEFINE = 'var'
	public static final String SELF_POINTER = 'self'
	public static final String ASSIGNMENT_OP = '<-'
	public static final String METHOD_CALL_SEPARATOR = '.'
	public static final String FIELD_ACESSOR = ':'
	public static final String COMMENT = ':-)'
	public static final String INHERITANCE_KEYWORD = 'extends'
	public static final String DEFAULT_PARENT = 'Object'
	public static final String STRING_CLASS = 'String'
	public static final String NUMBER_CLASS = 'Number'
	public static final String BOOL_CLASS = 'Bool'
	public static final String CLOSURE_CLASS = 'Closure'
	public static final String SYSTEMIO_CLASS = 'SystemIO'
	public static final String TRUE_STRING_VALUE = 'true'
	public static final String FALSE_STRING_VALUE = 'false'
	public static final String SINGLETON_KEYWORD = 'object'
	public static final String CLASS_KEYWORD = 'class'
	public static final String METHOD_ARGUMENT_START_KEYWORD = '('
	public static final String METHOD_ARGUMENT_START_END = ')'

	ClassFile classFile

	def methods = [] // for better code checking

	def compile(String path) {
		classFile = new ClassFile()
		def file = new File(path)

		def lines = file.readLines()

		lines = lines.findAll { line ->
			line.trim().startsWith(COMMENT) == false && line.trim() != ''
		}
		createClass(lines[0])
		createFields(lines)
		createMethods(lines)
		if(!lines.last().startsWith(END_CLASS)){
			throw new CompilerException('class shold end with ' + END_CLASS)
		}

		classFile
	}

	def createClass(String line){
		if(line.trim().startsWith(SINGLETON_KEYWORD)){
			classFile.isSingleton = true
			line = line.replace(SINGLETON_KEYWORD, CLASS_KEYWORD)
		}
		if(line.trim().startsWith(CLASS_KEYWORD)) {
			def classNameSplit = line.replace(CLASS_KEYWORD + ' ', '').trim().split(INHERITANCE_KEYWORD).collect{it.trim()}
			classFile.constantPool.add(classNameSplit[0])
			if(classNameSplit.size() == 1){
				classFile.constantPool.add(DEFAULT_PARENT)
			}else{
				classFile.constantPool.add(classNameSplit[1])
			}

		}
		else
			throw new CompilerException('file not start with class definition')
	}

	def createFields(List<String> lines){
		def fields = false

		lines.each{ line ->
			if(line.trim().startsWith(END_FIELDS)){
				fields = false
			}
			if(fields){
				createField(line)
			}
			if(line.trim().startsWith(START_FIELDS)){
				fields = true
			}
		}
	}

	def createField(String line){
		if(line.trim() == ''){
			return
		}
		classFile.fields.add(classFile.constantPool.size())
		classFile.constantPool.add(line.trim())
	}

	def createMethods(List<String> lines){
		def methods = false
		def method = false
		def methodBody = []
		def signature
		def argList

		lines.each{ line ->
			line = line.trim()

			if(line.startsWith(START_METHODS)){
				methods = true
			}
			if(line.startsWith(END_METHODS)){
				methods = false
			}
			if(methods){
				if(line.startsWith(METHOD_START)){
					method = true
					signature = createSignature(line)
					argList = createArgList(line)
					methodBody = []
					return
				}
				if(line.startsWith(METHOD_END)){
					createMethod(signature,argList,methodBody)
				}
				methodBody << line
			}
		}
	}

	def createMethod(String signature,List<String> argList, List<String> methodBody){
		def signitureIndex = classFile.constantPool.size()
		if(methods.contains(signature)) {
			throw new CompilerException('method "' + signature +'" already exists in class ' + classFile.constantPool[0])
		}
		methods.add(signature)
		classFile.constantPool.add(signature)

		def definitions = [], code = []
		methodBody.each{ line ->
			line = line.trim()
			if(line.startsWith(LOCAL_DEFINE)){
				line = line.replace(LOCAL_DEFINE + ' ', '')
				definitions.add(line)
			}
			else{
				code.add(line)
			}
		}
		def locals = createLocalsMap(definitions, argList)

		def method = new PrecompiledMethod()

		method.signatureIndex = signitureIndex
		code.each { String line ->
			method.instructions.addAll(compileLine(line, locals, classFile.constantPool))
		}

		classFile.methods.add(method)
	}

	def findIndex(String field, List<String> constPool){
		def index = -1
		constPool.eachWithIndex{ it, idx ->
			if(it == field){
				index = idx
			}
		}
		// field not found in constant pool
		if(index == -1){
			index = constPool.size()
			constPool.add(field)
		}
		index
	}

	enum EvalSituation {NUMBER, STRING, BOOL, NULL, METHOD_CALL, CREATE_OBJECT, CLOSURE, VARIABLE, FIELD}

	def resolveSituation(String expr){
		// todo Closure
		if(expr[0] == '"' && expr[expr.length()-1] == '"'){
			return EvalSituation.STRING
		}
		if(expr == 'null'){
			return EvalSituation.NULL
		}
		if(expr == 'true' || expr == 'false'){
			return EvalSituation.BOOL
		}
		if(expr.endsWith('.new')){
			return EvalSituation.CREATE_OBJECT
		}
		if(expr.indexOf(METHOD_ARGUMENT_START_KEYWORD) != -1 && expr.indexOf(METHOD_ARGUMENT_START_END) != -1){
			return EvalSituation.METHOD_CALL
		}
		def isNum = true
		try{
			expr.toInteger()
		}catch(NumberFormatException e){
			isNum = false
		}
		if(isNum){
			return EvalSituation.NUMBER
		}
		if(expr.startsWith(SELF_POINTER + '' + FIELD_ACESSOR)){
			return EvalSituation.FIELD
		}
		if(expr == expr.find(~/[a-z0-9]+/)){
			return EvalSituation.VARIABLE
		}
		throw new CompilerException('unexpected expression "'+ expr +'" exception')
	}

	def compileLine(String line, Map locals, List<String> constantPool){
		List<Instruction> instructionList = []
		def expr = line.trim()
		def assignmentVar = null
		if(line.indexOf(ASSIGNMENT_OP) != -1){
			def assignmentSplit = line.split(ASSIGNMENT_OP)
			assignmentVar = assignmentSplit[0].trim()
			expr = assignmentSplit[1].trim()
		}
		// evaluation
		// todo Closure
		def situation = resolveSituation(expr)
		if(situation == EvalSituation.METHOD_CALL && expr.indexOf(METHOD_CALL_SEPARATOR) == -1){
			expr =  SELF_POINTER + METHOD_CALL_SEPARATOR + expr
		}
		def instructions
		switch(situation){
			case EvalSituation.NUMBER:
				instructions = processNumber(expr,constantPool)
				break
			case EvalSituation.STRING:
				instructions = processString(expr,constantPool)
				break
			case EvalSituation.BOOL:
				instructions = processBool(expr,constantPool)
				break
			case EvalSituation.NULL:
				instructions = processNull()
				break
			case EvalSituation.METHOD_CALL:
				instructions = processMethodCall(expr,locals,constantPool)
				break
			case EvalSituation.CREATE_OBJECT:
				instructions = processCreateObject(expr,constantPool)
				break;
			case EvalSituation.FIELD:
				instructions = processField(expr,constantPool)
				break;
			case EvalSituation.VARIABLE:
				instructions = processVariable(expr,locals)
				break
			default:
				throw new CompilerException('can not parse line with: ' + expr)
		}

		instructionList.addAll(instructions)

		// assigment
		if(assignmentVar){
			// field assigment
			// todo to refaktor # 1
			if(assignmentVar.indexOf(SELF_POINTER + FIELD_ACESSOR) != -1){
				def loadSelfInst = new PrecompiledInstruction()
				loadSelfInst.instruction = Instruction.LOAD
				loadSelfInst.paramVal = 0

				def putfieldInst = new PrecompiledInstruction()
				putfieldInst.instruction = Instruction.PUTFIELD
				putfieldInst.paramVal = findIndex(assignmentVar.replace(SELF_POINTER + FIELD_ACESSOR,''), constantPool)


				instructionList.add(loadSelfInst)
				instructionList.add(putfieldInst)
			}
			// local var assigment
			else{
				def inst = new PrecompiledInstruction()
				inst.instruction = Instruction.STORE
				inst.paramVal = locals.get(assignmentVar)
				instructionList.add(inst)
			}
		}

		return instructionList
	}

	// expr - method call string
	List<PrecompiledInstruction> processMethodCall(String expr, Map locals, List<Integer> constantPool){
		def methodSplit = expr.split('\\' + METHOD_CALL_SEPARATOR)
		def objectName = methodSplit[0]
		def methodName = methodSplit[1]
		// load method args
		def argNames = createArgList(methodName)
		def instructionList = []

		argNames.each{ argName ->
			if(argName.indexOf(FIELD_ACESSOR) != -1){
				// todo to refaktor # 1
				def loadSelfInst = new PrecompiledInstruction()
				loadSelfInst.instruction = Instruction.LOAD
				loadSelfInst.paramVal = 0

				def getfieldInst = new PrecompiledInstruction()
				getfieldInst.instruction = Instruction.GETFIELD
				getfieldInst.paramVal = findIndex(argName.replace(SELF_POINTER + FIELD_ACESSOR,''), constantPool)

				instructionList.add(loadSelfInst)
				instructionList.add(getfieldInst)
			}
			else{
				def loadInst = new PrecompiledInstruction()
				loadInst.instruction = Instruction.LOAD
				loadInst.paramVal = locals.get(argName)
				instructionList.add(loadInst)
			}
		}

		// object is local variable
		def local = locals.get(objectName)
		def field = findIndex(objectName.replace(SELF_POINTER + FIELD_ACESSOR, ''),constantPool)
		if(local != null){
			def loadInst = new PrecompiledInstruction()
			loadInst.instruction = Instruction.LOAD
			loadInst.paramVal = local
			instructionList.add(loadInst)

			def invokeInst = new PrecompiledInstruction()
			invokeInst.instruction = Instruction.INVOKE
			invokeInst.paramVal = findIndex(createSignature(methodName),constantPool)
			instructionList.add(invokeInst)
			return instructionList
		}
		else if(field != null){
			def loadInst = new PrecompiledInstruction()
			loadInst.instruction = Instruction.LOAD
			loadInst.paramVal = 0
			instructionList.add(loadInst)

			def getfieldInst = new PrecompiledInstruction()
			getfieldInst.instruction = Instruction.GETFIELD
			getfieldInst.paramVal = field
			instructionList.add(getfieldInst)

			def invokeInst = new PrecompiledInstruction()
			invokeInst.instruction = Instruction.INVOKE
			invokeInst.paramVal = findIndex(createSignature(methodName),constantPool)
			instructionList.add(invokeInst)
			return instructionList
		}
		else{
			throw new CompilerException('can not call method on not object ' + objectName)
		}

	}

	List<PrecompiledInstruction> processBool(String expr, List<String> constantPool){
		Integer size = constantPool.size()
		constantPool.add(expr)
		def initBool = new PrecompiledInstruction()
		initBool.instruction = Instruction.INIT_BOOL
		initBool.paramVal = size
		[initBool]
	}

	List<PrecompiledInstruction> processNumber(String expr, List<String> constantPool){
		Integer size = constantPool.size()
		constantPool.add(expr)
		def initBool = new PrecompiledInstruction()
		initBool.instruction = Instruction.INIT_NUM
		initBool.paramVal = size
		[initBool]
	}

	List<PrecompiledInstruction> processString(String expr, List<String> constantPool){
		Integer size = constantPool.size()
		constantPool.add(expr)
		def initBool = new PrecompiledInstruction()
		initBool.instruction = Instruction.INIT_STRING
		initBool.paramVal = size
		[initBool]
	}

	List<PrecompiledInstruction> processField(String expr, List<String> constantPool){
		def loadSelf = new PrecompiledInstruction()
		loadSelf.instruction = Instruction.LOAD
		loadSelf.paramVal = 0

		def fieldInx = findIndex(expr.replace(SELF_POINTER + '' + FIELD_ACESSOR , ''),constantPool)
		def getField = new PrecompiledInstruction()
		getField.instruction = Instruction.GETFIELD
		getField.paramVal = fieldInx
		[loadSelf, getField]
	}

	List<PrecompiledInstruction> processVariable(String expr, Map<String , Integer> locals){
		def load = new PrecompiledInstruction()
		load.instruction = Instruction.LOAD
		load.paramVal = locals.get(expr)
		if(load.paramVal == null){
			throw new CompilerException('local variable: ' + expr + ' not found')
		}
		[load]
	}

	List<PrecompiledInstruction> processNull(String expr, List<String> constantPool){
		// todo
	}

	List<PrecompiledInstruction> processCreateObject(String expr, List<String> constantPool){
		String className= expr.replace('.new','')
		Integer classIdx = findIndex(className,constantPool)
		def init = new PrecompiledInstruction()
		init.instruction = Instruction.INIT
		init.paramVal = classIdx
		[init]
	}

	def createLocalsMap(List<String> definitions,List<String> argList){
		def locals = [:]
		locals.put(SELF_POINTER, 0)
		argList.each { arg ->
			locals.put( arg, locals.size())
		}
		definitions.each{ line ->
			def args = line.split(',').collect { it.trim()}
			args.each { arg ->
				locals.put( arg, locals.size())
			}
		}
		locals
	}

	def createSignature(String line){
		def head = line.replace(METHOD_START + ' ', '')
		def args = head.find(~/\(.*\)/)
		def argsCount = args == '()' ? 0 : args.split(',').size()
		head.replace(args,"(${argsCount})")
	}

	def createArgList(String line){
		def wrappedArgs = line.find(~/\(.*\)/)
		def args = wrappedArgs.substring(1, wrappedArgs.length()-1);
		return args == '' ? [] : args.split(',').toList()
	}
}
