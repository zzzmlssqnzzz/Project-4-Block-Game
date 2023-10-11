package assignment3;

import java.awt.Color;

public class BlobGoal extends Goal {

	public BlobGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattened = board.flatten();
		boolean[][] visited = new boolean[flattened.length][flattened.length];
		int score = 0;
		for (int i = 0; i < flattened.length; i++) {
			for (int j = 0; j < flattened.length; j++) {
				score += undiscoveredBlobSize(i, j, flattened, visited);

			}
		}
		return score;
	}

	@Override
	public String description() {
		return "Create the largest connected blob of " + GameColors.colorToString(targetGoal)
				+ " blocks, anywhere within the block";
	}


	public int undiscoveredBlobSize(int i, int j, Color[][] unitCells, boolean[][] visited) {
		/*if (i < 0 || j < 0 || i >= unitCells.length || j >= unitCells.length || visited[i][j] || unitCells[i][j] != targetGoal) {
			return 0;
		} else {
			visited[i][j] = true;
			int size = 1 + undiscoveredBlobSize(i - 1, j, unitCells, visited)
					+ undiscoveredBlobSize(i + 1, j, unitCells, visited)
					+ undiscoveredBlobSize(i, j - 1, unitCells, visited)
					+ undiscoveredBlobSize(i, j + 1, unitCells, visited);
			return size;
		}
	}*/
		if (i < 0 || j < 0 || i >= unitCells.length || j >= unitCells.length || visited[i][j] || unitCells[i][j] != targetGoal) {
			return 0;
		}
			int size = 1;
			//check input cell
			if (unitCells[i][j] == targetGoal && !(visited[i][j])) {
				visited[i][j] = true;
				size += undiscoveredBlobSize(i - 1, j, unitCells, visited);
				size += undiscoveredBlobSize(i + 1, j, unitCells, visited);
				size += undiscoveredBlobSize(i, j - 1, unitCells, visited);
				size += undiscoveredBlobSize(i, j + 1, unitCells, visited);
			}
			//check for cell above
			//if (i != 0) {
				//if (unitCells[i - 1][j] == targetGoal && !(visited[i - 1][j])) {
				//	size += undiscoveredBlobSize(i - 1, j, unitCells, visited);
				//}
			//}
			//check for cell below
			//if (i != unitCells.length - 1) {
				//if (unitCells[i + 1][j] == targetGoal && !(visited[i + 1][j])) {
				//	size += undiscoveredBlobSize(i + 1, j, unitCells, visited);
				//}
			//}
			//check cell left to input cell
			//if (j != 0) {
				//if (unitCells[i][j - 1] == targetGoal && !(visited[i][j - 1])) {
					//size += undiscoveredBlobSize(i, j - 1, unitCells, visited);
				//}
			//}
			//check cell right to input cell
			//if (j != unitCells.length - 1) {
				//if (unitCells[i][j + 1] == targetGoal && !(visited[i][j + 1])) {
					//size += undiscoveredBlobSize(i, j + 1, unitCells, visited);
				//}
			//}
			return size;
		}
}
