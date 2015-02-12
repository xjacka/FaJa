package faJa.tests

import faJa.compilator.evaluation.Expression
import faJa.compilator.parser.ClosureParser
import faJa.compilator.parser.Code
import faJa.compilator.parser.Parser

/**
 * Created by Kamil on 12. 2. 2015.
 */
class ParserTest {
	public testActionResolvers(){
		Parser parser = new Parser()

		assert parser.startDeclaration(" var sasddweqsa") != null
		assert parser.hasDefinition(" var sasddweqsa") == false
		assert parser.hasDefinition(" var sasddweqsa <- asd:asda") == true

		assert parser.startNumber('  432421') != null
		assert parser.cleanNumber('  432421') == 432421

		assert parser.startMethodCall('    .asdasd()') != null
		assert parser.cleanMethodName('    .asdasd()') == 'asdasd'
		assert parser.methodArgs('    .asdasd()').empty

		assert parser.startMethodCall('    .asdasd(asda, 5, 4)') != null
		assert parser.cleanMethodName('    .asdasd(asda, 5, 4)') == 'asdasd'
		assert parser.methodArgs('    .asdasd(asda, 5, 4)') == ['asda', '5', '4']


		assert parser.startObject("    a5sdfsd5") != null
		assert parser.cleanVarName("    a5sdfsd5") == 'a5sdfsd5'

		assert parser.startFieldAssigment(':dasdasda <-') != null
		assert parser.cleanFieldAssigmee(':dasdasda <-') == 'dasdasda'


		assert parser.startAccessField('  :adsadadsa') != null
		assert parser.cleanFieldName('  :adsadadsa') == 'adsadadsa'

		assert parser.startObjectCreation(' Rasdadsa.new') != null
		assert parser.cleanClassName(' Rasdadsa.new') == 'Rasdadsa'

		assert parser.startAssignment(' asd <-') != null
		assert parser.cleanAssignee(' asd <-') == 'asd'

		assert parser.startBool('  true') == 'true'
		assert parser.startBool('false') == 'false'
		assert parser.startBool('asdad') == null

		assert parser.startNull('    null') == true
		assert parser.startNull('    true') == false

		assert parser.startClosure('{ a, b |') != null

		assert parser.startString(' "dadssad"') != null
		assert parser.cleanString(' "dadssad"') == 'dadssad'

		assert parser.betweenParentheses('.call(sadas, asda.wrwer(asdasd,dasdas,456), a:sadasd, Object.new)') == 'sadas, asda.wrwer(asdasd,dasdas,456), a:sadasd, Object.new'


		ClosureParser closureParser = new ClosureParser()
		assert closureParser.afterClosureEnd('   }  ') != null

	}

	def  testParsing(){
		List<String> body = []
		body.add('var a <- 5, b <- "test"')
		body.add('self:test.call(1,2,true)')
		body.add('self:test <- a.call()')
		body.add('self:test <- null')
		body.add('self:test <- [ a, 2]')

		Code code = new Code(body)
		Parser parser = new Parser()
		List<Expression> expressionList = parser.parseCode(code)
		expressionList.each{
			println(it.toString())
		}

	}
}
