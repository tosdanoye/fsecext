/**
 * 
 */
package no.tosin.oyetoyan.experiment;

import weka.classifiers.Classifier;

/**
 * @author tdoy
 *
 */
public class BestModel {

	private String project;
	private String featuretype;
	private double meanGmeasure;
	private Classifier model;
	private String algorithm;
	private double ratio;
	private boolean secinclude;
	
	public BestModel(String project, String ft, double mg,Classifier model, 
			String algorithm, double ratio, boolean secinclude) {
		this.project = project;
		this.featuretype = ft;
		this.meanGmeasure = mg;
		this.model = model;
		this.algorithm = algorithm;
		this.ratio = ratio;
		this.secinclude = secinclude;
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

	/**
	 * @return the featuretype
	 */
	public String getFeaturetype() {
		return featuretype;
	}

	/**
	 * @return the meanGmeasure
	 */
	public double getMeanGmeasure() {
		return meanGmeasure;
	}

	/**
	 * @return the model
	 */
	public Classifier getModel() {
		return model;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @return the ratio
	 */
	public double getRatio() {
		return ratio;
	}

	/**
	 * @return the secinclude
	 */
	public boolean isSecinclude() {
		return secinclude;
	}

}
