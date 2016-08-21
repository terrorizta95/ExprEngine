package io.lambdacloud.node;

import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.util.Deque;

import org.objectweb.asm.Type;
import org.objectweb.asm.Opcodes;

import io.lambdacloud.MethodGenHelper;

public class RangeNode extends ExprNode {
	public ExprNode start;
	public ExprNode end;
	public ExprNode step;
	public boolean includeEnd;
	public int INDEX_SHIFT = 0;
	private int flag = 0; //return matrix
	
	public RangeNode(ExprNode start, ExprNode end, boolean includeEnd) {
		this.start = start;
		this.step = null;
		this.end = end;
		this.includeEnd = includeEnd;
	}
	
	public RangeNode(ExprNode start, ExprNode step, ExprNode end, boolean includeEnd) {
		this.start = start;
		this.step = step;
		this.end = end;
		this.includeEnd = includeEnd;
	}
	
	@Override
	public void genCode(MethodGenHelper mg) {
		String type2 = "(III)[I";
		String type3 = "(IIII)[I";
		Type retType = this.getType();
		Type myType = null;
		if(flag == 0) {
			myType = Type.DOUBLE_TYPE;
			type2 = "(DDI)[D";
			type3 = "(DDDI)[D";
		} else {
			myType = retType.getElementType();
			if(myType.getSort() == Type.DOUBLE) {
				type2 = "(DDI)[D";
				type3 = "(DDDI)[D";
			}			
		}
		
		try {
			mg.visitTypeInsn(Opcodes.NEW, "Jama/Matrix");
			mg.visitInsn(DUP);
			if(start != null) {
				//range(0, 10)
				if(null == this.step) {
					start.genCode(mg);
					Tools.insertConversionInsn(mg, start.getType(), myType);
					end.genCode(mg);
					Tools.insertConversionInsn(mg, end.getType(), myType);
					if(INDEX_SHIFT == 0)
						mg.visitInsn(Opcodes.ICONST_0);
					else
						mg.visitInsn(Opcodes.ICONST_1);
					if(this.includeEnd)
						mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range2", 
								type2, false);
					else
						mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range", 
								type2, false);
				} else {
					//range(0, 2, 10)
					start.genCode(mg);
					Tools.insertConversionInsn(mg, start.getType(), myType);
					step.genCode(mg);
					Tools.insertConversionInsn(mg, step.getType(), myType);
					end.genCode(mg);
					Tools.insertConversionInsn(mg, end.getType(), myType);
					if(INDEX_SHIFT == 0)
						mg.visitInsn(Opcodes.ICONST_0);
					else
						mg.visitInsn(Opcodes.ICONST_1);
					//handled [s,step,e] and [s,step,e)
					if(this.includeEnd)
						mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range2", 
									type3, false);
					else
						mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range", 
								type3, false);				}
			} else {
				//range(3)
				end.genCode(mg);
				if(INDEX_SHIFT == 0)
					mg.visitInsn(Opcodes.ICONST_0);
				else
					mg.visitInsn(Opcodes.ICONST_1);
				mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range", 
						"(II)[I", false);
			}
			
			if(flag == 0) {
				mg.visitInsn(DUP);
				mg.visitInsn(Opcodes.ARRAYLENGTH);
				mg.visitMethodInsn(Opcodes.INVOKESPECIAL, "Jama/Matrix", "<init>", "([DI)V", false);
				mg.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Jama/Matrix", "transpose", "()LJama/Matrix;", false);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void test() {
		double[] d = new double[10];
		int a = d.length;
	}

	@Override
	public Type getType(Deque<Object> stack) {
		if(flag == 0)
			return Type.getType(Jama.Matrix.class);
		else {
			if(null == this.end)
				return Tools.getArrayType(start.getType(stack));
			else if(null == this.step) {
				if(null != start)
					return Tools.getArrayType(Tools.typeConversion(start.getType(stack), end.getType(stack)));
				else
					return Tools.getArrayType(end.getType(stack));
					
			} else {
				Type eleType = Tools.typeConversion(start.getType(), step.getType(stack));
				eleType = Tools.typeConversion(eleType, end.getType(stack));
				return Tools.getArrayType(eleType);
			}
		}
	}
	
	public void genStartCode(MethodGenHelper mg) {
		if(start == null)
			mg.visitInsn(Opcodes.ICONST_0);
		else
			start.genCode(mg);
	}
	
	public void genEndCode(MethodGenHelper mg) {
		end.genCode(mg);
		if(this.includeEnd) {
			mg.visitInsn(Opcodes.ICONST_1);
			mg.visitInsn(Opcodes.IADD);
		}
	}
	
	public void genStepCode(MethodGenHelper mg) {
		step.genCode(mg);
	}
	
	public String toString() {
		if(null == this.step) {
			if(includeEnd)
				return "["+start+", "+end+"]";
			else
				return "["+start+", "+end+")";
		} else {
			if(includeEnd)
				return "["+start+", "+step+", "+end+"]";
			else
				return "["+start+", "+step+", "+end+")";
		}
			
	}

	@Override
	public void updateType(Deque<Object> stack) {
		//circle check
		if(stack.contains(this)) 
			return;
		stack.push(this);
		if(null != this.start)
			this.start.updateType(stack);
		if(null != this.end)
			this.end.updateType(stack);
		if(null != this.step)
			this.step.updateType(stack);
		stack.pop();
	}
	
	public void setAsMatirx() {
		this.flag = 0;
	}
	
	public void setAsRange() {
		this.flag = 1;
	}
}
