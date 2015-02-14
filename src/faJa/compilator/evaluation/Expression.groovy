package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

public interface Expression {
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals)
}