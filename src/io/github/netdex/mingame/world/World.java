package io.github.netdex.mingame.world;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class World {
	public Tile[][] level;
	
	public World(Tile[][] level){
		this.level = level;
	}
	
	public static World generateWorld(int width, int height){
		Tile[][] map = new Tile[height][width];
		int ground = height / 2;
		for(int x = 0; x < width; x++){
			int change = (int)(Math.random() * 3) - 1;
			ground += change;
			for(int i = 0; i < height; i++){
				if(i < ground)
					map[i][x] = new Tile(TileType.AIR);
				else if(i == ground)
					map[i][x] = new Tile(TileType.GRASS);
				else if(i < ground + 5)
					map[i][x] = new Tile(TileType.DIRT);
				else
					map[i][x] = new Tile(TileType.STONE);
			}
		}
		return new World(map);
	}
	
	public static World loadWorld(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		int dimx = Integer.parseInt(br.readLine());
		int dimy = Integer.parseInt(br.readLine());
		Tile[][] map = new Tile[dimy][dimx];
		for(int i = 0; i < dimy; i++){
			char[] temp = br.readLine().toCharArray();
			for(int j = 0; j < temp.length; j++){
				map[i][j] = new Tile(TileType.getTile(temp[j]));
			}
		}
		br.close();
		return new World(map);
	}
}
