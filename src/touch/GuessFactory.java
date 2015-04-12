package touch;
import java.util.ArrayList;
import java.util.Collections;

import touch.Circle;
import touch.Consts;
import touch.Guess;
import touch.Point;
import touch.Touch;





public abstract class GuessFactory {

	public static Guess avarage(ArrayList<Touch> touches){
		double totX = 0;
		double totY = 0;
		double totRad = 0;

		for (Touch touch : touches){
			totX += touch.getPoint().getX();
			totY += touch.getPoint().getY();
			totRad += touch.getSize();
		}

		double xAv = totX/touches.size();
		double yAv = totY/touches.size();
		double radAv = totRad/touches.size() * Consts.TOUCH_SIZE_FACTOR;
		
		return new Guess(new Circle(new Point(xAv, yAv), radAv), touches.size());
	}


	public static double getDistanceFromCenter(Circle testCircle, Circle guessCircle){
		return Point.distance(testCircle.getCenter(), guessCircle.getCenter());
	}


}
