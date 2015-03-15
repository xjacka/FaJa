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
 |  
 +- FaJaSrc/....................Contains FaJa source code  
   |  
   +- SAT/......................FaJa program (SAT solver)  
      |  
      +- data/..................prepared data set  


(3) REQUIRED SYSTEMS
----------------------
- groovy


(4) USAGE
----------------------
Syntax:

	$ groovy Main.groovy ClassName [-c] [argument]...

(a) Run program as groovy script (groovy must be installed in PATH).

	$ cd src
	$ groovy Main.groovy ../FaJaSrc/Main command_line_arguments

(b) It is possible to run program from java jar file. Download jar file from [Google drive](https://drive.google.com/file/d/0B7t47lnMahV2dUF3RWpMN25SRTQ/view?usp=sharing) and then type:

	$ java -jar FaJa.jar FaJaSrc/SAT/Main FaJaSrc/SAT/data/sat1.out

(5) REQUIREMENTS
----------------------
All source FaJa files must be in the same folder and name must match class name.


(6) EXAMPLE
----------------------
To run example of FaJa program (weighted MAX-SAT) with prepared data set, type:

  $ cd src
  $ groovy Main.groovy ../FaJaSrc/SAT/Main ../FaJaSrc/SAT/data/sat1.out

(7) FEATURES
----------------------
- Parser and Compiler
- Virtual machine
	- methods invoke cache
- Closures
- Garbage collector
- Threads
- Read & write from/to file
