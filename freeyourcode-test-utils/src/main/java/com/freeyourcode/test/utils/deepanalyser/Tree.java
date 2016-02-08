package com.freeyourcode.test.utils.deepanalyser;

import java.util.LinkedList;

public class Tree {
	
	private final LinkedList<Leaf> leafs = new LinkedList<Leaf>();

	public static class Leaf{
		public final String name;
		
		public Leaf(String name) {
			this.name = name;
		}
	}
	
	public Leaf topDown(){
		return leafs.pollLast();
	}
	
	public void bottomUp(String name){
		leafs.add(new Leaf(name));
	}
	
	@Override
	public String toString() {
		String s = "";
		for(int i = 0; i < leafs.size(); i++){
			if(i > 0){
				s += ".";
			}
			s += leafs.get(i).name;
		}
		return s;
	}
}
