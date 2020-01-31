package com.idealgen.solution.calculator;

public class Token {
	private String expression;
	private int id;
	private int parentId = -1;
	
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public String toString() {
		return "Id:" + id + " Expression: " + this.expression + " ParentID" + this.getParentId();
	}
	
	
}
