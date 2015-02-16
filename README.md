FaJa compiler and virtual machine

This application contains FaJa compiler and bytecode interpreter. Application is written in Groovy language and is
compiled into Java jar file. This archive contains three folders:
  /src - contains Groovy source codes of the program.
  /fajaSrc - contains source codes of program (SAT solver) in FaJa language
  /out - compiled groovy/java class files

In root folder, there is also runnable jar file FaJa.jar and documentation.html with description od FaJa language and native Classes.

RUN

to run compiler and virtual machine type:
  java -jar FaJa.jar fajaSrc/Main command_line_arguments
  -> this will compile file Main.faja in folder fajaSrc and interpret them.
to only compile file fajaSrc/Main into bytecode and print then to stdOut type:
  java -jar FaJa.jar fajaSrc/Main -c
  
It is also possible to run program as groovy script (groovy must be installed in PATH). 
  cd src
  groovy Main.groovy ../fajaSrc/Main command_line_arguments
