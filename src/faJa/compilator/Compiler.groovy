package faJa.compilator

import faJa.ClassFile
import faJa.Instruction
import faJa.PrecompiledClosure
import faJa.PrecompiledInstruction
import faJa.PrecompiledMethod
import faJa.exceptions.CompilerException

class Compiler {

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
	public static final String CONSTRUCTOR_INVOKE_NAME = '.new'
	public static final String DEFAULT_PARENT = 'Object'
	public static final String STRING_CLASS = 'String'
	public static final String NUMBER_CLASS = 'Number'
	public static final String BOOL_CLASS = 'Bool'
	public static final String CLOSURE_CLASS = 'Closure'
	public static final String NULL_CLASS = 'Null'
	public static final String NULL_KEYWORD = 'null'
	public static final String SYSTEMIO_CLASS = 'SystemIO'
	public static final String TRUE_STRING_VALUE = 'true'
	public static final String FALSE_STRING_VALUE = 'false'
	public static final String SINGLETON_KEYWORD = 'object'
	public static final String CLASS_KEYWORD = 'class'
	public static final String METHOD_ARGUMENT_START_KEYWORD = '('
	public static final String METHOD_ARGUMENT_START_END = ')'
	public static final String CLOSURE_OPEN_KEYWORD = '{'
	public static final String CLOSURE_CLOSE_KEYWORD = '}'
	public static final String ARGUMENT_SEPARATOR = ','
	public static final String CLOSURE_PARAMS_END_KEYWORD = '|'


	ClassFile classFile

	def methods = [] // for better code checking
	def fields = [] // for better code checking

	def compile(String path) {
		classFile = new ClassFile()
		def file = new File(path)

		List<String> lines = file.readLines()

		// remove comments and empty lines
		lines = lines.findAll { line ->
			line.trim().startsWith(COMMENT) == false && line.trim() != ''
		}

		// hack for more methods call on one line
		List<String> convertedLines = []
		lines.each { line ->
			if(line.split("\\" + METHOD_CALL_SEPARATOR).size() > 2){
				if(line.contains(ASSIGNMENT_OP)){
					List<String> splitLine = line.split(ASSIGNMENT_OP)
					String var = splitLine[0].trim()
					if(splitLine[1].contains(var)){
						throw new CompilerException("Could not invoke method chain with assigment to property which is also an argument")
					}
					List<String> methodCalls = splitLine[1].split("\\" + METHOD_CALL_SEPARATOR)
					String targetObject = methodCalls.remove(0).trim()
					methodCalls.each { methodCall ->
						convertedLines << var + ASSIGNMENT_OP + targetObject + METHOD_CALL_SEPARATOR + methodCall.trim()
						targetObject = var
					}
				}else{
					throw new CompilerException("Can not call methods in chain")
				}
			}else {
				convertedLines << line
			}
		}
		lines = convertedLines
		// end hack (replace with methodBody << line)

		createClass(lines[0])
		createFields(lines)
		createMethods(lines)
		if(!lines.last().startsWith(END_CLASS)){
			throw new CompilerException('class shold end with ' + END_CLASS)
		}

		classFile
	}

	// create class in constant pool and set parent (default or extends)
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
				ArrayList<String> nativeClass = [STRING_CLASS, NULL_CLASS, NUMBER_CLASS, BOOL_CLASS, CLOSURE_CLASS, SYSTEMIO_CLASS]
				if(nativeClass.contains(classNameSplit[1])){
					throw new CompilerException('Could not extend class from default class ' + classNameSplit[1])
				}
				classFile.constantPool.add(classNameSplit[1])
			}

		}
		else
			throw new CompilerException('file not start with class definition')
	}

	// parse block with class properties
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

	// add class fields to constant pool
	def createField(String line){
		if(line.trim() == ''){
			return
		}
		line.split(ARGUMENT_SEPARATOR).each { var ->
			var = var.trim()
			if(fields.contains(var)){
				throw new CompilerException('field "' + var +'" already exists in class ' + classFile.constantPool[0])
			}
			fields.add(var)
			classFile.fields.add(classFile.constantPool.size())
			classFile.constantPool.add(var)
		}
	}

	// parse block with method definitions
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

	// parse method body without start and end keyword
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
				definitions.add(line.trim())
			}
			else{
				code.add(line)
			}
		}

		// locals contains self, arguments and local variables
		def locals = createLocalsMap(definitions, argList)

		def method = new PrecompiledMethod()

		method.signatureIndex = signitureIndex
		for(int i=0; i < code.size();){
			def result = compileLine(code[i], locals, classFile.constantPool)
			if(result){
				method.instructions.addAll(result)
				i++
			}
			else{
				result = compileClosure(code, i, classFile, createArgsForClosure(definitions , argList),  locals)
				method.instructions.addAll(result[0])
				i = result[1]

			}
		}
		classFile.methods.add(method)
	}


	// finds field in konstantPool or add them and return his position
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
		if(expr.startsWith(CLOSURE_OPEN_KEYWORD)){
			return EvalSituation.CLOSURE
		}
		if(expr[0] == '"' && expr[expr.length()-1] == '"'){
			return EvalSituation.STRING
		}
		if(expr == NULL_KEYWORD){
			return EvalSituation.NULL
		}
		if(expr == TRUE_STRING_VALUE || expr == FALSE_STRING_VALUE){
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
		def situation = resolveSituation(expr)
		if(situation == EvalSituation.METHOD_CALL && expr.indexOf(METHOD_CALL_SEPARATOR) == -1){
			expr =  SELF_POINTER + METHOD_CALL_SEPARATOR + expr
		}
		def instructions
		switch(situation){
			case EvalSituation.CLOSURE:
				return null
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
				if(inst.paramVal == null){
					throw new CompilerException("Not fount local variable '" + assignmentVar + "' for " + expr + " in class " + classFile.className)
				}
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
				if(loadInst.paramVal == null){
					throw new CompilerException("Not fount local variable '" + argName + "' for " + expr + " in class " + classFile.className)
				}
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
		constantPool.add(expr.replaceAll('"',''))
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

	List<PrecompiledInstruction> processNull(){
		def pushNull = new PrecompiledInstruction()
		pushNull.instruction = Instruction.PUSH_NULL
		[pushNull]
	}

	List<PrecompiledInstruction> processCreateObject(String expr, List<String> constantPool){
		String className= expr.replace(CONSTRUCTOR_INVOKE_NAME,'')
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
		getDefinitionsList(definitions).each { arg ->
			locals.put( arg, locals.size())
		}
		locals
	}
	def createArgsForClosure(List<String> definitions,List<String> argList){
		def locals = []
		argList.each { arg ->
			locals.add( arg )
		}
		getDefinitionsList(definitions).each { arg ->
			locals.add( arg )
		}
		locals
	}
	def getDefinitionsList(List<String> linesOfDefinitions){
		def result = []
		linesOfDefinitions.each{ line ->
			def args = line.split(ARGUMENT_SEPARATOR).collect { it.trim()}
			args.each { arg ->
				result.add(arg)
			}
		}
		result
	}

	def createSignature(String line){
		String head = line.replace(METHOD_START + ' ', '')
		String args = head.find(~/\(.*\)/)
		String name = head.substring(0,head.indexOf(METHOD_ARGUMENT_START_KEYWORD))
		Integer argsCount = args == '()' ? 0 : args.split(ARGUMENT_SEPARATOR).size()
		head.replace(args,name == 'call' ? "(0)" :"(${argsCount})")
	}

	def createArgList(String line){
		def wrappedArgs = line.find(~/\(.*\)/)
		def args = wrappedArgs.substring(1, wrappedArgs.length()-1);
		return args == '' ? [] : args.split(ARGUMENT_SEPARATOR).toList()
	}


	/// ------------------- Closure compiler methods ----------------------------

	def compileClosure(List<String> code, Integer codePtr, ClassFile classFile, List<String> parentLocalList, Map locals) {
		List instructions = []

		Integer closureIdx = classFile.closures.size()
		List<String> lineSplit = code[codePtr].split(ASSIGNMENT_OP)
		String assignmentVar = lineSplit[0].trim()

		List<String> args = []
		if(lineSplit[1].indexOf(CLOSURE_PARAMS_END_KEYWORD) != -1) {
			String closureArgs = lineSplit[1].substring(lineSplit[1].indexOf(CLOSURE_OPEN_KEYWORD) + 1, lineSplit[1].indexOf(CLOSURE_PARAMS_END_KEYWORD) - 1)
			args = closureArgs.split(ARGUMENT_SEPARATOR).collect { it.trim() }
		}

		codePtr++
		// parse body
		List<String> closureBody = []
		int closureOpen = 1
		int closureClose = 0
		int overflowProt = 0
		while(closureOpen){
			// closing keyword missing protection
			if(overflowProt++ > 2000){
				throw new CompilerException('closure body too big or closing keyword missing')
			}
			def currentLine = code[codePtr].trim()
			if(currentLine.startsWith(CLOSURE_CLOSE_KEYWORD)){
				closureClose++
			}
			if(currentLine.indexOf(CLOSURE_OPEN_KEYWORD) != -1){
				closureOpen++
			}
			if(closureClose >= closureOpen){
				codePtr++
				break
			}
			closureBody.add(code[codePtr++])
		}

		// add local variables from context
//		args.addAll(definitions)

		// create closure bytecode
		classFile.closures.add(createClosure(classFile, closureIdx, args, parentLocalList, closureBody))


		// create instruction to init closure
		def initClosureInst = new PrecompiledInstruction()
		initClosureInst.instruction = Instruction.INIT_CLOSURE
		initClosureInst.paramVal = closureIdx
		instructions.add(initClosureInst)

		// assignV value on stack
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
				putfieldInst.paramVal = findIndex(assignmentVar.replace(SELF_POINTER + FIELD_ACESSOR,''), classFile.constantPool)


				instructions.add(loadSelfInst)
				instructions.add(putfieldInst)
			}
			// local var assigment
			else{
				def inst = new PrecompiledInstruction()
				inst.instruction = Instruction.STORE
				inst.paramVal = locals.get(assignmentVar)
				if(inst.paramVal == null){
					throw new CompilerException("Not fount local variable '" + assignmentVar + "' for " + expr + " in class " + classFile.className)
				}
				instructions.add(inst)
			}
		}
		[instructions, codePtr]
	}

	// creates closure bytecode
	def createClosure(ClassFile classFile, Integer closureIdx, List<String> argList, List<String> parentArgList, List<String> methodBody){

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

		def closure = new PrecompiledClosure()
		closure.argsCount = argList.size()
		closure.instructions = []

		// add parent args to argList
		argList.addAll(parentArgList)
		// create Locals from closure
		def locals = createLocalsMap(definitions, argList)


		for(int i=0; i < code.size();){
			def result = compileLine(code[i], locals, classFile.constantPool)
			if(result){
				closure.instructions.addAll(result)
				i++
			}
			else{
				result = compileClosure(code, i,  classFile, createArgsForClosure(definitions, argList), locals)
				closure.instructions.addAll(result[0])
				i = result[1]

			}
		}
		closure
	}
}
