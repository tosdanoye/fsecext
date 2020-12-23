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


public final class PropertyKeys {
	
	//machine learning
	public static final String DATA_FILE = "data_file";
	public static final String DATA_TESTFILE = "data_testfile";
	public static final String TEXT_INDEX = "text_index";
	public static final String CLASS_INDEX = "class_index";
	public static final String SEPARATOR = "separator";
	public static final String HEADER = "header";
	public static final String ALGORITHM = "algorithm";
	public static final String TRAIN_SIZE = "train_size";
	public static final String TEST_ALL = "test_all";
	public static final String NUM_EXPERIMENT = "num_expr";
	public static final String RATIO = "ratio";
	public static final String FEATURE_TYPE = "feature_type";
	
	public static final String CONFIG_PATH = "config_path";
	public static final String DICTIONARY_FILE = "dict_file";
	public static final String STOPWORD_FILE = "stopword_file";

	public static final int TOPN = 100;							// tfidf topn terms (project specific terms)
	public static final boolean STEM = false;					// stem text
	
	public static String PATH;	
	public static String FEATURE_PATH;

	
	private PropertyKeys(){
		//can't instantiate
	}
}
