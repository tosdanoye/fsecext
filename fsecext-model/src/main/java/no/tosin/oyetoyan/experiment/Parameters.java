package no.tosin.oyetoyan.experiment;

import java.util.ArrayList;
import java.util.List;

public class Parameters {
	
	private String projectDataset;
	private String trainDataset;
	private String testDataset;	
	private List<String> validationDatasets;

	
	public Parameters(String project) {
		validationDatasets = new ArrayList<String>();
		this.projectDataset = project;
	}
	
	/**
	 * @return the projectDataset
	 */
	public String getProjectDataset() {
		return projectDataset;
	}
	
	public void setTestDataset(String testDataset) {
		this.testDataset = testDataset;
	}

	/**
	 * @return the testDataset
	 */
	public String getTestDataset() {
		return testDataset;
	}
	public String getTrainDataset() {
		return trainDataset;
	}

	public void setTrainDataset(String trainDataset) {
		this.trainDataset = trainDataset;
	}
	
	public void addValidationDataset(String testdata) {
		validationDatasets.add(testdata);
	}
	public List<String> getValidationDatasets() {
		return validationDatasets;
	}

}
