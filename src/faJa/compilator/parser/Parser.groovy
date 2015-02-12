package faJa.compilator.parser

import faJa.compilator.evaluation.Assigment
import faJa.compilator.evaluation.Declaration
import faJa.compilator.evaluation.EmptyExpression
import faJa.compilator.evaluation.Expression
import  faJa.compilator.Compiler
import faJa.compilator.evaluation.FieldAccess
import faJa.compilator.evaluation.MethodCall
import faJa.compilator.evaluation.ObjectAccess
import faJa.compilator.evaluation.ObjectCreation
import faJa.exceptions.CompilerException

/**
 * Created by Kamil on 11. 2. 2015.
 */
class Parser {

	enum EvalSituation {ASSINUMBER, STRING, BOOL, NULL, METHOD_CALL, CREATE_OBJECT, CLOSURE, VARIABLE, FIELD}


	def Expression parse(String line){
		if(line.trim() == ''){
			return new EmptyExpression()
		}
		// check string creation
		// check string number
		// check string bool
		// check string closure

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
			result.assigned = parse(line.substring(line.indexOf(Compiler.ARGUMENT_SEPARATOR + Compiler.ARGUMENT_SEPARATOR.length())))
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

	// field assigment
	private startFieldAssigment(String line){
		line.find(~/$ *(:[a-zA-Z0-9]*)? +<\-/)
	}
	private cleanFieldAssigment(String line){

	}
	// field access
	private startAccessField(String line){
		line.find(~/$ *:[a-z]+[a-zA-Z0-9]*/)
	}
	private cleanFieldName(String line){
		startAccessField(line).trim().substring(1)
	}
	// method call
	private startMethodCall(String line){
		line.find(~/$ *\.[a-z]+[a-zA-Z0-9]*\(/)
	}
	private cleanMethodName(String line){
		line.substring(line.indexOf('.') + 1, line.indexOf('('))
	}
	// ObjectCreation
	private startObjectCreation(String line) {
		line.find(~/$ *[A-Z]+[a-zA-Z0-9]*\.new/)
	}
	private cleanClassName(String line){
		String tmp = startObjectCreation(line)
		tmp.substring(0,tmp.length() - '.new'.length()).trim()
	}
// ObjectAccess
	private startObject(String line){
		line.find(~/$ *[a-z]+[a-zA-Z0-9]*/)
	}
	private cleanVarName(String line){
		line.find(~/$ *[a-z]+[a-zA-Z0-9]*/).trim()
	}

	/// Assignment
	private cleanAssignee(String assignee){
		assignee.substring(0, assignee.indexOf(Compiler.ARGUMENT_SEPARATOR)).trim()
	}
	private startAssignment(String line){
		line.find(~/$ *[a-z]+[a-zA-Z0-9]* +<\-/)
	}


	/// Declaration
	private cleanDeclaration(String declaration){
		declaration.substring(declaration.indexOf("var") + "var".length()).trim()
	}
	private hasDefinition(String line){
		String followingString = line.substring(startDeclaration(line).length()).trim()
		if(followingString == "" || followingString.startsWith(',')){
			return false
		}
		return true
	}
	private nextDeclaration(String line){
		skipToSameLevelComma( line.substring(startDeclaration(line).length())).substring(1)
	}
	private startDeclaration(String line){
		line.find(~/$ *var +[a-z]+[a-zA-Z0-9]*/)
	}

	/// skip
	private String skipToSameLevelComma(String line){
		int openParentheses = 0
		int closeParentheses = 0
		int cntr = 0
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
	private List<String> methodArgs(String line){
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
	private String betweenParentheses(String line){
		Integer firstParenthesesIdx = line.indexOf('(')
		line = line.substring(firstParenthesesIdx + 1)
		int openParentheses = 1
		int closeParentheses = 0
		int cntr = 0
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
