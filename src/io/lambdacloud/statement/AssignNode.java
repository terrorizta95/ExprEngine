package io.lambdacloud.statement;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

public class AssignNode extends ExprNode {
	VariableNode left;
	ExprNode right;
	public AssignNode(VariableNode left, ExprNode right) {
		this.left = left;
		this.right = right;
		this.type = right.getType();
	}
	
	public String toString() {
		return left + "=" + right;
	}
	
	@Override
	public Type getType() {
		return right.getType();
	}
	
	public void genCode(MethodVisitor mv) {
		right.genCode(mv);
		if(right instanceof AssignNode) {
			AssignNode r = (AssignNode)right;
			mv.visitIntInsn(getType().getOpcode(Opcodes.ILOAD), r.left.idxLVT);
		}
		mv.visitIntInsn(getType().getOpcode(Opcodes.ISTORE), left.idxLVT);
	}
	
}
