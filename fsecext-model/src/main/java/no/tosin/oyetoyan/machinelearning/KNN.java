/**
 * 
 */
package no.tosin.oyetoyan.machinelearning;


import weka.classifiers.Evaluation;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;

/**
 * @author tdoy
 *
 */
public class KNN extends AlgorithmSuper {

	/**
	 * @param instances
	 * @param n
	 */
	public KNN(Instances train, Instances test, double n) {
		super(train, test, n);
	}
	
	@Override
	public void model() {
		
		model = new IBk();
		try {
			model.buildClassifier(trainInstances);
			//System.out.println(nbModel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//test model
		
		try 
		{
			modelEval = new Evaluation(trainInstances);
			
			modelEval.evaluateModel(model, testInstances);	//test with the remaining unseen data
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
