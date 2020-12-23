package no.tosin.oyetoyan.experiment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import no.tosin.oyetoyan.keywords.PropertyKeys;
import no.tosin.oyetoyan.keywords.TFIDFFeatures;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;

/**
 * @author tdoy = tosindo :)
 *
 */
public class DataPreProcess {

	private String arffFile;
	private String datasetFile;
	private int classIndex;
	
	
	/**
	 * @param datasetFile
	 * @param arffFile
	 */
	public DataPreProcess(String datasetFile, String arffFile, int classIndex) {
		this.datasetFile = datasetFile;
		this.arffFile = arffFile;
		this.classIndex = classIndex;
	}
	
	public DataPreProcess() {
		//
	}
	
	public Instances getDictionaryFilteredInstances(String dictFile) throws Exception {
		
		// Get data
		Instances data = new Instances(new FileReader(arffFile));
		data.setClassIndex(1);
		
		FixedDictionaryStringToWordVector dictFilter = new FixedDictionaryStringToWordVector();
		dictFilter.setDictionaryFile(new File(dictFile));
		dictFilter.setInputFormat(data);
		dictFilter.setLowerCaseTokens(true);
		dictFilter.setTFTransform(true);
		dictFilter.setIDFTransform(false);
		dictFilter.setOutputWordCounts(true);
		if(SetupExperiments.STEM) {
			Stemmer stemmer = new SnowballStemmer();
			dictFilter.setStemmer(stemmer);
		}
				
		Instances dataFiltered = Filter.useFilter(data, dictFilter);
		
		
		return dataFiltered;
	}
	
	public Instances getDictionaryFilteredInstances(String dictFile, String arffFile) throws Exception {
		
		// Get data
		Instances data = new Instances(new FileReader(arffFile));
		data.setClassIndex(1);
		
		FixedDictionaryStringToWordVector dictFilter = new FixedDictionaryStringToWordVector();
		dictFilter.setDictionaryFile(new File(dictFile));
		dictFilter.setInputFormat(data);
		dictFilter.setLowerCaseTokens(true);
		dictFilter.setTFTransform(true);
		dictFilter.setIDFTransform(false);
		dictFilter.setOutputWordCounts(true);
		if(SetupExperiments.STEM) {
			Stemmer stemmer = new SnowballStemmer();
			dictFilter.setStemmer(stemmer);
		}
				
		Instances dataFiltered = Filter.useFilter(data, dictFilter);
		
		
		return dataFiltered;
	}
	
	public void createARFFFile() {
		// create a new arff file from the data. 
		
		String header = "@RELATION 'project-security'\n" + 
				"\n" + 
				"@ATTRIBUTE bugreport string\n" + 
				"@ATTRIBUTE class-att {1,0} nominal\n" + 
				"\n" + 
				"@data\n";
		
		writeHeader(header);
		readWriteFile();
	}
	
	private void writeHeader(String header) {
		
		String outfile = new File(arffFile).getAbsolutePath();
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){
			
			bw.write(header);
		}catch(Exception e) {
			//
		}
	}
	
	private void readWriteFile() {
		
		String path = new File(datasetFile).getAbsolutePath();
		String outfile = new File(arffFile).getAbsolutePath();
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){
			
			try(BufferedReader br = new BufferedReader(new FileReader(path))){
				String line = "";
				
				int i=0;
				while((line=br.readLine())!=null) {
					if(i++==0) continue;																// ignore header in file
					String target = line.split(";")[classIndex-1];									// this is separated by semicolon
					line = line.replaceFirst(target+";", "");

					line = Util.getOnlyStrings(line).trim();
					if(line.trim().isEmpty())									 // discard missing data
						continue;
					bw.write("\""+line+"\","+target.trim()+"\n");			// cols[1]+cols[2]=bugreport: cols[0]=type-of-bug (sec/no-sec)
				}
				
			}catch(Exception e) {
				//
			}
		}catch(Exception e) {
			//
		}		
	}
    
	public void writeUnlabeledInstance(String unlabeledTxt, String arffFile) {
		
		String header = "@RELATION 'project-security'\n" + 
				"\n" + 
				"@ATTRIBUTE bugreport string\n" + 
				"@ATTRIBUTE class-att {1,0} nominal\n" + 
				"\n" + 
				"@data\n";
		
		// write arff header
		String outfile = new File(arffFile).getAbsolutePath();
		
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile))){
			
			bw.write(header);
		}catch(Exception e) {
			//
		}
		
		// write the unlabeled txt in arff file with a default 0 label
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){
			
			String cleanedtxt1 = Util.getOnlyStrings(unlabeledTxt);
			bw.write("\""+cleanedtxt1+"\","+0+"\n");
			
		}catch(Exception e) {
			//
		}
	}
	
	private void writeFeaturesInDataset(List<String> features) {
		
		String outfile = new File(arffFile).getAbsolutePath();
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outfile, true))){
			
			for(String f : features)
				bw.write("\""+f+"\","+"1\n");				// every feature is a security term
		}catch(Exception e) {
			//
		}
	}

	public void includeSecurityFeaturesinTrain(boolean control, boolean attack, boolean asset, boolean indirect) {
		
		List<String> features = Util.chooseFeatures(control, attack, asset, indirect);
		if(features.size() > 0 )
			writeFeaturesInDataset(features);
	}
	
	public void includeSecurityFeaturesinTrain(String dictfile) throws IOException {
		TFIDFFeatures tf = new TFIDFFeatures();
		List<String> features = tf.readStopwords(dictfile);

		if(features.size() > 0 )
			writeFeaturesInDataset(features);
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		String project = "chromium";
		String fpath = PropertyKeys.PATH+"/features/";
		String datafile = fpath+"/"+project+"/"+project+"_train.csv";
		String arfffilename = new File(datafile).getName().replace(".csv", "");
		String arfffile = new File(datafile).getParent()+"/"+arfffilename+".arff";
		String dictFile = PropertyKeys.PATH+"/"+project+"/"+project+"_dictfile.txt";
		
		DataPreProcess d = new DataPreProcess(datafile, arfffile, 1);
		
		d.createARFFFile();
		Instances inst = d.getDictionaryFilteredInstances(dictFile);

		System.out.println(inst.size()+" "+arfffilename);

		Instances binst = Util.balanceNSBR(inst, 0.5);
		System.out.println(binst.size());

	}

}
