package io.github.netdex.mingame;
import io.github.netdex.mingame.physics.PhysExtensions;
import io.github.netdex.mingame.physics.Vector;
import io.github.netdex.mingame.world.Tile;
import io.github.netdex.mingame.world.World;
import io.github.netdex.mingame.world.entity.Entity;
import io.github.netdex.mingame.world.entity.Player;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Plat extends JPanel {
	
	public final int WINDOW_WIDTH = 1024;
	public final int WINDOW_HEIGHT = 768;
	
	public final static int TILE_WIDTH = 32;
	
	public final int DEAD_ZONE_WIDTH = 150;
	public final double PLAYER_SPEED_ACCL = 0.2;
	public final double PLAYER_MAX_SPEED = 2.7;
	public final double PLAYER_SPEED_DECAY = 1.3;
	
	public final double GRAVITY = 0.2;
	public final int COLLISION_SEARCH_SPACE = 5;
	public final int PLAYER_REACH = 3;
	
	public final Color SKY_COLOR = new Color(180,200,255);
	public double SCALE = 1;
	
	public boolean[] KEY_STATE = new boolean[256];
	public Image[] RESOURCES = new Image[16];
	
	public World world;
	public Player player;
	
	public Vector cameraPos = new Vector(0,0);
	boolean shouldJump = true;
	private Point hoverTile;
	
	ArrayList<Entity> npe = new ArrayList<Entity>();
	
	public Plat() throws Exception {
		this.setBackground(Color.WHITE);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
		
		loadResources();
		
//		world = World.loadWorld(new File("map.txt"));
		world = World.generateWorld(100, 50);
		player = new Player();
		player.loc = player.loc.add(new Vector(90,90));
		
		this.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent event){
				KEY_STATE[event.getKeyCode()] = true;
			}
			@Override
			public void keyReleased(KeyEvent event){
				KEY_STATE[event.getKeyCode()] = false;
			}
		});
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent event){
				Point p = event.getPoint();
				Vector rel = new Vector(p.x -(-cameraPos.x * SCALE) - WINDOW_WIDTH / 2, p.y -(-cameraPos.y * SCALE) - WINDOW_HEIGHT / 2);
				Vector d = rel.divide(TILE_WIDTH * SCALE);
				Tile t = world.level[(int)d.y][(int)d.x];
				if(event.getButton() == MouseEvent.BUTTON1){
					if(hoverTile != null)
						player.placeTile(t);
				}
				else if(event.getButton() == MouseEvent.BUTTON3){
					if(hoverTile != null)
						player.breakTile(t);
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseMoved(MouseEvent event){
				Point p = event.getPoint();
				Vector rel = new Vector(p.x -(-cameraPos.x * SCALE) - WINDOW_WIDTH / 2, p.y -(-cameraPos.y * SCALE) - WINDOW_HEIGHT / 2);
				Vector d = rel.divide(TILE_WIDTH * SCALE);
				Point prel = player.getRelative();
				
				// Make sure the block is within range
				if(Math.abs(d.x - prel.x) >= PLAYER_REACH || Math.abs(d.y - prel.y) >= PLAYER_REACH)
					hoverTile = null;
				else
					hoverTile = new Point((int)d.x, (int)d.y);
			}
		});
		this.addMouseWheelListener(new MouseWheelListener(){
			@Override
			public void mouseWheelMoved(MouseWheelEvent event) {
				int notches = event.getWheelRotation();
				if(notches < 0)
					SCALE *= 2;
				else
					SCALE /= 2;
			}
		});
	}
	
	public void loadResources() throws IOException{
		RESOURCES[0] = ImageIO.read(PlatFrame.class.getResourceAsStream("/res/grass.png"));
		RESOURCES[1] = ImageIO.read(PlatFrame.class.getResourceAsStream("/res/dirt.png"));
		RESOURCES[2] = ImageIO.read(PlatFrame.class.getResourceAsStream("/res/stone.png"));
	}
	
	public void tick(){
		if(KEY_STATE[KeyEvent.VK_W]){
			if(shouldJump){
				player.vel.y = -7;
				shouldJump = false;
			}
		}
			
		if(KEY_STATE[KeyEvent.VK_A])
			player.vel.x = Math.max(player.vel.x - PLAYER_SPEED_ACCL, -PLAYER_MAX_SPEED);
		if(KEY_STATE[KeyEvent.VK_D])
			player.vel.x = Math.min(player.vel.x + PLAYER_SPEED_ACCL, PLAYER_MAX_SPEED);
		
		// Velocity updates
		player.loc.x += player.vel.x;
		player.loc.y += player.vel.y;
		// Apply gravity
		player.vel.y += GRAVITY;
		
		Point rel = player.getRelative();
		// Check tile collisions
		for(int y = Math.max(0,rel.y - COLLISION_SEARCH_SPACE); y < Math.min(rel.y + COLLISION_SEARCH_SPACE, world.level.length); y++){
			for(int x = Math.max(0,rel.x - COLLISION_SEARCH_SPACE); x < Math.min(rel.x + COLLISION_SEARCH_SPACE, world.level[y].length); x++){
				Rectangle2D tileBounds = new Rectangle2D.Double(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
				if(world.level[y][x].type.isSolid){
					if(player.getBounds().intersects(tileBounds)){
						Vector intd = PhysExtensions.getIntersectionDepth(player.getBounds(), tileBounds);
						if(Math.abs(intd.x) < Math.abs(intd.y)){
							player.loc.x += intd.x;
						}
						else{
							player.loc.y += intd.y;
							player.vel.y = 0;
							// Ground collision
							if(intd.y < 0){
								shouldJump = true;
								player.vel.x /= PLAYER_SPEED_DECAY;
							}
							
						}
					}
				}
				
			}
		}
		
		double scaledDeadPan = DEAD_ZONE_WIDTH / SCALE;
		// Dead zone camera panning
		if(player.loc.y - cameraPos.y > scaledDeadPan)
			cameraPos.y = player.loc.y - HEIGHT - scaledDeadPan + 1;
		else if(player.loc.y - cameraPos.y < -scaledDeadPan)
			cameraPos.y = player.loc.y + scaledDeadPan - 1;
		if(player.loc.x - cameraPos.x < -scaledDeadPan)
			cameraPos.x = player.loc.x + scaledDeadPan;
		else if(player.loc.x - cameraPos.x > scaledDeadPan)
			cameraPos.x = player.loc.x - WIDTH - scaledDeadPan;
		
		for(Entity e : npe){
			e.loc = e.loc.add(e.vel);
		}
		
		// Apply "tick" to tiles
		for(Tile[] ta : world.level){
			for(Tile t : ta){
				t.tick();
			}
		}
		repaint();
	}
	
	public void render(Graphics2D g){
		AffineTransform at = g.getTransform();
		g.setColor(SKY_COLOR);
		g.fillRect(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
		g.translate((int)( -cameraPos.x * SCALE + WINDOW_WIDTH / 2), (int)( -cameraPos.y * SCALE + WINDOW_HEIGHT / 2));
		g.scale(SCALE, SCALE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw tile grid
//		g.setColor(Color.LIGHT_GRAY);
//		for(int y = 0; y < world.level.length; y++){
//			for(int x = 0; x < world.level[y].length; x++){
//				g.drawRect(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
//			}
//		}
		
		Composite origComposite = g.getComposite();
		// Draw each tile
		for(int y = 0; y < world.level.length; y++){
			for(int x = 0; x < world.level[y].length; x++){
				Tile tile = world.level[y][x];
				int opacity = tile.fadeFrame == 0 ? 255 : Math.abs(tile.fadeFrame) * 2;
				float fopacity = opacity / 255.0f;
				switch(tile.type){
				case AIR:
					break;
				case LAVA:
					Color lavaCol = new Color(200, (int)( Math.abs(tile.animationFrame) / 1.5), 20, opacity);
					g.setColor(lavaCol.darker());
					g.fillRect(x * TILE_WIDTH + 2, y * TILE_WIDTH + 2, TILE_WIDTH, TILE_WIDTH);
					g.setColor(lavaCol);
					g.fillRect(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
					break;
				case WALL:
					Color wallCol = new Color(60, 60, 60, opacity);
					g.setColor(wallCol.darker());
					g.fillRect(x * TILE_WIDTH + 3, y * TILE_WIDTH + 3, TILE_WIDTH, TILE_WIDTH);
					g.setColor(wallCol);
					g.fillRect(x * TILE_WIDTH, y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
					break;
				case GRASS:
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fopacity));
					g.drawImage(RESOURCES[0], x * TILE_WIDTH, y * TILE_WIDTH, 
							x * TILE_WIDTH + TILE_WIDTH, y * TILE_WIDTH + TILE_WIDTH, 0, 0, 32, 32, null);
					break;
				case DIRT:
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fopacity));
					g.drawImage(RESOURCES[1], x * TILE_WIDTH, y * TILE_WIDTH, 
							x * TILE_WIDTH + TILE_WIDTH, y * TILE_WIDTH + TILE_WIDTH, 0, 0, 32, 32, null);
					break;
				case STONE:
					g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fopacity));
					g.drawImage(RESOURCES[2], x * TILE_WIDTH, y * TILE_WIDTH, 
							x * TILE_WIDTH + TILE_WIDTH, y * TILE_WIDTH + TILE_WIDTH, 0, 0, 32, 32, null);
					break;
				default:
					break;
				}
				g.setComposite(origComposite);
			}
		}
		// Draw the hover selection box
		if(hoverTile != null){
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(hoverTile.x * TILE_WIDTH, hoverTile.y * TILE_WIDTH, TILE_WIDTH, TILE_WIDTH);
		}
		
		// Draw player
		player.draw(g);
//		g.draw(player.getBounds());
		for(Entity e : npe){
			e.draw(g);
		}
		
		// ALL DRAWING FROM THIS POINT ONWARD WILL BE HUD
		g.setTransform(at);
		g.setColor(new Color(180, 180, 180, 50));
		g.fillRect(10, 15, 400, 50);
		g.fillRect(13, 18, 400, 50);
		g.setColor(Color.GRAY);
		g.drawRect(10, 15, 400, 50);
		g.drawRect(13, 18, 400, 50);
		for(int i = 10; i <= 410; i += 50){
			g.drawLine(i, 15, i, 65);
		}
		g.setColor(Color.CYAN);
		g.fillRect(10, 1, 80, 13);
		g.setColor(Color.BLACK);
		g.drawString("| INVENTORY |", 10, 12);
		g.setColor(Color.WHITE);
		String[] debug = {"scale: " + SCALE,
						"camera: " + cameraPos.toString()};
		for(int i = 0; i < debug.length; i++){
			g.drawString(debug[i], 10, 150 + i * 15);
		}
		g.dispose();
	}
	
	@Override
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		render(g);
	}
}
