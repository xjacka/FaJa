package faJa.tests

import faJa.helpers.ByteHelper

class ConvertTest {

	def test1(){
		assert ([0, 10] as byte[]) == ByteHelper.IntegerTo2Bytes(10)
		assert  10 == ByteHelper.bytesToInt((byte)0, (byte)10)
		assert  10 == ByteHelper.bytesToIntAt([0, 10] as byte[], 0)
		assert  10 == ByteHelper.bytesToIntAt([122, 33, 0, 10] as byte[], 2)


		def testNums = [432, 255, 256, 0, 256*10]
		testNums.each{testNum ->
			byte[] tmp = ByteHelper.IntegerTo2Bytes(testNum)
			assert ByteHelper.bytesToInt(tmp[0],tmp[1]) == testNum

			assert ByteHelper.bytesToIntAt(tmp, 0) == testNum
		}

	}

}