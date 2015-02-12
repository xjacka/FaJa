package faJa.compilator.parser

import faJa.compilator.evaluation.Expression

/**
 * Created by Kamil on 12. 2. 2015.
 */
class ClosureParser extends Parser{

	@Override
	List<Expression> parseCode(Code code){
		List<Expression> expressionList
		while(code.hasNextLine()){
			String line = code.nextLine()
			if(startsWithClosureEnd(line)){
				String afterClosureEnd = afterClosureEnd(line)
				if(afterClosureEnd != null){
					code.insert(afterClosureEnd)
				}
				return expressionList
			}
			expressionList.add(parse(code.nextLine(), code))
		}
	}


	def startsWithClosureEnd(String line){
		line.find(~/^ *\}/) != null
	}

	def afterClosureEnd(String line){
		line.substring(line.indexOf('}') + 1).trim()
	}
}
