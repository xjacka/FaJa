package faJa.compilator.parser

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
			expressionList(parse(code.nextLine(), code))
		}
	}

	Expression parse(String line, Code code){
		if(line.trim() == ''){
			return new EmptyExpression()
		}
		// check string creation
		if(startString(line) != null){
			StringCreation stringCreation = new StringCreation(cleanString(line))
			stringCreation.memberAccess = parse(line.substring(startString(line).length()))
			return stringCreation
		}
		// check number creation
		if(startNumber(line) != null){
			NumberCreation numberCreation = new NumberCreation(cleanNumber(line))
			numberCreation.memberAccess = parse(line.substring(startNumber(line).length()))
			return numberCreation
		}
		// check bool creation
		if(startBool(line) != null){
			BoolCreation boolCreation = new BoolCreation(cleanBool(line))
			boolCreation.memberAccess = parse(line.substring(startBool(line).length()))
			return boolCreation
		}
		// check closure creation
		if(startClosure(line)){
			List args = closureArgList(line)
			return new ClosureCreation(args, code)
		}
		// check declaration
		if(startDeclaration(line) != null){
			Declaration declaration = new Declaration(cleanDeclaration(startDeclaration(line)))
			if(hasDefinition(line)){
				declaration.definition = new Assigment(declaration.varName)
				declaration.definition.assigned = parse(line.substring(startDeclaration(line).length()))
			}
			if(nextDeclaration(line).trim() != '' ){
				declaration.nextDeclaration = parse('var ' + nextDeclaration(line))
			}
			return declaration

		}
		// check assignment
		if(startAssignment(line) != null){
			Assigment result = new Assigment()
			result.assignee = cleanAssignee(startAssignment(line))
			result.assigned = parse(line.substring(line.indexOf(Compiler.ARGUMENT_SEPARATOR) + Compiler.ARGUMENT_SEPARATOR.length()))
			return result
		}
		// check field assignment

		if(startFieldAssigment(line)){
			FieldAssignment result = new FieldAssignment()
			result.field = cleanFieldAssigmee(startAssignment(line))
			result.assigned = parse(line.substring(line.indexOf(Compiler.ARGUMENT_SEPARATOR) + Compiler.ARGUMENT_SEPARATOR.length()))
			return result
		}
		// check objectCreation
		if(startObjectCreation(line) != null){
			ObjectCreation objectCreation = new ObjectCreation(cleanClassName(line))
			objectCreation.methodCall = parse(line.substring(startObjectCreation(line).length()))
			return objectCreation
		}
		// check objectAccess
		if(startObject(line)){
			ObjectAccess objectAccess = new ObjectAccess(cleanVarName(line))
			objectAccess.memberAccess = parse(line.substring(startObject(line).length()))
			return objectAccess
		}

		// check field access
		if(startAccessField(line)){
			FieldAccess fieldAccess = new FieldAccess(cleanFieldName(line))
			 fieldAccess.nextMemberAccess = parse(line.substring(startAccessField(line).length()))
			return fieldAccess
		}

		// check method call
		if(startMethodCall(line)){
			MethodCall methodCall = new MethodCall(cleanMethodName(line))
			List<String> args = methodArgs(line)
			args.each {
				methodCall.args.add(parse(it))
			}
			methodCall.nextMemberAccess = parse(line.substring(startMethodCall(line).length() + betweenParentheses(line).length() + ')'.length()))
			return methodCall
		}
		throw new CompilerException('unexpected token on line: ' + line)
	}

	String cleanBool(def s) {

		// todo
	}

	String startBool(def s) {
// todo

	}

	def cleanString(def s) {
// todo
	}

	def startString(String s) {
// todo
	}
// closure creation
	protected startClosure(String line){
		line.find(~/$ *\{/)
	}
	protected closureArgList(String line){
		if(line.indexOf('|') == -1){
			return []
		}
		String args = line.substring(line.indexOf('{') + 1, line.indexOf('|'))
		args.split(',').collect { it.trim() }
	}
	// number creation
	protected startNumber(String line){
		line.find(~/$ *[0-9]+/)
	}
	protected Integer cleanNumber(String line){
		startNumber(line).trim().toInteger()
	}

	// field assigment
	protected startFieldAssigment(String line){
		line.find(~/$ *(:[a-zA-Z0-9]*)? +<\-/)
	}
	protected cleanFieldAssigmee(String line){
		cleanAssignee(startFieldAssigment(line)).substring(1)
	}
	// field access
	protected startAccessField(String line){
		line.find(~/$ *:[a-z]+[a-zA-Z0-9]*/)
	}
	protected cleanFieldName(String line){
		startAccessField(line).trim().substring(1)
	}
	// method call
	protected startMethodCall(String line){
		line.find(~/$ *\.[a-z]+[a-zA-Z0-9]*\(/)
	}
	protected cleanMethodName(String line){
		line.substring(line.indexOf('.') + 1, line.indexOf('('))
	}
	// ObjectCreation
	protected startObjectCreation(String line) {
		line.find(~/$ *[A-Z]+[a-zA-Z0-9]*\.new/)
	}
	protected cleanClassName(String line){
		String tmp = startObjectCreation(line)
		tmp.substring(0,tmp.length() - '.new'.length()).trim()
	}
// ObjectAccess
	protected startObject(String line){
		line.find(~/$ *[a-z]+[a-zA-Z0-9]*/)
	}
	protected cleanVarName(String line){
		line.find(~/$ *[a-z]+[a-zA-Z0-9]*/).trim()
	}

	/// Assignment
	protected cleanAssignee(String assignee){
		assignee.substring(0, assignee.indexOf(Compiler.ARGUMENT_SEPARATOR)).trim()
	}
	protected startAssignment(String line){
		line.find(~/$ *[a-z]+[a-zA-Z0-9]* +<\-/)
	}


	/// Declaration
	protected cleanDeclaration(String declaration){
		declaration.substring(declaration.indexOf("var") + "var".length()).trim()
	}
	protected hasDefinition(String line){
		String followingString = line.substring(startDeclaration(line).length()).trim()
		if(followingString == "" || followingString.startsWith(',')){
			return false
		}
		return true
	}
	protected nextDeclaration(String line){
		skipToSameLevelComma( line.substring(startDeclaration(line).length())).substring(1)
	}
	protected startDeclaration(String line){
		line.find(~/$ *var +[a-z]+[a-zA-Z0-9]*/)
	}

	/// skip
	protected String skipToSameLevelComma(String line){
		int openParentheses = 0
		int closeParentheses = 0
		int cntr = 0
		// hledat v dalsi methode pokud se nenajdou na jednom radku?
		while(cntr < line.length()){
			if(line[cntr] == openParentheses){
				openParentheses++
			}
			if(line[cntr] == closeParentheses){
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
	protected List<String> methodArgs(String line){
		String args = betweenParentheses(line).trim()
		List<String> result = []
		while(args != ''){
			String rest = skipToSameLevelComma(args)
			String arg = args.substring(0, args.length() - rest.length())
			result.add(arg)
			args = rest.substring(1).trim()
		}
		result
	}
	protected String betweenParentheses(String line){
		Integer firstParenthesesIdx = line.indexOf('(')
		line = line.substring(firstParenthesesIdx + 1)
		int openParentheses = 1
		int closeParentheses = 0
		int cntr = 0
		// hledat v dalsi methode pokud se nenajdou na jednom radku?
		while(cntr < line.length()){
			if(line[cntr] == openParentheses){
				openParentheses++
			}
			if(line[cntr] == closeParentheses){
				closeParentheses++
			}
			if(openParentheses == closeParentheses){
				break
			}
			cntr++
		}
		line.substring(0, cntr)
	}
}
