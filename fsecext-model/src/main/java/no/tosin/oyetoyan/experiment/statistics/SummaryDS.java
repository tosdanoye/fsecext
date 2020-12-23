/**
 * 
 */
package no.tosin.oyetoyan.experiment.statistics;

/**
 * @author tdoy
 *
 */
public class SummaryDS {

	private String target;
	private double pgvalue;
	private double pfvalue;
	private double prvalue;
	private double gmean;
	private double gmin;
	private double gmax;
	private double gstddev;
	private double fmean;
	private double fmin;
	private double fmax;
	private double fstddev;
	private double rmean;
	private double rmin;
	private double rmax;
	private double rstddev;
	private double rocmean;
	private double rocmin;
	private double rocmax;
	private double rocstddev;
	
	
	// selection parameters
	private String featureMethod;				// feature selection method
	private double classRatio; 					// class balance ratio
	private String featureIncluded;				// is security term included in training set before modeling?
	private String securityCategory;			// security feature used
	private String algorithm;					// algorithm
	
	
	public SummaryDS() {
		// TODO Auto-generated constructor stub
	}


	public String getTarget() {
		return target;
	}


	public void setTarget(String target) {
		this.target = target                                                                                                                     ;
	}


	public double getPgvalue() {
		return pgvalue;
	}


	public void setPgvalue(double pgvalue) {
		this.pgvalue = pgvalue;
	}


	public double getPfvalue() {
		return pfvalue;
	}


	public void setPfvalue(double pfvalue) {
		this.pfvalue = pfvalue;
	}


	public double getPrvalue() {
		return prvalue;
	}


	public void setPrvalue(double prvalue) {
		this.prvalue = prvalue;
	}


	public double getGmean() {
		return gmean;
	}


	public void setGmean(double gmean) {
		this.gmean = gmean;
	}


	public double getGmin() {
		return gmin;
	}


	public void setGmin(double gmin) {
		this.gmin = gmin;
	}


	public double getGmax() {
		return gmax;
	}


	public void setGmax(double gmax) {
		this.gmax = gmax;
	}


	public double getGstddev() {
		return gstddev;
	}


	public void setGstddev(double gstddev) {
		this.gstddev = gstddev;
	}


	public double getFmean() {
		return fmean;
	}


	public void setFmean(double fmean) {
		this.fmean = fmean;
	}


	public double getFmin() {
		return fmin;
	}


	public void setFmin(double fmin) {
		this.fmin = fmin;
	}


	public double getFmax() {
		return fmax;
	}


	public void setFmax(double fmax) {
		this.fmax = fmax;
	}


	public double getFstddev() {
		return fstddev;
	}


	public void setFstddev(double fstddev) {
		this.fstddev = fstddev;
	}


	public double getRmean() {
		return rmean;
	}


	public void setRmean(double rmean) {
		this.rmean = rmean;
	}


	public double getRmin() {
		return rmin;
	}


	public void setRmin(double rmin) {
		this.rmin = rmin;
	}


	public double getRmax() {
		return rmax;
	}


	public void setRmax(double rmax) {
		this.rmax = rmax;
	}


	public double getRstddev() {
		return rstddev;
	}


	public void setRstddev(double rstddev) {
		this.rstddev = rstddev;
	}


	public String getFeatureMethod() {
		return featureMethod;
	}


	public void setFeatureMethod(String featureMethod) {
		this.featureMethod = featureMethod;
	}


	public double getClassRatio() {
		return classRatio;
	}


	public void setClassRatio(double classRatio) {
		this.classRatio = classRatio;
	}


	public String getFeatureIncluded() {
		return featureIncluded;
	}


	public void setFeatureIncluded(String featureIncluded) {
		this.featureIncluded = featureIncluded;
	}


	public String getSecurityCategory() {
		return securityCategory;
	}


	public void setSecurityCategory(String securityCategory) {
		this.securityCategory = securityCategory;
	}


	public double getRocmean() {
		return rocmean;
	}


	public void setRocmean(double rocmean) {
		this.rocmean = rocmean;
	}


	public double getRocmin() {
		return rocmin;
	}


	public void setRocmin(double rocmin) {
		this.rocmin = rocmin;
	}


	public double getRocmax() {
		return rocmax;
	}


	public void setRocmax(double rocmax) {
		this.rocmax = rocmax;
	}


	public double getRocstddev() {
		return rocstddev;
	}


	public void setRocstddev(double rocstddev) {
		this.rocstddev = rocstddev;
	}


	public String getAlgorithm() {
		return algorithm;
	}


	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

}
