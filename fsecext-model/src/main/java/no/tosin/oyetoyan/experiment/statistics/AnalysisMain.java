/**
 * 
 */
package no.tosin.oyetoyan.experiment.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.tosin.oyetoyan.experiment.Algorithm;
import no.tosin.oyetoyan.experiment.SetupExperiments;
import no.tosin.oyetoyan.keywords.PropertyKeys;



/**
 * @author tdoy
 *
 */
public class AnalysisMain {

	private static List<AnalysisData> analysisData; 
	public static String path = PropertyKeys.PATH+"/";
	
	// to analyse for Farsec paper dataset, remove odcv, apache, mozilla, comm and run the StatisticalTests.java
	private static String[] projects = {"ambari","derby","camel","wicket","chromium","odcv","apache","mozilla","comm"}; //,"odcv","apache","mozilla","comm"
	private static Algorithm[] algorithms = Algorithm.values();
	
	public static void main(String[] args) {
		
		process();

	}
	
	public static void process() {
		
		analysisData = new ArrayList<>();
		
		for(String project : projects) {
			for(Algorithm algorithm : algorithms) {
				String alg = algorithm.name();
				try {
					readFolders(project, alg);
				}catch(Exception e) {
					System.out.println(project+" "+alg);
					//e.printStackTrace();
				}
				
			}
			
		}
	}
	
	private static void readFolders(String project, String alg) {
		String fpath = path+project+"/"+alg+"/";
		File dir = new File(fpath);
		String[] files = dir.list();
		for(String f : files) {
			if(f.equalsIgnoreCase(".DS_Store"))
				continue;
			
			AnalysisData data = new AnalysisData();
			data.setSource(project);
			data.setAlgorithm(alg);
			
			readParam(f, data);
			readFile(fpath+f, data);
			
			analysisData.add(data);				// store the data
		}
	}
	
	public static StringBuffer build(List<AnalysisData> m) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("source ; target ; feature.sel.approach; class.bal.ratio; sec.feature.include; security.category; "
				+ "algorithm; TN;TP;FN;FP; Pd;Pf;Prec;Fmeasure;Gmeasure;AuROC;Kappa  \n");
		
		for(int i=0; i<m.size(); i++) {
			AnalysisData d = m.get(i);
			sb.append(d.getSource()+";"+d.getTarget()+";"+d.getFeatureSelectionMethod()+
					";"+d.getTrainBalanceRatio()+";"+d.isSecurityFeatureIncluded()+
					";"+d.getSecurityFeatureType()+";"+d.getAlgorithm()+";"+d.getTn()+
					";"+d.getTp()+";"+d.getFn()+";"+d.getFp()+";"+d.getPd()+
					";"+d.getPf()+";"+d.getPrec()+";"+d.getFmeasure()+";"+d.getGmeasure()+
					";"+d.getAuroc()+";"+d.getKappa()+"\n");
		}
		
		return sb;
	}
	
	// by target
	public static Map<String, List<AnalysisData>> statPerTargetTPP() {
		
		Map<String, List<AnalysisData>> pertarget = new HashMap<>();
		for(String project : projects) {
			List<AnalysisData> results = new ArrayList<>();
			for(AnalysisData ad : analysisData) {
				if(ad.getTarget().equals(project) && !ad.getSource().equals(ad.getTarget())) {
					results.add(ad);
				}
			}
			pertarget.put(project, results);
		}
		
		
		return pertarget;
	}
	
	// by target
	public static Map<String, List<AnalysisData>> statPerTargetWPP() {
		
		Map<String, List<AnalysisData>> pertarget = new HashMap<>();
		for(String project : projects) {
			List<AnalysisData> results = new ArrayList<>();
			for(AnalysisData ad : analysisData) {
				if(ad.getSource().equals(project) && ad.getSource().equals(ad.getTarget())) {
					results.add(ad);
				}
			}
			pertarget.put(project, results);
		}
		
		
		return pertarget;
	}

	public static void printtofile(String fappend, StringBuffer sb) {
		String store = path + "analysis/"+fappend;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(store));
			bw.write(sb.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void readParam(String f, AnalysisData data) {
		// file looks like: ambari_test-Controlratio-0.1sec-true
		// ambari_test-TFIDFHighratio-0.0sec-true

		String target = f.split("_")[0].trim();
		String[] tokens = f.split("-");
		String fmethod = tokens[1].replace("ratio", "").trim();
		String ratio = tokens[2].replace("sec", "").trim();
		String secincl = tokens[3].trim().replace(".txt", "");
		
		data.setTarget(target);
		data.setFeatureSelectionMethod(fmethod.trim());
		data.setTrainBalanceRatio(Double.valueOf(ratio));
		data.setSecurityFeatureIncluded(secincl.trim());
		data.setSecurityFeatureType(fmethod.trim());
	}
	
	private static void readFile(String f, AnalysisData data) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = "";
			// AuROCMean, AuRPCMean, KappaMean
			while((line=br.readLine())!=null) {
				
				String[] tokens = line.trim().split(":");
				if(tokens[0].trim().equals("RecallAvg")){					
					data.setPd(format(tokens[1].trim())*100);
				}else if(tokens[0].trim().equals("PrecisionAvg")) {
					data.setPrec(format(tokens[1].trim())*100);
				}else if(tokens[0].trim().equals("FscoreAvg")) {
					data.setFmeasure(format(tokens[1].trim())*100);
				}else if(tokens[0].trim().equals("PfAvg")) {
					data.setPf(format(tokens[1].trim())*100);
				}else if(tokens[0].trim().equals("GmeasureAvg")) {
					data.setGmeasure(format(tokens[1].trim()));
				}else if(tokens[0].trim().equals("AuROCMean")) {
					data.setAuroc(format(tokens[1].trim())*100);
				}else if(tokens[0].trim().equals("AuRPCMean")) {
					data.setAuprc(format(tokens[1].trim())*100);
				}else if(tokens[0].trim().equals("KappaMean")) {
					data.setKappa(format(tokens[1].trim()));
				}else if(tokens[0].trim().equals("1") || tokens[0].trim().equals("0")) {
					String[] m = tokens[1].trim().split(" ");
					//System.out.println(line);
					data.setTp(format(m[2]));
					data.setFp(format(m[5]));
					data.setTn(format(m[8]));
					data.setFn(format(m[11]));
				}					
			}
			
			br.close();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static double format(String d) {
		if(d.equals("NaN"))
			return 0;
		else
			return Double.parseDouble(d);
	}

}
