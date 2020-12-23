/**
 * 
 */
package no.tosin.oyetoyan.keywords;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import no.tosin.oyetoyan.experiment.SetupExperiments;
import no.tosin.oyetoyan.experiment.Util;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;

/**
 * @author tdoy
 *
 */
public class TFIDFFeatures {

	private Map<String, Integer> tfreq;
	private Map<String, Double> idf;
	private List<String> documents;
	private List<String> terms;
	private List<TFIDF> tfIDF;
	
	public TFIDFFeatures() {

	}
	
	public List<String> getFeatures(int topN) {
		
		List<String> features = new ArrayList<>();

		for(int i=0; i<topN; i++) {
			String name = tfIDF.get(i).getTerm();
			
			features.add(name);
		}
		
		return features;
	}
	
	public void readData(String file, List<String> unwanted, boolean onlysecurityterms) throws IOException {
		
		documents = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		
		int i=0;
		while((line=br.readLine())!=null) {
			i++;
			if(i==0) continue;
			
			line = line.toLowerCase().trim();

			if(unwanted != null) {
				for(String u : unwanted) {
					//System.out.println(u);
					line = line.replace(u.trim(), " ");
				}
			}
			
			if(onlysecurityterms) {
				String[] cols = line.split(";");								// this is separated by semicolon
				String sec = cols[0].trim();
				if(sec.equals("1")) {	
					line = filterStrings(line);
					documents.add(line);
				}
			} else {
				line = Util.getOnlyStrings(line);
				documents.add(line);
			}
				

			
		}
		
		br.close();
		
	}
	
	public List<String> readStopwords(String file) throws IOException {
		
		List<String> stops = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";

		while((line=br.readLine())!=null) {
			stops.add(line.trim());
		}
		
		br.close();
		
		return stops;
	}
	
	public List<String> readFarsecDict(String file) throws IOException {
		
		List<String> dict = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";

		while((line=br.readLine())!=null) {
			String[] tokens = line.split(" ");
			for(String t : tokens)
				dict.add(t.trim());
		}
		
		br.close();
		
		return dict;
	}
	
	public void tokenize(List<String> stopwords) {
		Stemmer stemmer = new SnowballStemmer();
		tfreq = new HashMap<>();
		Set<String> tokens = new HashSet<>();
		for(String s : documents) {
			for(String t : s.split(" ")) {
				t = t.trim();
				if(!stopwords.contains(t) && t.length() > 2) {
					// stem?
					if(PropertyKeys.STEM)
						t = stemmer.stem(t);
					tokens.add(t);
					if(tfreq.containsKey(t)) {
						int value = tfreq.get(t) + 1;
						tfreq.put(t, value);
					} else {
						tfreq.put(t, 1);
					}
				}
			}
		}
		terms = new ArrayList<>(tokens);
	}
	
	public void computeAggregateTFIDF() {

		idf = new HashMap<>();
		tfIDF = new ArrayList<>();
		this.computeIDFCorpus();
		
		Map<String, Double> atfidf = new HashMap<>();

		for(String document : documents) {
			Map<String, Double> tfidfs = this.computeTFIDFDocument(document);
			for(String term : tfidfs.keySet()) {
				if(atfidf.containsKey(term)) {
					double val = atfidf.get(term) + tfidfs.get(term);
					atfidf.put(term, val);
				} else {
					atfidf.put(term, tfidfs.get(term));
				}
			}

		}
		// store the total tfidf
		atfidf.forEach((term, tidf) -> {
			tfIDF.add(new TFIDF(term, tidf));
		});
		
		// sort
		tfIDF.sort(Comparator.comparingDouble(TFIDF::getTfidf).reversed());
		
	}
	
	private Map<String, Double> computeTFIDFDocument(String document) {
		Map<String, Double> tfd = new HashMap<>();
		String[] terms = document.split(" ");
		for(String term : terms) {
			if(!this.terms.contains(term)) continue;
			term = term.trim();
			
			if(term.isEmpty()) continue;

			if(tfd.containsKey(term)) {
				double nval = tfd.get(term) + 1;
				tfd.put(term, nval);
			} else {
				tfd.put(term, 1.0);
			}
		}
		double max = 0;
		for(Double d : tfd.values())
			max = Math.max(max, d);
		
		// tfidf
		for(String term : tfd.keySet()) {
			
			double tfreq = tfd.get(term);
			// compute tf
			double tf = 0.5 + (0.5*tfreq/max);

			try {
				double tfidf = tf * this.idf.get(term);
				tfd.put(term, tfidf);
			}catch(Exception e) {
				System.out.println(term);
			}
		}
		
		return tfd;
	}
	
	public void computeIDFCorpus() {
		
		terms.forEach(term -> {
			int appear = 0;
			double N = documents.size();
			for(String document : documents) {
				List<String> tokens = Arrays.asList(document.split(" "));
				if(tokens.contains(term.trim()))
					appear++;
			}
			
			double t_idf = Math.log(N/((double)appear+1));
			
			idf.put(term, t_idf);
		});
		
	}
	
	
	private String filterStrings(String s) {
		Pattern pattern = Pattern.compile("[^a-z A-Z]");
	    Matcher matcher = pattern.matcher(s);
	    String string = matcher.replaceAll("");
	    
	    return string;
	}
	
	public void compare(List<String> features100, List<String> farsec) {
		
		List<String> owndiff = new ArrayList<>(features100);
		List<String> farsecdiff = new ArrayList<>(farsec);
		
		owndiff.retainAll(farsec);
		farsecdiff.retainAll(features100);
		
		System.out.println(owndiff);
		System.out.println(owndiff.size());
		System.out.println(farsecdiff);
		System.out.println(farsecdiff.size());
	}
	
	/**
	 * @return the terms
	 */
	public List<String> getTerms() {
		return terms;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		//wicket=87, derby=93, camel=88, ambari=92, chromium=93, mozilla, comm, apache, odcv
		String project = "derby";
		
		String fpath = PropertyKeys.PATH +"/features/";
		String datafile = fpath+"/"+project+"/"+project+"_train.csv";
		String stopfile = fpath+"/"+"stopwords/english";
		String unwantfile = fpath+"/"+project+"/"+project+"_unwanted.txt";
		String dictfile = fpath+"/"+project+"/"+project+"_dictfile.txt";
		String fdictfile = fpath+"/"+project+"/"+project+"_peters_dictfile.txt";
	
		TFIDFFeatures tfidf = new TFIDFFeatures();
		List<String> farsecdict = tfidf.readFarsecDict(fdictfile);
		
		List<String> stops = tfidf.readStopwords(stopfile);
		List<String> unwanted = null;
		try {
			unwanted = tfidf.readStopwords(unwantfile);
		}catch(FileNotFoundException e) {
			
		}
		tfidf.readData(datafile, unwanted, true);
		tfidf.tokenize(stops);

		tfidf.computeAggregateTFIDF();
		
		List<String> features100 = tfidf.getFeatures(100);								// we need top 100

		tfidf.compare(features100, farsecdict);
		
		PrintWriter pw = new PrintWriter(dictfile);
		pw.print(features100);
		pw.close();
		System.out.println(features100.size());

		System.out.println(features100);
		System.out.println(farsecdict);



	}


}
