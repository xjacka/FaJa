package faJa.natives


class NativesRegister {

	static register = [
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
	        'times(1)Number' : NumberNatives.times,
	        '<(1)Number' : NumberNatives.lessThen,
	        '>(1)Number' : NumberNatives.greaterThen,
	        '>=(1)Number' : NumberNatives.greaterOrEqualThen,
	        '<=(1)Number' : NumberNatives.lessOrEqualThen,

	        'length(0)String' : StringNatives.length,
	        '==(1)String' : StringNatives.equals,
	        '+(1)String' : StringNatives.plus,
	        'ifTrue(1)String' : StringNatives.ifTrue,
	        'ifFalse(1)String' : StringNatives.ifFalse,
	        'toS(0)String' : StringNatives.toS,
	        'toNumber(0)String' : StringNatives.toNumber,

	        '==(1)Bool' : BoolNatives.equals,
	        'ifTrue(1)Bool' : BoolNatives.ifTrue,
	        'ifFalse(1)Bool' : BoolNatives.ifFalse,
	        'and(1)Bool' : BoolNatives.and,
	        'or(1)Bool' : BoolNatives.or,
	        'not(0)Bool' : BoolNatives.not,
	        'toS(0)Bool' : BoolNatives.toS,

	        'writeToFile(2)SystemIO' : SystemIONatives.writeToFile,     // firsta srg = fileName, second arg = value
	        'readFromFile(1)SystemIO' : SystemIONatives.readFromFile,
	        'out(1)SystemIO' : SystemIONatives.out,
	        'inString(0)SystemIO' : SystemIONatives.inputString,
	        'inNumber(0)SystemIO' : SystemIONatives.inputNumber,
	        'inBool(0)SystemIO' : SystemIONatives.inputBool,

	        'call(0)Closure' : ClosureNatives.call,

	        '==(1)Object' : ObjectNatives.equals,
	        'toS(0)Object' : ObjectNatives.toS,
	        'isNull(0)Object' : ObjectNatives.isNull,

	        'isNull(0)Null' : NullNatives.isNull,
	        '==(1)Null' : NullNatives.equals,
	        'ifTrue(1)Null' : NullNatives.ifTrue,
	        'ifFalse(1)Null' : NullNatives.ifFalse,
	        'toS(0)Null' : NullNatives.toS,

	        'ifTrue(1)Array': ArrayNatives.ifTrue,
	        'ifFalse(1)Array': ArrayNatives.ifFalse,
	        'toS(0)Array': ArrayNatives.toS,
	        'each(1)Array': ArrayNatives.each,
	        'collect(1)Array': ArrayNatives.collect,
	        'add(1)Array': ArrayNatives.add1,
	        'add(2)Array': ArrayNatives.add2,               // first arg = position, secodn arg = value
	        'get(1)Array': ArrayNatives.get,
	        'push(1)Array': ArrayNatives.push,
	        'pop(0)Array': ArrayNatives.pop,
	        'top(0)Array': ArrayNatives.top,
	        'size(0)Array': ArrayNatives.size,
	        'contains(1)Array': ArrayNatives.contains,
	        'select(1)Array': ArrayNatives.select
	]
}
