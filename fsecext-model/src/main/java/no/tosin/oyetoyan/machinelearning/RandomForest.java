package no.tosin.oyetoyan.machinelearning;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class RandomForest extends AlgorithmSuper {
	
	public RandomForest(Instances train, Instances test, double n) {
		super(train, test, n);
	}
	
	@Override
	public void model(){
		//train a model
		model = new weka.classifiers.trees.RandomForest();
		try {
			model.buildClassifier(trainInstances);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//test model		
		try 
		{
			modelEval = new Evaluation(trainInstances);
			
			modelEval.evaluateModel(model, testInstances);	//test with the remaining unseen data
			
			System.out.println("Train Instances = "+trainInstances.size());
			System.out.println("Test Instances = "+testInstances.size());

			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
	
	@Override
	public void crossValidateModel() {
		
		List<Evaluation> modelsEval = new ArrayList<Evaluation>(); 
		List<Classifier> models = new ArrayList<Classifier>();
		Classifier model;
		Evaluation modelEval = null;
		// each time use 9 folds for training and hold one fold for testing
		Instances train = new Instances(nfoldinstances.get(0));
		Instances test = new Instances(nfoldinstances.get(0));
		
		List<Instances> copy;
		
		for(int i=0; i<nfoldinstances.size(); i++) {
			train.delete();			// must be empty
			test.delete();			// must be empty
			System.out.println("Test instance before = "+test.numInstances());
			test.addAll(nfoldinstances.get(i));
			copy = new ArrayList<Instances>(nfoldinstances);
			copy.remove(i);
			for(int k=0; k<copy.size(); k++) {
				train.addAll(copy.get(k));
			}
			
			System.out.println("Total Instances = "+trainInstances.numInstances());
			System.out.println("Train dataset size = "+train.numInstances());
			System.out.println("Test dataset size = "+test.numInstances());
			//train a model
			model = new weka.classifiers.trees.RandomForest();
			try {
				model.buildClassifier(train);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//test model		
			try 
			{
				modelEval = new Evaluation(train);
				modelEval.evaluateModel(model, test);	//test with the unseen 1 fold data
								
			} catch (Exception e) {
				e.printStackTrace();
			}
			// save model/Eval
			models.add(model);
			modelsEval.add(modelEval);
		}
		
		// we use the best model
		this.saveBestModel(modelsEval, models, true);
				
	}
}
