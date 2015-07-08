package io.github.netdex.mingame.world.entity;

public class ItemStack {
	private Item item;
	private int amt;
	public ItemStack(Item item, int amt){
		this.item = item;
		this.amt = amt;
	}
	
	public Item getItem(){
		return item;
	}
	public int getAmount(){
		return amt;
	}
}
