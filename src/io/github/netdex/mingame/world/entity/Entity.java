package io.github.netdex.mingame.world.entity;
import io.github.netdex.mingame.physics.Vector;

import java.awt.Graphics2D;


public abstract class Entity implements Drawable {
	
	public Vector loc;
	public Vector vel;
	
	public Entity(){
		loc = new Vector(0,0);
		vel = new Vector(0,0);
	}
	
	public abstract void draw(Graphics2D g);
}
