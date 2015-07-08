package io.github.netdex.mingame.world;


public class Tile {
	
	public TileType type;
	public byte tileData;	// Stores tiles' magic value for tile specific tasks
	public byte animationFrame;
	public byte fadeFrame;
	
	public Tile(TileType type){
		this.type = type;
	}
	
	public void startBreak(){
		if(fadeFrame == 0 && type != TileType.AIR){
			fadeFrame = 126;
		}
	}
	public void startCreate(){
		if(fadeFrame == 0 && type == TileType.AIR){
			type = TileType.WALL;
			fadeFrame = -1;
		}
	}
	public void tick(){
		switch(type){
		case AIR:
			break;
		case LAVA:
			animationFrame += 2;
			break;
		case WALL:
			breakCheck();
			break;
		case GRASS:
			breakCheck();
			break;
		case DIRT:
			breakCheck();
			break;
		case STONE:
			breakCheck();
			break;
		default:
			break;
		
		}
		if(fadeFrame > 1)
			fadeFrame -= 5;
		if(fadeFrame < 0 && fadeFrame > -126)
			fadeFrame -= 5;
	}
	private void breakCheck(){
		if(fadeFrame == -126){
			fadeFrame = 0;
		}
		if(fadeFrame == 1){
			type = TileType.AIR;
			fadeFrame = 0;
		}
	}
}
