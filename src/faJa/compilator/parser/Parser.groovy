package faJa.compilator.parser

import faJa.compilator.evaluation.ArrayCreation
import faJa.compilator.evaluation.Assigment
import faJa.compilator.evaluation.BoolCreation
import faJa.compilator.evaluation.ClosureCreation
import faJa.compilator.evaluation.Declaration
import faJa.compilator.evaluation.EmptyExpression
import faJa.compilator.evaluation.Expression
import  faJa.compilator.Compiler
import faJa.compilator.evaluation.FieldAccess
import faJa.compilator.evaluation.FieldAssignment
import faJa.compilator.evaluation.MethodCall
import faJa.compilator.evaluation.NullLoad
import faJa.compilator.evaluation.NumberCreation
import faJa.compilator.evaluation.ObjectAccess
import faJa.compilator.evaluation.ObjectCreation
import faJa.compilator.evaluation.StringCreation
import faJa.exceptions.CompilerException

/**
 * Created by Kamil on 11. 2. 2015.
 */
class Parser {


	List<Expression> parseCode(Code code){
		List<Expression> expressionList = []
		while(code.hasNextLine()){
			expressionList.add(parse(code.nextLine(), code))
		}
		expressionList
	}

	Expression parse(String line, Code code){
		line = line.trim()
		if(line == ''){
			return new EmptyExpression()
		}
		// check null
		if(startNull(line)){
			return new NullLoad()
		}
		// check array
		if(startArray(line) != null){
			Integer currentLineIdx = code.currentLineIdx()
			String args = betweenParentheses(line,code, '[', ']')
			List<String> argList = args.split(',').collect{it.trim()}
			ArrayCreation arrayCreation = new ArrayCreation(argList)
			if(currentLineIdx < code.currentLineIdx()) { // code moved
				line = code.currentLine()
			}
			String nextToken = tokenAfterArray(line)
			arrayCreation.memberAccess = parse(nextToken, code)
			return arrayCreation
		}

		// check string creation
		if(startString(line) != null){
			StringCreation stringCreation = new StringCreation(cleanString(line))
			String nextToken = line.substring(line.indexOf('"', startString(line).length())+ 1)
			stringCreation.memberAccess = parse(nextToken, code)
			return stringCreation
		}
		// check number creation
		if(startNumber(line) != null){
			NumberCreation numberCreation = new NumberCreation(cleanNumber(line))
			String nextToken = line.substring(startNumber(line).length())
			numberCreation.memberAccess = parse(nextToken, code)
			return numberCreation
		}
		// check bool creation
		if(startBool(line) != null){
			BoolCreation boolCreation = new BoolCreation(startBool(line))
			String nextToken = line.substring(startBool(line).length())
			boolCreation.memberAccess = parse(nextToken, code)
			return boolCreation
		}
		// check closure creation
		if(startClosure(line)){
			List args = closureArgList(line)
			List body = closureBody(line, code).findAll{ it.trim() != ''}
			return new ClosureCreation(args, body)
		}
		// check declaration
		if(startDeclaration(line) != null){
			Declaration declaration = new Declaration(cleanDeclaration(startDeclaration(line)))
			if(hasDefinition(line)){
				String rest = skipToSameLevelComma(line, code) // todo nextToken
				String nextToken = line.substring(line.indexOf('var') + 'var'.length(), line.length() - rest.length())
				declaration.definition = parse(nextToken, code)
			}
			if(nextDeclaration(line, code).trim() != '' ){
				declaration.nextDeclaration = parse('var ' + nextDeclaration(line, code), code)
			}
			return declaration

		}
		// check assignment
		if(startAssignment(line) != null){
			Assigment result = new Assigment()
			result.assignee = cleanAssignee(line)
			String nextToken = line.substring(line.indexOf(Compiler.ASSIGNMENT_OP) + Compiler.ASSIGNMENT_OP.length())
			result.assigned = parse(nextToken, code)
			return result
		}
		// check field assignment
		if(startFieldAssigment(line)){
			FieldAssignment result = new FieldAssignment()
			result.field = cleanFieldAssignee(line)
			String nextToken = line.substring(line.indexOf(Compiler.ASSIGNMENT_OP) + Compiler.ASSIGNMENT_OP.length())
			result.assigned = parse(nextToken, code)
			return result
		}
		// check objectCreation
		if(startObjectCreation(line) != null){
			ObjectCreation objectCreation = new ObjectCreation(cleanClassName(line))
			String nextToken = line.substring(startObjectCreation(line).length())
			objectCreation.memberAccess = parse(nextToken, code)
			return objectCreation
		}
		// check objectAccess
		if(startObject(line)){
			ObjectAccess objectAccess = new ObjectAccess(cleanVarName(line))
			String nextToken = line.substring(startObject(line).length())
			objectAccess.memberAccess = parse(nextToken, code)
			return objectAccess
		}

		// check field access
		if(startAccessField(line)){
			FieldAccess fieldAccess = new FieldAccess(cleanFieldName(line))
			String nextToken =line.substring(startAccessField(line).length())
			fieldAccess.nextMemberAccess = parse(nextToken, code)
			return fieldAccess
		}

		// check method call
		if(startMethodCall(line)){
			MethodCall methodCall = new MethodCall(cleanMethodName(line))
			Integer currentLineIdx = code.currentLineIdx()
			String args = betweenParentheses(line, code)
			List<String> argList = methodArgs(args)
			argList.each {
				methodCall.args.add(parse(it, code))
			}
			if(currentLineIdx < code.currentLineIdx()) { // code moved
				line = code.currentLine()
			}
			String nextToken = tokenAfterMethod(line)
			methodCall.nextMemberAccess = parse(nextToken, code)
			return methodCall
		}
		throw new CompilerException('unexpected token on line: ' + line)
	}


	boolean startNull(String line) {
		line.trim().startsWith(Compiler.NULL_KEYWORD)
	}

	String startBool(String line) {
		if(line.trim().startsWith(Compiler.TRUE_STRING_VALUE)){
			return Compiler.TRUE_STRING_VALUE
		}
		if(line.trim().startsWith(Compiler.FALSE_STRING_VALUE)){
			return Compiler.FALSE_STRING_VALUE
		}
		null
	}

	def cleanString(String line) {
		int firstQuote = line.indexOf('"')
		line.substring(firstQuote+1, line.indexOf('"', firstQuote+1))
	}

	def startString(String line) {
		line.find(~/^ *"/)
	}

	// closure array
	def startArray(String line){
		line.find(~/^ *\[/)
	}
	def tokenAfterArray(String line){
		if(!startArray(line)){
			line = '[' + line
		}
		String inParenthesses = betweenParentheses(line, null, '[', ']')
		line.substring(line.indexOf('[') + 1 + inParenthesses.length() + 1)
	}
// closure creation
	def startClosure(String line){
		line.find(~/^ *\{/)
	}
	def closureArgList(String line){
		if(line.indexOf('|') == -1){
			return []
		}
		String args = line.substring(line.indexOf('{') + 1, line.indexOf('|'))
		args.split(',').collect { it.trim() }
	}
	// number creation
	def startNumber(String line){
		line.find(~/^ *[0-9]+/)
	}
	def Integer cleanNumber(String line){
		startNumber(line).trim().toInteger()
	}

	// field assigment
	def startFieldAssigment(String line){
		line.find(~/^ *(:[a-zA-Z0-9]*)? +<\-/)
	}
	def cleanFieldAssignee(String line){
		cleanAssignee(startFieldAssigment(line)).substring(1)
	}
	// field access
	def startAccessField(String line){
		line.find(~/^ *:[a-z]+[a-zA-Z0-9]*/)
	}
	def cleanFieldName(String line){
		startAccessField(line).trim().substring(1)
	}
	// method call
	def startMethodCall(String line){
		line.find(~/^ *\.[^\.^:^\(]+\(/)
	}
	def cleanMethodName(String line){
		line.substring(line.indexOf('.') + 1, line.indexOf('('))
	}
	def tokenAfterMethod(String line){
		if(!startMethodCall(line)){
			line = '(' + line
		}
		String inParenthesses = betweenParentheses(line, null)
		line.substring(line.indexOf('(') + 1 + inParenthesses.length() + 1)
	}
	// ObjectCreation
	def startObjectCreation(String line) {
		line.find(~/^ *[A-Z]+[a-zA-Z0-9]*\.new/)
	}
	def cleanClassName(String line){
		String tmp = startObjectCreation(line)
		tmp.substring(0,tmp.length() - '.new'.length()).trim()
	}
// ObjectAccess
	def startObject(String line){
		line.find(~/^ *[a-z]+[a-zA-Z0-9]*/)
	}
	def cleanVarName(String line){
		line.find(~/^ *[a-z]+[a-zA-Z0-9]*/).trim()
	}

	/// Assignment
	def cleanAssignee(String line){
		line.substring(0, line.indexOf(Compiler.ASSIGNMENT_OP)).trim()
	}
	def startAssignment(String line){
		line.find(~/^ *[a-z]+[a-zA-Z0-9]* +<\-/)
	}


	/// Declaration
	def cleanDeclaration(String declaration){
		declaration.substring(declaration.indexOf("var") + "var".length()).trim()
	}
	def hasDefinition(String line){
		String followingString = line.substring(startDeclaration(line).length()).trim()
		if(followingString == "" || followingString.startsWith(',')){
			return false
		}
		return true
	}
	def nextDeclaration(String line, Code code){
		String rest = skipToSameLevelComma( line.substring(startDeclaration(line).length()))
		if(rest == ''){
			return ''
		}
		rest.substring(1)
	}
	def startDeclaration(String line){
		String tmp = line.find(~/^ *var +[a-z]+[a-zA-Z0-9]*/)
		tmp
	}

	/// skip todo test
	def String skipToSameLevelComma(String line, Code code = null){
		int openParentheses = 0
		int closeParentheses = 0
		int cntr = 0
		// hledat v dalsi methode pokud se nenajdou na jednom radku?
		while(cntr < line.length() || (code && code.hasNextLine() && (openParentheses - closeParentheses) > 1)){
			if(cntr >= line.length()){
				cntr = 0
				line = code.nextLine()
			}
			if(line[cntr] == '(' || line[cntr] == '{' || line[cntr] == '[' ){
				openParentheses++
			}
			if(line[cntr] == ')' || line[cntr] == '}' || line[cntr] == ']' ){
				closeParentheses++
			}
			if(openParentheses == closeParentheses && line[cntr] == ','){
				break
			}
			cntr++
		}
		line.substring(cntr)
	}

	// method call
	def List<String> methodArgs(String args){
		List<String> result = []
		while(1){
			String rest = skipToSameLevelComma(args, null)
			String arg = args.substring(0, args.length() - rest.length())
			if(args == ''){
				break
			}
			result.add(arg)
			if(rest.length() == 0){
				break
			}
			args = rest.substring(1).trim()
		}
		result
	}
	List<String> closureBody(String line, Code code ){
		String open = '{'
		String close = '}'
		String endLineSeparator = '\n'
		String result = betweenParentheses('{', code,  open, close, endLineSeparator)
		result.split(endLineSeparator).toList()
	}
	String betweenParentheses(String line, Code code, String open = '(', String close = ')', String endLineSeparator = ' '){
		Integer firstParenthesesIdx = line.indexOf(open)
		line = line.substring(firstParenthesesIdx + 1)
		int openParentheses = 1
		int closeParentheses = 0
		int cntr = 0
		String result = ""
		// hledat v dalsi methode pokud se nenajdou na jednom radku?
		while(cntr < line.length() || (code && code.hasNextLine())){
			if(cntr >= line.length()){
				cntr = 0
				result = result + endLineSeparator + line
				line = code.nextLine()
			}
			if(line[cntr] == open){
				openParentheses++
			}
			if(line[cntr] == close){
				closeParentheses++
			}
			if(openParentheses == closeParentheses){
				break
			}
			cntr++
		}
		result = result + line.substring(0, cntr)
		result
	}
}
