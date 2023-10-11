import java.awt.Color;

public class PerimeterGoal extends Goal{

	public PerimeterGoal(Color c) {
		super(c);
	}

	@Override
	public int score(Block board) {
		Color[][] flattened = board.flatten();
		int score = 0;

		//check for corners; corners are double scored
		// (but only +1 since we will be checking it again in the following for loops)???? why???
		if (flattened[0][0] == targetGoal){
			score +=1;
		}
		if (flattened[0][flattened.length-1] == targetGoal){
			score +=1;
		}
		if (flattened[flattened.length-1][0] == targetGoal){
			score +=1;
		}
		if (flattened[flattened.length-1][flattened.length-1] == targetGoal){
			score +=1;
		}

		//Check top row and bottom row
		for (int i = 0; i < flattened[0].length; i++){
			if (flattened[0][i] == targetGoal){
				score += 1;}
			if (flattened[flattened.length-1][i] == targetGoal){
				score +=1;}
		}
		//Check left column and right column
		for (int i = 1; i < flattened.length-1; i++){
			if (flattened[i][0] == targetGoal){
				score += 1; }
			if (flattened[i][flattened[0].length-1] == targetGoal){
				score +=1;
			}
		}
		return score;
	}

	@Override
	public String description() {
		return "Place the highest number of " + GameColors.colorToString(targetGoal) 
		+ " unit cells along the outer perimeter of the board. Corner cell count twice toward the final score!";
	}

}
