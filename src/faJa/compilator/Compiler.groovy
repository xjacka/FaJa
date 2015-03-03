package faJa.compilator

import faJa.compilator.representation.ClassFile
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
	public static final String FIELD_ACCESSOR = ':'
	public static final String COMMENT = ':-)'
	public static final String INHERITANCE_KEYWORD = 'extends'
	public static final String CONSTRUCTOR_INVOKE_NAME = '.new'
	public static final String DEFAULT_PARENT = 'Object'
	public static final String STRING_CLASS = 'String'
	public static final String NUMBER_CLASS = 'Number'
	public static final String BOOL_CLASS = 'Bool'
	public static final String CLOSURE_CLASS = 'Closure'
	public static final String ARRAY_CLASS = 'Array'
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
	public static final String THREAD_CLASS = "Thread"
	public static final String STRING_START = '"'
	public static final String ARRAY_START = '['
	public static final String ARRAY_END = ']'
	public static final String VARIABLE_PATTERN = '[a-z]+[a-zA-Z0-9]*'


	ClassFile classFile

	def classMethods = [] // for better code checking
	def fields = [] // for better code checking

	ClassFile compile(String path) {
		classFile = new ClassFile()
		def file = new File(path)

		if (!file.exists()) {
			println(path)
		}
		List<String> lines = file.readLines()

		// remove comments and empty lines
		lines = lines.findAll { line ->
			line.trim().startsWith(COMMENT) == false && line.trim() != ''
		}

		createClass(lines[0])
		createFields(lines)
		createMethods(lines)
		if (!lines.last().startsWith(END_CLASS)) {
			throw new CompilerException('class should end with ' + END_CLASS)
		}

		classFile
	}

	// create class in constant pool and set parent (default or extends)
	def createClass(String line) {
		if (line.trim().startsWith(SINGLETON_KEYWORD)) {
			classFile.isSingleton = true
			line = line.replace(SINGLETON_KEYWORD, CLASS_KEYWORD)
		}
		if (line.trim().startsWith(CLASS_KEYWORD)) {
			def classNameSplit = line.replace(CLASS_KEYWORD + ' ', '').trim().split(INHERITANCE_KEYWORD).collect {
				it.trim()
			}
			classFile.constantPool.add(classNameSplit[0])
			if (classNameSplit.size() == 1) {
				classFile.constantPool.add(DEFAULT_PARENT)
			} else {
				ArrayList<String> nativeClass = [STRING_CLASS, NULL_CLASS, NUMBER_CLASS, BOOL_CLASS, CLOSURE_CLASS, SYSTEMIO_CLASS]
				if (nativeClass.contains(classNameSplit[1])) {
					throw new CompilerException('Could not extend class from default class ' + classNameSplit[1])
				}
				classFile.constantPool.add(classNameSplit[1])
			}

		} else
			throw new CompilerException('file not start with class definition')
	}

	// parse block with class properties
	def createFields(List<String> lines) {
		def fields = false

		lines.each { line ->
			if (line.trim().startsWith(END_FIELDS)) {
				fields = false
			}
			if (fields) {
				createField(line)
			}
			if (line.trim().startsWith(START_FIELDS)) {
				fields = true
			}
		}
	}

	// add class fields to constant pool
	def createField(String line) {
		if (line.trim() == '') {
			return
		}
		line.split(ARGUMENT_SEPARATOR).each { var ->
			var = var.trim()
			if (fields.contains(var)) {
				throw new CompilerException('field "' + var + '" already exists in class ' + classFile.constantPool.get(0))
			}
			fields.add(var)
			classFile.fields.add(classFile.constantPool.add(var))
		}
	}

	// parse block with method definitions
	def createMethods(List<String> lines) {
		Boolean methods = false
		List<String> methodBody = []
		String signature
		List<String> argList

		lines.each { line ->
			line = line.trim()

			if (line.startsWith(START_METHODS)) {
				methods = true
			}
			if (line.startsWith(END_METHODS)) {
				methods = false
			}
			if (methods) {
				if (line.startsWith(METHOD_START)) {
					signature = createSignature(line)
					if (classMethods.contains(signature)) {
						throw new CompilerException('method "' + signature + '" already exists in class ' + classFile.constantPool.get(0))
					}
					classMethods.add(signature)
					argList = createArgList(line)
					methodBody = []
					return
				}
				if (line.startsWith(METHOD_END)) {
					new MethodCompiler(classFile).createMethod(signature, argList, methodBody)
				}
				methodBody << line
			}
		}
	}

	def createSignature(String line) {
		String head = line.replace(METHOD_START + ' ', '')
		String args = head.find(~/\(.*\)/)
		Integer argsCount = args == '()' ? 0 : args.split(ARGUMENT_SEPARATOR).size()
		head.replace(args, "(${argsCount})")
	}

	def createArgList(String line) {
		def wrappedArgs = line.find(~/\(.*\)/)
		def args = wrappedArgs.substring(1, wrappedArgs.length() - 1);
		return args == '' ? [] : args.split(ARGUMENT_SEPARATOR).toList()
	}
}
