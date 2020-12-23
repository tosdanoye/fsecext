/**
 * 
 */
package no.tosin.oyetoyan.experiment.statistics;

/**
 * @author tdoy
 *
 */
public class AnalysisData {

	private String source;								// source project used for training
	private String target;								// target project used for testing
	
	// selection parameters
	private String featureSelectionMethod;				// feature selection method
	private double trainBalanceRatio; 					// class balance ratio
	private String securityFeatureIncluded;				// is security term included in training set before modeling?
	private String securityFeatureType;					// security feature used
	
	// algorithm
	private String algorithm;
	
	// metrics
	private double tn;
	private double tp;
	private double fn;
	private double fp;
	private double pd;					// recall
	private double pf;
	private double prec;
	private double fmeasure;
	private double gmeasure;
	private double auroc;
	private double auprc;
	private double kappa;
	
	
	public AnalysisData() {
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}


	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}


	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}


	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}


	/**
	 * @return the featureSelectionMethod
	 */
	public String getFeatureSelectionMethod() {
		return featureSelectionMethod;
	}


	/**
	 * @param featureSelectionMethod the featureSelectionMethod to set
	 */
	public void setFeatureSelectionMethod(String featureSelectionMethod) {
		this.featureSelectionMethod = featureSelectionMethod;
	}


	/**
	 * @return the trainBalanceRatio
	 */
	public double getTrainBalanceRatio() {
		return trainBalanceRatio;
	}


	/**
	 * @param trainBalanceRatio the trainBalanceRatio to set
	 */
	public void setTrainBalanceRatio(double trainBalanceRatio) {
		this.trainBalanceRatio = trainBalanceRatio;
	}


	/**
	 * @return the securityFeatureIncluded
	 */
	public String isSecurityFeatureIncluded() {
		return securityFeatureIncluded;
	}


	/**
	 * @param securityFeatureIncluded the securityFeatureIncluded to set
	 */
	public void setSecurityFeatureIncluded(String securityFeatureIncluded) {
		this.securityFeatureIncluded = securityFeatureIncluded;
	}


	/**
	 * @return the securityFeatureType
	 */
	public String getSecurityFeatureType() {
		return securityFeatureType;
	}


	/**
	 * @param securityFeatureType the securityFeatureType to set
	 */
	public void setSecurityFeatureType(String securityFeatureType) {
		this.securityFeatureType = securityFeatureType;
	}


	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}


	/**
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}


	/**
	 * @return the tn
	 */
	public double getTn() {
		return tn;
	}


	/**
	 * @param tn the tn to set
	 */
	public void setTn(double tn) {
		this.tn = tn;
	}


	/**
	 * @return the tp
	 */
	public double getTp() {
		return tp;
	}


	/**
	 * @param tp the tp to set
	 */
	public void setTp(double tp) {
		this.tp = tp;
	}


	/**
	 * @return the fn
	 */
	public double getFn() {
		return fn;
	}


	/**
	 * @param fn the fn to set
	 */
	public void setFn(double fn) {
		this.fn = fn;
	}


	/**
	 * @return the fp
	 */
	public double getFp() {
		return fp;
	}


	/**
	 * @param fp the fp to set
	 */
	public void setFp(double fp) {
		this.fp = fp;
	}


	/**
	 * @return the pd
	 */
	public double getPd() {
		return pd;
	}


	/**
	 * @param pd the pd to set
	 */
	public void setPd(double pd) {
		this.pd = pd;
	}


	/**
	 * @return the pf
	 */
	public double getPf() {
		return pf;
	}


	/**
	 * @param pf the pf to set
	 */
	public void setPf(double pf) {
		this.pf = pf;
	}


	/**
	 * @return the prec
	 */
	public double getPrec() {
		return prec;
	}


	/**
	 * @param prec the prec to set
	 */
	public void setPrec(double prec) {
		this.prec = prec;
	}


	/**
	 * @return the fmesaure
	 */
	public double getFmeasure() {
		return fmeasure;
	}


	/**
	 * @param fmesaure the fmesaure to set
	 */
	public void setFmeasure(double fmeasure) {
		this.fmeasure = fmeasure;
	}


	/**
	 * @return the gmeasure
	 */
	public double getGmeasure() {
		return gmeasure;
	}


	/**
	 * @param gmeasure the gmeasure to set
	 */
	public void setGmeasure(double gmeasure) {
		this.gmeasure = gmeasure;
	}


	/**
	 * @return the auroc
	 */
	public double getAuroc() {
		return auroc;
	}


	/**
	 * @param auroc the auroc to set
	 */
	public void setAuroc(double auroc) {
		this.auroc = auroc;
	}


	/**
	 * @return the auprc
	 */
	public double getAuprc() {
		return auprc;
	}


	/**
	 * @param auprc the auprc to set
	 */
	public void setAuprc(double auprc) {
		this.auprc = auprc;
	}


	/**
	 * @return the kappa
	 */
	public double getKappa() {
		return kappa;
	}


	/**
	 * @param kappa the kappa to set
	 */
	public void setKappa(double kappa) {
		this.kappa = kappa;
	}
	

}
