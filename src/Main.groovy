import faJa.FaJaExecutor
import faJa.exceptions.CompilerException
import faJa.exceptions.InputException
import faJa.exceptions.InstructionException
import faJa.exceptions.InterpretException

final Boolean DEBUG = true

try {
	new FaJaExecutor().run(args.length != 0 ? args[0] : '../fajaSrc/Main')
}
catch (CompilerException cpE){
	println "compile ERROR: " + cpE.getMessage()
	if(DEBUG)
		cpE.printStackTrace()
}
catch (InputException inE){
	println "input ERROR: " + inE.getMessage()
	if(DEBUG)
		inE.printStackTrace()
}
catch (InstructionException isE){
	println "instruction ERROR: " + isE.getMessage()
	if(DEBUG)
		isE.printStackTrace()
}
catch (InterpretException itE){
	println "interpret ERROR: " + itE.getMessage()
	if(DEBUG)
		itE.printStackTrace()
}
finally {
	System.exit(1)
}
System.exit(0)