package io.lambdacloud.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.Opcodes;

public class BOrNode extends ExprNode {
	ExprNode left;
	ExprNode right;
	public BOrNode(ExprNode left, ExprNode right) {
		this.left = left;
		this.left.genLoadInsn(true);
		this.right = right;
		this.right.genLoadInsn(true);
		this.type = getType();
	}
	
	public String toString() {
		return left + "|" + right;
	}
	
	public void genCode(MethodVisitor mv) {
		left.genCode(mv);
		right.genCode(mv);
		mv.visitInsn(getType().getOpcode(Opcodes.IOR));
	}
	
	@Override
	public Type getType() {
		return Utils.typeConversion(left.getType(), right.getType());
	}
	
	public int test(int a, int b) {
		int c = a | b;
		return c;
	}
}