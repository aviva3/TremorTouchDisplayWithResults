package idoelad.finalproject.tremortouch.displaywithresults;

import java.util.ArrayList;

import bigtouch.UserParamsBigTouch;

import multitouch.UserParamsMultiTouch;

public class TestPoint {
	private TestCircle targetCircle;
	private ArrayList<TestCircle> testCircles;
	
	public TestPoint(TestCircle targetCircle) {
		this.targetCircle = targetCircle;
		testCircles = new ArrayList<TestCircle>();
	}
	
	public void addTestCircle(TestCircle tc){
		testCircles.add(tc);
	}

	
	public TestCircle getTargetCircle() {
		return targetCircle;
	}

	public void setTargetCircle(TestCircle targetCircle) {
		this.targetCircle = targetCircle;
	}

	public ArrayList<TestCircle> getTestCircles() {
		return testCircles;
	}

	public void setTestCircles(ArrayList<TestCircle> testCircles) {
		this.testCircles = testCircles;
	}


	
	
	
	
	
}
