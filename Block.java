package assignment3;

import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

public class Block {

	private int xCoord;
	private int yCoord;
	private int size; // height/width of the square
	private int level; // the root (outer most block) is at level 0
	private int maxDepth;
	private Color color;
	private Block[] children; // {UR, UL, LL, LR}

	public static Random gen = new Random(2);


	/*
	 * These two constructors are here for testing purposes.
	 */
	public Block() {
	}

	public Block(int x, int y, int size, int lvl, int maxD, Color c, Block[] subBlocks) {
		this.xCoord = x;
		this.yCoord = y;
		this.size = size;
		this.level = lvl;
		this.maxDepth = maxD;
		this.color = c;
		this.children = subBlocks;
	}


	/*
	 * Creates a random block given its level and a max depth.
	 *
	 * xCoord, yCoord, size, and highlighted should not be initialized
	 * (i.e. they will all be initialized by default)
	 */
	public Block(int lvl, int maxDepth) {
		if (lvl > maxDepth){
			throw new IllegalArgumentException("Level is greater than maxDepth");
		}
		if (lvl < 0){
			throw new IllegalArgumentException("Level can not be negative");
		}
		if (maxDepth < 0 || maxDepth == 0){
			throw new IllegalArgumentException("maxDepth can not be negative or the top level");
		}
		this.level = lvl;
		this.maxDepth = maxDepth;
		double randInt = gen.nextDouble(1);

		if (lvl == maxDepth){
			int colorIndex = gen.nextInt(4);
			color = GameColors.BLOCK_COLORS[colorIndex];
			children = new Block[0];
		}
		else{
			if (lvl < maxDepth && randInt < Math.exp(-0.25 * lvl)) {
				this.children = new Block[4];
				for (int b = 0; b < children.length; b++) {
					this.children[b] = new Block(lvl + 1, maxDepth);
				}
			} else {
				this.children = new Block[0];
				int colorIndex = gen.nextInt(4);
				color = GameColors.BLOCK_COLORS[colorIndex];
			}
		}
	}


	/*
	 * Updates size and position for the block and all of its sub-blocks, while
	 * ensuring consistency between the attributes and the relationship of the
	 * blocks.
	 *
	 *  The size is the height and width of the block. (xCoord, yCoord) are the
	 *  coordinates of the top left corner of the block.
	 */
	public boolean checkSize(int s) {
		this.size = s;
		if (s <= 0) {
			return false;
		}
		for (int i = 0; i < maxDepth - level; i++) {
			if (s % 2 != 0) {
				return false;
			}
			s = s / 2;
		}
		return true;
	}

	public void updateSizeAndPosition(int size, int xCoord, int yCoord) {
		if (!(checkSize(size))) {
			throw new IllegalArgumentException("Invalid size input:" + size);
		}
		this.size = size;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		if (children.length == 0) {
			return;
		}
		int subSize = size / 2;
		children[0].updateSizeAndPosition(subSize, xCoord + subSize, yCoord); //Upper Right
		children[1].updateSizeAndPosition(subSize, xCoord, yCoord); //Upper Left
		children[2].updateSizeAndPosition(subSize, xCoord, yCoord + subSize); // Lower Left
		children[3].updateSizeAndPosition(subSize, xCoord + subSize, yCoord + subSize); // Lower Right

	}


	/*
	 * Returns a List of blocks to be drawn to get a graphical representation of this block.
	 *
	 * This includes, for each undivided Block:
	 * - one BlockToDraw in the color of the block
	 * - another one in the FRAME_COLOR and stroke thickness 3
	 *
	 * Note that a stroke thickness equal to 0 indicates that the block should be filled with its color.
	 *
	 * The order in which the blocks to draw appear in the list does NOT matter.
	 */
	public ArrayList<BlockToDraw> getBlocksToDraw() {
		ArrayList<BlockToDraw> blocksToDraw = new ArrayList<BlockToDraw>();
		Color frame = GameColors.FRAME_COLOR;
		if (children.length == 0) {
			BlockToDraw blockColor = new BlockToDraw(color, xCoord, yCoord, size, 0);
			blocksToDraw.add(blockColor);

			BlockToDraw blockFrame = new BlockToDraw(frame, xCoord, yCoord, size, 3);
			blocksToDraw.add(blockFrame);
		} else {
			for (int i = 0; i < children.length; i++) {
				blocksToDraw.addAll(children[i].getBlocksToDraw());
			}
		}
		return blocksToDraw;
		//return null;
	}

	/*
	 * This method is provided and you should NOT modify it.
	 */
	public BlockToDraw getHighlightedFrame() {
		return new BlockToDraw(GameColors.HIGHLIGHT_COLOR, this.xCoord, this.yCoord, this.size, 5);
	}


	/*
	 * Return the Block within this Block that includes the given location
	 * and is at the given level. If the level specified is lower than
	 * the lowest block at the specified location, then return the block
	 * at the location with the closest level value.
	 *
	 * The location is specified by its (x, y) coordinates. The lvl indicates
	 * the level of the desired Block. Note that if a Block includes the location
	 * (x, y), and that Block is subdivided, then one of its sub-Blocks will
	 * contain the location (x, y) too. This is why we need lvl to identify
	 * which Block should be returned.
	 *
	 * Input validation:
	 * - this.level <= lvl <= maxDepth (if not throw exception)
	 * - if (x,y) is not within this Block, return null.
	 */
	// TODO: DoubleCheck with people
	public Block getSelectedBlock(int x, int y, int lvl) {
		if (lvl < level || lvl > maxDepth) {
			throw new IllegalArgumentException("Invalid level:" + lvl);
		}
		if (xCoord > x || xCoord + size < x || yCoord > y || yCoord + size < y) {
			return null;
		}
		if (level >= lvl || children.length == 0) {
			return this;
		}
		for (int i = 0; i < children.length; i++) {
			if (x <= children[i].xCoord + size && y<= children[i].yCoord + size){
				Block selected = children[i].getSelectedBlock(x, y, lvl);
				if (selected != null) {
					return selected;
				}
			}
		}
		return this;
	}


	/*
	 * Swaps the child Blocks of this Block. 
	 * If input is 1, swap vertically. If 0, swap horizontally. 
	 * If this Block has no children, do nothing. The swap 
	 * should be propagate, effectively implementing a reflection
	 * over the x-axis or over the y-axis.
	 * 
	 */
	//TODO: Still not working
	public void reflect(int direction) {
		//if (direction != 0 || direction != 1) {
			//throw new IllegalArgumentException("Invalid direction:" + direction);
		//}
		//if (children.length !=0) {

		if (direction == 0) {
			if (children.length != 0) {
				Block child1, child2, child3, child4;
				child1 = this.children[0];
				child2 = this.children[1];
				child3 = this.children[2];
				child4 = this.children[3];
				children[0] = child4;
				children[1] = child3;
				children[2] = child2;
				children[3] = child1;
			}
		}
		else if (direction == 1){
			if (children.length !=0){
				Block child1, child2, child3, child4;
				child1 = this.children[0];
				child2 = this.children[1];
				child3 = this.children[2];
				child4 = this.children[3];
				children[0] = child2;
				children[1] = child1;
				children[2] = child4;
				children[3] = child3;
			}
		}
		else{
			throw new IllegalArgumentException("Invalid direction:" + direction);
		}

		for (int i = 0; i < children.length; i++) {
			children[i].reflect(direction);}

		this.updateSizeAndPosition(size, xCoord, yCoord);
	}
 

 
	/*
	 * Rotate this Block and all its descendants. 
	 * If the input is 1, rotate clockwise. If 0, rotate 
	 * counterclockwise. If this Block has no children, do nothing.
	 */
	public void rotate(int direction) {
		//if (direction != 0 || direction != 1) {
			//throw new IllegalArgumentException("Invalid direction:" + direction);
		//}
		if (direction == 0) {
			if (children.length != 0) {
				Block child1, child2, child3, child4;
				child1 = children[0];
				child2 = children[1];
				child3 = children[2];
				child4 = children[3];
				children[0] = child4;
				children[1] = child1;
				children[2] = child2;
				children[3] = child3;

			}
		}
		else if (direction == 1) {
			if (children.length != 0) {
				Block child1, child2, child3, child4;
				child1 = children[0];
				child2 = children[1];
				child3 = children[2];
				child4 = children[3];
				children[0] = child2;
				children[1] = child3;
				children[2] = child4;
				children[3] = child1;
			}
		}
		else {
			throw new IllegalArgumentException("Invalid direction:"+direction);
		}

		for (int i = 0; i < children.length; i++) {
			children[i].rotate(direction);}

		this.updateSizeAndPosition(size, xCoord, yCoord);
	}
 


	/*
	 * Smash this Block.
	 * 
	 * If this Block can be smashed,
	 * randomly generate four new children Blocks for it.  
	 * (If it already had children Blocks, discard them.)
	 * Ensure that the invariants of the Blocks remain satisfied.
	 * 
	 * A Block can be smashed iff it is not the top-level Block 
	 * and it is not already at the level of the maximum depth.
	 * 
	 * Return True if this Block was smashed and False otherwise.
	 * 
	 */
	public boolean smash() {
		if (level == 0 || level == maxDepth){
			return false;
		}
		else{
			children = new Block[4];
			for (int i = 0; i < 4; i++){
				children[i] = new Block(level+1, maxDepth);
				}
			this.updateSizeAndPosition(size, xCoord, yCoord);
			return true;
		}
	}
 
 
	/*
	 * Return a two-dimensional array representing this Block as rows and columns of unit cells.
	 * 
	 * Return and array arr where, arr[i] represents the unit cells in row i, 
	 * arr[i][j] is the color of unit cell in row i and column j.
	 * 
	 * arr[0][0] is the color of the unit cell in the upper left corner of this Block.
	 */
	public Color[][] flatten() {
		int maxSize = (int) Math.pow(2, maxDepth - level);
		Color[][] grid = new Color [maxSize][maxSize];

		if (children.length == 0){
			for (int i = 0; i < maxSize; i++){
				for (int j = 0; j < maxSize; j++){
					grid[i][j] = color;
				}
			}
		}
		else{
			Color [][] upperRight = children[0].flatten();
			Color [][] upperLeft = children[1].flatten();
			Color [][] lowerLeft = children[2].flatten();
			Color [][] lowerRight = children[3].flatten();

			int subSize = (int) Math.pow(2, maxDepth - level - 1);
			for (int i = 0; i < maxSize; i++){
				for (int j = 0; j < maxSize; j++){
					if (i < subSize && j < subSize){
						grid[i][j] = upperLeft[i][j];
					}
					else if (i < subSize && j >= subSize){
						grid[i][j] = upperRight[i][j - subSize];
					}
					else if (i >= subSize && j < subSize){
						grid[i][j] = lowerLeft[i - subSize][j];
					}
					else{
						grid[i][j] = lowerRight[i - subSize][j - subSize];
					}
				}
			}
		}
		return grid;
	}


	// These two get methods have been provided. Do NOT modify them.
	public int getMaxDepth() {
		return this.maxDepth;
	}
 
	public int getLevel() {
		return this.level;
	}


	/*
	 * The next 5 methods are needed to get a text representation of a block. 
	 * You can use them for debugging. You can modify these methods if you wish.
	 */
	public String toString() {
		return String.format("pos=(%d,%d), size=%d, level=%d", this.xCoord, this.yCoord, this.size, this.level);
	}

	public void printBlock() {
		this.printBlockIndented(0);
	}

	private void printBlockIndented(int indentation) {
		String indent = "";
		for (int i=0; i<indentation; i++) {
			indent += "\t";
		}

		if (this.children.length == 0) {
			// it's a leaf. Print the color!
			String colorInfo = GameColors.colorToString(this.color) + ", ";
			System.out.println(indent + colorInfo + this);   
		} 
		else {
			System.out.println(indent + this);
			for (Block b : this.children)
				b.printBlockIndented(indentation + 1);
		}
	}
 
	private static void coloredPrint(String message, Color color) {
		System.out.print(GameColors.colorToANSIColor(color));
		System.out.print(message);
		System.out.print(GameColors.colorToANSIColor(Color.WHITE));
	}

	public void printColoredBlock(){
		Color[][] colorArray = this.flatten();
		for (Color[] colors : colorArray) {
			for (Color value : colors) {
				String colorName = GameColors.colorToString(value).toUpperCase();
				if(colorName.length() == 0){
					colorName = "\u2588";
				}
				else{
					colorName = colorName.substring(0, 1);
				}
				coloredPrint(colorName, value);
			}
			System.out.println();
		}
	}
}