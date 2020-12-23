/**
 * 
 */
package no.tosin.oyetoyan.experiment;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import no.tosin.oyetoyan.keywords.PropertyKeys;
import no.tosin.oyetoyan.machinelearning.Algorithms;
import no.tosin.oyetoyan.machinelearning.KNN;
import no.tosin.oyetoyan.machinelearning.LogisticRegression;
import no.tosin.oyetoyan.machinelearning.NaiveBayes;
import no.tosin.oyetoyan.machinelearning.RandomForest;
import no.tosin.oyetoyan.machinelearning.SupportVectorMachine;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * @author tosindo
 *
 */
public class Experiments {

	private List<Classifier> models;
	private List<Evaluation> modelsEval;
	private Classifier bestModel;
	private Evaluation bestModelEval;
	private Instances trainInstances;
	private Instances testInstances = null;
	
	private static final String RANDOMFOREST = "RF";
	private static final String NAIVEBAYES = "NB";
	private static final String SVM = "SVM";
	private static final String LR = "LR";
	private static final String KNN = "KNN";
	
	private List<Performance> metricsExprs;
	private StringBuffer rawMetrics;
	
	private Properties properties;
	private boolean print;
	private String testfile;
	
	public Experiments(Properties properties, boolean print, boolean includesec) throws Exception{
		this.properties = properties;
		this.print = print;

		String trainfile = properties.getProperty(PropertyKeys.DATA_FILE);
		double ratio = Double.valueOf(properties.getProperty(PropertyKeys.RATIO));

		testfile = properties.getProperty(PropertyKeys.DATA_TESTFILE);
		trainInstances = Util.getARFFInstances(properties, trainfile, includesec);
		
		
		preProcess(ratio); 
		
		trainModel();
	}
	
	/**
	 * 
	 * @param properties configuration settings
	 * @param print print output to console
	 * @param control include control terms
	 * @param attack include attack terms
	 * @param asset include asset terms
	 * @param indirect include indirect terms
	 * @param dict should dictionary list be used or bag of words?
	 * @throws Exception 
	 */
	public Experiments(Properties properties, boolean print, boolean control, boolean attack, boolean asset,
			boolean implicit, boolean includesec) throws Exception{
		this.properties = properties;
		this.print = print;

		String trainfile = properties.getProperty(PropertyKeys.DATA_FILE);
		testfile = properties.getProperty(PropertyKeys.DATA_TESTFILE);
		double ratio = Double.valueOf(properties.getProperty(PropertyKeys.RATIO));
		trainInstances = Util.getARFFInstances(properties, trainfile, control, attack, asset, implicit, includesec);
		
		preProcess(ratio);
		
		trainModel();
	}
	
	private void preProcess(double ratio) throws Exception {
		
		if(ratio > 0)
			trainInstances = Util.balanceNSBR(trainInstances, ratio);


		if(!testfile.isEmpty())
			testInstances = Util.getARFFInstances(properties, testfile, false);
	}
	
	private void trainModel(){

		String algorithm = properties.getProperty(PropertyKeys.ALGORITHM);
		String num_expr = properties.getProperty(PropertyKeys.NUM_EXPERIMENT);
		String train_size = properties.getProperty(PropertyKeys.TRAIN_SIZE);	

		double trainSize = Double.parseDouble(train_size);
		int numExpr = Integer.parseInt(num_expr);
		
		models = new ArrayList<Classifier>();
		modelsEval = new ArrayList<Evaluation>();

		//run experiments
		metricsExprs = new ArrayList<Performance>();
		rawMetrics = new StringBuffer();
		
		Algorithms alg = null;
		int counter = 0;
				
		switch(algorithm) {
		
			case RANDOMFOREST: {
				System.out.println("Using Random Forest Algorithm...");
				for(int i=0; i<numExpr; i++){
					counter++;
					alg = new RandomForest(trainInstances, testInstances, trainSize);
					doTrain(alg, counter);
				}
				break;
			}
			
			case NAIVEBAYES: {
				System.out.println("Using NaiveBayes Algorithm...");
				for(int i=0; i<numExpr; i++){
					counter++;
					alg = new NaiveBayes(trainInstances, testInstances, trainSize);
					doTrain(alg, counter);
				}
				break;
			}
			
			case SVM: {
				System.out.println("Using Support Vector Machine Algorithm...");
				for(int i=0; i<numExpr; i++){
					counter++;
					alg = new SupportVectorMachine(trainInstances, testInstances, trainSize);
					doTrain(alg, counter);
				}
				break;
			}
			case LR: {
				System.out.println("Using Logistic Regression Algorithm...");
				for(int i=0; i<numExpr; i++){	
					counter++;
					alg = new LogisticRegression(trainInstances, testInstances, trainSize);
					doTrain(alg, counter);
				}
				break;
			}
			case KNN: {
				System.out.println("Using K-Nearest Neighbor Algorithm...");
				for(int i=0; i<numExpr; i++){	
					counter++;
					alg = new KNN(trainInstances, testInstances, trainSize);
					doTrain(alg, counter);
				}
				break;
			}

			default: {
				System.out.println("Using Random Forest Algorithm...");
				for(int i=0; i<numExpr; i++){
					counter++;
					alg = new RandomForest(trainInstances, testInstances, trainSize);
					doTrain(alg, counter);
				}
				break;
			}
		}

		//print out results
		if(print){
			for(int i=0; i<modelsEval.size(); i++){
				try {
					alg.printModelPerformance(modelsEval.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		saveBestModel(true);

	}
	
	private void doTrain(Algorithms alg, int counter) {
		//System.out.println("Experiment #"+counter+" starting...");
		alg.model();
		
		Util.addPerformance(alg.getModelEval(), metricsExprs, rawMetrics, counter);
		bestModel = alg.getModel();
		bestModelEval = alg.getModelEval();
		models.add(alg.getModel());	
		modelsEval.add(alg.getModelEval());

	}
	
	public int saveBestModel(boolean useKappa){
		//System.out.println("Saving best model...");
		double recall = 0.0;
		double kappa = -1.0;
		double fmeasure = 0.0;
		for(int i=0; i<modelsEval.size(); i++){
			Evaluation modelEval = modelsEval.get(i);
			recall = Math.max(recall, modelEval.recall(0));
			kappa = Math.max(kappa, modelEval.kappa());
			fmeasure = Math.max(fmeasure, modelEval.fMeasure(0));
		}
		
		int bmodelIndex = -1;
		//System.out.println("recall: "+recall);
		//use kappa
		if(useKappa){
			for(int i=0; i<modelsEval.size(); i++){
				if(modelsEval.get(i).kappa() == kappa){
					bestModel = models.get(i);
					bestModelEval = modelsEval.get(i);
					bmodelIndex = i;
					break;
				}
			}
		}else{
			for(int i=0; i<modelsEval.size(); i++){
				if(modelsEval.get(i).recall(0) == recall){
					bestModel = models.get(i);
					bestModelEval = modelsEval.get(i);
					bmodelIndex = i;
					break;
				}
			}
		}
		
		return bmodelIndex;
		
	}
	
	/**
	 * @return the models
	 */
	public List<Classifier> getModels() {
		return models;
	}


	/**
	 * @param bestModel the bestModel to set
	 */
	public void setBestModel(Classifier bestModel) {
		this.bestModel = bestModel;
	}

	/**
	 * @return the bestModel
	 */
	public Classifier getBestModel() {
		return bestModel;
	}

	/**
	 * @return the bestModelEval
	 */
	public Evaluation getBestModelEval() {
		return bestModelEval;
	}

	/**
	 * @param bestModelEval the bestModelEval to set
	 */
	public void setBestModelEval(Evaluation bestModelEval) {
		this.bestModelEval = bestModelEval;
	}

	public Instances getTrainInstances() {
		return trainInstances;
	}
	/**
	 * @return the metricsExprs
	 */
	public List<Performance> getMetricsExprs() {
		return metricsExprs;
	}
	/**
	 * @return the rawMetrics
	 */
	public StringBuffer getRawMetrics() {
		return rawMetrics;
	}

}
