/**
 * 
 */
package no.tosin.oyetoyan.experiment;

/**
 * @author tosindo
 *
 */
public class Performance {
	
	private double recall;
	private double precision;
	private double fscore;
	private double pf;
	private double gmeasure;
	private double auroc;
	private double auprc;
	private double kappa;
	
	/**
	 * @return the recall
	 */
	public double getRecall() {
		return recall;
	}
	/**
	 * @param recall the recall to set
	 */
	public void setRecall(double recall) {
		this.recall = recall;
	}
	/**
	 * @return the precision
	 */
	public double getPrecision() {
		return precision;
	}
	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	/**
	 * @return the fscore
	 */
	public double getFscore() {
		return fscore;
	}
	/**
	 * @param fscore the fscore to set
	 */
	public void setFscore(double fscore) {
		this.fscore = fscore;
	}
	public double getPf() {
		return pf;
	}
	public void setPf(double pf) {
		this.pf = pf;
	}
	public double getGmeasure() {
		return gmeasure;
	}
	public void setGmeasure(double gmeasure) {
		this.gmeasure = gmeasure;
	}
	/**
	 * @return the auroc
	 */
	public double getAUROC() {
		return auroc;
	}
	/**
	 * @param auroc the auroc to set
	 */
	public void setAUROC(double auroc) {
		this.auroc = auroc;
	}
	/**
	 * @return the auprc
	 */
	public double getAUPRC() {
		return auprc;
	}
	/**
	 * @param auprc the auprc to set
	 */
	public void setAUPRC(double auprc) {
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
