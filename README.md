FaJa
======================================

				////////////////////////////////////////////////////////////////
				//  	     FaJa compiler and virtual machine                //
				////////////////////////////////////////////////////////////////

Name:-      FaJa VM  
Authors:-   Kamil Falta (faltakam)  
			      Lukáš Janeček (janeclu1)  
Date:-      20.2.2015  


(1) INTRODUCTION
----------------------
This application contains FaJa compiler and bytecode interpreter. Application is written
in Groovy language and is compiled into Java jar file.


(2) PACKAGE FILES
----------------------
The following files are included in the FaJa package.  
 |  
 +- Documentation/..............Contains documentation in HTML format (syntax, native classes, example)  
 |  
 +- src/........................Contains Groovy source codes of the program  
 |  |  
 |  +- src/.....................Contains source files  
 |  |  
 |  +- Main.groovy..............Executable file  
 |  |  
 |  +- Test.groovy..............Execute tests  
 |  
 +- FaJaSrc/SAT/................Contains source codes of program (SAT solver) in FaJa language  
 |  
 +- out/........................Compiled groovy/java class file  
    |  
    +- FaJa.jar.................Compiled program into Java JAR file  


(3) REQUIRED SYSTEMS
----------------------
- java


(4) USAGE
----------------------
Syntax:

	$ java -jar FaJa.jar ClassName [-c] [argument]...

(a) To run compiler, virtual machine and program type:

	$ java -jar out/FaJa.jar FaJaSrc/Main command_line_arguments

  -> this will compile file Main.faja in folder FaJaSrc and interpret them.

(b) To only compile file FaJaSrc/Main.faja into bytecode and print then to stdOut type:

	$ java -jar out/FaJa.jar FaJaSrc/Main -c
  
(c) It is also possible to run program as groovy script (groovy must be installed in PATH).

	$ cd src
	$ groovy Main.groovy ../FaJaSrc/Main command_line_arguments


(5) REQUIREMENTS
----------------------
All source FaJa files must be in the same folder and name must match class name.


(6) EXAMPLE
----------------------
To run example of FaJa program (weighted MAX-SAT) with prepared data set, type:

	$ java -jar out/FaJa.jar FaJaSrc/Main FaJaSrc/data/sat1.out


(7) FEATURES
----------------------
- Parser and Compiler
- Virtual machine
- Closures
- Garbage collector
- Threads
- Read & write from/to file
