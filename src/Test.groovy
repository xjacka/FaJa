import faJa.tests.ClassFileTest
import faJa.tests.ConvertTest

def convertTest = new ConvertTest()
convertTest.test1()

def classFileTest = new ClassFileTest()
classFileTest.testPrefix()
classFileTest.testWithoutMethods()