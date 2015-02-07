package faJa.tests

import faJa.ClassFile
import faJa.compilator.Compiler

class CompilerTest {

	def compile(){
		Compiler compilator = new Compiler()
		ClassFile  classFile = compilator.compile('/home/xjacka/Dokumenty/Skola/treti_semestr_MI/MI-RUN/FaJa/fajaSrc/Main.faja')
		ClassFile  classFile2 = compilator.compile('/home/xjacka/Dokumenty/Skola/treti_semestr_MI/MI-RUN/FaJa/fajaSrc/Main2.faja')

		println(classFile.toString())
		println(classFile2.toString())
	}

}
