/**
 * 
 */
package no.tosin.oyetoyan.machinelearning;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * @author tosindo
 *
 */
public interface Algorithms {
	
	public void model();
	
	public void crossValidateModel();
	
	public Classifier getModel();
	
	public Evaluation getModelEval();
	
	public Instances getTrainInstances();
	
	public void printModelPerformance(Evaluation modelEval) throws Exception;
}
