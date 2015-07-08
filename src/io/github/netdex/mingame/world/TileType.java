package io.github.netdex.mingame.world;

public enum TileType {
	
	WALL('X', true),
	GRASS('G', true),
	DIRT('D', true),
	STONE('S', true),
	LAVA('L', false),
	AIR('.', false);
	
	public char ID;
	public boolean isSolid;
	TileType(char ID, boolean solid){
		this.ID = ID;
		this.isSolid = solid;
	}
	
	public static TileType getTile(char ID){
		for(TileType t : TileType.values())
			if(t.ID == ID)
				return t;
		return TileType.AIR;
	}
}
