package io.lambdacloud.statement;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

import com.sun.xml.internal.ws.org.objectweb.asm.Opcodes;

import io.lambdacloud.MethodGenHelper;

public class RangeNode extends ExprNode {
	ExprNode start;
	ExprNode end;
	boolean includeEnd;
	
	public RangeNode(ExprNode start, ExprNode end, boolean includeEnd) {
		this.start = start;
		this.end = end;
		this.includeEnd = includeEnd;
	}
	
	@Override
	public void genCode(MethodGenHelper mg) {
		try {
			if(start != null) {
				start.genCode(mg);
				end.genCode(mg);
				if(this.includeEnd)
					mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range2", 
						"(II)[I", false);
				else
					mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range", 
							"(II)[I", false);
			} else {
				end.genCode(mg);
				mg.visitMethodInsn(INVOKESTATIC, "io/lambdacloud/BytecodeSupport", "range", 
						"(I)[I", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public Type getType() {
		return Type.getType(int[].class);
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
	
	public String toString() {
		if(includeEnd)
			return "["+start+", "+end+"]";
		else
			return "["+start+", "+end+")";
			
	}
}
