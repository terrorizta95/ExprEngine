package io.lambdacloud.node.matrix;

import org.objectweb.asm.Opcodes;

import io.lambdacloud.MethodGenHelper;
import io.lambdacloud.node.ExprNode;
import io.lambdacloud.node.UnaryOp;

public class TransposeNode extends UnaryOp {
	public TransposeNode(ExprNode expr) {
		super(expr);
	}

	public String toString() {
		return expr+"'";
	}

	@Override
	public void genCode(MethodGenHelper mg) {
		expr.genCode(mg);
		mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Jama/Matrix", "transpose", "()LJama/Matrix;", false);
	}
}
