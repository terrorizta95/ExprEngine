package io.lambdacloud.node.arithmetric;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.lambdacloud.MethodGenHelper;
import io.lambdacloud.node.ExprNode;
import io.lambdacloud.node.UnaryOp;

public class NegateNode extends UnaryOp {

	public NegateNode(ExprNode expr) {
		this.expr = expr;
		expr.genLoadInsn(true);
	}

	public String toString() {
		return "-" + expr;
	}

	public void genCode(MethodGenHelper mg) {
		Type myType = this.getType();
		expr.genCode(mg);
		if((myType.getDescriptor().equals(Type.getType(Jama.Matrix.class).getDescriptor()))) {
			mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Jama/Matrix", "uminus", "()LJama/Matrix;", false);
		} else {
			mg.visitInsn(getType().getOpcode(Opcodes.INEG));
		}
		
	}
}