import faJa.compilator.parser.Parser
import faJa.tests.ClassAccessHelperTest
import faJa.tests.ClassFileTest
import faJa.tests.CompilerTest
import faJa.tests.ConvertTest
import faJa.tests.ObjectInitHelperTest
import faJa.tests.ParserTest

def convertTest = new ConvertTest()
convertTest.test1()
convertTest.test2()

def classFileTest = new ClassFileTest()
classFileTest.testPrefix()
classFileTest.testWithoutMethods()
classFileTest.testMethods()

CompilerTest compileTest = new CompilerTest()
//compileTest.compile()

ClassAccessHelperTest classAcessHelperTest = new ClassAccessHelperTest()
classAcessHelperTest.test()
classAcessHelperTest.test(100)

ObjectInitHelperTest objectInitHelperTest = new ObjectInitHelperTest()
objectInitHelperTest.testBoolen()
objectInitHelperTest.testNumber()


ParserTest parserTest = new ParserTest()
parserTest.testActionResolvers()