package faJa.compilator

import faJa.exceptions.CompilerException

class LocalVariables {

	List<String> localVariables = []

	LocalVariables(){
	}

	def addLocalVariable(String name){
		name = name.trim()
		if(innerFindIndexByName(name) != null){
			throw new CompilerException('local variable: ' + name + ' already defined')
		}
		localVariables.push(name)
	}

	def addLocalVariables(List<String> names){
		names.each{
			addLocalVariable(it)
		}
	}

	List<String> asList(){
		List newList = []
		newList.addAll(localVariables)
		return newList
	}

	Integer findIndexByName(String name){
		Integer result = innerFindIndexByName(name)
		if(result != null){
			return result
		}
		throw new CompilerException('local variable: ' + name + ' not found')
	}

	private Integer innerFindIndexByName(String name){
		Integer result = null
		localVariables.eachWithIndex { String varName, int idx ->
			if(name == varName){
				result = idx
			}
		}
		return result
	}

	Integer count() {
		localVariables.size()
	}
}
