package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GameOfLife_Model {

	private boolean[][] board;
	
	public GameOfLife_Model(int xSize, int ySize) {
		this.board = new boolean[ySize][xSize];
	}
	
	public GameOfLife_Model(int xSize, int ySize, ArrayList<Integer[]> positions) {
		this(ySize, xSize);
		initBoard(positions);
	}
	
	public GameOfLife_Model(String filepath){
		ArrayList<Integer[]> positions = new ArrayList<>();
		int x = 0;
		int y = 0;
		
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			
			String line = null;
			
			while((line = br.readLine()) != null){
				
				if(line.startsWith("x")){
					x = Integer.parseInt(line.substring(2));
					continue;
				}
				if(line.startsWith("y")){
					y = Integer.parseInt(line.substring(2));
					continue;
				}
				String[] splitted = line.split(",");
				positions.add(new Integer[]{Integer.parseInt(splitted[0]),Integer.parseInt(splitted[1])});
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.board = new boolean[y][x];
		this.initBoard(positions);
	}
	
	public void saveToFile(String filepath){
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
			
			bw.write("x="+board[0].length+"\n");
			bw.write("y="+board.length+"\n");
			
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if(board[i][j]){
						bw.write(j+","+i+"\n");						
					}
				}
			}
			
			bw.flush();
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initBoard(ArrayList<Integer[]> positions){
		for(Integer[] array : positions){
			this.board[array[1]][array[0]] = true;
		}
	}
	
	private int getNeighbors(int x, int y){
		int neighbors = 0;
		
		for (int i = -1; i <= 1; i++) {
			
			if(y+i >= 0 && y+i < this.board.length){
				if((x-1) >= 0){
					neighbors = board[y+i][x-1] ? neighbors+1 : neighbors;									
				}
				if(x+1 < this.board[0].length){
					neighbors = board[y+i][x+1] ? neighbors+1 : neighbors;					
				}
				if(i != 0){
					neighbors = board[y+i][x] ? neighbors+1 : neighbors;
				}
			}
		}
		return neighbors;
	}
	
	private boolean livesNextCycle(int x, int y){
		
		int neighbors = getNeighbors(x, y);
		
		if(this.board[y][x]){
			if(neighbors == 2 || neighbors == 3){
				return true;
			}else{
				return false;
			}
		}else{
			if(neighbors == 3){
				return true;
			}else{
				return false;
			}
		}
	}
	
	public void nextCycle(){
		
		boolean[][] newBoard = new boolean[this.board.length][this.board[0].length];
		
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				newBoard[i][j] = livesNextCycle(j, i);
			}
		}
		this.board = newBoard;
	}
	
	public void printBoard(){
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if(board[i][j]){
					System.out.print(" ■");
				}else{
					System.out.print(" □");
				}
			}
			System.out.println();
		}
	}
	
	public boolean[][] getBoard(){
		return this.board;
	}
	
	public void resetBoard(int x, int y){
		this.board = new boolean[y][x];
	}
	
	public void resetBoard(){
		this.board = new boolean[this.board.length][this.board[0].length];
	}
	
}
