package org.grycap.gpf4med.akka;

public class Progress {
	
	public String className;
	public double percent = 0.0;

    public Progress(String className) {
    	this.className = className;
    }
    
    public double getPercent() {
    	return percent;
    }
    
    public void setPercent(double percent) {
    	this.percent = percent;
    }
    
    public String toString() {
    	return className + String.format("\t%s", percent) + "%";
    }
    
}
