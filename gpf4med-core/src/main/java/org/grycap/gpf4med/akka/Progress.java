package org.grycap.gpf4med.akka;

public class Progress {
	
	private String className;
	private double percent = 0.0;

    public Progress(String className) {
    	this.className = className;
    }
    
    public double getPercent() {
    	return percent;
    }
    
    public void setPercent(double percent) {
    	this.percent = percent;
    }
    
    public String getClassName() {
    	return className;
    }
    
    public String toString() {
    	return String.format("%s", percent) + "%";
    }
    
}
