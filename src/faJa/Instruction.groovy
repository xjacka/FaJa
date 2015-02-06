package faJa

enum Instruction {
	INVOKE(0,2), // 2 bytes pointer to constanPool
	LOAD(1,1), // index of local property ( from local variable to stack)
	STORE(2,1), // index of local property (from stack to local variable)
	GETFIELD(3,2), // 2 bytes pointer to constanPool ( replace object reference on stack with field value )
	PUTFIELD(4,2), // 2 bytes pointer to constanPool ( take first value from stack and set this value to the object referenced by second value on stack )
	INIT(5,2),        // init object
	INIT_NUM(6,2),      // create number object without constructor
	INIT_STRING(7, 2),  // create string object without constructor
	INIT_BOOL(8, 2),    // create bool object without constructor
	PUSH_NULL(9, 0),   // push null object to stack
	INIT_CLOSURE(10, 1) // create closure

	Instruction(int id, int params) {
		this.id = id
		this.params = params
	}

	private final int params
	private final int id

	public int getParams() { return params }
	public int getId() { return id }

}