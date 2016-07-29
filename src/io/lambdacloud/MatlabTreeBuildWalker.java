package io.lambdacloud;

import java.io.FileOutputStream;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.lambdacloud.exprengine.ExprGrammarParser.Array_indexContext;
import io.lambdacloud.matlab.MatlabGrammarBaseListener;
import io.lambdacloud.matlab.MatlabGrammarParser;
import io.lambdacloud.matlab.MatlabGrammarParser.ArithmeticExpressionAddContext;
import io.lambdacloud.matlab.MatlabGrammarParser.ArithmeticExpressionRangeContext;
import io.lambdacloud.matlab.MatlabGrammarParser.ArithmeticExpressionSolveContext;
import io.lambdacloud.matlab.MatlabGrammarParser.Array_initContext;
import io.lambdacloud.matlab.MatlabGrammarParser.EntityArrayAccessContext;
import io.lambdacloud.matlab.MatlabGrammarParser.EntityConstFloatContext;
import io.lambdacloud.matlab.MatlabGrammarParser.EntityConstIntegerContext;
import io.lambdacloud.matlab.MatlabGrammarParser.EntityVariableContext;
import io.lambdacloud.matlab.MatlabGrammarParser.ExpressionContext;
import io.lambdacloud.matlab.MatlabGrammarParser.ProgContext;
import io.lambdacloud.matlab.MatlabGrammarParser.TransposeContext;
import io.lambdacloud.node.ConstantNode;
import io.lambdacloud.node.ExprNode;
import io.lambdacloud.node.FuncDefNode;
import io.lambdacloud.node.RangeNode;
import io.lambdacloud.node.VariableNode;
import io.lambdacloud.node.arithmetric.AddNode;
import io.lambdacloud.node.arithmetric.DivNode;
import io.lambdacloud.node.arithmetric.MultNode;
import io.lambdacloud.node.arithmetric.NegateNode;
import io.lambdacloud.node.arithmetric.SubNode;
import io.lambdacloud.node.array.ArrayAccessNode;
import io.lambdacloud.node.array.ArrayNode;
import io.lambdacloud.node.matrix.MatrixAccessNode;
import io.lambdacloud.node.matrix.MatrixDLDivNode;
import io.lambdacloud.node.matrix.MatrixDMulNode;
import io.lambdacloud.node.matrix.MatrixDRDivNode;
import io.lambdacloud.node.matrix.MatrixInitNode;
import io.lambdacloud.node.matrix.SolveNode;
import io.lambdacloud.node.matrix.TransposeNode;

public class MatlabTreeBuildWalker extends MatlabGrammarBaseListener {
	public static boolean DEBUG = true;
//	public Deque<ExprNode> stack = new LinkedList<ExprNode>();
//	
//	//Variable map which is generated after parsing
//	//Another place is the phase of code generation
//	public SortedMap<String, VariableNode> varMap = new TreeMap<String, VariableNode>();
	
	public Deque<Scope> scope = new LinkedList<Scope>();

	//Parameter types which should be passed in before parsing
	protected Class<?> defaultParameterTypeOrInterface = null;
	protected Map<String, Class<?>> mapParameterTypes = null;

	public static HashMap<String, FuncDefNode> funcMap = new HashMap<String, FuncDefNode>();
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
	
	public static CallSite bootstrap(MethodHandles.Lookup caller, String name, MethodType type) throws Exception {
		
		FuncDefNode fnode = funcMap.get(name);
		fnode.setParamTypes(type.parameterArray());
		String tt = type.toMethodDescriptorString();
		if(DEBUG)
			System.out.println("bootstrap: "+fnode.getFuncClassName()+"."+name+":"+tt);
		//tt = tt.replaceAll("\\(|\\)", "_");
		Class<?> cls = fnode.genFuncCode(true);
		
		MethodHandle mh = MethodHandles.lookup().findStatic(cls, name,
		MethodType.methodType(type.returnType(),type.parameterArray()));

		return new ConstantCallSite(mh);
	}
	
	public void printInfo() {
		System.out.println("Parameters:");
		for(VariableNode n : currentScope().varMap.values()) {
			if(n.isParameter())
				System.out.println(n.name+": "+n.getType().getDescriptor());
		}
		System.out.println("Local Variables:");
		for(VariableNode n : currentScope().varMap.values()) {
			if(n.isLocalVar())
				System.out.println(n.name+": "+n.getType().getDescriptor());
		}
	}
	
	public Type[] getAndFixParameterTypes(Map<String, Class<?>> mapParameterTypes) {
		List<VariableNode> pList = new ArrayList<VariableNode>();
		for(Entry<String, VariableNode> e : this.currentScope().varMap.entrySet()) {
			if(e.getValue().isParameter())
				pList.add(e.getValue());
		}
		Collections.sort(pList, new Comparator<VariableNode>() {
			@Override
			public int compare(VariableNode o1, VariableNode o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		
		Type[] ret = new Type[pList.size()];
		int i = 0;
		for(VariableNode node : pList) {
			
			//Fix parameter types according the passed in types
			if(null == mapParameterTypes)
				throw new RuntimeException("Need parameter for variable: "+node.name);
			Class<?> cls = mapParameterTypes.get(node.name);
			if(null == cls)
				throw new RuntimeException("Need parameter for variable: "+node.name);
			node.setType(Type.getType(mapParameterTypes.get(node.name)));
			
			ret[i] = Type.getType(mapParameterTypes.get(node.name));
			
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
				return o1.name.compareTo(o2.name);
			}
		});
		
		Type[] ret = new Type[pList.size()];
		this.mapParameterTypes = new HashMap<String, Class<?>>();
		int i = 0;
		for(VariableNode node : pList) {
			
			//Fix parameter types according the passed in types
			node.setType(Type.getType(aryParameterTypes[i]));
			
			ret[i] = Type.getType(aryParameterTypes[i]);
			
			this.mapParameterTypes.put(node.name, aryParameterTypes[i]);
			
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
				return o1.name.compareTo(o2.name);
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
				return o1.name.compareTo(o2.name);
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
				ret[i] = mapParameterTypes.get(node.name);
				
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
			
			//Define method
			Type retType = null;
			Type[] paramTypes = null;
			int access =  Opcodes.ACC_PUBLIC;
			if(isStatic) access |= Opcodes.ACC_STATIC;
			
			//There are two ways to specify parameter types of the generated method
			if(null == aryParameterTypes) {
				if(null != this.defaultParameterTypeOrInterface) {
					if(this.defaultParameterTypeOrInterface.isInterface()) {
						Method m = this.defaultParameterTypeOrInterface.getDeclaredMethods()[0];
						paramTypes = getAndFixParameterTypes(m.getParameterTypes());
						//Check m.getReturnType() == retType ? 
						retType = currentScope().stack.peek().getType(); //return type of the last expression
						if(null == retType) throw new RuntimeException("Return type (top element of stack) is null!");
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
						retType = currentScope().stack.peek().getType(); //return type of the last expression
						if(null == retType) throw new RuntimeException("Return type (top element of stack) is null!");
						cgen.startMethod(access, methodName,
								Type.getMethodDescriptor(retType, paramTypes));
					}
				} else {
					paramTypes = getAndFixParameterTypes(this.mapParameterTypes);
					retType = currentScope().stack.peek().getType(); //return type of the last expression
					if(null == retType) throw new RuntimeException("Return type (top element of stack) is null!");
					cgen.startMethod(access, methodName,
							Type.getMethodDescriptor(retType, paramTypes));
				}
			} else {
				paramTypes = getAndFixParameterTypes(aryParameterTypes);
				retType = currentScope().stack.peek().getType(); //return type of the last expression
				if(null == retType) throw new RuntimeException("Return type (top element of stack) is null!");
				cgen.startMethod(access, methodName,
						Type.getMethodDescriptor(retType, paramTypes));
			}
			cgen.startCode();
			
			MethodVisitor mv = cgen.getMV();
			MethodGenHelper mg = new MethodGenHelper(mv, currentScope().varMap);
			
//			int index = 1;
//			if(isStatic) index = 0;
//			for(Entry<String, VariableNode> e : varMap.entrySet()) {
//				if(e.getValue().isLocalVar()) continue;
//				VariableNode var = e.getValue();
//				var.idxLVT = index;
//				if(var.getType().getSort() == Type.DOUBLE)
//					index += 2;
//				else
//					index++;
//			}
//			for(Entry<String, VariableNode> e : varMap.entrySet()) {
//				if(e.getValue().isParameter()) continue;
//				VariableNode var = e.getValue();
//				var.idxLVT = index;
//				if(var.getType().getSort() == Type.DOUBLE)
//					index += 2;
//				else
//					index++;
//			}
			mg.updateLVTIndex(isStatic);

			
			//Generate code for all the expressions
			while(!currentScope().stack.isEmpty()) {
				ExprNode expr = currentScope().stack.pollLast();
				expr.genCode(mg);
			}
			
			mg.visitInsn(retType.getOpcode(Opcodes.IRETURN));
			if(!isStatic)
				mg.visitLocalVariable("this", "L"+className+";", 
						null, cgen.labelStart, cgen.lableEnd, 0);
			for(VariableNode var : currentScope().varMap.values()) {
				mg.visitLocalVariable(var.name, var.getType().getDescriptor(),
						null, cgen.labelStart, cgen.lableEnd, var.idxLVT);
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
//			for (Method m : c.getMethods()) {
//				System.out.println(m.getName());
//			}
			return c;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override public void exitEntityVariable(MatlabGrammarParser.EntityVariableContext ctx) {
		//System.out.println("exitEntityVariable: "+ctx.getText());
		String varName = ctx.getText();
		VariableNode val = currentScope().varMap.get(varName);
		if(null == val) {
			if(null != this.mapParameterTypes) {
				Class<?> varCls = this.mapParameterTypes.get(varName);
				if(null != varCls)
					val = VariableNode.newParameter(varName, Type.getType(varCls));
				else
					val = VariableNode.newLocalVar(varName, Type.getType(double.class));
					//throw new RuntimeException("No type info provied for '"+varName+"'!");
				
			} else if(null != this.defaultParameterTypeOrInterface) {
				//default to double
				if(this.defaultParameterTypeOrInterface.isInterface()) {
					//call getAndFixParameterTypes(Class<?>[] aryParameterTypes) before generate code
					//TODO need better solution
					val = VariableNode.newParameter(varName, Type.getType(double.class));
				} else {
					val = VariableNode.newParameter(varName, Type.getType(this.defaultParameterTypeOrInterface));
				}
			} else {
				//call getAndFixParameterTypes(Class<?>[] aryParameterTypes) before generate code
				//TODO need better solution
				val = VariableNode.newParameter(varName, Type.getType(double.class));
			}
			currentScope().varMap.put(varName, val);
		}
		currentScope().stack.push(val);
	}
	
	@Override public void exitProg(MatlabGrammarParser.ProgContext ctx) { 
	}
	
	@Override public void exitEntityConstInteger(MatlabGrammarParser.EntityConstIntegerContext ctx) {
		//System.out.println("exitConstInteger"+ctx.getText());
		currentScope().stack.push(new ConstantNode(ctx.getText(), Type.INT_TYPE));
	}
	@Override public void exitEntityConstFloat(MatlabGrammarParser.EntityConstFloatContext ctx) { 
		//System.out.println("exitEntityConstFloat:"+ctx.getText());
		currentScope().stack.push(new ConstantNode(ctx.getText(), Type.DOUBLE_TYPE));
	}
	@Override public void exitArray_init(MatlabGrammarParser.Array_initContext ctx) {
		System.out.println(ctx.getText());
		
		if(null == ctx.expr_list()) {
			//empty matrix
			throw new RuntimeException("empty matrix is not supported so far.");
		}
		int cols = ctx.expr_list(0).expression().size();
		int rows = ctx.expr_list().size();
		MatrixInitNode node = new MatrixInitNode(rows);
		for(int i=0; i<cols; i++) {
			for(int j=0; j<rows; j++) {
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
	
	@Override public void exitArithmeticExpressionSolve(MatlabGrammarParser.ArithmeticExpressionSolveContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new SolveNode(v1, v2));
	}
	
	@Override public void exitArithmeticExpressionRange(MatlabGrammarParser.ArithmeticExpressionRangeContext ctx) {
		ExprNode idxE = this.currentScope().stack.pop();
		ExprNode idxS = this.currentScope().stack.pop();
		RangeNode node = new RangeNode(idxS, idxE, true);
		currentScope().stack.push(node);
	}
	
	@Override public void exitEntityArrayAccess(MatlabGrammarParser.EntityArrayAccessContext ctx) { 
		String varName = ctx.IDENTIFIER().getText();
		VariableNode var = this.currentScope().varMap.get(varName);
		if(null == var) {
			var = VariableNode.newParameter(varName, Type.getType(int[].class)); //default to double
			currentScope().varMap.put(varName, var);
		}
		
//		ArrayAccessNode node = new ArrayAccessNode(var);
//		for(int i=ctx.func_args().expr_list().expression().size()-1; i>=0; i--) {
//			//ExpressionContext aic = ctx.func_args().expr_list().expression(i);
//			ExprNode idxS = this.currentScope().stack.pop();
//			ExprNode idxE = null;
//			if(idxS instanceof RangeNode) {
//				RangeNode range = (RangeNode)idxS;
//				idxS = range.start;
//				idxE = range.end; //end+1 =>new AddNode(end, 1)
//			}
//			node.addIndex(idxS, idxE);
//		}
		
		MatrixAccessNode node = new MatrixAccessNode(var);
		for(int i=ctx.func_args().expr_list().expression().size()-1; i>=0; i--) {
			ExprNode idxS = this.currentScope().stack.pop();
			ExprNode idxE = null;
			if(idxS instanceof RangeNode) {
				RangeNode range = (RangeNode)idxS;
				idxS = range.start;
				idxE = range.end; //end+1 =>new AddNode(end, 1)
			}
			node.addIndex(idxS, idxE);
		}

		this.currentScope().stack.push(node);
	}
	
	@Override public void exitArithmeticExpressionAdd(MatlabGrammarParser.ArithmeticExpressionAddContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new AddNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionSub(MatlabGrammarParser.ArithmeticExpressionSubContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new SubNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionMul(MatlabGrammarParser.ArithmeticExpressionMulContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new MultNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionDiv(MatlabGrammarParser.ArithmeticExpressionDivContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new DivNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionDMul(MatlabGrammarParser.ArithmeticExpressionDMulContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new MatrixDMulNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionDLDiv(MatlabGrammarParser.ArithmeticExpressionDLDivContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new MatrixDLDivNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionDRDiv(MatlabGrammarParser.ArithmeticExpressionDRDivContext ctx) {
		ExprNode v2 = currentScope().stack.pop();
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new MatrixDRDivNode(v1, v2));
	}
	@Override public void exitArithmeticExpressionNegationEntity(MatlabGrammarParser.ArithmeticExpressionNegationEntityContext ctx) {
		ExprNode v1 = currentScope().stack.pop();
		currentScope().stack.push(new NegateNode(v1));
		
	}
	
	@Override public void exitArithmeticExpressionEntity(MatlabGrammarParser.ArithmeticExpressionEntityContext ctx) {
		//System.out.println("exitArithmeticExpressionEntity:"+ctx.getText());
		//Do nothing
	}

}
