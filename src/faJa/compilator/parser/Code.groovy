package faJa.compilator.parser

import faJa.exceptions.CompilerException

/**
 * Created by Kamil on 12. 2. 2015.
 */
class Code {
	List<String> code
	Integer codePtr = 0
	public Code(List<String> code){
		this.code = code
	}
	boolean hasNextLine(){
		codePtr < code.size()
	}
	String insert(String line){
		code.add(codePtr, line)
	}
	String nextLine(){
		if(!hasNextLine()){
			throw CompilerException('code pointer out of bounds')
		}
		String result = code[codePtr++]
		result
	}
	Integer currentLineIdx(){
		if(codePtr == 0){
			throw CompilerException('code line not loaded (codePtr == 0)')
		}
		codePtr-1
	}
	String currentLine() {
		if(codePtr == 0){
			throw CompilerException('code line not loaded (codePtr == 0)')
		}
		code[codePtr-1]
	}
}
