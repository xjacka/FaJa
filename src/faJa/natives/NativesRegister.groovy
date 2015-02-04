package faJa.natives


class NativesRegister {

	def register = [
	        '+(1)Number' : NumberNatives.plus,
	        '-(1)Number' : NumberNatives.minus,
	        '*(1)Number' : NumberNatives.mul,
	        '/(1)Number' : NumberNatives.div,
	        '%(1)Number' : NumberNatives.mod,
	        '==(1)Number' : NumberNatives.equals,
	        'ifTrue(1)Number' : NumberNatives.ifTrue,
	        'ifFalse(1)Number' : NumberNatives.ifFalse,
	        'init(1)Number' : NumberNatives.init,
	        'toS(0)Number' : NumberNatives.toS,

	        'length(0)String' : StringNatives.length,
	        '==(1)String' : StringNatives.equals,
	        '+(1)String' : StringNatives.plus,
	        'ifTrue(1)String' : StringNatives.ifTrue,
	        'ifFalse(1)String' : StringNatives.ifFalse,
	        'toS(0)String' : StringNatives.toS,

	        '==(1)Bool' : BoolNatives.equals,
	        'ifTrue(1)Bool' : BoolNatives.ifTrue,
	        'ifFalse(1)Bool' : BoolNatives.ifFalse,
	        'and(1)Bool' : BoolNatives.and,
	        'or(1)Bool' : BoolNatives.or,
	        'not(0)Bool' : BoolNatives.not,

	        'writeToFile(2)SystemIO' : SystemIONatives.writeToFile,
	        'readFromFile(2)SystemIO' : SystemIONatives.readFromFile,
	        'out(1)SystemIO' : SystemIONatives.out,

	        'call(0)Closure' : ClosureNatives.call,

	        'equals(1)Object' : ObjectNatives.equals,
	        'toS(0)Object' : ObjectNatives.toS,
	]
}
