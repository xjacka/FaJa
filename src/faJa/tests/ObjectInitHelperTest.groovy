package faJa.tests

import faJa.helpers.ObjectInitHelper

class ObjectInitHelperTest {

	def testBoolen(){

		assert ObjectInitHelper.createBool(10,true) == [0, 10, 1].toArray() as byte[]
		assert ObjectInitHelper.createBool(10,false) == [0, 10, 0].toArray() as byte[]
	}

	def testNumber(){

		assert ObjectInitHelper.createNumber(10,12342) == [0, 10, 0,0,48,54].toArray() as byte[]
		assert ObjectInitHelper.createNumber(10,16777473) == [0, 10, 1,0,1,1].toArray() as byte[]
	}
}
