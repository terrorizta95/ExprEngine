package io.lambdacloud.node.arithmetric;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.lambdacloud.BytecodeSupport;
import io.lambdacloud.MethodGenHelper;
import io.lambdacloud.node.BinaryOp;
import io.lambdacloud.node.ExprNode;
import io.lambdacloud.node.Tools;

public class RemNode extends BinaryOp {
	public RemNode(ExprNode left, ExprNode right) {
		super(left, right);
		this.left.genLoadInsn(true);
		this.right.genLoadInsn(true);
	}

	public String toString() {
		return left + "%" + right;
	}

	public void _genCode(MethodGenHelper mg) {
		Type myType = this.getType();
		Type lt = left.getType();
		Type rt = right.getType();
		if(null == lt || null == rt)
			return;
		
		if((myType.getDescriptor().equals(Type.getType(Jama.Matrix.class).getDescriptor()))) {
			if(lt.getSort() == Type.OBJECT && rt.getSort() == Type.OBJECT) {
				left.genCode(mg);
				right.genCode(mg);
				mg.visitMethodInsn(Opcodes.INVOKESTATIC, BytecodeSupport.getMyName(), "rem", "(LJama/Matrix;LJama/Matrix;)LJama/Matrix;", false);
			} else if(lt.getSort() == Type.OBJECT) {
				left.genCode(mg);
				right.genCode(mg);
				Tools.insertConversionInsn(mg, rt, Type.DOUBLE_TYPE);
				mg.visitMethodInsn(Opcodes.INVOKESTATIC, BytecodeSupport.getMyName(), "rem", "(LJama/Matrix;D)LJama/Matrix;", false);
			} else if(rt.getSort() == Type.OBJECT) {
				right.genCode(mg);
				left.genCode(mg);
				Tools.insertConversionInsn(mg, lt, Type.DOUBLE_TYPE);
				mg.visitMethodInsn(Opcodes.INVOKESTATIC, BytecodeSupport.getMyName(), "rem", "(LJama/Matrix;D)LJama/Matrix;", false);
			} else {
				throw new RuntimeException();
			}
		} else {
			left.genCode(mg);
			Tools.insertConversionInsn(mg, left.getType(), myType);
			right.genCode(mg);
			Tools.insertConversionInsn(mg, right.getType(), myType);
			mg.visitInsn(myType.getOpcode(Opcodes.IREM));
		}
	}
}
