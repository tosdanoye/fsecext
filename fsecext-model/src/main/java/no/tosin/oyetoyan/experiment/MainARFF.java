/**
JiraSecPlugin 
Copyright 2020 Tosin Daniel Oyetoyan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package no.tosin.oyetoyan.experiment;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import no.tosin.oyetoyan.keywords.PropertyKeys;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 * @author tosindo
 *
 */
public class MainARFF {

	private static Properties properties;
	private static List<Performance> metricsExprs;
	private static StringBuffer rawMetrics;
	private static List<Performance> metricsExprsAvg;
	private static StringBuffer rawMetricsAvg;

	private static List<BestModel> savedModels;

	private static Experiments experiment = null;

	public static String project;
	public static String test_project;
	public static String algorithm;

	private static String dictFile;
	private static FeatureSelection fs;
	
	private static int expnumber = 0;
	
	private static String algorithms;
	private static String features;
	
	public static String expname;
	private static boolean includesec;
	private static String includesecfeature;
	private static double ratio;
	
	private static boolean print = false;
	
	private static final String DATA_PATH = "DATA_PATH";
	private static final String HEADER = "HEADER";
	private static final String SEP = "SEPARATOR";
	private static final String TRAIN_SIZE = "TRAIN_SIZE";
	private static final String NUM_EXP = "NUM_EXP";
	private static final String CLASS_INDEX = "CLASS_INDEX";
	private static final String RATIOS = "CLASS_BALANCE_RATIOS";
	private static final String ALGORITHMS = "ALGORITHMS";
	private static final String FEATURES = "FEATURES";
	private static final String INCLUDE_SEC_FEATURES = "INCLUDE_SEC_FEATURES";
	private static final String TRAIN_FOLDERS = "TRAIN_FOLDER_NAMES";
	private static final String VALIDATION_FOLDERS = "VALIDATION_FOLDER_NAMES";
	
	/**
	 * @param args
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		
		// read the config file from console and set the parameters for the experiment	
		
//		String config = "./experiment.prop";
//		args = new String[2];
//		args[0] = "-config";
//		args[1] = config;
		
		if(args.length == 0 || ("-help").equals(args[0])){
			System.out.println(showMessage());
			return;
		}
		//first check if it is a config file
		if(args[0].equals("-config") && args.length == 2){
			String configfile = args[1].trim();
			//check for canonical path for a valid file
			try{
				File f = new File(configfile);
				configfile = f.getCanonicalPath();
			}catch(IOException | SecurityException e){
				System.out.println("Error: this is not a valid file. Check that you have specified a correct config file");
				return;
			}
			args = readConfiguration(configfile, args.length);
		}
		
		// collect the necessary parameters for the experiment
		properties = new Properties();
		
		String trainFolders = "";
		String valFolders = "";
		String ratios = "";
		for(int i=0; i<args.length; i++){
			String arg = args[i];

			switch(arg) {
				case DATA_PATH: PropertyKeys.PATH = args[i+1]; break;
				case HEADER: properties.setProperty(PropertyKeys.HEADER, args[i+1]); break;
				case SEP: properties.setProperty(PropertyKeys.SEPARATOR, args[i+1]); break;
				case TRAIN_SIZE: properties.setProperty(PropertyKeys.TRAIN_SIZE, args[i+1]); break;
				case NUM_EXP: properties.setProperty(PropertyKeys.NUM_EXPERIMENT, args[i+1]); break;
				case CLASS_INDEX: properties.setProperty(PropertyKeys.CLASS_INDEX, args[i+1]); break;
				case RATIOS: ratios = args[i+1]; break;
				case ALGORITHMS: algorithms = args[i+1]; break;
				case FEATURES: features = args[i+1]; break;
				case INCLUDE_SEC_FEATURES: includesecfeature = args[i+1]; break;
				case TRAIN_FOLDERS: trainFolders = args[i+1]; break;
				case VALIDATION_FOLDERS: valFolders = args[i+1]; break;
				
				default: break;
			}
		}
		
		// configure the SBR:NSBR ratio
		String[] aratio = ratios.split(",");
		double[] dratio = new double[aratio.length];
		for(int i=0; i<aratio.length; i++) {
			dratio[i] = Double.valueOf(aratio[i]);
		}
		
		// configure the training vs validation (e.g. cross project prediction)
		String[] trainprojects = trainFolders.split(",");
		String[] valprojects = valFolders.split(",");
		
		List<Parameters> projectconfig = new ArrayList<>();
		for(String project : trainprojects) {
			Parameters proj = new Parameters(project);
			proj.setTrainDataset(project+"_train");
			proj.setTestDataset(project+"_test");
			
			for(String valproject : valprojects) {
				if(valproject.trim().equals(project.trim()))
					continue;
				proj.addValidationDataset(valproject+"_test");
			}
			
			projectconfig.add(proj);
		}
		
		doExperiment(dratio, projectconfig);
	}
	
	private static void doExperiment(double[] ratios, List<Parameters> projects) throws Exception {
		savedModels = new ArrayList<>();
		//set the path to the project vocabulary (feature set) aka dictionary
		PropertyKeys.FEATURE_PATH = PropertyKeys.PATH+"/features/";
		
		/**
		 * Search parameters: Imbalance-Ratio and includesec
		 * ratio: 0 to 2)
		 * includesec: true & false
		 * algorithms
		 * 
		 */
		//Algorithm[] algorithms = Algorithm.values();
		String[] algs = algorithms.split(",");
		//double[] ratios = {0,0.5,1};			// SBR : NSBR (SBR is always 1) => 1:0.5, 1:1, 1:1.5, 1:2
		String[] secs = includesecfeature.split(",");
		boolean[] includesecs = new boolean[secs.length];			//  true = include features in training dataset
		for(int k=0; k<secs.length; k++) {
			if(secs[k].equalsIgnoreCase("yes"))
				includesecs[k] = true;
			else if(secs[k].equalsIgnoreCase("no"))
				includesecs[k] = false;
		}
		
		for(int l=0; l<10; l++) {
			for(String alg : algs) {
				algorithm = alg;
				properties.setProperty(PropertyKeys.ALGORITHM, algorithm);					// set the algorithm
				
				for(int i=0; i<ratios.length; i++) {
					ratio = ratios[i]; 
					properties.setProperty(PropertyKeys.RATIO, String.valueOf(ratio));		// set the ratio for sampling minority class
					
					for(int j=0; j<includesecs.length; j++) {
						includesec = includesecs[j];
						
						String paramname = "ratio-"+ratio+"sec-"+includesec;
						// start experiment
						start(paramname, projects);
					}
				}
			}
		}
		
		saveBestModelandFeatures();
	}
	
	private static void saveBestModelandFeatures() throws IOException {
		// find best model - use g-measure
		if(savedModels.size() > 0) {
			savedModels.sort(Comparator.comparing(BestModel::getMeanGmeasure).reversed());

			String expfeatures = savedModels.get(0).getFeaturetype();
			String project = savedModels.get(0).getProject();
			double gmeasure = savedModels.get(0).getMeanGmeasure();
			double ratio = savedModels.get(0).getRatio();
			String algorithm = savedModels.get(0).getAlgorithm();
			System.out.println("\nSaving the Best Model with: \nmean gmeasure = "+gmeasure+"\nProject = "+project+ "\nFeatures = "+expfeatures
					+"\nalgorithm = "+algorithm+" \nratio = "+ratio+"\nincludesec = "+includesec);
			
			System.out.println("Full path to the trained model and dictionary files:");
			Classifier model = savedModels.get(0).getModel();
			
			Util.serializeModel(PropertyKeys.PATH, model);
			
			/** create dictionary file for the feature set */
			String dictFile = PropertyKeys.PATH+"/dictfile.txt";
			ExperimentModels em = new ExperimentModels(dictFile, project);
			em.execute(expfeatures); 					// generate the appropriate dictionary file from the training dataset
			System.out.println(PropertyKeys.PATH);
		} else {
			System.out.println("No TPP model was saved. You may need to train again with new parameters and use TPP");
		}

				
	}
	
	private static void start(String paramname, List<Parameters> projects) throws Exception {
		
		for(int i=0; i<projects.size(); i++){
			long start = System.currentTimeMillis();
			Parameters param = projects.get(i);
			project = param.getProjectDataset();
			List<String> testdataset = param.getValidationDatasets();   //TPP dataset		
			
			/** create dictionary file for the feature set */
			dictFile = PropertyKeys.PATH+"/"+project+"/dictfile.txt";
			properties.setProperty(PropertyKeys.DICTIONARY_FILE, dictFile);
			
			ExperimentModels em = new ExperimentModels(dictFile, project);
			
			String[] featuretypes = features.split(",");
			//Selector[] featuretypes = Selector.values();
			
			for(String expfeatures : featuretypes) {
				
				//String expfeatures = featuretype;				

				em.execute(expfeatures); 					// generate the appropriate dictionary file from the training dataset
				
				expname = expfeatures + paramname;			// add the feature types to the experiment name file 
				fs = em.getFeatureConfig();
				
				/** Now train and test for each feature type and parameter set */
				/** WPP */
				String test = param.getTestDataset();
				test_project = test.split("_")[0].trim();
				System.out.println("Main experiment project = "+project+" and Test dataset = "+test +" algorithm = "+algorithm+" Params = "+expname);
				
				// where to store
				//metricsExprs = new ArrayList<Performance>();
				//rawMetrics = new StringBuffer();
				//metricsExprsAvg = new ArrayList<Performance>();
				//rawMetricsAvg = new StringBuffer();				
				
				trainModel(param, em.isProjectSpecific());					
				
				// results
				metricsExprs = experiment.getMetricsExprs();
				rawMetrics = experiment.getRawMetrics();
				metricsExprsAvg = experiment.getMetricsExprs();
				rawMetricsAvg = experiment.getRawMetrics();				
				
				//compute descriptive statistics and save to file
				saveExperiment(test, "", false);
				
				/** TPP **/
				if(testdataset.size() > 0)
					doTPP(testdataset, expfeatures);
				
				long end = System.currentTimeMillis();
				
				System.out.println("Experiment time = "+ (end-start));
			}
		}
	}
	
	private static void doTPP(List<String> testdataset, String expfeatures) throws Exception {
		
		/** TPP */
		for(String testdata : testdataset) {
			
			// where to store
			metricsExprs = new ArrayList<Performance>();
			rawMetrics = new StringBuffer();
			
			testModel(testdata);							//TPP
			
			//compute descriptive statistics and save to file
			saveExperiment(testdata, "", false);				
						
		}
		
		//use to search for best model for jirasecplugin - descriptive statistics for average and save to file
		saveExperiment("avg-result", expfeatures, true);
	}
	
	private static void saveExperiment(String testdata, String expfeatures, boolean avg) {
		//compute descriptive statistics and save to file
		
		String statFile = PropertyKeys.PATH+"/"+project+"/"+algorithm+"/"+testdata+"-"+expname+".txt";
		// check that folder exist otherwise create one
		String pdir = PropertyKeys.PATH+"/"+project+"/"+algorithm;
		if(!new File(pdir).exists())
			new File(pdir).mkdir();
		Util.computeAverage(metricsExprs, rawMetrics, statFile);
		
		if(avg) {
			double meangmeasure = Util.computeAverage(metricsExprsAvg, rawMetricsAvg, statFile);
			savedModels.add(new BestModel(project, expfeatures, meangmeasure, 
					experiment.getBestModel(), algorithm, ratio, includesec));
		}

	}
	
	private static void trainModel(Parameters param, boolean isprojectfeatures) throws Exception{		
		/**
		 * Build experiment parameters
		 */

		String project = param.getProjectDataset();

		// generate balanced train dataset if configured
		String trainfile = PropertyKeys.PATH+"/"+project+"/"+project+"_train.csv";
		String testfile = PropertyKeys.PATH+"/"+project+"/"+project+"_test.csv";

		properties.setProperty(PropertyKeys.DATA_FILE, trainfile);
		properties.setProperty(PropertyKeys.DATA_TESTFILE, testfile);

		// train a new model for the project
		if(isprojectfeatures)
			experiment = new Experiments(properties, print, includesec);
		else
			experiment = new Experiments(properties, print, fs.isControl(), fs.isAttack(), fs.isAsset(), fs.isImplicit(), includesec);
		
		Evaluation modelEval = experiment.getBestModelEval();
		// not necessary but to see if we have some good results
		double recall = modelEval.recall(0);
		double fscore = modelEval.fMeasure(0);
		double gmeasure = (2 * modelEval.recall(0)*100*(100 - modelEval.falsePositiveRate(0)*100))/(modelEval.recall(0)*100 + (100 - modelEval.falsePositiveRate(0)*100));
		if(gmeasure > 75) {
			System.out.println("====WPP====");
			System.out.println("Algorithm="+algorithm+ "| param="+expname+" | project="+project+" | test="+testfile);
			System.out.println("TP="+modelEval.numTruePositives(0)+" | FP="+modelEval.numFalsePositives(0)+" | TN="+modelEval.numTrueNegatives(0)+" | FN="+modelEval.numFalseNegatives(0));
			System.out.println("recall="+recall+" | fscore="+fscore+" | gmeasure="+gmeasure);
			
			//Util.adjustedThresholdComparison(test_project, project, algorithm, expname, experiment.getBestModelEval());
		}
		
	}

	private static void testModel(String testdata) throws Exception {		
		// test config
		test_project = testdata.split("_")[0].trim();
		String testfile = PropertyKeys.PATH+"/"+test_project+"/"+testdata+".csv";

		properties.setProperty(PropertyKeys.DATA_TESTFILE, testfile);
		
		validateWithUnseenTestDataset(testfile);
	}
	
	private static void validateWithUnseenTestDataset(String testFile) throws Exception {
		//checking explicitly for .csv or xlsx!
		if(testFile.endsWith(".csv") || testFile.endsWith(".xlsx")){ 			
			//generate validation instances
			Instances testinstances = Util.getARFFInstances(properties, testFile, false);
			Evaluation modelEval = new Evaluation(experiment.getTrainInstances());
			modelEval.evaluateModel(experiment.getBestModel(), testinstances);
			/** add performance */
			Util.addPerformance(modelEval, metricsExprs, rawMetrics, expnumber);
			Util.addPerformance(modelEval, metricsExprsAvg, rawMetricsAvg, expnumber);
			// not necessary but to see if we have some good results
			double recall = modelEval.recall(0);
			double fscore = modelEval.fMeasure(0);
			double gmeasure = (2 * modelEval.recall(0)*100*(100 - modelEval.falsePositiveRate(0)*100))/(modelEval.recall(0)*100 + (100 - modelEval.falsePositiveRate(0)*100));
			if(gmeasure > 75) {
				System.out.println("====TPP====");
				System.out.println("Algorithm="+algorithm+ "| param="+expname+" | project="+project+" | test="+testFile);
				System.out.println("TP="+modelEval.numTruePositives(0)+" | FP="+modelEval.numFalsePositives(0)+" | TN="+modelEval.numTrueNegatives(0)+" | FN="+modelEval.numFalseNegatives(0));
				System.out.println("recall="+recall+" | fscore="+fscore+" | gmeasure="+gmeasure);
				
				//Util.adjustedThresholdComparison(test_project, project, algorithm, expname, modelEval);
			}
		}else{
			System.out.println("Invalid file");
		}
	}
	
	private static String showMessage(){
		String message = "Usage: "
				+ "java -jar fsecextplugin.jar -config experiment.prop \n\n"
				+ "---Mandatory settings in the experiment.prop file---\n"
				+ "DATA_PATH=/Users/tdoy/fsecext-sqj/data # set the path to the folder where all your data is located \n"
				+ "HEADER=true # does the file has a header: true or false? \n"
				+ "SEPARATOR=; # specify csv column separator\n"
				+ "TRAIN_SIZE=0.95 # specify ratio for splitting dataset into train and test\n"
				+ "NUM_EXP=1 # specify how many times we should train. \n"
				+ "CLASS_INDEX=1 # specify index of class label \n"
				+ "CLASS_BALANCE_RATIOS=0,0.5,1,2 # get the ratios (separated by comma) for sampling minority class - SBR : NSBR (SBR is always 1) => 1:0.5, 1:1, 1:1.5, 1:2 \n"
				+ "ALGORITHMS=KNN,LR,NB,SVM,RF # algorithms to use for training a model. LR-Logistic Regression,NB-Naive Bayes,KNN-K Nearest Neighbor,SVM-Support Vector Machine,RF-Random Forest\n"
				+ "FEATURES=TC,TCA,TCAI # add one or more features to be used (external or internal): fsec-tfidf, fsec-ext\n"
				+ "INCLUDE_SEC_FEATURES=yes,no # include security features in the training dataset {yes,no} {yes} {no}\n"
				+ "TRAIN_FOLDER_NAME=apache # name of the folder containing the train and test csv files. Note: csv files must be named as folder_train.csv (e.g. apache_train.csv) and folder_test.csv (apache_test.csv) \n"
				+ "VALIDATION_FOLDER_NAMES=derby,wicket,ambari,camel # name of the folders (separated by comma) containing other projects' csv test files for validation. Note: csv files must be named as folder_test.csv (e.g. derby_test.csv) \n";
		return message;		
	}
	
	private static String[] readConfiguration(String config_file, int args_len){
		
		List<String> content = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(config_file));)
		{
			String line = "";
			
			while((line=br.readLine())!=null){
				if(line.trim().startsWith("#") || line.trim().isEmpty())
					continue;
				content.add(line);
				//System.out.println("Reading config line "+line);
				
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		
		String[] res = new String[content.size()*2];
		int index = 0;
		for(int i=0; i<content.size(); i++){
			String[] tokens = content.get(i).trim().split("=");				
			for(int ind=0; ind<tokens.length; ind++){
				res[index++] = tokens[ind];
			}
		}
		
		return res;
	}

}
