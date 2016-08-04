package io.lambdacloud.node.matrix;

import org.objectweb.asm.Opcodes;

import io.lambdacloud.MethodGenHelper;
import io.lambdacloud.node.BinaryOp;
import io.lambdacloud.node.ExprNode;

public class MatrixDRDivNode extends BinaryOp {
	public MatrixDRDivNode(ExprNode left, ExprNode right) {
		this.left = left;
		this.left.genLoadInsn(true);
		this.right = right;
		this.right.genLoadInsn(true);
	}

	public String toString() {
		return left + ".*" + right;
	}

	public void genCode(MethodGenHelper mg) {
		left.genCode(mg);
		right.genCode(mg);
		mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Jama/Matrix", "arrayRightDivide", "(LJama/Matrix;)LJama/Matrix;", false);
	}
}