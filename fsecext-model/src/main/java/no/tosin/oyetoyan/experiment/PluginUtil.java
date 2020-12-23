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
import weka.core.Instance;
import weka.core.Instances;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.FixedDictionaryStringToWordVector;
import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 * @author tdoy
 *
 */
public class PluginUtil {
	
	public static Instances getDictionaryFilteredInstances(String dictFile, String arffFile) throws Exception {
		
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
				
		Instances dataFiltered = Filter.useFilter(data, dictFilter);
		
		
		return dataFiltered;
	}
    
	public static void writeUnlabeledInstance(String unlabeledTxt, String arffFile) {
		
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
			
			String cleanedtxt1 = getOnlyStrings(unlabeledTxt);
			bw.write("\""+cleanedtxt1+"\","+0+"\n");
			
		}catch(Exception e) {
			//
		}
	}
	
	private static String getOnlyStrings(String s) {
		Pattern pattern = Pattern.compile("[^a-z A-Z]");
	    Matcher matcher = pattern.matcher(s);
	    String string = matcher.replaceAll(" ");
	    
	    return string;
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
	
	public static double classify(Classifier model, Instance inst) {
		
		double predclass = 0;
		try {
			predclass = model.classifyInstance(inst);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return predclass;
	}
}
