package idoelad.finalproject.tremortouch.displaywithresults;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;

import fixtouch.Circle;
import fixtouch.Point;
import fixtouch.Test;
import fixtouch.Touch;
import fixtouch.User;

import multitouch.MultiTouch;
import multitouch.UserParamsMultiTouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import bigtouch.BigTouch;
import bigtouch.UserParamsBigTouch;

public class DrawingView extends View implements OnTouchListener{

	private Paint circlesPaint;
	private Paint textPaint;
	private String resultsFilePath;
	private String circlesFilePath;
	private ArrayList<TestPoint> testPoints;
	private TestPoint currTestPoint;
	private ListIterator<TestPoint> testPointsIter;
	private Test currTest;
	private ListIterator<Test> testsIter;
	private User user;

	private Context context;
	///
	private ArrayList<Test> tests;
	private UserParamsBigTouch upBig;
	private UserParamsMultiTouch upMulti;

	private float x1,x2;
	private Random rand;
	static final int MIN_DISTANCE = 20;


	public DrawingView(Context context, String fileSelected, String circlesFilePath) throws IOException {
		super(context);
		this.resultsFilePath = fileSelected;
		this.circlesFilePath = circlesFilePath;
		this.context = context;
		this.setOnTouchListener(this);
		rand = new Random();

		circlesPaint = new Paint();
		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		createTestPoints();
		createUserFromTestPoints();
		testPointsIter = testPoints.listIterator();
		testsIter = tests.listIterator();
		if (testPointsIter.hasNext()){
			currTestPoint = testPointsIter.next();
			currTest = testsIter.next();
		}

	}


	private void createTestPoints() {
		testPoints = new ArrayList<TestPoint>();
		File resultsFile = new File(resultsFilePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(resultsFile));
			String line = br.readLine(); //Skip first line

			int lastShapeId = 0;
			int currShapeId;
			TestPoint currTestPoint = null;
			while ((line = br.readLine()) != null && !line.startsWith("TPS")) {
				String[] lineParts = line.split(",");
				currShapeId = Integer.parseInt(lineParts[0]);
				if (currShapeId != lastShapeId){
					if (currTestPoint != null){
						testPoints.add(currTestPoint);
					}
					currTestPoint = new TestPoint(getTatgetCircle(Integer.parseInt(lineParts[0])));
					TestCircle tc = new TestCircle(Float.parseFloat(lineParts[6]), Float.parseFloat(lineParts[7]), Float.parseFloat(lineParts[9]), Float.parseFloat(lineParts[8]), Integer.parseInt(lineParts[1]),Integer.parseInt(lineParts[3]),lineParts[4],Long.parseLong(lineParts[5]),Integer.parseInt(lineParts[2]));
					currTestPoint.addTestCircle(tc);
					lastShapeId = currShapeId;
				}
				else{
					TestCircle tc = new TestCircle(Float.parseFloat(lineParts[6]), Float.parseFloat(lineParts[7]), Float.parseFloat(lineParts[9]), Float.parseFloat(lineParts[8]), Integer.parseInt(lineParts[1]),Integer.parseInt(lineParts[3]),lineParts[4],Long.parseLong(lineParts[5]),Integer.parseInt(lineParts[2]));
					currTestPoint.addTestCircle(tc);
				}
			}

			if (currTestPoint != null){
				testPoints.add(currTestPoint);
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private void createUserFromTestPoints() throws IOException {
		tests = new ArrayList<Test>();
		user = new User("tmp");
		for (TestPoint tp : testPoints){
			Test t = new Test(new Circle(new Point(tp.getTargetCircle().getX(), tp.getTargetCircle().getY()), tp.getTargetCircle().getRadius()), testCirclesToTouches(tp.getTestCircles()), null);
			tests.add(t);
		}
		user.setTests(tests);
		user.setUserParamsBigTouch(BigTouch.getBigTouchParams(tests));
		user.setUserParamsMultiTouch(MultiTouch.getMultiTouchParams(tests));
	}

	private ArrayList<Touch> testCirclesToTouches(ArrayList<TestCircle> testCircles){
		ArrayList<Touch> touches = new ArrayList<Touch>();
		int firstPointerId = testCircles.get(0).getPointerId();
		for (TestCircle tc : testCircles){
//			int pointerId = tc.getPointerId() - firstPointerId +1;
			int pointerId = tc.getPointerId();
			touches.add(new Touch(new Point(tc.getX(), tc.getY()), tc.getType(), tc.getRadius()/60.0, tc.getPressure(), tc.getTimeSinceStart(), tc.getNumOfPointers(), pointerId));
		}
		return touches;
	}

	private TestCircle getTatgetCircle(int shapeId) throws NumberFormatException, IOException {
		File circlesFile = new File(circlesFilePath);
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(circlesFile));
		String line;
		while ((line = br.readLine()) != null){
			String[] lineParts = line.split(",");
			if (Integer.parseInt(lineParts[0]) == shapeId){
				br.close();
				return new TestCircle(Float.parseFloat(lineParts[1]),Float.parseFloat(lineParts[2]),(float)(Float.parseFloat(lineParts[3])*0.7));
			}
		}	
		br.close();
		return null;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		//Clear canvas
		canvas.drawColor(Color.BLACK);

		//Set paint general properties
		circlesPaint.setAntiAlias(true);
		
		//Draw userParams
		textPaint.setTextSize(15f);
		UserParamsBigTouch upBig = user.getUserParamsBigTouch();
		UserParamsMultiTouch upMulti = user.getUserParamsMultiTouch();
		String paramsBig = "BIG: wAvg="+d2s(upBig.getwAvg())+" | "+"wDown="+d2s(upBig.getwDown())+" | "+"wPressure="+d2s(upBig.getwPressure())+" | "+"wRadius="+d2s(upBig.getwRadius())+" | "+"wTime="+d2s(upBig.getwTime());
		String paramsMulti ="MULTI: wFirst="+d2s(upMulti.getwFirst())+" | "+"wMaxPressure="+d2s(upMulti.getwMaxPressure())+" | "+"wMaxTime="+d2s(upMulti.getwMaxTime())+" | "+"wStructure="+d2s(upMulti.getwStructure());
		canvas.drawText(paramsBig, 0, 20, textPaint);
		canvas.drawText(paramsMulti, 0, 50, textPaint);
		
		//Draw target circle
		circlesPaint.setColor(Color.MAGENTA);
		circlesPaint.setAlpha(255);
		TestCircle tc = currTestPoint.getTargetCircle();
		canvas.drawCircle(tc.getX(), tc.getY(), tc.getRadius(), circlesPaint);

		//Draw touches
		int lastTouchId = -1;
		int currTouchId;
				
		for (TestCircle circle : currTestPoint.getTestCircles()){
			currTouchId = circle.getTouchId();
			if (currTouchId != lastTouchId){
				circlesPaint.setColor(getColorFromTouchId(currTouchId));
				lastTouchId = currTouchId;
			}
			
			circlesPaint.setAlpha(circle.getAlpha());
			canvas.drawCircle(circle.getX(), circle.getY(), circle.getRadius(), circlesPaint);
		}
		
		//Draw target circle
		circlesPaint.setColor(Color.MAGENTA);
		circlesPaint.setAlpha(30);
		TestCircle tc2 = currTestPoint.getTargetCircle();
		canvas.drawCircle(tc2.getX(), tc2.getY(), tc2.getRadius(), circlesPaint);

		//Draw Guess
		circlesPaint.setColor(Color.YELLOW);
		circlesPaint.setAlpha(255);
		ArrayList<Touch> fTouches = MultiTouch.filterTouchesByFinger(currTest.getTouches(), MultiTouch.guessFingure(currTest.getTouches(), user.getUserParamsMultiTouch()));
		Circle g = BigTouch.guessCircleBigTouch(fTouches, user.getUserParamsBigTouch());
		canvas.drawCircle((float)g.getCenter().getX(),(float)g.getCenter().getY(),(float)g.getRadius(), circlesPaint);

		//Draw times
		textPaint.setTextSize(25f);
		int currPointerId;
		ArrayList<Integer> pointers = null;
		lastTouchId = -1;
		for (TestCircle circle : currTestPoint.getTestCircles()){
			currTouchId = circle.getTouchId();
			if (currTouchId != lastTouchId){
				pointers = new ArrayList<Integer>();
				lastTouchId = currTouchId;
			}
			currPointerId = circle.getPointerId();
			if (!pointers.contains(currPointerId)){
				canvas.drawText(String.valueOf(circle.getTimeSinceStart()),circle.getX(), circle.getY(), textPaint);
				pointers.add(currPointerId);
			}
		}
	}


	private int getColorFromTouchId(int touchId){
		rand.setSeed(touchId*1000);
		int r = rand.nextInt(255);
		int g = rand.nextInt(255);
		int b = rand.nextInt(255);
		return Color.rgb(r,g,b);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			x1 = event.getX();                         
			break;         
		case MotionEvent.ACTION_UP:
			x2 = event.getX();
			float deltaX = x2 - x1;
			if (Math.abs(deltaX) > MIN_DISTANCE && deltaX < 0)
			{
				if (testPointsIter.hasPrevious() && testsIter.hasPrevious()){
					currTestPoint = testPointsIter.previous();
					currTest = testsIter.previous();
					invalidate();
				}
				else{
					Toast.makeText(context, "< No more shapes", Toast.LENGTH_SHORT).show();
				}
			}
			else if (Math.abs(deltaX) > MIN_DISTANCE && deltaX > 0)
			{
				if (testPointsIter.hasNext() && testsIter.hasNext()){
					currTestPoint = testPointsIter.next();
					currTest = testsIter.next();
					invalidate();
				}
				else{
					Toast.makeText(context, "No more shapes >", Toast.LENGTH_SHORT).show();
				}
			}     
			break;   
		}           
		return true;
	}

	private static String d2s(double d){
		String s = String.format("%.3f",d);
		return s;
	}

}
