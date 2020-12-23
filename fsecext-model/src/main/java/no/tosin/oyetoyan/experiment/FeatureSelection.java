/**
 * 
 */
package no.tosin.oyetoyan.experiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import no.tosin.oyetoyan.keywords.PropertyKeys;
import no.tosin.oyetoyan.keywords.TFIDFFeatures;

/**
 * @author tdoy
 *
 */
public class FeatureSelection {

	private boolean attack;
	private boolean control;
	private boolean asset;
	private boolean implicit;
	
	private String traindatafile;
	private String stopwordfile = PropertyKeys.PATH+"/stopwords/english";
	private String unwantedfile;
	
	private boolean onlySecurityReports;
	private String dictionaryfile;
	
	public FeatureSelection(String dictionaryfile) {		
		this.dictionaryfile = dictionaryfile;
	}
	
	public void printFinalDictionaryFile(List<String> features) {
		
		Util.writeResultIntoFile(features, dictionaryfile);
	}
	
	public List<String> generateExternalFeatures() {
		
		List<String> features = Util.chooseFeatures(control, attack, asset, implicit);
		
		return features;
		
	}
	
	public List<String> generateProjectTopNFeaturesTFIDFNew() throws IOException {

		List<String> features = Util.loadDict(dictionaryfile); // can use dictfile instead of generating list everytime
		
		return features;								
	}
	
	public List<String> generateProjectTopNFeaturesTFIDF() throws IOException {

		TFIDFFeatures fsec = new TFIDFFeatures();
		generateFeatures(fsec);
		
		fsec.computeAggregateTFIDF();
		List<String> features = new ArrayList<>();
		features.add("");	// add nothing at the begining. Will be overwritten by weka class-att. Otherwise, we loose the first feature
		
		int topn = PropertyKeys.TOPN;
		features.addAll(fsec.getFeatures(topn));
		return features;								
	}

	private void generateFeatures(TFIDFFeatures fsec) throws IOException {
		
		List<String> stops = fsec.readStopwords(stopwordfile);
		List<String> unwanted = null;
		try {
			unwanted = fsec.readStopwords(unwantedfile);
		}catch(FileNotFoundException e) {
			//
		}
		fsec.readData(traindatafile, unwanted, onlySecurityReports);
		fsec.tokenize(stops);
	}
	
	/**
	 * @param attack the attack to set
	 */
	public void setAttack(boolean attack) {
		this.attack = attack;
	}

	/**
	 * @param control the control to set
	 */
	public void setControl(boolean control) {
		this.control = control;
	}

	/**
	 * @param asset the asset to set
	 */
	public void setAsset(boolean asset) {
		this.asset = asset;
	}

	/**
	 * @param implicit the implicit to set
	 */
	public void setImplicit(boolean implicit) {
		this.implicit = implicit;
	}

	/**
	 * @return the attack
	 */
	public boolean isAttack() {
		return attack;
	}

	/**
	 * @return the control
	 */
	public boolean isControl() {
		return control;
	}

	/**
	 * @return the asset
	 */
	public boolean isAsset() {
		return asset;
	}

	/**
	 * @return the implicit
	 */
	public boolean isImplicit() {
		return implicit;
	}

	/**
	 * @return the dictionaryfile
	 */
	public String getDictionaryfile() {
		return dictionaryfile;
	}

	/**
	 * @param traindatafile the traindatafile to set
	 */
	public void setTraindatafile(String traindatafile) {
		this.traindatafile = traindatafile;
	}

	/**
	 * @param unwantedfile the unwantedfile to set
	 */
	public void setUnwantedfile(String unwantedfile) {
		this.unwantedfile = unwantedfile;
	}

	/**
	 * @param onlySecurityTerms the onlySecurityTerms to set
	 */
	public void setOnlySecurityReports(boolean onlySecurityTerms) {
		this.onlySecurityReports = onlySecurityTerms;
	}
	

}
