/**
 * 
 */
package no.tosin.oyetoyan.experiment;

import java.io.IOException;

import no.tosin.oyetoyan.keywords.PropertyKeys;

/**
 * @author tdoy
 *
 */
public class ExperimentModels {

	// configure all the models 
	private String dictionaryfile;
	private String trainfile;
	private String unwantedfile;
	
	private String featurepath;
	
	private FeatureSelection featureConfig;
	
	private static final String TFIDFHigh = "TFIDFHigh";
	private static final String Threat = "Threat";
	private static final String Control = "Control";
	private static final String TC = "TC";
	private static final String CA = "CA";
	private static final String TA = "TA";
	private static final String TCA = "TCA";
	private static final String TCAI = "TCAI";
	
	private boolean projectSpecific = false;
	
	public ExperimentModels(String dictionaryfile, String project) {
		this.dictionaryfile = dictionaryfile;
		trainfile = PropertyKeys.PATH + "/"+project+"/"+project+"_train.csv";
		unwantedfile = PropertyKeys.PATH + "/"+project+"/"+project+"_unwanted.txt";
		//cpath = gpath+"/features/";
		//this.featurepath = PropertyKeys.PATH+"/features/";
		
	}
	
	public void execute(String featuretype) throws IOException {
		
		switch(featuretype) {
			case TFIDFHigh: model_TFIDFHigh(); break;
			case Threat: model_T(); break;
			case Control: model_C(); break;
			case TC: model_TC(); break;
			case CA: model_CA(); break;
			case TA: model_TA(); break;
			case TCA: model_TCA(); break;
			case TCAI: model_TCAI(); break;
			
			default: break;
		}
	}

	// 1. TFIDF top100 high-low (terms can discriminate relevant documents)
	private void model_TFIDFHigh() throws IOException {
		projectSpecific = true;
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setOnlySecurityReports(true);							// terms are determined from SBR
		fs.setTraindatafile(trainfile);
		fs.setUnwantedfile(unwantedfile);		
		fs.printFinalDictionaryFile(fs.generateProjectTopNFeaturesTFIDF());		// Top 100
		featureConfig = fs;
	}
	
	// 2. Threat
	private void model_T() {
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/features/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(true);
		fs.setControl(false);
		fs.setAsset(false);
		fs.setImplicit(false);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	// 3. Control
	private void model_C() {
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/config/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(false);
		fs.setControl(true);
		fs.setAsset(false);
		fs.setImplicit(false);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	// 4. Threat-Control
	private void model_TC() {	
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/config/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(true);
		fs.setControl(true);
		fs.setAsset(false);
		fs.setImplicit(false);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	// 5. Control-Asset
	private void model_CA() {
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/config/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(false);
		fs.setControl(true);
		fs.setAsset(true);
		fs.setImplicit(false);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	// 6. Threat-Asset
	private void model_TA() {
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/config/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(true);
		fs.setControl(false);
		fs.setAsset(true);
		fs.setImplicit(false);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	// 7. Threat-Control-Asset
	private void model_TCA() {	
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/config/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(true);
		fs.setControl(true);
		fs.setAsset(true);
		fs.setImplicit(false);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	// 8. Threat-Control-Asset-Implicit
	private void model_TCAI() {	
		projectSpecific = false;
		//String cpath = PropertyKeys.PATH+"/config/";
		FeatureSelection fs = new FeatureSelection(dictionaryfile);
		fs.setAttack(true);
		fs.setControl(true);
		fs.setAsset(true);
		fs.setImplicit(true);		
		fs.printFinalDictionaryFile(fs.generateExternalFeatures());
		featureConfig = fs;
	}
	
	/**
	 * @return the featureConfig
	 */
	public FeatureSelection getFeatureConfig() {
		return featureConfig;
	}

	/**
	 * @return the projectSpecific
	 */
	public boolean isProjectSpecific() {
		return projectSpecific;
	}	
	
}
