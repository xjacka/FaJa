import faJa.FaJaExecutor
import faJa.compilator.evaluation.Expression
import faJa.exceptions.CompilerException
import faJa.exceptions.InputException
import faJa.exceptions.InstructionException
import faJa.exceptions.InterpretException

final Boolean DEBUG = true
Exception e = null
try {
	new FaJaExecutor().run(args.length != 0 ? args[0] : '../fajaSrc/Main')
}
catch (CompilerException cpE){
	println "compile ERROR: " + cpE.getMessage()
	e = cpE
}
catch (InputException inE){
	println "input ERROR: " + inE.getMessage()
	e = inE
}
catch (InstructionException isE){
	println "instruction ERROR: " + isE.getMessage()
	e = isE
}
catch (InterpretException itE){
	println "interpret ERROR: " + itE.getMessage()
	e = itE
}
catch (Exception itE){
	println "exception ERROR: " + itE.getMessage()
	e = itE
}
if(e){
	if(DEBUG)
		e.printStackTrace()
	System.exit(1)
}

System.exit(0)