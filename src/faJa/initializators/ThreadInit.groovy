package faJa.initializators

import faJa.compilator.Compiler
import faJa.compilator.representation.ClassFile
import faJa.compilator.representation.ConstantPool
import faJa.compilator.representation.PrecompiledMethod

/**
 *         THREAD OBJECT
 * +-----------------------+
 * | 4 |  threadClassPtr   |
 * +-----------------------+
 *
 */
class ThreadInit extends BaseInit {

	ThreadInit(){
		classFile = new ClassFile()
		classFile.constantPool.add(Compiler.THREAD_CLASS)
		classFile.constantPool.add('Object')

		classFile.fields = []

//		Method run(1) - Native
		def run = new PrecompiledMethod()
		run.signatureIndex = classFile.constantPool.add('run(1)')
		classFile.methods.add(run)

//		Method wait(0) - Native
		def wait = new PrecompiledMethod()
		wait.signatureIndex = classFile.constantPool.add('wait(0)')
		classFile.methods.add(wait)
	}
}

