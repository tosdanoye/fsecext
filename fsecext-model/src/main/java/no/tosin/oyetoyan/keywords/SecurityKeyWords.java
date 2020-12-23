/**
JiraSecPlugin 
Copyright 2016 Tosin Daniel Oyetoyan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/


package no.tosin.oyetoyan.keywords;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author tosindo
 *
 */
public final class SecurityKeyWords {
	
	
	private Set<String> controlTerms;
	private Set<String> threatTerms;
	private Set<String> implicitTerms;
	private Set<String> assetTerms;

	/**
	 * 
	 */
	public SecurityKeyWords() {

		controlTerms = loadKeyword("controlterms.prop", true, true);
		threatTerms = loadKeyword("threatterms.prop", true, true);
		implicitTerms = loadKeyword("implicitterms.prop", true, true);
		assetTerms = loadKeyword("assetterms.prop", true, true);
	}

	public Set<String> controlTerms() {
		
		return Collections.unmodifiableSet(controlTerms);
	}

	public Set<String> threatTerms() {
		
		return Collections.unmodifiableSet(threatTerms);
	}

	public Set<String> implicitTerms() {
		
		return Collections.unmodifiableSet(implicitTerms);
	}

	public Set<String> assetTerms() {
		
		return Collections.unmodifiableSet(assetTerms);
	}
	
	private Set<String> loadKeyword(String resourceFile, boolean usehelpermethod, boolean makeclones){
		Set<String> keyword = new HashSet<String>();

		String appResourceFile = PropertyKeys.FEATURE_PATH+resourceFile;

		try(BufferedReader br = new BufferedReader(new FileReader(appResourceFile));)
		{
			readerHelper(br, keyword, usehelpermethod, makeclones, true);
		}catch(IOException e){

		}
		
		return keyword;
	}
	
	private void readerHelper(BufferedReader br, Set<String> keywords, boolean usehelpermethod, boolean makeclones, boolean changecase){
		String line = "";
		try {
			while((line=br.readLine()) != null){
				if(line.startsWith("#")) 
					continue;
				if(usehelpermethod)
					keyWordLoaderHelper(line, makeclones, keywords, changecase);
				else
					keywords.add(line);
			}
		} catch (IOException e) {

		}
	}
	
	/**
	 * Break the line into term, properties and threats and add properties and threats into 
	 * the securityDescription map
	 * ,;,;
	 */
	private void keyWordLoaderHelper(String line, boolean makeclones, Set<String> keywords, boolean changecase){
		if(line.equals(","))
			return;
		String[] tokens = line.split(",");
		String term = "";
		try{
			term = tokens[0].trim();			
		}catch(IndexOutOfBoundsException e){
			term = line;	//If error, return line
		}
		if(changecase)	//we don't want to alter the .properties file values
			term = term.toLowerCase();
		keywords.add(term);
		//create compound terms by replacing term that has space with '-', '_', '' and add to list. Check for opposite too
		String term_hyphen = term;
		String term_underscore = term;
		String term_nospace = term;
		if(makeclones){
			//if term has space between two words, create modified clones with '', '-' and '_'
			if (term.contains(" ")){
				term_hyphen = term_hyphen.replaceAll(" ", "-");
				term_underscore = term_underscore.replaceAll(" ", "_");
				term_nospace = term_nospace.replaceAll(" ", "");
			}
			//if term has '-' between two words, create clones with '', ' ' and '_'
			if (term.contains("-")){
				term_hyphen = term_hyphen.replaceAll("-", " ");
				term_underscore = term_underscore.replaceAll("-", "_");
				term_nospace = term_nospace.replaceAll("-", "");
			}
			//if term has '_' between two words, create clones with '', ' ' and '-'
			if (term.contains("_")){
				term_hyphen = term_hyphen.replaceAll("_", " ");
				term_underscore = term_underscore.replaceAll("_", "-");
				term_nospace = term_nospace.replaceAll("_", "");
			}
			keywords.add(term_nospace);
			keywords.add(term_underscore);
			keywords.add(term_hyphen);
		}
		
	}
}
