/**
 * 
 */
package no.tosin.oyetoyan.machinelearning;

import java.util.List;
import java.util.Properties;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author tosindo
 *
 */
public class AlgorithmSuper implements Algorithms{
	
	protected Classifier model;
	protected Instances trainInstances;
	protected Instances testInstances;
	protected Instances validationInstances;
	protected Instances instances;
	protected Evaluation modelEval;
	protected Properties properties;
	private StringBuffer results;
	protected List<Instances> nfoldinstances;
	
	/**
	 * 
	 * @param instances dataset instances
	 * @param n train percentage (between 0.0 and 1.0)
	 * @param properties property set for each train variable
	 */
	public AlgorithmSuper(Instances train, Instances test, double n) {
		
		if(test != null) {
			trainInstances = train;
			testInstances = test;
		} else {
			this.instances = train;
			divideData(n);
		}
	}
	
	private void divideData(double n){

		//set default training size here...if user supply wrong values
		if(n < 0.0 || n >= 1.0)
			n = 0.7;
		
		//int train_size = Math.round((float) n * instances.size());
		Random r = new Random();
		
		int pos = 0;
		int neg = 0;
		for(Instance inst : instances){
			
			if(inst.classValue() == 0.0)
				pos++;
			else
				neg++;
		}
		
		System.out.println("pos="+pos+" | neg="+neg);
		
		int train_pos = Math.round((float) n * pos);
		int train_neg = Math.round((float) n * neg);
		
		System.out.println("n="+n+ " | train_pos="+train_pos+" | train_neg="+train_neg);
		
		Instances posInstances = new Instances(instances);
		posInstances.delete();
		Instances negInstances = new Instances(instances);
		negInstances.delete();
		
		for(Instance inst : instances){
			if(inst.classValue() == 0.0)
				posInstances.add(inst);
			else
				negInstances.add(inst);
		}

		//randomize dataset 
		posInstances.randomize(r);
		negInstances.randomize(r);

		// now add train_size for each class
		// class = 1
		Instances train_posInstances = new Instances(posInstances, 0, train_pos);
		Instances test_posInstances = new Instances(posInstances, train_pos, posInstances.size()-train_pos);
		// class = 0
		Instances train_negInstances = new Instances(negInstances, 0, train_neg);
		Instances test_negInstances = new Instances(negInstances, train_neg, negInstances.size()-train_neg);
		
		// merge the training set and test set for each class
		
		trainInstances = new Instances(train_posInstances);
		trainInstances.addAll(train_negInstances);
		
		testInstances = new Instances(test_posInstances);
		testInstances.addAll(test_negInstances);
		
		System.out.println("train: "+trainInstances.size());
		System.out.println("test: "+testInstances.size());
		
	}
	
	@Override
	public void printModelPerformance(Evaluation modelEval) throws Exception{
		results = new StringBuffer();
		
		String summary = modelEval.toSummaryString("\nResults\n=====\n", false);
		System.out.println(summary);
		System.out.println(modelEval.toMatrixString());
		System.out.println(modelEval.toClassDetailsString());
		results.append(summary);
		results.append("\n"+modelEval.toMatrixString());
		results.append("\n"+modelEval.toClassDetailsString());
		
	}
	
	@Override
	public Instances getTrainInstances() {
		return trainInstances;
	}

	@Override
	public Classifier getModel() {
		
		return model;
	}

	@Override
	public void model() {
		
	}

	@Override
	public Evaluation getModelEval() {
		
		return modelEval;
	}

	/**
	 * @return the results
	 */
	public StringBuffer getResults() {
		return results;
	}

	@Override
	public void crossValidateModel() {
		// TODO Auto-generated method stub
		
	}
	
	protected int saveBestModel(List<Evaluation> modelsEval, List<Classifier> models, boolean useKappa){
		System.out.println("Saving the best model...");
		double recall = 0.0;
		double kappa = -1.0;
		double fmeasure = 0.0;
		for(int i=0; i<modelsEval.size(); i++){
			Evaluation modelEval = modelsEval.get(i);
			recall = Math.max(recall, modelEval.recall(0));
			kappa = Math.max(kappa, modelEval.kappa());
			fmeasure = Math.max(fmeasure, modelEval.fMeasure(1));
		}
		
		int bmodelIndex = -1;
		//System.out.println("recall: "+recall);
		//use kappa
		if(useKappa){
			for(int i=0; i<modelsEval.size(); i++){
				if(modelsEval.get(i).kappa() == kappa){
					model = models.get(i);
					modelEval = modelsEval.get(i);
					bmodelIndex = i;
					break;
				}
			}
		}else{
			for(int i=0; i<modelsEval.size(); i++){
				if(modelsEval.get(i).recall(0) == recall){
					model = models.get(i);
					modelEval = modelsEval.get(i);
					bmodelIndex = i;
					break;
				}
			}
		}
		
		return bmodelIndex;
		
	}

}
