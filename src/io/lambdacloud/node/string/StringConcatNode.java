package io.lambdacloud.node.string;

import java.util.Deque;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.lambdacloud.MethodGenHelper;
import io.lambdacloud.node.ExprNode;
import io.lambdacloud.node.VariableNode;

public class StringConcatNode extends ExprNode {
	ExprNode left;
	ExprNode right;

	public StringConcatNode(ExprNode left, ExprNode right) {
		this.left = left;
		this.right = right;
	}
	
	@Override
	public void genCode(MethodGenHelper mg) {
		if(left instanceof StringNode && right instanceof StringNode) {
			StringNode node = new StringNode(((StringNode)left).strVal+((StringNode)right).strVal);
			node.genCode(mg);
		} else if(left instanceof VariableNode && right instanceof StringNode) {
			left.genCode(mg);
			right.genCode(mg);
			mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
		} else if(left instanceof StringNode && right instanceof VariableNode) {
			left.genCode(mg);
			right.genCode(mg);
			mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
			
		} else if(left instanceof VariableNode && right instanceof VariableNode) {
			left.genCode(mg);
			right.genCode(mg);
			mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;", false);
			
		} else {
			throw new RuntimeException("");
		}
		
	}
	
	public String test(String a, String b) {
		return a.concat(b);
	}

	@Override
	public Type getType() {
		return Type.getType(String.class);
	}

	@Override
	public Type getType(Deque<Object> stack) {
		return Type.getType(String.class);
	}

}