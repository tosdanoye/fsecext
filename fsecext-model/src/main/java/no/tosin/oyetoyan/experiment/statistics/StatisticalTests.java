/**
 * 
 */
package no.tosin.oyetoyan.experiment.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import no.tosin.oyetoyan.experiment.Algorithm;
import no.tosin.oyetoyan.experiment.Selector;

/**
 * @author tdoy
 *
 */
public class StatisticalTests {


	private static Map<String, double[]> gmsec = new HashMap<>();
	private static Map<String, double[]> gmnsec = new HashMap<>();
	private static Map<String, double[]> fssec = new HashMap<>();
	private static Map<String, double[]> fsnsec = new HashMap<>();
	private static Map<String, double[]> rssec = new HashMap<>();
	private static Map<String, double[]> rsnsec = new HashMap<>();
	private static Map<String, double[]> rocssec = new HashMap<>();
	private static Map<String, double[]> rocsnsec = new HashMap<>();
	
	private static Map<String, List<AnalysisData>> modelsec = new HashMap<>();
	private static Map<String, List<AnalysisData>> modelnsec = new HashMap<>();

	private static Algorithm[] algorithms = Algorithm.values();	
	private static double[] ratios = {0,0.5,1,1.5,2};
	
	private static List<String> extaxis = new ArrayList<>();
	private static List<String> idfaxis = new ArrayList<>();
	
	private static List<AnalysisData> fsec_extp_tpp = new ArrayList<>();
	private static List<AnalysisData> fsec_extm_tpp = new ArrayList<>();
	private static List<AnalysisData> fsec_extp_wpp = new ArrayList<>();
	private static List<AnalysisData> fsec_extm_wpp = new ArrayList<>();
	
	private static List<AnalysisData> fsec_tfidfp_tpp = new ArrayList<>();
	private static List<AnalysisData> fsec_tfidfm_tpp = new ArrayList<>();
	private static List<AnalysisData> fsec_tfidfp_wpp = new ArrayList<>();
	private static List<AnalysisData> fsec_tfidfm_wpp = new ArrayList<>();
	
	private static List<AnalysisData> bestcombined;
	private static List<SummaryDS> bestsummcombined;
	private static List<SummaryDS> top20_wpp = new ArrayList<>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		AnalysisMain.process();
		
		Map<String, List<AnalysisData>> wpp = AnalysisMain.statPerTargetWPP();
		
		Map<String, List<AnalysisData>> tpp = AnalysisMain.statPerTargetTPP();
		
		setupCats();
		
		summaryStatsAndBest(tpp, "summ_tpp");
		mergeAndBest(tpp, "all_tpp", true, false);		// unpaired data
		mergeAndBest(wpp, "all_wpp", false, false);		// unpaired data
		
		mergeAndBest(tpp, "all_paired_tpp", true, true);		// paired data
		mergeAndBest(wpp, "all_paired_wpp", false, true);		// paired data
		
		globalData();

	}
	
	private static void globalData() {
		// fsec-ext
		String fname = "fsec_extp_tpp.csv";
		sortAndPrint2(fsec_extp_tpp, fname);
		
		fname = "fsec_extm_tpp.csv";
		sortAndPrint2(fsec_extm_tpp, fname);
		
		fname = "fsec_extp_wpp.csv";
		sortAndPrint2(fsec_extp_wpp, fname);
		
		fname = "fsec_extm_wpp.csv";
		sortAndPrint2(fsec_extm_wpp, fname);
		
		// fsec-tfidf
		fname = "fsec_tfidfp_tpp.csv";
		sortAndPrint2(fsec_tfidfp_tpp, fname);
		
		fname = "fsec_tfidfm_tpp.csv";
		sortAndPrint2(fsec_tfidfm_tpp, fname);
		
		fname = "fsec_tfidfp_wpp.csv";
		sortAndPrint2(fsec_tfidfp_wpp, fname);
		
		fname = "fsec_tfidfm_wpp.csv";
		sortAndPrint2(fsec_tfidfm_wpp, fname);
	}
	
	private static void setupCats() {
		// group all fsecext	
		//extaxis.add(Selector.Threat.name());
		//extaxis.add(Selector.Control.name());
		extaxis.add(Selector.TA.name());
		extaxis.add(Selector.CA.name());
		extaxis.add(Selector.TC.name());
		extaxis.add(Selector.TCA.name());
		extaxis.add(Selector.TCAI.name());
		
		// group all fsectfidf
		//idfaxis.add(Selector.TFIDFHigh.name());

	}
	
	private static void separateSecAndNoSec(List<AnalysisData> data) {
		modelsec = new HashMap<>();
		modelnsec = new HashMap<>();
		
		// method
		Selector[] featuretypes = Selector.values();
		
		for(Selector m : featuretypes) { 
			List<AnalysisData> model_sec = new ArrayList<>();
			List<AnalysisData> model_nsec = new ArrayList<>();
			for(AnalysisData d : data) {
				if(m.name().equals(d.getFeatureSelectionMethod()) 
						&& d.isSecurityFeatureIncluded().equals("true"))
					model_sec.add(d);
				else if(m.name().equals(d.getFeatureSelectionMethod()) 
						&& d.isSecurityFeatureIncluded().equals("false"))
					model_nsec.add(d);
			}
			
			modelsec.put(m.name(), model_sec);
			modelnsec.put(m.name(), model_nsec);
		}
	}  
	
	private static void populate(List<AnalysisData> model_sec, List<AnalysisData> model_nsec, String mname) {
		
		gmsec = new HashMap<>();
		gmnsec = new HashMap<>();
		fssec = new HashMap<>();
		fsnsec = new HashMap<>();
		rssec = new HashMap<>();
		rsnsec = new HashMap<>();
		rocssec = new HashMap<>();
		rocsnsec = new HashMap<>();
		
		double[] gsec = new double[model_sec.size()];
		double[] fsec = new double[model_sec.size()];
		double[] rsec = new double[model_sec.size()];
		double[] rocsec = new double[model_sec.size()];
		
		double[] gnsec = new double[model_nsec.size()];
		double[] fnsec = new double[model_nsec.size()];
		double[] rnsec = new double[model_nsec.size()];
		double[] rocnsec = new double[model_nsec.size()];
		
		for(int i=0; i<model_sec.size(); i++) {
			AnalysisData d = model_sec.get(i);
			if(d.getGmeasure() == Double.NaN)
				gsec[i] = 0;
			else
				gsec[i] = d.getGmeasure();
			
			if(d.getFmeasure() == Double.NaN)
				fsec[i] = 0;
			else
				fsec[i] = d.getFmeasure();
			
			if(d.getPd() == Double.NaN)
				rsec[i] = 0;
			else
				rsec[i] = d.getPd();
			if(d.getAuroc() == Double.NaN)
				rocsec[i] = 0;
			else
				rocsec[i] = d.getAuroc();
		}
		
		for(int i=0; i<model_nsec.size(); i++) {
			AnalysisData d = model_nsec.get(i);
			
			if(d.getGmeasure() == Double.NaN)
				gnsec[i] = 0;
			else
				gnsec[i] = d.getGmeasure();
			
			if(d.getFmeasure() == Double.NaN)
				fnsec[i] = 0;
			else
				fnsec[i] = d.getFmeasure();
			
			if(d.getPd() == Double.NaN)
				rnsec[i] = 0;
			else
				rnsec[i] = d.getPd();
			if(d.getAuroc() == Double.NaN)
				rocnsec[i] = 0;
			else
				rocnsec[i] = d.getAuroc();
		}
		
		gmsec.put(mname, gsec);
		gmnsec.put(mname, gnsec);
		fssec.put(mname, fsec);
		fsnsec.put(mname, fnsec);
		rssec.put(mname, rsec);
		rsnsec.put(mname, rnsec);
		rocssec.put(mname, rocsec);
		rocsnsec.put(mname, rocnsec);
	}
	
	/**
	 * Aggregate the statistics per project per method per include-sec (i.e. true or false) and sort (best - worst)
	 * examples
	 * TPP:
	 * 	source   target  method      	alg  ratio security.cat	include-sec  	performance (e.g.)
	 *  ambari   camel   fsec-ext   	LR   0.0   CA			True			1 (Pd, f-score, g-measure)
	 *  chromium camel   fsec-ext   	KNN  2.0   TA			True			2 (Pd, f-score, g-measure)
	 *  wicket   camel   fsec-ext   	NB   0.0   C			True 			3 (Pd, f-score, g-measure)
	 *  derby    camel   fsec-ext   	LR   1.5   TCA			True 			4 (Pd, f-score, g-measure)
	 *  
	 *  WPP:
	 * 	source   target  method      	alg  ratio security.cat	include-sec  	performance (e.g.)
	 *  camel    camel   fsec-ext   	LR   0.0   CA			True			1 (Pd, f-score, g-measure)
	 *  camel 	 camel   fsec-ext   	KNN  2.0   TA			True			2 (Pd, f-score, g-measure)
	 *  camel    camel   fsec-ext   	NB   0.0   C			True 			3 (Pd, f-score, g-measure)
	 *  camel    camel   fsec-ext   	LR   1.5   TCA			True 			4 (Pd, f-score, g-measure)
	 *  
	 *  Best is at index zero
	 * @param data
	 * @param type
	 * @param tpp
	 */
	private static void mergeAndBest(Map<String, List<AnalysisData>> data, String type, boolean tpp, boolean paired) {
		List<AnalysisData> bestsec = new ArrayList<>();
		List<AnalysisData> bestnsec = new ArrayList<>();
		bestcombined = new ArrayList<>();
		
		// per target
		data.forEach((target, pdata) -> {
			// fsecext
			List<AnalysisData> fsecext_t = new ArrayList<>();		// sec categories included in training dataset
			List<AnalysisData> fsecext_f = new ArrayList<>();		// sec categories NOT included in training dataset
			// group all fsectfidf
			List<AnalysisData> fsectfidf_t = new ArrayList<>();		// same description as above
			List<AnalysisData> fsectfidf_f = new ArrayList<>();

			for(AnalysisData d : pdata) {

				String model = d.getFeatureSelectionMethod();
				if(d.isSecurityFeatureIncluded().equals("true")) {
					if(extaxis.contains(model)) {
						fsecext_t.add(d);
						if(tpp) {
							fsec_extp_tpp.add(d);
						} else {
							fsec_extp_wpp.add(d);
						}
					}
					if(idfaxis.contains(model)) {
						fsectfidf_t.add(d);
						if(tpp) {
							fsec_tfidfp_tpp.add(d);
						} else {
							fsec_tfidfp_wpp.add(d);
						}
					}
					
				} else {
					if(extaxis.contains(model)) {
						fsecext_f.add(d);
						if(tpp) {
							fsec_extm_tpp.add(d);
						} else {
							fsec_extm_wpp.add(d);
						}
					}
					if(idfaxis.contains(model)) {
						fsectfidf_f.add(d);
						if(tpp) {
							fsec_tfidfm_tpp.add(d);
						} else {
							fsec_tfidfm_wpp.add(d);
						}
					}
				}
			}
			
			// build and print to file
			String fname = target+"/fsecext_true_"+type+".csv";			
			if(paired) 
				generatePairedData(fsecext_t, fname);
			else
				sortAndPrint(fsecext_t, bestsec, fname, tpp, target, "fsec-ext+");

			fname = target+"/fsecext_false_"+type+".csv";			
			if(paired) 
				generatePairedData(fsecext_f, fname);
			else
				sortAndPrint(fsecext_f, bestnsec, fname, tpp, target, "fsec-ext");
			
			fname = target+"/fsectfidf_true_"+type+".csv";
			if(paired) 
				generatePairedData(fsectfidf_t, fname);
			else
				sortAndPrint(fsectfidf_t, bestsec, fname, tpp, target, "fsec-tfidf+");

			fname = target+"/fsectfidf_false_"+type+".csv";			
			if(paired) 
				generatePairedData(fsectfidf_f, fname);
			else
				sortAndPrint(fsectfidf_f, bestnsec, fname, tpp, target, "fsec-tfidf");
		});
		
		String fname = "/best_true_"+type+".csv";
		if(!paired) {
			
			StringBuffer sb = AnalysisMain.build(bestsec);
			AnalysisMain.printtofile(fname, sb);
			
			fname = "/best_false_"+type+".csv";
			sb = AnalysisMain.build(bestnsec);
			AnalysisMain.printtofile(fname, sb);
			
			fname = "/best_combined_"+type+".csv";
			sb = AnalysisMain.build(bestcombined);
			AnalysisMain.printtofile(fname, sb);
		}
		
		if(!tpp && !paired) {
			// build and print
			fname = "/allstats_"+type+".csv";
			StringBuffer sb = build(top20_wpp);
			AnalysisMain.printtofile(fname, sb);
		}
		
	}
	
	/**
	 * TPP: For a target, compute the summary statistics (avg, min, max, std)
	 *  for the combination (method, alg, ratio, includesec)
	 *  e.g.
	 *  source   target  method      alg  ratio include-sec  	performance
	 *  ambari   camel   TFIDFHigh   LR   0.0   True			1 (Pd, f-score, g-measure)
	 *  chromium camel   TFIDFHigh   LR   0.0   True			2 (Pd, f-score, g-measure)
	 *  wicket   camel   TFIDFHigh   LR   0.0   True 			3 (Pd, f-score, g-measure)
	 *  derby    camel   TFIDFHigh   LR   0.0   True 			4 (Pd, f-score, g-measure)
	 *  odcv     camel   TFIDFHigh   LR   0.0   True 			5 (Pd, f-score, g-measure)
	 *  apache   camel   TFIDFHigh   LR   0.0   True 			6 (Pd, f-score, g-measure)
	 *  mozilla  camel   TFIDFHigh   LR   0.0   True 			7 (Pd, f-score, g-measure)
	 *  comm     camel   TFIDFHigh   LR   0.0   True 			8 (Pd, f-score, g-measure)
	 *  Then, we find the min, max, mean, std of Pd, f-score, g-measure
	 *  These statistics are very important for TPP and are proof that a method is robust and can generalize well
	 *  A robust method will have high min, mean and low std.dev across different metrics
	 *  A poor or unstable method will have low min and high std.dev across different metrics
	 * @param data
	 * @param type
	 */
	private static void summaryStatsAndBest(Map<String, List<AnalysisData>> data, String type) {
		
		List<SummaryDS> bestsec = new ArrayList<>();
		List<SummaryDS> bestnsec = new ArrayList<>();
		bestsummcombined = new ArrayList<>();
		
		// per target
		data.forEach((target, pdata) -> {
			// fsecext
			List<SummaryDS> fsecext_t = new ArrayList<>();		// sec categories included in training dataset
			List<SummaryDS> fsecext_f = new ArrayList<>();		// sec categories NOT included in training dataset
			// group all fsectfidf
			List<SummaryDS> fsectfidf_t = new ArrayList<>();
			List<SummaryDS> fsectfidf_f = new ArrayList<>();

			separateSecAndNoSec(pdata);				// per feature selection model
			modelsec.forEach((model, mdata) -> {	// for each feature selection type
				List<AnalysisData> nmdata = modelnsec.get(model);		// no sec inlcuded in train
				// group by ratio and algorithm and find the min, mean, max, and std.dev for each metric (g, fscore, pd)
				for(double ratio : ratios) {					
					for(Algorithm algorithm : algorithms) {
						String alg = algorithm.name();
						List<AnalysisData> secdata = new ArrayList<>();
						for(AnalysisData d : mdata) {
							if(d.getAlgorithm().equals(alg) && d.getTrainBalanceRatio()==ratio) {
								secdata.add(d);
							}
						}
						
						List<AnalysisData> nsecdata = new ArrayList<>();
						for(AnalysisData d : nmdata) {
							if(d.getAlgorithm().equals(alg) && d.getTrainBalanceRatio()==ratio) {
								nsecdata.add(d);
							}
						}
						populate(secdata, nsecdata, model);	 		// 
						if(extaxis.contains(model)) {
							try {
								summByRatioAlgorithm(target, model, alg, ratio,
									secdata.get(0).isSecurityFeatureIncluded(), nsecdata.get(0).isSecurityFeatureIncluded(), fsecext_t, fsecext_f);
							}catch(Exception e) {
								
							}
						}
						if(idfaxis.contains(model)) {
							try {
								summByRatioAlgorithm(target, model, alg, ratio,
									secdata.get(0).isSecurityFeatureIncluded(), nsecdata.get(0).isSecurityFeatureIncluded(), fsectfidf_t, fsectfidf_f);
							}catch(Exception e) {
								
							}
						}

					}
				}

			});
			
			// check that folder exist otherwise create one
			String pdir = AnalysisMain.path+"/analysis/"+target;
			if(!new File(pdir).exists())
				new File(pdir).mkdir();
			
			// build and print to file
			String fname = target+"/fsecext_true_"+type+".csv";
			collate(fsecext_t, bestsec, fname);

			fname = target+"/fsecext_false_"+type+".csv";
			collate(fsecext_f, bestnsec, fname);
			
			fname = target+"/fsectfidf_true_"+type+".csv";
			collate(fsectfidf_t, bestsec, fname);

			fname = target+"/fsectfidf_false_"+type+".csv";
			collate(fsectfidf_f, bestnsec, fname);

		});
		
		String fname = "/best_true_"+type+".csv";
		StringBuffer sb = build(bestsec);
		AnalysisMain.printtofile(fname, sb);
		
		fname = "/best_false_"+type+".csv";
		sb = build(bestnsec);
		AnalysisMain.printtofile(fname, sb);
		
		fname = "/best_combined_"+type+".csv";
		sb = build(bestsummcombined);
		AnalysisMain.printtofile(fname, sb);
	}
	
	private static void collate(List<SummaryDS> data, List<SummaryDS> best, String fname) {
		
		data.sort(Comparator.comparing(SummaryDS::getGmean)
				.thenComparing(SummaryDS::getFmean)
				.thenComparing(SummaryDS::getRmean).reversed());
		bestsummcombined.add(data.get(0));
		best.add(data.get(0));
		StringBuffer sb = build(data);
		AnalysisMain.printtofile(fname, sb);
	}
	
	private static void sortAndPrint(List<AnalysisData> data, List<AnalysisData> best, String fname, boolean tpp, String target, String model) {
		data.sort(Comparator.comparing(AnalysisData::getGmeasure)
				.thenComparing(AnalysisData::getFmeasure)
				.thenComparing(AnalysisData::getPd).reversed());
		
		if(best != null) {
			best.add(data.get(0));
			bestcombined.add(data.get(0));
		}
		StringBuffer sb = AnalysisMain.build(data);
		AnalysisMain.printtofile(fname, sb);
		
		if(!tpp)
			statsTop20(data, target, model);
	}
	
	private static void generatePairedData(List<AnalysisData> data, String fname) {
		data.sort(Comparator.comparing(AnalysisData::getTarget)
		.thenComparing(AnalysisData::getAlgorithm)
		.thenComparing(AnalysisData::getTrainBalanceRatio)
		.thenComparing(AnalysisData::getSource));
		
		StringBuffer sb = AnalysisMain.build(data);
		AnalysisMain.printtofile(fname, sb);
	}
	
	private static void sortAndPrint2(List<AnalysisData> data, String fname) {
		data.sort(Comparator.comparing(AnalysisData::getTarget)
				.thenComparing(AnalysisData::getAlgorithm)
				.thenComparing(AnalysisData::getTrainBalanceRatio)
				.thenComparing(AnalysisData::getFeatureSelectionMethod));

		StringBuffer sb = AnalysisMain.build(data);
		AnalysisMain.printtofile(fname, sb);
	}
	
	private static void statsTop20(List<AnalysisData> data, String target, String model) {
		
		// collect top20 observations for wpp and compute the mean for each metric
		int size = 20;
		double[] gm = new double[size];
		double[] fs = new double[size];
		double[] rs = new double[size];
		double[] roc = new double[size];
		
		for(int i=0; i<size; i++) {
			gm[i] = data.get(i).getGmeasure();
			fs[i] = data.get(i).getFmeasure();
			rs[i] = data.get(i).getPd();
			roc[i] = data.get(i).getAuroc();
		}
		
		SummaryStatistics gsumm = summarystats(gm);
		SummaryStatistics fsumm = summarystats(fs);
		SummaryStatistics rsumm = summarystats(rs);
		SummaryStatistics rocsumm = summarystats(roc);
		
		SummaryDS summ = new SummaryDS();
		summ.setTarget(target);
		summ.setFeatureMethod(model);
		setValues(summ, gsumm, fsumm, rsumm, rocsumm);
		
		top20_wpp.add(summ);
	}
	
	private static void summByRatioAlgorithm(String target, String model, String alg, double ratio, String secincl, String nsecincl, List<SummaryDS> fsec, List<SummaryDS> fnsec) {
		double[] gm = gmsec.get(model);
		double[] fs = fssec.get(model);
		double[] rs = rssec.get(model);
		double[] rocs = rocssec.get(model);
		
		double[] gmn = gmnsec.get(model);
		double[] fsn = fsnsec.get(model);
		double[] rsn = rsnsec.get(model);
		double[] rocsn = rocsnsec.get(model);
		
		// summary sec included
		SummaryStatistics gstat = summarystats(gm);
		SummaryStatistics fstat = summarystats(fs);
		SummaryStatistics rstat = summarystats(rs);
		SummaryStatistics rocstat = summarystats(rocs);
		
		SummaryDS summ = new SummaryDS();
		summ.setTarget(target);
		summ.setFeatureMethod(model);
		summ.setAlgorithm(alg);
		summ.setFeatureIncluded(secincl);
		summ.setClassRatio(ratio);
		setValues(summ, gstat, fstat, rstat, rocstat);
		fsec.add(summ);
		
		// summary sec Not included
		SummaryStatistics gnstat = summarystats(gmn);
		SummaryStatistics fnstat = summarystats(fsn);
		SummaryStatistics rnstat = summarystats(rsn);
		SummaryStatistics rocnstat = summarystats(rocsn);
		
		SummaryDS nsumm = new SummaryDS();
		nsumm.setTarget(target);
		nsumm.setFeatureMethod(model);
		nsumm.setAlgorithm(alg);
		nsumm.setFeatureIncluded(nsecincl);
		nsumm.setClassRatio(ratio);
		setValues(nsumm, gnstat, fnstat, rnstat, rocnstat);
		fnsec.add(nsumm);
	}
	
	private static void setValues(SummaryDS summ, SummaryStatistics gstat, SummaryStatistics fstat,
			SummaryStatistics rstat, SummaryStatistics rocstat) {
		// g-measure
		summ.setGmin(gstat.getMin());
		summ.setGmax(gstat.getMax());
		summ.setGmean(gstat.getMean());
		summ.setGstddev(gstat.getStandardDeviation());
		
		// f-measure
		summ.setFmin(fstat.getMin());
		summ.setFmax(fstat.getMax());
		summ.setFmean(fstat.getMean());
		summ.setFstddev(fstat.getStandardDeviation());
		
		// recall (Pd)
		summ.setRmin(rstat.getMin());
		summ.setRmax(rstat.getMax());
		summ.setRmean(rstat.getMean());
		summ.setRstddev(rstat.getStandardDeviation());
		
		// roc
		summ.setRocmin(rocstat.getMin());
		summ.setRocmax(rocstat.getMax());
		summ.setRocmean(rocstat.getMean());
		summ.setRocstddev(rocstat.getStandardDeviation());
	}
	
	private static SummaryStatistics summarystats(double[] data) {
		
		SummaryStatistics s = new SummaryStatistics();
		
		for(int i=0; i<data.length; i++) {
			s.addValue(data[i]);
		}
		
		return s;
	}
	
	private static StringBuffer build(List<SummaryDS> m) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("target ; method; ratio; feature-include; security.category; "
				+ "algorithm; gmin; gmax; gmean; gstd; fmin; fmax; fmean; fstd; rmin; rmax; rmean; "
				+ "rstd; rocmin; rocmax; rocmean; rocstd \n");
		
		for(int i=0; i<m.size(); i++) {
			SummaryDS d = m.get(i);
			sb.append(d.getTarget()+";"+d.getFeatureMethod()+";"+d.getClassRatio()+";"+d.getFeatureIncluded()+";"+
					d.getFeatureMethod()+";"+d.getAlgorithm()+";"+d.getGmin()+";"+d.getGmax()+";"+d.getGmean()+";"+d.getGstddev()+";"+
					d.getFmin()+";"+d.getFmax()+";"+d.getFmean()+";"+d.getFstddev()+";"+
					d.getRmin()+";"+d.getRmax()+";"+d.getRmean()+";"+d.getRstddev()+";"+
					d.getRocmin()+";"+d.getRocmax()+";"+d.getRocmean()+";"+d.getRocstddev()+"\n");
		}
		
		return sb;
	}

}
