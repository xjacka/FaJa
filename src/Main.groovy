import faJa.FaJaExecutor
import faJa.compilator.Compiler
import faJa.compilator.representation.ClassFile
import faJa.exceptions.CompilerException
import faJa.exceptions.GarbageCollectorException
import faJa.exceptions.InputException
import faJa.exceptions.InstructionException
import faJa.exceptions.InterpretException
import faJa.interpreter.ClassLoader

final Boolean DEBUG = true
Exception e = null
try {
	if (args.length == 2 && args[1] == "-c") {
		ClassFile classFile = new Compiler().compile(args[0] + ClassLoader.FAJA_EXTENSION)
		print(classFile.toString())
	} else{
		new FaJaExecutor().run(args.length != 0 ? args[0] : '../FaJaSrc/Main',args.length >= 2 ?args.tail() : (String [])["../FaJaSrc/data/sat1.out"])
	}
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
catch (GarbageCollectorException gcE){
	println "interpret ERROR: " + gcE.getMessage()
	e = gcE
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