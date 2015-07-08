package io.github.netdex.mingame.world.entity;
import java.util.ArrayList;


public class Inventory {
	
	private ArrayList<ItemStack> inv;
	
	public Inventory(){
		this.inv = new ArrayList<ItemStack>();
	}
	
	public ArrayList<ItemStack> getContents(){
		return inv;
	}
}
