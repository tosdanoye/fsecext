/**
 * 
 */
package no.tosin.oyetoyan.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import no.tosin.oyetoyan.keywords.PropertyKeys;
import no.tosin.oyetoyan.keywords.SecurityKeyWords;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 * @author tdoy
 *
 */
public class Util {
	
	
//	public static void writeToFile(List<String> data, String outfile) {
//		
//		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){
//			for(String f : data)
//				bw.write(f+"\n");
//		}catch(Exception e) {
//			//
//		}
//	}
	
	/**
	 * 
	 * @param control set to true to include control terms
	 * @param attack set to true to include attack terms
	 * @param asset set to true to include asset terms
	 * @param indirect set to true to include indirect terms
	 * @return features as a List
	 */
	public static List<String> chooseFeatures(boolean control, boolean attack, boolean asset, boolean indirect) {
		
		SecurityKeyWords skw = new SecurityKeyWords();

		//loaded keywords are used to represent the feature set
		Set<String> attributes = new HashSet<String>();
		attributes.add("");
		if(control) attributes.addAll(skw.controlTerms());
		if(attack) attributes.addAll(skw.threatTerms());
		if(asset) attributes.addAll(skw.assetTerms());
		if(indirect) attributes.addAll(skw.implicitTerms());		
		
		// stem?
		if(SetupExperiments.STEM) {
			Set<String> stemmedFeatures = new HashSet<>();
			Stemmer stemmer = new SnowballStemmer();
			for(String s : attributes)
				stemmedFeatures.add(stemmer.stem(s));
			
			return (new ArrayList<String>(stemmedFeatures));
		}
		//change to list to enforce order
		return (new ArrayList<String>(attributes));
	}
	
	public static Instances getARFFInstances(Properties prop, String datasetfile, boolean includesec) throws IOException {
		
		Instances instances = null;
		//generate instances based on the dictionary file
		String dictfile = prop.getProperty(PropertyKeys.DICTIONARY_FILE);
		int classIndex = Integer.valueOf(prop.getProperty(PropertyKeys.CLASS_INDEX));
		
		String arfffilename = new File(datasetfile).getName().replace(".csv", "");
		String arfffile = new File(datasetfile).getParent()+"/"+arfffilename+".arff";
		DataPreProcess arff = new DataPreProcess(datasetfile, arfffile, classIndex);
		arff.createARFFFile();
		if(includesec)
			arff.includeSecurityFeaturesinTrain(dictfile);
		try {
			instances = arff.getDictionaryFilteredInstances(dictfile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return instances;
	}
	
	public static Instances getARFFInstances(Properties prop, String datasetfile, boolean control, boolean attack,
			boolean asset, boolean implicit, boolean includesec) {
		
		Instances instances = null;
		//generate instances based on the dictionary file
		String dictfile = prop.getProperty(PropertyKeys.DICTIONARY_FILE);

		int classIndex = Integer.valueOf(prop.getProperty(PropertyKeys.CLASS_INDEX));
		
		String arfffilename = new File(datasetfile).getName().replace(".csv", "");
		String arfffile = new File(datasetfile).getParent()+"/"+arfffilename+".arff";
		DataPreProcess arff = new DataPreProcess(datasetfile, arfffile, classIndex);
		arff.createARFFFile();
		if(includesec)
			arff.includeSecurityFeaturesinTrain(control, attack, asset, implicit);
		
		try {
			instances = arff.getDictionaryFilteredInstances(dictfile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return instances;
	}
	
	public static String getOnlyStrings(String s) {
		Pattern pattern = Pattern.compile("[^a-z A-Z]");
	    Matcher matcher = pattern.matcher(s);
	    String string = matcher.replaceAll(" ");
	    
	    return string;
	}
	
	public static Instances balanceNSBR(Instances instances, double ratio) throws Exception {
		
		// Make a new dataset with only SBRs
        RemoveWithValues rwv = new RemoveWithValues();
        rwv.setAttributeIndex("1");
        rwv.setNominalIndices("2");
        rwv.setInputFormat(instances);
        Instances sbrs = Filter.useFilter(instances, rwv);

        // Make a new dataset with only NSBRs
        RemoveWithValues nrwv = new RemoveWithValues();
        nrwv.setAttributeIndex("1");
        nrwv.setNominalIndices("1");
        nrwv.setInputFormat(instances);
        Instances nsbrs = Filter.useFilter(instances, nrwv);
              
        System.out.println("SBRs = "+sbrs.size());
        System.out.println("NSBRs = "+nsbrs.size());
        
        Random rnd = new Random();
        Instances binstances = null;
		long n = 0;
		// undersample or oversample NSBR
		if(nsbrs.size() > sbrs.size() || nsbrs.size() < sbrs.size()) {
			binstances = new Instances(sbrs);		// here we add all instances of the sbr (has lower percentage)
			//n = Math.abs(nsbrs.size() - sbrs.size());
			// adjust sbr size by ratio
			n = Math.round(sbrs.size() * ratio);
			// Now over/undersample nsbr using n					
			nsbrs.randomize(new Random());
			for(int i=0; i<n; i++){						// here we over/undersample the class (nsbr) with higher percentage by adjusted ratio
				binstances.add(nsbrs.get(rnd.nextInt(nsbrs.size())));
			}
		} else { // sbr = nsbr
			binstances = new Instances(sbrs);
			binstances.addAll(nsbrs);
		} 
		
		return binstances;
	}
	
	public static void addPerformance(Evaluation modelEval, List<Performance> metricsExprs, StringBuffer rawMetrics, int expnumber){
		Performance perf = new Performance();
		perf.setRecall(modelEval.recall(0));
		perf.setPrecision(modelEval.precision(0));
		perf.setFscore(modelEval.fMeasure(0)); 
		perf.setPf(modelEval.falsePositiveRate(0));
		double gmeasure = (2 * perf.getRecall()*100*(100 - perf.getPf()*100))/(perf.getRecall()*100 + (100 - perf.getPf()*100));
		perf.setGmeasure(gmeasure);
		perf.setAUPRC(modelEval.areaUnderPRC(0));
		perf.setAUROC(modelEval.areaUnderROC(0));
		perf.setKappa(modelEval.kappa());
		metricsExprs.add(perf);
		
		double TP = modelEval.numTruePositives(0);
		double FP = modelEval.numFalsePositives(0);
		double TN = modelEval.numTrueNegatives(0);
		double FN = modelEval.numFalseNegatives(0);
		// add TP, FP, TN, FN
		rawMetrics.append(expnumber+": "+"TP = "+TP+" FP = "+FP+" TN = "+TN+" FN = "+FN+"\n");
	}
	
	
	public static double computeAverage(List<Performance> metricsExprs, StringBuffer rawMetrics, String outFilename){
		SummaryStatistics statRecall = new SummaryStatistics();
		SummaryStatistics statPrecision = new SummaryStatistics();
		SummaryStatistics statFscore = new SummaryStatistics();
		SummaryStatistics statPf = new SummaryStatistics();
		SummaryStatistics statGmeasure = new SummaryStatistics();
		SummaryStatistics statAuprc = new SummaryStatistics();
		SummaryStatistics statAuroc = new SummaryStatistics();
		SummaryStatistics statKappa = new SummaryStatistics();
		for(Performance value : metricsExprs){
			statRecall.addValue(value.getRecall());
			statPrecision.addValue(value.getPrecision());
			statFscore.addValue(value.getFscore());
			statPf.addValue(value.getPf());
			statGmeasure.addValue(value.getGmeasure());
			statAuprc.addValue(value.getAUPRC());
			statAuroc.addValue(value.getAUROC());
			statKappa.addValue(value.getKappa());
		}
		
		List<String> res = new ArrayList<String>();
		res.add("#Experiments: "+metricsExprs.size());
		res.add("===============================");
		res.add("RecallAvg: "+statRecall.getMean());
		res.add("RecallMax: "+statRecall.getMax());
		res.add("RecallMin: "+statRecall.getMin());
		res.add("RecallStd: "+statRecall.getStandardDeviation());
		
		res.add("PrecisionAvg: "+statPrecision.getMean());
		res.add("PrecisionMax: "+statPrecision.getMax());
		res.add("PrecisionMin: "+statPrecision.getMin());
		res.add("PrecisionStd: "+statPrecision.getStandardDeviation());
		
		res.add("FscoreAvg: "+statFscore.getMean());
		res.add("FscoreMax: "+statFscore.getMax());
		res.add("FscoreMin: "+statFscore.getMin());
		res.add("FscoreStd: "+statFscore.getStandardDeviation());
		
		res.add("PfAvg: "+statPf.getMean());
		res.add("PfMax: "+statPf.getMax());
		res.add("PfMin: "+statPf.getMin());
		res.add("PfStd: "+statPf.getStandardDeviation());
		
		res.add("GmeasureAvg: "+statGmeasure.getMean());
		res.add("GmeasureMax: "+statGmeasure.getMax());
		res.add("GmeasureMin: "+statGmeasure.getMin());
		res.add("GmeasureStd: "+statGmeasure.getStandardDeviation());
		
		res.add("AuROCMean: "+statAuroc.getMean());
		res.add("AuROCMax: "+statAuroc.getMax());
		res.add("AuROCMin: "+statAuroc.getMin());
		res.add("AuROCStd: "+statAuroc.getStandardDeviation());
		
		res.add("AuRPCMean: "+statAuprc.getMean());
		res.add("AuRPCMax: "+statAuprc.getMax());
		res.add("AuRPCMin: "+statAuprc.getMin());
		res.add("AuRPCStd: "+statAuprc.getStandardDeviation());
		
		res.add("KappaMean: "+statKappa.getMean());
		res.add("KappaMax: "+statKappa.getMax());
		res.add("KappaMin: "+statKappa.getMin());
		res.add("KappaStd: "+statKappa.getStandardDeviation());
		
		res.add("===============================");
		
		//System.out.println(res);
		//write to file
		writeResultIntoFile(res, outFilename);
		writeRawMetricsIntoFile(rawMetrics, outFilename);
		
		return statGmeasure.getMean();
	}
	
	public static void writeResultIntoFile(List<String> result, String outFilename){
		
		try (BufferedWriter br = new BufferedWriter(new FileWriter(outFilename, false));)
		{			
			for(String line : result)
				br.write(line+"\n");
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void writeRawMetricsIntoFile(StringBuffer result, String outFilename){
		
		try (BufferedWriter br = new BufferedWriter(new FileWriter(outFilename, true));)
		{			
			br.write(result.toString());
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void readFile(String file){
		try (BufferedReader br = new BufferedReader(new FileReader(file));)
		{			
			String line = "";

			while((line = br.readLine()) != null){
				System.out.println(line);
			}

		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static List<String> load(String file){
		List<String> content = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file));)
		{			
			String line = "";

			while((line = br.readLine()) != null){
				if(line.trim().isEmpty()) continue;
				content.add(line.trim());
			}

		}catch(IOException e){
			e.printStackTrace();
		}
		
		return content;
	}
	
	public static List<String> loadDict(String file){
		List<String> content = new ArrayList<>();
		content.add("");
		try (BufferedReader br = new BufferedReader(new FileReader(file));)
		{			
			String line = "";

			while((line = br.readLine()) != null){
				if(line.trim().isEmpty()) continue;
				line = line.replace("[", "");
				line = line.replace("]", "");
				String[] tokens = line.split(",");
				for(String token : tokens)
					content.add(token.trim());
			}

		}catch(IOException e){
			e.printStackTrace();
		}
		
		return content;
	}
	
	public static void serializeModel(String path, Classifier model) {
		// serialize model
		 try {
			weka.core.SerializationHelper.write(path+"/trmodel.model", model);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	public static Classifier deserializeModel(String trmodel) {		
		// de-serialize model
		Classifier model = null;
		try {
			model = (Classifier) weka.core.SerializationHelper.read(trmodel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
		
	}
	
	public static Classifier saveBestModel(List<Evaluation> modelsEval, List<Classifier> models){
		
		int bmodelIndex = -1;
		double maxgmeasure = 0.0;
		for(int i=0; i<modelsEval.size(); i++){
			Evaluation modelEval = modelsEval.get(i);
			double gmeasure = (2 * modelEval.recall(0)*100*(100 - modelEval.falsePositiveRate(0)*100))/(modelEval.recall(0)*100 + (100 - modelEval.falsePositiveRate(0)*100));
			maxgmeasure = Math.max(maxgmeasure, gmeasure);
			if(maxgmeasure == gmeasure) 
				bmodelIndex = i;
			
		}
		
		System.out.println("Best Model Saved with max-gmeasure = "+maxgmeasure);
		return models.get(bmodelIndex);
		
	}
	
	public static double classify(Classifier model, Instance inst) {
		
		double predclass = 0;
		try {
			predclass = model.classifyInstance(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return predclass;
	}
	
	public static void adjustedThresholdComparison(String test_project, String project, String algorithm, String expname,
			Evaluation modelEval) {
		// This is threshold comparison with Peters et al. TP for Chromium and Ambari
		// where the precisions in Peters et al. were better.
		// wpp
		// peters et al.: chromium tp=57 fp=789
		// peters et al.: ambari tp=4 fp=15
		// tpp
		// peters et al.: chromium tp=56 fp=1038
		// peters et al.: ambari tp=3 fp=19
		
		int tp_threshold = 0;
		
		List<String> res = new ArrayList<String>();
		if(test_project.equals("chromium") && project.equals("chromium")) {
			tp_threshold = 57;
			res.add("Source=chromium and Target=chromium");
			res.add("tp_threshold = 57 | fp_threshold = 789");
		}
		if(test_project.equals("ambari") && project.equals("ambari")) {
			tp_threshold = 4;
			res.add("Source=ambari and Target=ambari");
			res.add("tp_threshold = 4 | fp_threshold = 15");
		}
		if(test_project.equals("chromium") && project.equals("camel")) {
			tp_threshold = 56;
			res.add("Source=camel and Target=chromium");
			res.add("tp_threshold = 56 | fp_threshold = 1038");
		}
		if(test_project.equals("ambari") && project.equals("chromium")) {
			tp_threshold = 3;
			res.add("Source=chromium and Target=ambari");
			res.add("tp_threshold = 3 | fp_threshold = 19");
		}
		if(test_project.equals("ambari") && project.equals("derby")) {
			tp_threshold = 3;
			res.add("Source=derby and Target=ambari");
			res.add("tp_threshold = 3 | fp_threshold = 19");
		}
		ArrayList<Prediction> predictions = modelEval.predictions();
		int fp = 0;
		int tp = 0;
		for(int i=0; i<predictions.size(); i++) {
			Prediction pred = predictions.get(i);
			//double target = 0.0;
			if(tp == tp_threshold)
				break;
			if(pred.predicted() == 0.0 && pred.actual() == 0.0) {
				tp++;
			}else if(pred.predicted() == 0.0 && pred.actual() == 1.0)
				fp++;
		}
		
		res.add("tp_model="+tp+" | fp_model="+fp);
		double adj_precision = (double)tp/((double)tp+fp);
		res.add("adj_precision="+adj_precision);
		res.add("Total="+predictions.size());
		
		saveThresholds(res, project, test_project, algorithm, expname);
	}
	
	private static void saveThresholds(List<String> res, String project, String test_project,
			String algorithm, String expname) {
		
		String statFile = PropertyKeys.PATH+"/"+project+"/"+algorithm+"/"+test_project+"-"+expname+"_tpthreshold.txt";
		String pdir = PropertyKeys.PATH+"/"+project+"/"+algorithm;
		if(!new File(pdir).exists())
			new File(pdir).mkdir();
		
		writeResultIntoFile(res, statFile);
		System.out.println(res);
	}
}
