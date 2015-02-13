package faJa.interpreter

/**
 *                          BYTECODE INSTRUCTION LIST
 * +----+-------------+-------------------------------+-------------------------------------+
 * | id | name        |        other bytes            |              stack                  |
 * |    |             |                               |       [before] -> [after]           |
 * +----+-------------+-------------------------------+-------------------------------------+
 * | 0  | INVOKE      |   constantPoolPointer     (2) | arg1,...,argN   -> retVal           |
 * | 1  | LOAD        |   localPropertyIndex      (1) |                 -> newVal           |
 * | 2  | STORE       |   localPropertyIndex      (1) | val             ->                  |
 * | 3  | GETFIELD    |   constantPoolPointer     (2) | object          -> field            |
 * | 4  | PUTFIELD    |   constantPoolPointer     (2) | val, object   ->                  |
 * | 5  | INIT        |   constantPoolPointer     (2) |                 -> newObject        |
 * | 6  | INIT_NUM    |   constantPoolPointer     (2) |                 -> newNumberObject  |
 * | 7  | INIT_STRING |   constantPoolPointer     (2) |                 -> newStringObject  |
 * | 8  | INIT_BOOL   |   constantPoolPointer     (2) |                 -> newBoolObject    |
 * | 9  | PUSH_NULL   |                           (0) |                 -> nullObj          |
 * | 10 | INIT_CLOSURE|   indexOfClosureInClass   (1) | initObject      -> newClosureObject |
 * | 11 | INIT_ARRAY  |                           (0) |                 -> newArrayObject   |
 * +----+-------------+-------------------------------+-------------------------------------+
 */
enum Instruction {
	INVOKE(0,2),        // 2 bytes pointer to constanPool
	LOAD(1,1),          // index of local property ( from local variable to stack)
	STORE(2,1),         // index of local property (from stack to local variable)
	GETFIELD(3,2),      // 2 bytes pointer to constanPool ( replace object reference on stack with field value )
	PUTFIELD(4,2),      // 2 bytes pointer to constanPool ( take first value from stack and set this value to the object referenced by second value on stack )
	INIT(5,2),          // init object
	INIT_NUM(6,2),      // create number object without constructor
	INIT_STRING(7, 2),  // create string object without constructor
	INIT_BOOL(8, 2),    // create bool object without constructor
	PUSH_NULL(9, 0),    // push null object to stack
	INIT_CLOSURE(10, 1),// create closure
	INIT_ARRAY(11, 0)   // create array with default length

	Instruction(int id, int params) {
		this.id = id
		this.params = params
	}

	private final int params
	private final int id

	public int getParams() { return params }
	public int getId() { return id }

}