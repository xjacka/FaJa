package faJa

enum Instruction {
	INVOKE(0,2), // 2 bytes pointer to constanPool
	LOAD(1,1), // index of local property ( from local variable to stack)
	STORE(2,1), // index of local property (from stack to local variable)
	GETFIELD(3,2), // 2 bytes pointer to constanPool ( replace object reference on stack with field value )
	PUTFIELD(4,2) // 2 bytes pointer to constanPool ( take first value from stack and set this value to the object referenced by second value on stack )

	Instruction(int id, int params) {
		this.id = id
		this.params = params
	}

	private final int params
	private final int id

	public int getParams() { return params }
	public int getId() { return id }
}