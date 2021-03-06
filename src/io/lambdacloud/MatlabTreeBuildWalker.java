package io.lambdacloud;

import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.lambdacloud.matlab.MatlabGrammarBaseListener;
import io.lambdacloud.matlab.MatlabGrammarParser;
import io.lambdacloud.matlab.MatlabGrammarParser.Case_expr_and_bodyContext;
import io.lambdacloud.matlab.MatlabGrammarParser.Expr_endContext;
import io.lambdacloud.matlab.MatlabGrammarParser.StatementContext;
import io.lambdacloud.node.AssignNode;
import io.lambdacloud.node.ConstantNode;
import io.lambdacloud.node.ExprNode;
import io.lambdacloud.node.ForNode;
import io.lambdacloud.node.FuncCallNode;
import io.lambdacloud.node.FuncDefNode;
import io.lambdacloud.node.IfNode;
import io.lambdacloud.node.NArgInNode;
import io.lambdacloud.node.NArgOutNode;
import io.lambdacloud.node.RangeNode;
import io.lambdacloud.node.ReturnListNode;
import io.lambdacloud.node.ReturnNode;
import io.lambdacloud.node.StatementNode;
import io.lambdacloud.node.SwitchNode;
import io.lambdacloud.node.Tools;
import io.lambdacloud.node.VariableNode;
import io.lambdacloud.node.WhileNode;
import io.lambdacloud.node.arithmetric.AddAsignNode;
import io.lambdacloud.node.arithmetric.AddNode;
import io.lambdacloud.node.arithmetric.DivAsignNode;
import io.lambdacloud.node.arithmetric.DivNode;
import io.lambdacloud.node.arithmetric.IncNode;
import io.lambdacloud.node.arithmetric.MulAsignNode;
import io.lambdacloud.node.arithmetric.MultNode;
import io.lambdacloud.node.arithmetric.NegateNode;
import io.lambdacloud.node.arithmetric.RemAsignNode;
import io.lambdacloud.node.arithmetric.SubAsignNode;
import io.lambdacloud.node.arithmetric.SubNode;
import io.lambdacloud.node.binary.BAndNode;
import io.lambdacloud.node.binary.BOrNode;
import io.lambdacloud.node.binary.BXorNode;
import io.lambdacloud.node.comparion.EQNode;
import io.lambdacloud.node.comparion.GENode;
import io.lambdacloud.node.comparion.GTNode;
import io.lambdacloud.node.comparion.LENode;
import io.lambdacloud.node.comparion.LTNode;
import io.lambdacloud.node.comparion.NEQNode;
import io.lambdacloud.node.logical.AndNode;
import io.lambdacloud.node.logical.NotNode;
import io.lambdacloud.node.logical.OrNode;
import io.lambdacloud.node.matrix.CellAccessNode;
import io.lambdacloud.node.matrix.CellAssignNode;
import io.lambdacloud.node.matrix.CellInitNode;
import io.lambdacloud.node.matrix.EndIndexNode;
import io.lambdacloud.node.matrix.MatrixAccessNode;
import io.lambdacloud.node.matrix.MatrixAssignNode;
import io.lambdacloud.node.matrix.MatrixDLDivNode;
import io.lambdacloud.node.matrix.MatrixDMulNode;
import io.lambdacloud.node.matrix.MatrixDRDivNode;
import io.lambdacloud.node.matrix.MatrixInitNode;
import io.lambdacloud.node.matrix.SolveNode;
import io.lambdacloud.node.matrix.StructAccessNode;
import io.lambdacloud.node.matrix.StructAssignNode;
import io.lambdacloud.node.matrix.StructInitNode;
import io.lambdacloud.node.matrix.TransposeNode;
import io.lambdacloud.node.string.StringNode;
import io.lambdacloud.node.tool.ArrayAccess;
import io.lambdacloud.node.tool.ArrayLength;
import io.lambdacloud.util.ObjectArray;
import io.lambdacloud.util.Struct;

public class MatlabTreeBuildWalker extends MatlabGrammarBaseListener {
	public static boolean DEBUG = false;
//	public Deque<ExprNode> stack = new LinkedList<ExprNode>();
//	
//	//Variable map which is generated after parsing
//	//Another place is the phase of code generation
//	public SortedMap<String, VariableNode> varMap = new TreeMap<String, VariableNode>();
	
	public Deque<Scope> scope = new LinkedList<Scope>();

	//Parameter types which should be passed in before parsing
	protected Class<?> defaultParameterTypeOrInterface = null;
	protected Map<String, Class<?>> mapParameterTypes = null;

	//public static HashMap<String, FuncDefNode> funcMap = new HashMap<String, FuncDefNode>();
	public MatlabTreeBuildWalker() {
		scope.push(new Scope("global"));
	}
	
	public MatlabTreeBuildWalker(Class<?> defaultParameterTypeOrInterface) {
		this.defaultParameterTypeOrInterface = defaultParameterTypeOrInterface;
		scope.push(new Scope("global"));
	}
	
	public MatlabTreeBuildWalker(Map<String, Class<?>> mapParameterTypes) {
		this.mapParameterTypes = mapParameterTypes;
		scope.push(new Scope("global"));
	}
	
	public Scope currentScope() {
		return this.scope.peek();
	}
	
	public void addScope(String scopeName) {
		this.scope.push(new Scope(scopeName));
	}
	
	public Scope popScope() {
		return this.scope.pop();
	}
	
//	public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws Exception {
//		
//		FuncDefNode fnode = ExprTreeBuildWalker.funcMap.get(name);
//		fnode.setParamTypes(type.parameterArray());
//		String tt = type.toMethodDescriptorString();
//		//if(DEBUG)
//			System.out.println("bootstrap11: "+fnode.getFuncClassName()+"."+name+":"+tt);
//		
//		//tt = tt.replaceAll("\\(|\\)", "_");
//		Class<?> cls = fnode.genFuncCode(true);
//		
//		MethodHandle mh = MethodHandles.lookup().findStatic(cls, name,
//		MethodType.methodType(type.returnType(),type.parameterArray()));
//
//		return new ConstantCallSite(mh);
//	}
	
	public void printInfo() {
		System.out.println("Parameters:");
		for(VariableNode n : currentScope().varMap.values()) {
			if(n.isParameter()) System.out.println(n.toString());
		}
		System.out.println("Local Variables:");
		for(VariableNode n : currentScope().varMap.values()) {
			if(n.isLocalVar()) System.out.println(n.toString());
		}
	}
	
	public Type[] getAndFixParameterTypes(Map<String, Class<?>> mapParameterTypes) {
		List<VariableNode> pList = new ArrayList<VariableNode>();
		for(Entry<String, VariableNode> e : this.currentScope().varMap.entrySet()) {
			if(e.getValue().isParameter())
				pList.add(e.getValue());
		}
		//Sort argument names
		Collections.sort(pList, new Comparator<VariableNode>() {
			@Override
			public int compare(VariableNode o1, VariableNode o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		Type[] ret = new Type[pList.size()];
		int i = 0;
		for(VariableNode node : pList) {
			
			//Fix parameter types according the passed in types
			if(null == mapParameterTypes)
				throw new RuntimeException("Need parameter for variable: "+node.getName());
			Class<?> cls = mapParameterTypes.get(node.getName());
			if(null == cls)
				throw new RuntimeException("Need parameter for variable: "+node.getName());
			node.setType(Type.getType(mapParameterTypes.get(node.getName())));
			
			ret[i] = Type.getType(mapParameterTypes.get(node.getName()));
			
			i++;
		}
		return ret;
	}
	
	public Type[] getAndFixParameterTypes(Class<?>[] aryParameterTypes) {
		List<VariableNode> pList = new ArrayList<VariableNode>();
		for(Entry<String, VariableNode> e : this.currentScope().varMap.entrySet()) {
			if(e.getValue().isParameter())
				pList.add(e.getValue());
		}
		Collections.sort(pList, new Comparator<VariableNode>() {
			@Override
			public int compare(VariableNode o1, VariableNode o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		Type[] ret = new Type[pList.size()];
		this.mapParameterTypes = new HashMap<String, Class<?>>();
		int i = 0;
		for(VariableNode node : pList) {
			
			//Fix parameter types according the passed in types
			node.setType(Type.getType(aryParameterTypes[i]));
			
			ret[i] = Type.getType(aryParameterTypes[i]);
			
			this.mapParameterTypes.put(node.getName(), aryParameterTypes[i]);
			
			i++;
		}
		return ret;
	}
	
	public Type[] getAndFixParameterTypes(Class<?> defaultParameterType) {
		List<VariableNode> pList = new ArrayList<VariableNode>();
		for(Entry<String, VariableNode> e : this.currentScope().varMap.entrySet()) {
			if(e.getValue().isParameter())
				pList.add(e.getValue());
		}
		Collections.sort(pList, new Comparator<VariableNode>() {
			@Override
			public int compare(VariableNode o1, VariableNode o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		Type[] ret = new Type[pList.size()];
		int i = 0;
		for(VariableNode node : pList) {
			
			//Fix parameter types according the passed in types
			node.setType(Type.getType(defaultParameterType));
			
			ret[i] = Type.getType(defaultParameterType);
			
			i++;
		}
		return ret;
	}

	public Class<?>[] getParameterClassTypes() {
		if(null != this.defaultParameterTypeOrInterface && this.defaultParameterTypeOrInterface.isInterface()) {
			return this.defaultParameterTypeOrInterface.getMethods()[0].getParameterTypes();
		}
		
		List<VariableNode> pList = new ArrayList<VariableNode>();
		for(Entry<String, VariableNode> e : this.currentScope().varMap.entrySet()) {
			if(e.getValue().isParameter())
				pList.add(e.getValue());
		}
		Collections.sort(pList, new Comparator<VariableNode>() {
			@Override
			public int compare(VariableNode o1, VariableNode o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		Class<?>[] ret = new Class<?>[pList.size()];
		if(null != this.defaultParameterTypeOrInterface) {
			for(int i=0; i<pList.size(); i++) {
				ret[i] = this.defaultParameterTypeOrInterface;
			}
		} else {
			int i = 0;
			for(VariableNode node : pList) {
				ret[i] = mapParameterTypes.get(node.getName());
				
				i++;
			}
		}
		return ret;
	}
	
	/**
	 * Use mapParameterTypes to generate class
	 * @param className
	 * @param interfaces
	 * @param writeFile
	 * @param methodName
	 * @param isStatic
	 * @return
	 */
	public Class<?> genClass(String className, boolean writeFile, 
			String methodName, boolean isStatic) {
		return genClass(className, writeFile, methodName,  isStatic, null);
	}
	
	/**
	 * Use defaultParameterTypesOrInterface to generate class
	 * @param className
	 * @param writeFile
	 * @param methodName
	 * @param isStatic
	 * @param aryParameterTypes
	 * @return
	 */
	public Class<?> genClass(String className, boolean writeFile, 
			String methodName, boolean isStatic, Class<?>[] aryParameterTypes) {
		try {

			if(currentScope().stack.isEmpty()) 
				return null;

			ExprClassLoader mcl = new ExprClassLoader(CodeGenerator.class.getClassLoader());
			CodeGenerator cgen = new CodeGenerator();

			//Define class
			if(null != this.defaultParameterTypeOrInterface && this.defaultParameterTypeOrInterface.isInterface()) {
				cgen.startClass(className, new String[]{Type.getInternalName(this.defaultParameterTypeOrInterface)});
			} else {
				cgen.startClass(className, null);
			}

			//
			Map<String, VariableNode> sortedVarMap = new TreeMap<String, VariableNode>();
			sortedVarMap.putAll(currentScope().varMap);
			MethodGenHelper mg = new MethodGenHelper(sortedVarMap);

			StatementNode stmt = new StatementNode();
			for(ExprNode e : this.currentScope().stack) {
				stmt.exprs.add(e);
				e.setParent(stmt);
			}
			

			//Define method
			Type retType = null;
			Type[] paramTypes = null;
			int access =  Opcodes.ACC_PUBLIC;
			if(isStatic) access |= Opcodes.ACC_STATIC;
			Deque<Object> stack = new LinkedList<Object>();

			//There are two ways to specify parameter types of the generated method
			if(null == aryParameterTypes) {
				if(null != this.defaultParameterTypeOrInterface) {
					if(this.defaultParameterTypeOrInterface.isInterface()) {
						Method m = this.defaultParameterTypeOrInterface.getDeclaredMethods()[0];
						paramTypes = getAndFixParameterTypes(m.getParameterTypes());
						//updateType before updateType()
						stmt.updateType(stack);
						//Update tree before code generation
						stmt.updateTree(mg);
						//Check m.getReturnType() == retType ? 
						retType = currentScope().stack.peek().getType(); //return type of the last expression
						if(null == retType) {
							//retType = Type.VOID_TYPE; 
							throw new RuntimeException("Return type (top element of stack) is null!");
						}
						cgen.startMethod(access, m.getName(),
								Type.getMethodDescriptor(retType, paramTypes));
					} else {
						int nParam = 0;
						for(Entry<String, VariableNode> e : this.currentScope().varMap.entrySet()) {
							if(e.getValue().isParameter()) nParam++;
						}
						Class<?>[] pTypes = new Class<?>[nParam];
						for(int i=0; i<pTypes.length; i++) pTypes[i] = this.defaultParameterTypeOrInterface; 
						paramTypes = getAndFixParameterTypes(pTypes);
						//updateType before updateType()
						stmt.updateType(stack);
						//Update tree before code generation
						stmt.updateTree(mg);
						retType = currentScope().stack.peek().getType(); //return type of the last expression
						if(null == retType) {
							//retType = Type.VOID_TYPE; 
							throw new RuntimeException("Return type (top element of stack) is null!");
						}
						cgen.startMethod(access, methodName,
								Type.getMethodDescriptor(retType, paramTypes));
					}
				} else {
					paramTypes = getAndFixParameterTypes(this.mapParameterTypes);
					//updateType before updateType()
					stmt.updateType(stack);
					//Update tree before code generation
					stmt.updateTree(mg);
					retType = currentScope().stack.peek().getType(); //return type of the last expression
					if(null == retType) {
						//retType = Type.VOID_TYPE; 
						throw new RuntimeException("Return type (top element of stack) is null!");
					}
					cgen.startMethod(access, methodName,
							Type.getMethodDescriptor(retType, paramTypes));
				}
			} else {
				paramTypes = getAndFixParameterTypes(aryParameterTypes);
				//updateType before updateType()
				stmt.updateType(stack);
				//Update tree before code generation
				stmt.updateTree(mg);
				retType = currentScope().stack.peek().getType(); //return type of the last expression
				if(null == retType) {
					//retType = Type.VOID_TYPE; 
					throw new RuntimeException("Return type (top element of stack) is null!");
				}
				cgen.startMethod(access, methodName,
						Type.getMethodDescriptor(retType, paramTypes));
			}

			cgen.startCode();
			
			MethodVisitor mv = cgen.getMV();
			//TODO: bugfix - sort argument names for non-function script (???)
			//use TreeMap to sort variables in varMap
//			Map<String, VariableNode> sortedVarMap = new TreeMap<String, VariableNode>();
//			sortedVarMap.putAll(currentScope().varMap);
//			MethodGenHelper mg = new MethodGenHelper(mv, sortedVarMap);
			mg.setMethodVisitor(mv);
			
//			//Construct a StatementNode to contain all the elements in the stack
//			//This StatementNode is the root of expressions. So every expression 
//			//node has a parent with type ExprNode
//			StatementNode stmt = new StatementNode();
//			for(ExprNode e : this.currentScope().stack) {
//				stmt.exprs.add(e);
//				e.setParent(stmt);
//			}
//			
//			//Update tree before code generation
//			stmt.updateTree(mg);
			
			//Initiate call to updateParam()
			stmt.updateParam(null, null);

			//Initialize local variable table (LVT)
			mg.initLVTIndex(isStatic);

			//Generate code
			stmt.genCode(mg);
			
			//Generate return code (return void is supported)
			mg.visitInsn(retType.getOpcode(Opcodes.IRETURN));
			
			//Generate 'this' in LVT
			if(!isStatic)
				mg.visitLocalVariable("this", "L"+className+";", 
						null, cgen.labelStart, cgen.lableEnd, 0);
			
			//Sort variables according the index in LVT
			List<VariableNode> nodeList = new ArrayList<VariableNode>();
			nodeList.addAll(sortedVarMap.values());
			Collections.sort(nodeList, new Comparator<VariableNode>() {
				@Override
				public int compare(VariableNode o1, VariableNode o2) {
					return o1.getLVTIndex() - o2.getLVTIndex();
				}
			});
			//Generate LVT
			for(VariableNode var : nodeList) {
				ArrayList<String> varTypes = var.getVarTypes();
				for(String typeDesc : varTypes) {
					mg.visitLocalVariable(var.getName(typeDesc), typeDesc,
							null, cgen.labelStart, cgen.lableEnd, var.getLVTIndex(typeDesc));
				}
			}
			
			mg.visitMaxs(-1, -1); //Auto generated
			cgen.endCode();
			cgen.endClass();
			
			byte[] bcode = cgen.dump();
			if(writeFile) {
				FileOutputStream fos = new FileOutputStream(className+".class");
				fos.write(bcode);
				fos.close();
			}

			Class<?> c = mcl.defineClassForName(null, bcode);
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override public void exitEntityVariable(MatlabGrammarParser.EntityVariableContext ctx) {
		//System.out.println("exitEntityVariable: "+ctx.getText());
		
		String varName = ctx.IDENTIFIER(0).getText();
		//If the variable exists in the varMap of current scope
		//the variable is used directly instead of creating a new one
		VariableNode var = this.currentScope().varMap.get(varName);
		boolean isNewVariable = false;
		boolean isAssign = false;
		
		//We see varName for the first time
		if(null == var) {
			isNewVariable = true;
			
			//Don't change currentScope().varMap in other functions
			//It is suggested to change currentScope().varMap here only.
			
			var = VariableNode.newParameter(varName, null);
			//If varName is in the parameter map, this must be a parameter and the type is known. 
			//Put it in varMap.
			if(null != this.mapParameterTypes) {
				Class<?> varCls = this.mapParameterTypes.get(varName);
				if(null != varCls) {
					var.setType(Type.getType(varCls));
					this.currentScope().varMap.put(varName, var);
				}
			}
			
			//For ExprAssign, varName must be a new local variable
			//Add varName to varMap to generate local variable table
			//System.out.println(ctx.getParent().getClass().getName());
			if(ctx.getParent() instanceof MatlabGrammarParser.ExprAssignContext ||
					ctx.getParent() instanceof MatlabGrammarParser.ExprMultiAssignContext) {
				isAssign = true;
				if(var.getType() == null) {
					var.setAsLocalVar();
				}
				this.currentScope().varMap.put(varName, var);
			}
			
			if(ctx.getParent() instanceof MatlabGrammarParser.ArrayAccessOrFuncCallContext) {
				//Do nothing if it is a ArrayAccessOrFuncCallContext. varName could be a function name
			} else {
				if(null!=this.defaultParameterTypeOrInterface){
					var.setType(Type.getType(this.defaultParameterTypeOrInterface));
				}
				this.currentScope().varMap.put(varName, var);
			}
			
			if(varName.equalsIgnoreCase("varargout"))
				var.setType(Type.getType(ObjectArray.class));

		}
		if(ctx.IDENTIFIER().size() <= 1) {
			this.currentScope().stack.push(var);
		} else {
			StructAccessNode san = new StructAccessNode();
			san.var = var;
			san.var.setType(Type.getType(Struct.class));
			for(int i=1; i<ctx.IDENTIFIER().size(); i++)
				san.fields.add(ctx.IDENTIFIER(i).getText());
			if(isNewVariable && isAssign)
				this.currentScope().stack.push(new AssignNode(san.var, new StructInitNode()));
			this.currentScope().stack.push(san);
		}
	}
	
	@Override public void exitProg(MatlabGrammarParser.ProgContext ctx) {
		ExprNode node = currentScope().stack.peek();
		if(null != node)
			node.genLoadInsn(true);
	}
	
	@Override public void exitEntityConstInteger(MatlabGrammarParser.EntityConstIntegerContext ctx) {
		//System.out.println("exitConstInteger"+ctx.getText());
		String s = ctx.getText();
		if(s.endsWith("L"))
			currentScope().stack.push(new ConstantNode(s.substring(0, s.length()-1), Type.LONG_TYPE));
		else
			currentScope().stack.push(new ConstantNode(ctx.getText(), Type.INT_TYPE));
	}
	@Override public void exitEntityConstFloat(MatlabGrammarParser.EntityConstFloatContext ctx) { 
		//System.out.println("exitEntityConstFloat:"+ctx.getText());
		currentScope().stack.push(new ConstantNode(ctx.getText(), Type.DOUBLE_TYPE));
	}
	
	@Override public void exitArray_init(MatlabGrammarParser.Array_initContext ctx) {
		//System.out.println("exitArray_init:"+ctx.getText());
		
		if(null == ctx.ai_list()) {
			//empty matrix
			throw new RuntimeException("empty matrix is not supported so far.");
		}
		//int cols = ctx.ai_list(0).expression().size();
		int rows = ctx.ai_list().size();
		MatrixInitNode node = new MatrixInitNode();
		for(int i=0; i<rows; i++)
			node.colLenList.add(ctx.ai_list(i).expression().size());
		for(int i=0; i<rows; i++) {
			for(int j=0; j<ctx.ai_list(i).expression().size(); j++) {
				node.addInitValues(currentScope().stack.pop());
			}
		}
		currentScope().stack.push(node);
	}
	
	@Override public void exitTranspose(MatlabGrammarParser.TransposeContext ctx) { 
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new TransposeNode(v1));
	}

//	@Override public void exitArithmeticExpressionDMul(MatlabGrammarParser.Arithmetic_exprContext.ArithmeticExpressionDMulContext ctx) { 
//		System.out.println(ctx.getText());
//	}
	
//	@Override public void exitArithmeticExpressionSolve(MatlabGrammarParser.ArithmeticExpressionSolveContext ctx) {
//		ExprNode v2 = currentScope().stack.pop();
//		ExprNode v1 = currentScope().stack.pop();
//		currentScope().stack.push(new SolveNode(v1, v2));
//	}
	
//	public void exitArrayAccessOrFuncCallTest(MatlabGrammarParser.ArrayAccessOrFuncCallContext ctx) { 
//		System.out.println("exitArrayAccessOrFuncCall: "+ctx.getText());
//		System.out.println(">>exitArrayAccessOrFuncCall: "+ctx.aa_index().size());
//		System.out.println(">>>exitArrayAccessOrFuncCall: "+ctx.IDENTIFIER());
//		System.out.println(">>>>exitArrayAccessOrFuncCall: "+ctx.variable_entity().getText());
//		
//		StringBuilder sb = new StringBuilder();
//		for(int i=0; i<ctx.IDENTIFIER().size(); i++) {
//			sb.append(".").append(ctx.IDENTIFIER(i).getText());
//		}
//		
//		List<ExprNode> indices = new ArrayList<ExprNode>();
//		for(int i=0; i<ctx.aa_index().size(); i++) {
//			indices.add(this.currentScope().stack.pop());
//		}
//		System.out.println(indices);
//		ExprNode n = this.currentScope().stack.pop();
//		System.out.println(n.toString()+sb.toString());
//		
//		this.currentScope().stack.push(n);
//	}
	
	@Override public void exitCellAccess(MatlabGrammarParser.CellAccessContext ctx) { 
		//System.out.println("exitCellAccess: "+ctx.getText());
		
		//Get the variable node in array access (3 steps)
		//1. Pop all the indices from the stack and keep them in a list
		List<ExprNode> indices = new ArrayList<ExprNode>();
		for(int i=0; i<ctx.aa_index().size(); i++) {
			if(null != ctx.aa_index(i).COLON())
				continue; //nothing to pop for COLON
			if(null != ctx.aa_index(i).aa_range()) {
				if(null != ctx.aa_index(i).aa_range().aa_range_end()) { //pop "end" in a range
					indices.add(this.currentScope().stack.pop());
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_step()) { //pop "step" in a range
					indices.add(this.currentScope().stack.pop());
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_start()) { //pop "start" in a range
					indices.add(this.currentScope().stack.pop());
				}
			} else {
				indices.add(this.currentScope().stack.pop()); //pop others: expression, function_handle, mimi_arith_expr
			}
		}
		//2. Pop the variable 
		ExprNode var = this.currentScope().stack.pop();
		//3. Push back the indices to the stack for further process
		for(int i=indices.size()-1; i>=0; i--) {
			this.currentScope().stack.push(indices.get(i));
		}
		
		//------Construct cell access node, e.g. A{1,2}------
		CellAccessNode node = new CellAccessNode(var);
		node.setToAccessObject();
		for(int i=ctx.aa_index().size()-1; i>=0; i--) {
			ExprNode idxS = null;
			ExprNode idxStep = null;
			ExprNode idxE = null;
			if(null != ctx.aa_index(i).COLON()) {
				//A{:} -- Access all the elements
				node.addIndex(null, null); //use null to indicate this case
			} else if(null != ctx.aa_index(i).aa_range()) {
				//A{1:10}, A{1:end}, A{end:-1:1}
				//A{1:5,1:10}, A{5:end, 2:2:end}, A{end:1,end:2:1}
				if(null != ctx.aa_index(i).aa_range().aa_range_end()) {
					idxE = this.currentScope().stack.pop();
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_step()) {
					idxStep = this.currentScope().stack.pop();
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_start()) {
					idxS = this.currentScope().stack.pop();
				}
				if(null == idxStep)
					node.addIndex(idxS, idxE);
				else
					node.addIndex(new RangeNode(idxS, idxStep, idxE, true), null);
			} else {
				//A{end-1} -- expression, function_handle, mimi_arith_expr
				idxS = this.currentScope().stack.pop();
				idxE = null;
				node.addIndex(idxS, idxE);
			}
		}

		this.currentScope().stack.push(node);
	}
	
	@Override public void exitArrayAccessOrFuncCall(MatlabGrammarParser.ArrayAccessOrFuncCallContext ctx) { 
		//System.out.println("exitArrayAccessOrFuncCall: "+ctx.getText());

		//Get the variable node in array access (3 steps)
		//1. Pop all the indices from the stack and keep them in a list
		List<ExprNode> indices = new ArrayList<ExprNode>();
		for(int i=0; i<ctx.aa_index().size(); i++) {
			if(null != ctx.aa_index(i).COLON())
				continue; //nothing to pop for COLON
			if(null != ctx.aa_index(i).aa_range()) {
				if(null != ctx.aa_index(i).aa_range().aa_range_end()) { //pop "end" in a range
					indices.add(this.currentScope().stack.pop());
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_step()) { //pop "step" in a range
					indices.add(this.currentScope().stack.pop());
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_start()) { //pop "start" in a range
					indices.add(this.currentScope().stack.pop());
				}
			} else {
				indices.add(this.currentScope().stack.pop()); //pop others: expression, function_handle, mimi_arith_expr
			}
		}
		//2. Pop the variable 
		ExprNode var = this.currentScope().stack.pop();
		//3. Push back the indices to the stack for further process
		for(int i=indices.size()-1; i>=0; i--) {
			this.currentScope().stack.push(indices.get(i));
		}
		
		//Check if this is a function call
		String varName = null;
		StringBuilder sbNames = new StringBuilder();
		String funcName = null;
		if(var instanceof VariableNode) {
			VariableNode varNode = (VariableNode)var;
			varName = varNode.getName();
		} else if(var instanceof StructAccessNode) {
			StructAccessNode san = (StructAccessNode)var;
			var = san.var;
			varName = san.var.getName();
			if(san.fields.size() > 0) {
				sbNames.append(".").append(Tools.join(san.fields.toArray(new String[0]), "."));
				funcName = san.fields.get(san.fields.size()-1);
			}
		} else if(var instanceof CellAccessNode) {
			//Do nothing
			//e.g. varargin{1}(:)
		} else {
			throw new RuntimeException("var = "+var);
		}
		
		//Check if the variable is from external parameter
		boolean isFuncCall = false;
		boolean needInitialize = false;
		if(null != varName) {
		if( null == this.mapParameterTypes ||
			null == this.mapParameterTypes.get(varName)) {
			//Check if varName is a user defined function name
			FuncDefNode func = ExprTreeBuildWalker.funcMap.get(varName);
			if(null != func) {
				isFuncCall = true;
			} else {
				//Check if varName is a function defined in BytecodeSupport
				Method[] ms = BytecodeSupport.class.getMethods();
				String checkName = varName;
				if(null != funcName)
					checkName = funcName;
				for(Method m : ms) {
					if(checkName.equalsIgnoreCase(m.getName())) {
						isFuncCall = true;
						break;
					}
				}
				ms = Math.class.getMethods();
				for(Method m : ms) {
					if(checkName.equalsIgnoreCase(m.getName())) {
						isFuncCall = true;
						break;
					}
				}
			}
			//default to true 
//			if(null == this.currentScope().varMap.get(varName)) {
//				isFuncCall = true;
//			}
			if(!isFuncCall) {
				if(null == this.currentScope().varMap.get(varName)) {
					//The variable is referenced without initialization
					//
					needInitialize = true;
					VariableNode localVar = (VariableNode)var;
					localVar.setAsLocalVar();
					this.currentScope().varMap.put(varName, localVar);
					isFuncCall = false;
				}
			}
		}
		} else {
			isFuncCall = false;
		}
		
		if(isFuncCall) {
			String fullName = varName+sbNames.toString();
//			System.out.println("exitArrayAccessOrFuncCall()-full_var_name="+fullName);
			String[] ss = fullName.split("\\.");
			ArrayList<String> l = new ArrayList<String>();
			Collections.addAll(l, ss);
			
			String className = "";
			if(l.size() > 0)
				className = Tools.join(l.subList(0, l.size()-1).toArray(new String[]{}), ".");
			String methodName = ss[ss.length-1];
			
			//Class name transform
			if(className.equalsIgnoreCase("math"))
				className = "java.lang.Math";
			else if( methodName.equalsIgnoreCase("print")
					|| methodName.equalsIgnoreCase("println") 
					//|| methodName.equalsIgnoreCase("range")
				  ) {
					className = "io.lambdacloud.BytecodeSupport";
			} else if(className.length() == 0){
				className = "io.lambdacloud.BytecodeSupport";
			}
			
			//Method name transform
			if(methodName.equalsIgnoreCase("range")) {
//				ExprNode start = null;
//				ExprNode end = currentScope().stack.pop();
//				if(ctx.expression().size() > 1) {
//					start = currentScope().stack.pop();
//				}
//				RangeNode node = new RangeNode(start, end, false);
//				currentScope().stack.push(node);
			} else {
				FuncDefNode funcDef = ExprTreeBuildWalker.funcMap.get(methodName);
				if(DEBUG)
					System.out.println("Call "+methodName+" in scope "+this.currentScope());
				
				List<ExprNode> args = new ArrayList<ExprNode>();
				for(int i=0; i<ctx.aa_index().size(); i++) {
					//System.out.println(ctx.expression(i).getText());
					//TODO?			if(null == ctx.aa_index(i).COLON())

					ExprNode arg = currentScope().stack.pop();
					if(arg instanceof AssignNode) {
						arg.genLoadInsn(true);
					}
					args.add(arg);
				}
				
				boolean isDynamicCall = false;
				if(null != funcDef && !funcDef.name.equals(this.currentScope().toString())) {
					className = funcDef.getFuncClassName();//"global";
					isDynamicCall = true;
				} else if(null != funcDef && funcDef.name.equals(this.currentScope().toString())) {
					//This is the case that the recursively call of the function
					//The body of the function is not available here see enterFuncDef()
					className = funcDef.getFuncClassName();
					isDynamicCall = false; //TODO We are not able to determine this here! We need to determine it at code generation phase
				}
				FuncCallNode funcCall = new FuncCallNode(className, methodName, isDynamicCall);
				funcCall.args = args;
				for(ExprNode e : args) {
					e.setParent(funcCall);
				}
				funcCall.refFuncDefNode = funcDef;
				//Type body of funcDef has not been processed here if it is an recursive call
				//funcDef.setParamTypes(funcCall.getParameterClassTypes());
				//System.out.println(Type.getMethodDescriptor(funcCall.getType(), funcCall.getParameterTypes()));
				//System.out.println(funcDef.getType().getDescriptor());
				currentScope().stack.push(funcCall);
			}
			
			
			return;
		}
		
		//------Construct array access node, e.g. A(1,2)------
		//System.out.println("exitArrayAccessOrFuncCall()-varMap: "+this.currentScope().varMap);

		MatrixAccessNode node = new MatrixAccessNode(var);
		node.needInitialize = needInitialize;
		for(int i=ctx.aa_index().size()-1; i>=0; i--) {
			ExprNode idxS = null;
			ExprNode idxStep = null;
			ExprNode idxE = null;
			if(null != ctx.aa_index(i).COLON()) {
				//A(:) -- Access all the elements
				node.addIndex(null, null); //use null to indicate this case
			} else if(null != ctx.aa_index(i).aa_range()) {
				//A(1:10), A(1:end), A(end:-1:1)
				//A(1:5,1:10), A(5:end, 2:2:end), A(end:1,end:2:1)
				if(null != ctx.aa_index(i).aa_range().aa_range_end()) {
					idxE = this.currentScope().stack.pop();
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_step()) {
					idxStep = this.currentScope().stack.pop();
				}
				if(null != ctx.aa_index(i).aa_range().aa_range_start()) {
					idxS = this.currentScope().stack.pop();
				}
				if(null == idxStep)
					node.addIndex(idxS, idxE);
				else
					node.addIndex(new RangeNode(idxS, idxStep, idxE, true), null);
			} else {
				//A(end-1) -- expression, function_handle, mimi_arith_expr
				idxS = this.currentScope().stack.pop();
				idxE = null;
				node.addIndex(idxS, idxE);
			}
		}

		this.currentScope().stack.push(node);
	}
	
	@Override public void exitArithmeticExpressionAddSub(MatlabGrammarParser.ArithmeticExpressionAddSubContext ctx) {
		String op = ctx.add_sub_operator().getText();
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		if(op.equals("+"))
			currentScope().stack.push(new AddNode(v1, v2));
		else if(op.equals(".+"))
			currentScope().stack.push(new AddNode(v1, v2));
		else if(op.equals("-"))
			currentScope().stack.push(new SubNode(v1, v2));
		else if(op.equals(".-"))
			currentScope().stack.push(new SubNode(v1, v2));
		else
			throw new RuntimeException("Bad operator:"+op );
	}

	@Override public void exitArithmeticExpressionMulDiv(MatlabGrammarParser.ArithmeticExpressionMulDivContext ctx) {
		String op = ctx.mul_div_operator().getText();
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		if(op.equals("*"))
			currentScope().stack.push(new MultNode(v1, v2));
		else if(op.equals(".*"))
			currentScope().stack.push(new MatrixDMulNode(v1, v2));
		else if(op.equals("/"))
			currentScope().stack.push(new DivNode(v1, v2));
		else if(op.equals("./"))
			currentScope().stack.push(new MatrixDRDivNode(v1, v2));
		else if(op.equals("\\"))
			currentScope().stack.push(new SolveNode(v1, v2));
		else if(op.equals(".\\"))
			currentScope().stack.push(new MatrixDLDivNode(v1, v2));
		else
			throw new RuntimeException("Bad operator:"+op );
	}

	@Override public void exitArithmeticExpressionNegationEntity(MatlabGrammarParser.ArithmeticExpressionNegationEntityContext ctx) {
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new NegateNode(v1));
	}
	
//	@Override public void exitArithmeticExpressionRem(MatlabGrammarParser.ArithmeticExpressionRemContext ctx) { 
//		ExprNode v2 = currentScope().stack.pop();
//		ExprNode v1 = currentScope().stack.pop();
//		currentScope().stack.push(new RemNode(v1, v2));
//	}

	@Override public void exitArithmeticExpressionEntity(MatlabGrammarParser.ArithmeticExpressionEntityContext ctx) {
		//System.out.println("exitArithmeticExpressionEntity:"+ctx.getText());
		//Do nothing
	}
	
	@Override public void exitExprAssign(MatlabGrammarParser.ExprAssignContext ctx) {
		//variable_entity = value
		ExprNode value = this.currentScope().stack.pop();
		ExprNode var = this.currentScope().stack.pop();
		if(var instanceof VariableNode) {
			VariableNode varNode = (VariableNode)var;
			this.currentScope().stack.push(new AssignNode(varNode, value));
		} else if(var instanceof MatrixAccessNode) {
			MatrixAccessNode acc_node = (MatrixAccessNode)var;
			MatrixAssignNode ass_node = new MatrixAssignNode((VariableNode)acc_node.var, value);
			ass_node.indices = acc_node.indices;
			ass_node.needInitialize = acc_node.needInitialize;
			this.currentScope().stack.push(ass_node);
		} else if(var instanceof CellAccessNode) {
			CellAccessNode acc_node = (CellAccessNode)var;
			CellAssignNode ass_node = new CellAssignNode((VariableNode)acc_node.var, value);
			ass_node.indices = acc_node.indices;
			ass_node.needInitialize = acc_node.needInitialize;
			this.currentScope().stack.push(ass_node);
		} else if(var instanceof StructAccessNode) {
			StructAccessNode acc_node = (StructAccessNode)var;
			StructAssignNode ass_node = new StructAssignNode(acc_node.var, value);
			ass_node.fields = acc_node.fields;
			this.currentScope().stack.push(ass_node);
		} else {
			throw new RuntimeException("exitExprAssign: "+var+"  "+value);
		}
	}
	
	/**
	 * Extract function name and arguments (parameter names) before calling exitFuncDef
	 */
	@Override public void exitFuncDefNameArgs(MatlabGrammarParser.FuncDefNameArgsContext ctx) {
		
		String funcName = ctx.IDENTIFIER(0).getText();
		//Add function level scope for stack and varMap in enterFuncDef()
		//this.addScope(funcName);
		
		FuncDefNode fnode = ExprTreeBuildWalker.funcMap.get(funcName);
		if(null != fnode) {
			throw new RuntimeException("Function "+funcName+" already defined!");
		} else {
			fnode = new FuncDefNode(funcName);
			ExprTreeBuildWalker.funcMap.put(funcName, fnode);
			
			for(VariableNode var : currentScope().varMap.values()) {
				var.setAsLocalVar();
			}
			for(int i=1; i<ctx.IDENTIFIER().size(); i++) {
				String paramName = ctx.IDENTIFIER(i).getText();
				fnode.paramNames.add(paramName);
				//Bugfix: put param node into varMap too!
				VariableNode paramNode = currentScope().varMap.get(paramName);
				if(null == paramNode)
					paramNode = VariableNode.newParameter(paramName, null); //the default of type is null
				paramNode.setAsParameter();
				currentScope().varMap.put(paramName, paramNode);
			}
			fnode.funcVarMap.putAll(currentScope().varMap);
		}
	}

	@Override public void exitFuncDef(MatlabGrammarParser.FuncDefContext ctx) {
		//When enterFuncDef() we need to add a new scope in function level for stack and varMap
		//see enterFuncDef()
		String funcName = ctx.func_name_args().getTokens(MatlabGrammarParser.IDENTIFIER).get(0).getText();
		//System.out.println("exitFuncDef: "+ctx.func_name_args().getTokens(MatlabGrammarParser.IDENTIFIER).get(0).getText());
		
		FuncDefNode fNode = ExprTreeBuildWalker.funcMap.get(funcName);
		while(!this.currentScope().stack.isEmpty()) {
			ExprNode node = this.currentScope().stack.pop();
			node.setParent(fNode.body);
			fNode.body.exprs.add(node);
		}
		//Bugfix: Don't forget to put vars in local funcVarMap
		fNode.funcVarMap.putAll(currentScope().varMap);
		
		//Determine the return expression
		if(null != ctx.func_def_return()) { //It has return variable specified
			
			//Remove the return expression from body list
			ExprNode retNode = fNode.body.exprs.remove(fNode.body.exprs.size()-1);
			
			//Transform it to ReturnListNode
			if(retNode instanceof MatrixInitNode) {
				ReturnListNode retList = new ReturnListNode();
				retList.initExprList = ((MatrixInitNode)retNode).initExprList;
				retNode = retList;
				//((MatrixInitNode)retNode).returnAsArray(); //no longer used
			}
			//Don't put the return expression to the end of the function body
			//fNode.body.add(0, retNode);
			
			//Keep the the return expr in retExpr which will be used 
			//in Generating code, NArgOutNode and ReturnNode (early return)
			fNode.retExpr = retNode; 
			
		} else { //The last expression is the return value
			ExprNode lastExpr = fNode.body.exprs.get(0);
			if(lastExpr instanceof FuncCallNode) {
				FuncCallNode funcCallNode = (FuncCallNode)lastExpr;
				//this is handled in exitExprWithExprEnd()
//				if(funcCallNode.getMethodName().equals("println")) {
//					//TODO use DupNode?
//					DupNode dupNode = new DupNode(funcCallNode.args.get(0));
//					funcCallNode.args.set(0, dupNode);
//					//fNode.body.add(0, funcCallNode.args.get(0));
//					//System.out.println(funcCallNode);
//				}
			}
		}
		
		//Put fNode in funcMap at the function enterFuncDef()
		//so recursive call of itself can be handled correctly
		//by knowing the definition of function early
		//funcMap.put(fNode.name, fNode);
		
		if(DEBUG)
			System.out.println(fNode);
		
		this.popScope();
	}
	
	@Override public void enterFuncDef(MatlabGrammarParser.FuncDefContext ctx) {
		//Add function level scope for stack and varMap
		String funcName = ctx.func_name_args().getTokens(MatlabGrammarParser.IDENTIFIER).get(0).getText();
		this.addScope(funcName);
		//See exitFuncDefNameArgs()
	}
	
//	@Override public void exitExprStatement(MatlabGrammarParser.ExprStatementContext ctx) {
//		//System.out.println("exitExprStatement>>>>----"+ctx.getText()+"----<<<<");
//	}
	
	@Override public void exitExprArithmetic(MatlabGrammarParser.ExprArithmeticContext ctx) { 
		//System.out.println("exitExprArithmetic>>>>"+ctx.getText()+"<<<<");
	}
	
//	@Override public void exitExprStatements(MatlabGrammarParser.ExprStatementsContext ctx) {
//		//System.out.println("exitExprStatements>>>>"+ctx.getText()+"<<<<");
//		ExpressionContext lastExpr = ctx.expression();
//		if(null != lastExpr) {
//			if(null != ctx.expr_end() && ctx.expr_end().SEMI().size()>0)
//				return;
//			//print the LAST expression
//			ExprNode expr = this.currentScope().stack.pop();
//			expr.genLoadInsn(true);
//			FuncCallNode funcCall = new FuncCallNode(BytecodeSupport.class.getName(), "println", false);
////Don't use DupNode to avoid one element pushed on the top of stack
////			DupNode dupNode = new DupNode(expr);
////			funcCall.args.add(dupNode);
//			funcCall.args.add(expr);
//			this.currentScope().stack.push(funcCall);
//		}
//	}

	private void processExprEnd(Expr_endContext ctx) {
		if(null!=ctx && null != ctx.SEMI())
			return;
		
		//print the expression
		ExprNode expr = this.currentScope().stack.peek();
//		if(null != expr.getType() && expr instanceof FuncCallNode && expr.getType().getSort() != Type.VOID) {
//Note: don't call getType() in TreeBuildWalker since the type of expr may not fully assigned
			this.currentScope().stack.pop();
			expr.genLoadInsn(true);
			//TODO change 'println' to an internal name to indicate that it returns the type the same as the argument
			FuncCallNode funcCall = new FuncCallNode(BytecodeSupport.class.getName(), "println", false);
			//Don't use DupNode to avoid one element pushed on the top of stack
	//		DupNode dupNode = new DupNode(expr);
	//		funcCall.args.add(dupNode);
			funcCall.args.add(expr);
			expr.setParent(funcCall);
			this.currentScope().stack.push(funcCall);
//		}
		
	}
	@Override public void exitExprWithExprEnd(MatlabGrammarParser.ExprWithExprEndContext ctx) {
		//System.out.println("exitExprWithExprEnd>>>>"+ctx.getText()+"<<<<");
		processExprEnd(ctx.expr_end());
	}
	
	@Override public void exitLogicalExpressionAnd(MatlabGrammarParser.LogicalExpressionAndContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new AndNode(v1, v2));
	}
	
	@Override public void exitLogicalExpressionOr(MatlabGrammarParser.LogicalExpressionOrContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new OrNode(v1, v2));
	}
	
	@Override public void exitLogicalExpressionNot(MatlabGrammarParser.LogicalExpressionNotContext ctx) { 
		currentScope().stack.push(new NotNode(currentScope().stack.pop()));
	}
	
	@Override public void exitComparisonArithmeticExpression(MatlabGrammarParser.ComparisonArithmeticExpressionContext ctx) {
		String op = ctx.comp_operator().getText();
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		if(op.equals(">")) {
			currentScope().stack.push(new GTNode(v1, v2));
		} else if(op.equals(">=")) {
			currentScope().stack.push(new GENode(v1, v2));
		} else if(op.equals("<")) {
			currentScope().stack.push(new LTNode(v1, v2));
		} else if(op.equals("<=")) {
			currentScope().stack.push(new LENode(v1, v2));
		} else if(op.equals("==")) {
			currentScope().stack.push(new EQNode(v1, v2));
		} else if(op.equals("!=") || op.equals("~=")) {
			currentScope().stack.push(new NEQNode(v1, v2));
		}
	}
	
	@Override public void exitEntityLogicalConst(MatlabGrammarParser.EntityLogicalConstContext ctx) {
		if(null != ctx.TRUE())
			currentScope().stack.push(new ConstantNode(ctx.TRUE().getText(),Type.BOOLEAN_TYPE));
		else if(null != ctx.FALSE())
			currentScope().stack.push(new ConstantNode(ctx.FALSE().getText(),Type.BOOLEAN_TYPE));
		else
			throw new RuntimeException("Invalid logical constant: ["+ctx.getText()+"]");
	}
	
	@Override public void exitExprIf(MatlabGrammarParser.ExprIfContext ctx) {
		IfNode ifnode = new IfNode();
		//System.out.println("exitExprIf: >>>"+ctx.getText()+"<<<");

		if(null != ctx.else_body()) { //else branch
			//System.out.println("else:  "+ctx.else_body().getText());
			for(int i=ctx.else_body().statement_block().statement().size()-1; i>=0; i--) {
				ifnode.elseBlockExprs.add(currentScope().stack.pop());
			}
			if(null != ctx.else_body().statement_block().expression()) {
				ifnode.elseBlockExprs.add(currentScope().stack.pop());
				//processExprEnd(ctx.else_body().statement_block().expr_end());

			}
		}
		for(int i=ctx.if_cond_and_body().size()-1; i>=0; i--) {
			//System.out.println("if/elseif:  "+ctx.if_cond_and_body(i).getText());
			for(int j=ctx.if_cond_and_body(i).statement_block().statement().size(); j>0; j--) {
				ifnode.ifBlockExprs.add(currentScope().stack.pop());
			}
			if(null != ctx.if_cond_and_body(i).statement_block().expression()) {
				ifnode.ifBlockExprs.add(currentScope().stack.pop());
				//processExprEnd(ctx.if_cond_and_body(i).statement_block().expr_end());
			}
				
			//System.out.println("conditon: "+ctx.if_cond_and_body(i).logical_expr().getText());
			ifnode.condition = currentScope().stack.pop();
		}
		currentScope().stack.push(ifnode);
	}
	
	private VariableNode newOrGetVariableNode(String varName, Type type) {
		VariableNode node = this.currentScope().varMap.get(varName);
		if(null == node) {
			node = VariableNode.newLocalVar(varName, type);
			this.currentScope().varMap.put(varName, node);
		}
		return node;
	}
	
	@Override public void exitExprFor(MatlabGrammarParser.ExprForContext ctx) { 
		//System.out.println("exitExprFor: "+ctx.getText());
		
		ForNode forNode = new ForNode();
		for(int i=ctx.statement_block().statement().size()-1; i>=0; i--) {
			forNode.block.add(this.currentScope().stack.pop().setParent(forNode));
		}
		if(null != ctx.statement_block().expression()) {
			forNode.block.add(this.currentScope().stack.pop().setParent(forNode));
		}
		
		ExprNode forRange = this.currentScope().stack.pop();
		//create the loop variable if necessary
		String loopVarName = ctx.IDENTIFIER().getText();
		VariableNode loopVar = this.currentScope().varMap.get(loopVarName);
		if(null == loopVar) {
			loopVar = VariableNode.newLocalVar(loopVarName, null);
			this.currentScope().varMap.put(loopVarName, loopVar);
		} else {
			loopVar.setAsLocalVar();
		}
		
		//for i=1:10
		//for i=1:2:10
		//for i=10:-2:4
		if(forRange instanceof RangeNode) {
			RangeNode rangeNode = (RangeNode)forRange;
			rangeNode.setAsReturnArray();
			//loopVar.setType(rangeNode.getType().getElementType());
			
			forNode.init.add(0,new AssignNode(loopVar, rangeNode.start));
			forNode.cond = new LENode(loopVar, rangeNode.end);
			if(null != rangeNode.step)
				forNode.inc.add(new AddAsignNode(loopVar, rangeNode.step));
			else
				forNode.inc.add(new IncNode(loopVar));
			
		//for i=[10 20 30]
		} else if(forRange instanceof MatrixInitNode) {
			loopVar.setType(Type.DOUBLE_TYPE);
			
			VariableNode loopIdx = newOrGetVariableNode( loopVarName+"Idx", Type.INT_TYPE);
			VariableNode loopArray = newOrGetVariableNode(loopVarName+"Array", Type.getType(double[].class));
			VariableNode loopIdxEnd = newOrGetVariableNode( loopVarName+"End", Type.INT_TYPE);
			
			//idx=0
			forNode.init.add(0,new AssignNode(loopIdx, new ConstantNode(0))); 
			//int[] array=m.getColumnPackedCopy()
			forNode.init.add(0,new AssignNode(loopArray, new FuncCallNode(forRange, "getColumnPackedCopy", false) )); 
			//idxEnd = array.length
			forNode.init.add(0,new AssignNode(loopIdxEnd, new ArrayLength(loopArray))); 
			
			//idx < idxEnd
			forNode.cond = new LTNode(loopIdx, loopIdxEnd);
			
			forNode.inc.add(new IncNode(loopIdx));
			
			forNode.block.add(new AssignNode(loopVar,new ArrayAccess(loopArray, loopIdx)));
			
		}
		
		this.currentScope().stack.push(forNode);
	}
	
	//for i=10:-1:5
	@Override public void exitForRangeColon(MatlabGrammarParser.ForRangeColonContext ctx) { 
		//System.out.println("exitExprRange: "+ctx.getText());
		RangeNode node = null;
		if(ctx.arithmetic_expr().size() == 2) {
			ExprNode idxE = this.currentScope().stack.pop();
			ExprNode idxS = this.currentScope().stack.pop();
			if(idxS instanceof RangeNode) { //s:step:e
				ExprNode idxStep = ((RangeNode) idxS).end;
				idxS = ((RangeNode) idxS).start;
				node = new RangeNode(idxS, idxStep, idxE, true);
			} else {
				node = new RangeNode(idxS, idxE, true);
			}
		} else if(ctx.arithmetic_expr().size() == 3) {
			ExprNode idxE = this.currentScope().stack.pop();
			ExprNode idxStep = this.currentScope().stack.pop();
			ExprNode idxS = this.currentScope().stack.pop();
			node = new RangeNode(idxS, idxStep, idxE, true);
		} else {
			throw new RuntimeException("Range node error: "+ ctx.getText());
		}
		node.setAsReturnArray();
		currentScope().stack.push(node);

	}
	
	
	@Override public void exitExprRange(MatlabGrammarParser.ExprRangeContext ctx) { 
		//System.out.println("exitExprRange: "+ctx.getText());
		RangeNode node = null;
		if(ctx.arithmetic_expr().size() == 2) {
			ExprNode idxE = this.currentScope().stack.pop();
			ExprNode idxS = this.currentScope().stack.pop();
			node = new RangeNode(idxS, idxE, true);
		} else if(ctx.arithmetic_expr().size() == 3) {
			ExprNode idxE = this.currentScope().stack.pop();
			ExprNode idxStep = this.currentScope().stack.pop();
			ExprNode idxS = this.currentScope().stack.pop();
			node = new RangeNode(idxS, idxStep, idxE, true);
		} else {
			throw new RuntimeException("Range node error: "+ ctx.getText());
		}
		currentScope().stack.push(node);
	}
	
	@Override public void exitTicToc(MatlabGrammarParser.TicTocContext ctx) {
		String varName = "__curTime__";
		if(null != ctx.tic()) {
			VariableNode var = VariableNode.newLocalVar(varName, Type.LONG_TYPE);
			this.currentScope().varMap.put(var.getName(), var);
			FuncCallNode funcCall = new FuncCallNode(System.class.getName(), "currentTimeMillis", false);
			AssignNode a = new AssignNode(var, funcCall);
			this.currentScope().stack.push(a);
		}
		if(null != ctx.toc()) {
			VariableNode var = this.currentScope().varMap.get(varName);
			FuncCallNode funcCallCurTime = new FuncCallNode(System.class.getName(), "currentTimeMillis", false);
			SubNode timeDiffNode = new SubNode(funcCallCurTime, var);
			FuncCallNode printNode = new FuncCallNode(BytecodeSupport.class.getName(), "print", false);
			printNode.args.add(timeDiffNode);
			
			StringNode ms1 = new StringNode("Elapsed time is ");
			FuncCallNode print_ms1 = new FuncCallNode(BytecodeSupport.class.getName(), "print", false);
			print_ms1.args.add(ms1);

			StringNode ms2 = new StringNode(" ms.");
			FuncCallNode print_ms2 = new FuncCallNode(BytecodeSupport.class.getName(), "println", false);
			print_ms2.args.add(ms2);
			
			this.currentScope().stack.push(print_ms1);
			this.currentScope().stack.push(printNode);
			this.currentScope().stack.push(print_ms2);
			
		}
	}
	@Override public void exitStatement_block(MatlabGrammarParser.Statement_blockContext ctx) {
		//System.out.println("exitStatement_block: "+ctx.getText());
		if(null != ctx.expression())
			processExprEnd(ctx.expr_end());
	}
	
	@Override public void exitExprMulAssign(MatlabGrammarParser.ExprMulAssignContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new MulAsignNode((VariableNode)v1,v2));
	}
	
	@Override public void exitExprDivAssign(MatlabGrammarParser.ExprDivAssignContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new DivAsignNode((VariableNode)v1,v2));
	}
	
	@Override public void exitExprRemAssign(MatlabGrammarParser.ExprRemAssignContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new RemAsignNode((VariableNode)v1,v2));
	}
	
	@Override public void exitExprAddAssign(MatlabGrammarParser.ExprAddAssignContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new AddAsignNode((VariableNode)v1,v2));
	}
	
	@Override public void exitExprSubAssign(MatlabGrammarParser.ExprSubAssignContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new SubAsignNode((VariableNode)v1,v2));
	}
	
//	@Override public void enterExprWhile(MatlabGrammarParser.ExprWhileContext ctx) { 
//		this.addScope("while");
//	}
	
	@Override public void exitExprWhile(MatlabGrammarParser.ExprWhileContext ctx) {
		WhileNode wn = new WhileNode();
		for(int i=ctx.statement_block().statement().size()-1; i>=0; i--) {
			wn.block.add(currentScope().stack.pop().setParent(wn));
		}
		if(null != ctx.statement_block().expression()) {
			wn.block.add(currentScope().stack.pop().setParent(wn));
		}
		wn.condition = currentScope().stack.pop().setParent(wn);
		currentScope().stack.push(wn);
	}
	
	@Override public void exitArithmeticExpressionPow(MatlabGrammarParser.ArithmeticExpressionPowContext ctx) {
		ExprNode pow = this.currentScope().stack.pop();
		ExprNode base = this.currentScope().stack.pop();
//		pow.setType(Type.DOUBLE_TYPE);
//		base.setType(Type.DOUBLE_TYPE);
//		base.freezeType(true);
		FuncCallNode fnode = new FuncCallNode(BytecodeSupport.class.getName(), "pow", false);
		//reverse the order
		fnode.args.add(pow);
		fnode.args.add(base);
		currentScope().stack.push(fnode);
	}
	
	private int multi_assign_seq = 0;
	@Override public void exitExprMultiAssign(MatlabGrammarParser.ExprMultiAssignContext ctx) {
		try {
		ExprNode value = this.currentScope().stack.pop();
		String tmpVarName = "";
		ArrayList<VariableNode> multiAssignVars = new ArrayList<VariableNode>();
		for(int i=0; i<ctx.variable_entity().size(); i++) {
			String varName = ctx.variable_entity(i).getText();
			multiAssignVars.add(0,(VariableNode)this.currentScope().stack.pop());
			tmpVarName += "_"+varName;
		}
		tmpVarName += multi_assign_seq;
		multi_assign_seq++; //unique
		VariableNode tmpVar = VariableNode.newLocalVar(tmpVarName, null);
		this.currentScope().varMap.put(tmpVarName, tmpVar);
		AssignNode an = new AssignNode(tmpVar, value);
		an.multiAssignVars.addAll(multiAssignVars);
		this.currentScope().stack.push(an);
		} catch (Exception e) {
			System.out.println("exitExprMulAssign: "+ctx.getText());
			e.printStackTrace();
		}
	}
	
	@Override public void exitStringConst(MatlabGrammarParser.StringConstContext ctx) {
		String s = ctx.StringLiteral().getText();
		currentScope().stack.push(new StringNode(s.substring(1, s.length()-1)));
	}
	
//	@Override public void exitStringConcat(MatlabGrammarParser.StringConcatContext ctx) {
//		ExprNode v2 = currentScope().stack.pop();
//		ExprNode v1 = currentScope().stack.pop();
//		currentScope().stack.push(new StringConcatNode(v1,v2));
//	}
	
	@Override public void exitExprReturn(MatlabGrammarParser.ExprReturnContext ctx) {
		ReturnNode node = null;
		FuncDefNode refFunc = ExprTreeBuildWalker.funcMap.get(this.currentScope().getName());
		if(null != ctx.expression()) {
			node = new ReturnNode(this.currentScope().stack.pop(), refFunc);
		} else {
			node = new ReturnNode(null, refFunc);
		}
		this.currentScope().stack.push(node);
	}
	@Override public void enterNArgIn(MatlabGrammarParser.NArgInContext ctx) { 
		FuncDefNode refFunc = ExprTreeBuildWalker.funcMap.get(this.currentScope().getName());
		this.currentScope().stack.push(new NArgInNode(refFunc));
	}
	@Override public void enterNArgOut(MatlabGrammarParser.NArgOutContext ctx) {
		FuncDefNode refFunc = ExprTreeBuildWalker.funcMap.get(this.currentScope().getName());
		this.currentScope().stack.push(new NArgOutNode(refFunc));
	}

	@Override public void exitFuncHandle(MatlabGrammarParser.FuncHandleContext ctx) { 
		String funcName = ctx.IDENTIFIER().getText();
		StringNode s = new StringNode(funcName);
		this.currentScope().stack.push(s);
	}
	
	@Override public void exitCell_init(MatlabGrammarParser.Cell_initContext ctx) { 
		//System.out.println("exitCell_init"+ctx.getText());
		
		if(null == ctx.ai_list()) {
			//empty matrix
			throw new RuntimeException("empty cell is not supported so far.");
		}
		//int cols = ctx.ai_list(0).expression().size();
		int rows = ctx.ai_list().size();
		CellInitNode node = new CellInitNode();
		for(int i=0; i<rows; i++)
			node.colLenList.add(ctx.ai_list(i).expression().size());
		for(int i=0; i<rows; i++) {
			for(int j=0; j<ctx.ai_list(i).expression().size(); j++) {
				node.addInitValues(currentScope().stack.pop());
			}
		}
		currentScope().stack.push(node);
	}
	@Override public void exitArithmeticExpressionBit(MatlabGrammarParser.ArithmeticExpressionBitContext ctx) {
		String op = ctx.bit_operator().getText();
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		if(op.equals("&"))
			currentScope().stack.push(new BAndNode(v1, v2));
		else if(op.equals("|"))
			currentScope().stack.push(new BOrNode(v1, v2));
		else if(op.equals("^"))
			currentScope().stack.push(new BXorNode(v1, v2));
		else
			throw new RuntimeException("Bad operator:"+op );
	}
	
//	@Override public void exitLogicalExpressionBit(MatlabGrammarParser.LogicalExpressionBitContext ctx) { 
//		String op = ctx.bit_operator().getText();
//		ExprNode v2 = currentScope().stack.pop();
//		ExprNode v1 = currentScope().stack.pop();
//		if(op.equals("&"))
//			currentScope().stack.push(new BAndNode(v1, v2));
//		else if(op.equals("|"))
//			currentScope().stack.push(new BOrNode(v1, v2));
//		else if(op.equals("^"))
//			currentScope().stack.push(new BXorNode(v1, v2));
//		else
//			throw new RuntimeException("Bad operator:"+op );
//		
//	}
	
	@Override public void exitSpecialFuncCall(MatlabGrammarParser.SpecialFuncCallContext ctx) { 
		//System.out.println(ctx.getText());
		String methodName = ctx.special_name().getText()+"1"; //Add "1" to special function name
		String className = BytecodeSupport.class.getName();
			
		List<ExprNode> args = new ArrayList<ExprNode>();
		for(int i=0; i<ctx.aa_index().size(); i++) {
			ExprNode arg = currentScope().stack.pop();
			if(arg instanceof AssignNode) {
				arg.genLoadInsn(true);
			}
			args.add(arg);
		}
		FuncCallNode funcCall = new FuncCallNode(className, methodName, false);
		funcCall.args = args;
		currentScope().stack.push(funcCall);
	}

	@Override public void exitExprSwitch(MatlabGrammarParser.ExprSwitchContext ctx) { 
		SwitchNode sn = new SwitchNode();
		//System.out.println(this.currentScope().stack.toString());
		
		//otherwise body
		if(null != ctx.otherwise_body()) {
			StatementNode defaultBlock = new StatementNode();
			if(null != ctx.otherwise_body().statement_block() && null != ctx.otherwise_body().statement_block().expression()) {
				defaultBlock.exprs.add(this.currentScope().stack.pop());
			}
			List<StatementContext> stmtList = ctx.otherwise_body().statement_block().statement();
			for(int j=stmtList.size()-1; j>=0; j--) {
				defaultBlock.exprs.add(this.currentScope().stack.pop());
			}
			sn.defaultBlock = defaultBlock;
		}

		//cases
		List<Case_expr_and_bodyContext> caseExprBodyList = ctx.case_expr_and_body();
		for(int i=caseExprBodyList.size()-1; i>=0; i--) {
			Case_expr_and_bodyContext caseExprBody = caseExprBodyList.get(i);
			StatementNode caseBody = new StatementNode();
			if(null != caseExprBody.statement_block() && null != caseExprBody.statement_block().expression()) {
				caseBody.exprs.add(this.currentScope().stack.pop());
			}
			List<StatementContext> stmtList = caseExprBody.statement_block().statement();
			for(int j=stmtList.size()-1; j>=0; j--) {
				caseBody.exprs.add(this.currentScope().stack.pop());
			}
			sn.caseBlock.add(caseBody);
			
			sn.caseExprs.add(this.currentScope().stack.pop());
			
		}
		
		//switch
		sn.switchExpr = this.currentScope().stack.pop();
		
		this.currentScope().stack.push(sn);
	}

	@Override public void exitEndIndex(MatlabGrammarParser.EndIndexContext ctx) {
		EndIndexNode n = new EndIndexNode();
		this.currentScope().stack.push(n);
	}

	
	@Override public void exitMiniArithEntity(MatlabGrammarParser.MiniArithEntityContext ctx) { }
	@Override public void exitMiniArithAddSub(MatlabGrammarParser.MiniArithAddSubContext ctx) {
		String op = ctx.add_sub_operator().getText();
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		if(op.equals("+"))
			currentScope().stack.push(new AddNode(v1, v2));
		else if(op.equals(".+"))
			currentScope().stack.push(new AddNode(v1, v2));
		else if(op.equals("-"))
			currentScope().stack.push(new SubNode(v1, v2));
		else if(op.equals(".-"))
			currentScope().stack.push(new SubNode(v1, v2));
		else
			throw new RuntimeException("Bad operator:"+op );
		
	}
	@Override public void exitMiniArithMulDiv(MatlabGrammarParser.MiniArithMulDivContext ctx) {
		String op = ctx.mul_div_operator().getText();
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		if(op.equals("*"))
			currentScope().stack.push(new MultNode(v1, v2));
		else if(op.equals(".*"))
			currentScope().stack.push(new MatrixDMulNode(v1, v2));
		else if(op.equals("/"))
			currentScope().stack.push(new DivNode(v1, v2));
		else if(op.equals("./"))
			currentScope().stack.push(new MatrixDRDivNode(v1, v2));
		else if(op.equals("\\"))
			currentScope().stack.push(new SolveNode(v1, v2));
		else if(op.equals(".\\"))
			currentScope().stack.push(new MatrixDLDivNode(v1, v2));
		else
			throw new RuntimeException("Bad operator:"+op );
		
	}

}
