package faJa

class PrecompiledMethod {
	def signature
	List<PrecompiledInstruction> instructions

	def toBytecode(){
		[] as byte[]
	}
}
