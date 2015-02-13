package faJa.compilator.evaluation

import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.PrecompiledInstruction
import faJa.compilator.LocalVariables

/**
 * Created by Kamil on 11. 2. 2015.
 */
public interface Expression {
	List<PrecompiledInstruction> eval(ClassFile classFile, LocalVariables locals)
}