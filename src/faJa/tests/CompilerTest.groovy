package faJa.tests

import faJa.ClassFile
import faJa.compilator.Compilator

class CompilerTest {

	def compile(){
		Compilator compilator = new Compilator()
		ClassFile  classFile = compilator.compile('/home/xjacka/Dokumenty/Skola/treti_semestr_MI/MI-RUN/FaJa/fajaSrc/Main.faja')
		ClassFile  classFile2 = compilator.compile('/home/xjacka/Dokumenty/Skola/treti_semestr_MI/MI-RUN/FaJa/fajaSrc/Main2.faja')

		println(classFile.toString())
		println(classFile2.toString())
	}

}
