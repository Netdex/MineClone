package io.github.netdex.mingame.world.entity;
import io.github.netdex.mingame.Plat;
import io.github.netdex.mingame.physics.Vector;
import io.github.netdex.mingame.world.Tile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;


public class Player extends Entity {
	private static final int PLAYER_WIDTH = 20;
	private static final int PLAYER_HEIGHT = 35;
	private Inventory inv;
	
	public Player(){
		this.inv = new Inventory();
	}
	
	public Inventory getInventory(){
		return inv;
	}
	
	public void breakTile(Tile t){
		t.startBreak();
	}
	
	public void placeTile(Tile t){
		t.startCreate();
	}
	
	public Point getRelative(){
		Vector center = getCenter();
		Point rel = new Point((int)(center.x / Plat.TILE_WIDTH), (int)(center.y / Plat.TILE_WIDTH));
		return rel;
	}
	public Vector getCenter(){
		return new Vector(loc.x + PLAYER_WIDTH / 2.0, loc.y + PLAYER_HEIGHT / 2.0);
	}
	@Override
	public void draw(Graphics2D g){
		g.setColor(Color.YELLOW.darker());
		g.fillRect((int)loc.x + 2, (int)loc.y + 2, PLAYER_WIDTH, PLAYER_HEIGHT);
		g.setColor(Color.YELLOW);
		g.fillRect((int)loc.x, (int)loc.y, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
	
	public Rectangle2D getBounds(){
		return new Rectangle2D.Double(loc.x, loc.y, PLAYER_WIDTH, PLAYER_HEIGHT);
	}
}
