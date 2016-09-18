package gameBoard;

import java.util.Random;

import tetrads.Alpha;
import tetrads.Gamma;
import tetrads.LeftSnake;
import tetrads.RightSnake;
import tetrads.Square;
import tetrads.Straight;
import tetrads.TTurn;
import tetrads.Tetrad;
import tetrads.Tetrads;

public class GameBoard {
	private Tetrad hold;
	private Tetrad controlling;
	private Tetrad queue;
	private final int MAX_Y = 22;
	private final int MAX_X = 10;
	private boolean canHold;
	private boolean[][] field;
	private Tetrads[][] typeField;
	private int score;
	private boolean running;
	private int level;
	private int linesCleard;
	
	private Random rand;
	
	public GameBoard() {
		hold = null;
		canHold = true;
		controlling = null;
		rand = new Random();
		queue = getRandomTetrad();
		field = new boolean[MAX_Y][MAX_X];
		typeField = new Tetrads[MAX_Y][MAX_X];
		score = 0;
		running = true;
		level = 0;
		linesCleard = 0;
	}
	
	private Tetrad getRandomTetrad() {
		switch (rand.nextInt(7)) {
		case 0: 
			return new Alpha();
		case 1:
			return new Gamma();
		case 2:
			return new LeftSnake();
		case 3:
			return new RightSnake();
		case 4:
			return new Square();
		case 5:
			return new Straight();
		case 6:
			return new TTurn();
		default:
			throw new RuntimeException();
		}
	}
	
	private void spawnNew() {
		controlling = queue;
		if (!checkValidState(0, 0) && !checkValidState(0, 1)) {
			running = false;
		} else {
			queue = getRandomTetrad();
			canHold = true;
		}
	}
	
	public void hold() {
		if (canHold) {
			if (hold != null) {
				Tetrad temp = hold;
				hold = controlling;
				controlling = temp;
				
			} else {
				hold = controlling;
				controlling = queue;
				queue = getRandomTetrad();
			}
			canHold = false;
		}
	}
	
	private boolean checkValidState(int deltaX, int deltaY) {
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				if ((controlling.getXPos() + col + deltaX > MAX_X ||
					 controlling.getYPos() + row + deltaY > MAX_Y ||
					 controlling.getXPos() + col + deltaX < 0 ||
					 controlling.getYPos() + row + deltaY < 0) &&
					 controlling.colide(controlling.getXPos() + col, controlling.getYPos() + row)) {
					return false;
				} if (field[controlling.getYPos() + row + deltaY][controlling.getXPos() + col + deltaX] &&
					  controlling.colide(controlling.getXPos() + col, controlling.getYPos() + row)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void update() {
		if (running) {
			if (checkValidState(0, 1)) {
				controlling.fall();
			} else {
				System.out.println("hit the ground, start timer");
			}
		}
	}
	
	public void place() {
		boolean[][] orientation = controlling.getOrientation();
		int x = controlling.getXPos();
		int y = controlling.getYPos();
		Tetrads type = controlling.getType();
		for (int row = 0; row < orientation.length; row++) {
			for (int col = 0; col < orientation[0].length; col++) {
				field[y + row][x + col] = orientation[row][col];
				typeField[y + row][x + col] = type;
			}
		}
		checkTetris();
		spawnNew();
	}

	private void checkTetris() {
		int rowsRemoved = 0;
		for (int row = 0; row < MAX_Y; row++) {
			boolean tetrisOnRow = true;
			for (int col = 0; col < MAX_X; col++) {
				if (!field[row][col]) {
					tetrisOnRow = false;
					break;
				}
			}
			if (tetrisOnRow) {
				moveDown(row);
				rowsRemoved++;
			}
		}
		linesCleard += rowsRemoved;
		if (linesCleard >= 10) {
			level++;
			linesCleard = linesCleard % 10;
		}
		switch (rowsRemoved) {
		case 0:
			return;
		case 1:
			score += 40 * (level + 1);
			return;
		case 2:
			score += 100 * (level + 1);
			return;
		case 3:
			score += 300 * (level + 1);
			return;
		case 4:
			score += 1200 * (level + 1);
			return;
		default:
			throw new RuntimeException("Algorithm maninfuntion. More than 4 lines cleard");
		}
	}

	private void moveDown(int row) {
		for (; row >= 0; row--) {
			for (int col = 0; col < MAX_X; col++) {
				if (row != 0) {
					field[row][col] = field[row - 1][col];
					typeField[row][col] = typeField[row - 1][col];
			
				} else {
					field[row][col] = false;
					typeField[row][col] = null;
				}
			}
		}
		
	}
	
	public void moveLeft() {
		if (checkValidState(-1, 0)) {
			controlling.moveLeft();
		}
	}
	
	public void moveRight() {
		if (checkValidState(1, 0)) {
			controlling.moveRight();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < 2; row++) {
			for (int col = 0; col < MAX_X; col++) {
				sb.append(field[row][col] ? "o" : " ");
			}
			sb.append("\n");
		}
		for (int col = 0; col < MAX_X; col++) {
			sb.append("-");
		}
		sb.append("\n");
		for (int row = 2; row < MAX_Y; row++) {
			for (int col = 0; col < MAX_X; col++) {
				sb.append(field[row][col] ? "o" : " ");
			}
			sb.append("\n");
		}
		for (int col = 0; col < MAX_X; col++) {
			sb.append("-");
		}
		
		return sb.toString();
	}
	
	public int getScore() {
		return score;
	}
	
	public int getLevel() {
		return level;
	}
	
	public Tetrad getQueue() {
		return queue;
	}
	
	public Tetrad getControlling() {
		return controlling;
	}
	
	public Tetrads[][] getField() {
		return typeField;
	}
	
	public boolean isRuning() {
		return running;
	}
	
	public static void main(String[] args) {
		GameBoard g = new GameBoard();
		//System.out.println(g);
		g.spawnNew();
		g.update();
		g.update();
		g.update();
		g.place();
		System.out.println(g);
		for (int i = 0; i < 23; i++) {
			
		}
	}
}
