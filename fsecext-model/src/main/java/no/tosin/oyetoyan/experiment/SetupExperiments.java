/**
 * 
 */
package no.tosin.oyetoyan.experiment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tosindo
 * @deprecated Replaced with configuration file "experiment.prop"
 */
public class SetupExperiments {
	
	/** configuration parameters **/
	
	//public static final String PATH = "/fsecext-sqj/data"; 	//path to the "Experiment" folder - set the full path to the dataset folder
	public static final boolean STEM = false;					// stem text

	public static final String HEADER = "true";					// does the file has a header?
	public static final String SEPARATOR = ";";					// specify csv column separator 
	public static final double TRAIN_SIZE = 0.95;				// specify ratio for splitting dataset into train and test
	public static final int NUM_EXP = 1;						// specify how many times we should train
	public static final String CLASS_INDEX = "1"; 				// specify index of class label
	
	public static final int TOPN = 100; 						// top n = 100 for TFIDF
	
	public static List<Parameters> modelExperiments(){
		
		List<Parameters> expParam = new ArrayList<Parameters>();
		
		//derby
		/*Parameters derby = new Parameters("derby");
		derby.setTrainDataset("derby_train");
		derby.setTestDataset("derby_test");				//WPP: derby->derby 
		derby.addTestDataset("camel_test");				//TPP: derby->camel
		derby.addTestDataset("wicket_test"); 			//TPP: derby->wicket
		derby.addTestDataset("ambari_test"); 			//TPP: derby->ambari
		derby.addTestDataset("chromium_test"); 			//TPP: derby->chromium
		derby.addTestDataset("apache_test"); 			//TPP: derby->apache
		derby.addTestDataset("mozilla_test"); 			//TPP: derby->mozilla
		//derby.addTestDataset("comm_test"); 				//TPP: derby->comm  Not available due to privacy issue
		derby.addTestDataset("odcv_test"); 				//TPP: derby->odcv
		expParam.add(derby);
		
		//camel
		Parameters camel = new Parameters("camel");
		camel.setTrainDataset("camel_train");
		camel.setTestDataset("camel_test");			//WPP: camel->camel 
		camel.addTestDataset("derby_test");			//TPP: camel-derby
		camel.addTestDataset("wicket_test"); 		//TPP: derby->wicket
		camel.addTestDataset("ambari_test"); 		//TPP: derby->ambari
		camel.addTestDataset("chromium_test"); 		//TPP: derby->chromium
		camel.addTestDataset("apache_test"); 		//TPP: camel->apache
		camel.addTestDataset("mozilla_test"); 		//TPP: camel->mozilla
		//camel.addTestDataset("comm_test"); 			//TPP: camel->comm
		camel.addTestDataset("odcv_test"); 			//TPP: camel->odcv
		expParam.add(camel);
		
		//wicket
		Parameters wicket = new Parameters("wicket");
		wicket.setTrainDataset("wicket_train");
		wicket.setTestDataset("wicket_test");			//WPP: wicket->wicket 
		wicket.addTestDataset("camel_test");			//TPP: wicket->camel
		wicket.addTestDataset("derby_test"); 			//TPP: wicket->derby
		wicket.addTestDataset("ambari_test"); 			//TPP: wicket->ambari
		wicket.addTestDataset("chromium_test"); 		//TPP: wicket->chromium
		wicket.addTestDataset("apache_test"); 			//TPP: wicket->apache
		wicket.addTestDataset("mozilla_test"); 			//TPP: wicket->mozilla
		//wicket.addTestDataset("comm_test"); 			//TPP: wicket->comm
		wicket.addTestDataset("odcv_test"); 			//TPP: wicket->odcv
		expParam.add(wicket);
		
		//ambari
		Parameters ambari = new Parameters("ambari");
		ambari.setTrainDataset("ambari_train");
		ambari.setTestDataset("ambari_test");			//WPP: ambari->ambari 
		ambari.addTestDataset("camel_test");			//TPP: ambari->camel
		ambari.addTestDataset("derby_test"); 			//TPP: ambari->derby
		ambari.addTestDataset("wicket_test"); 			//TPP: ambari->wicket
		ambari.addTestDataset("chromium_test"); 		//TPP: ambari->chromium
		ambari.addTestDataset("apache_test"); 			//TPP: ambari->apache
		ambari.addTestDataset("mozilla_test"); 			//TPP: ambari->mozilla
		//ambari.addTestDataset("comm_test"); 			//TPP: ambari->comm
		ambari.addTestDataset("odcv_test"); 			//TPP: ambari->odcv
		expParam.add(ambari);
		
		//chromium
		Parameters chromium = new Parameters("chromium");
		chromium.setTrainDataset("chromium_train");
		chromium.setTestDataset("chromium_test");		//WPP: chromium->chromium 
		chromium.addTestDataset("camel_test");			//TPP: chromium->camel
		chromium.addTestDataset("ambari_test");			//TPP: chromium->ambari
		chromium.addTestDataset("derby_test"); 			//TPP: chromium->derby
		chromium.addTestDataset("wicket_test"); 		//TPP: chromium->wicket
		chromium.addTestDataset("apache_test"); 		//TPP: chromium->apache
		chromium.addTestDataset("mozilla_test"); 		//TPP: chromium->mozilla
		//chromium.addTestDataset("comm_test"); 			//TPP: chromium->comm
		chromium.addTestDataset("odcv_test"); 			//TPP: chromium->odcv
		expParam.add(chromium);
		
		// odcv
		Parameters odcv = new Parameters("odcv");
		odcv.setTrainDataset("odcv_train");
		odcv.setTestDataset("odcv_test");  				//WPP: odcv -> odcv
		odcv.addTestDataset("derby_test");				//TPP: odcv->derby
		odcv.addTestDataset("camel_test");				//TPP: odcv->camel
		odcv.addTestDataset("wicket_test"); 			//TPP: odcv->wicket
		odcv.addTestDataset("ambari_test"); 			//TPP: odcv->ambari
		odcv.addTestDataset("chromium_test"); 			//TPP: odcv->chromium
		odcv.addTestDataset("apache_test"); 			//TPP: odcv->apache
		odcv.addTestDataset("mozilla_test"); 			//TPP: odcv->mozilla
		//odcv.addTestDataset("comm_test"); 				//TPP: odcv->comm
		expParam.add(odcv); */
		
		// apache
		Parameters apache = new Parameters("apache");
		apache.setTrainDataset("apache_train");
		apache.setTestDataset("apache_test");  				//WPP: apache -> apache
		apache.addValidationDataset("derby_test");				//TPP: apache->derby
		apache.addValidationDataset("derby_train");				//TPP: apache->derby
		apache.addValidationDataset("camel_test");				//TPP: apache->camel
		apache.addValidationDataset("camel_train");				//TPP: apache->camel
		apache.addValidationDataset("wicket_test"); 				//TPP: apache->wicket
		apache.addValidationDataset("wicket_train"); 				//TPP: apache->wicket
		/*apache.addTestDataset("ambari_test"); 				//TPP: apache->ambari
		apache.addTestDataset("ambari_train"); 				//TPP: apache->ambari
		apache.addTestDataset("chromium_test"); 			//TPP: apache->chromium
		apache.addTestDataset("chromium_train"); 			//TPP: apache->chromium
		apache.addTestDataset("mozilla_test"); 				//TPP: apache->mozilla
		apache.addTestDataset("chromium_train"); 			//TPP: apache->chromium
		apache.addTestDataset("comm_test"); 				//TPP: apache->comm
		apache.addTestDataset("comm_train"); 				//TPP: apache->comm
		apache.addTestDataset("odcv_test"); 				//TPP: apache->odcv
		apache.addTestDataset("odcv_train"); 				//TPP: apache->odcv*/
		expParam.add(apache);
		
		// mozilla
		/*Parameters mozilla = new Parameters("mozilla");
		mozilla.setTrainDataset("mozilla_train");
		mozilla.setTestDataset("mozilla_test");  			//WPP: mozilla -> mozilla
		mozilla.addTestDataset("derby_test");				//TPP: mozilla->derby
		mozilla.addTestDataset("camel_test");				//TPP: mozilla->camel
		mozilla.addTestDataset("wicket_test"); 				//TPP: mozilla->wicket
		mozilla.addTestDataset("ambari_test"); 				//TPP: mozilla->ambari
		mozilla.addTestDataset("chromium_test"); 			//TPP: mozilla->chromium
		mozilla.addTestDataset("apache_test"); 				//TPP: mozilla->apache
		//mozilla.addTestDataset("comm_test"); 				//TPP: mozilla->comm
		mozilla.addTestDataset("odcv_test"); 				//TPP: mozilla->odcv
		expParam.add(mozilla);
		
		// comm - commercial data not available due to privacy issue
		Parameters comm = new Parameters("comm");
		comm.setTrainDataset("comm_train");
		comm.setTestDataset("comm_test");  				//WPP: comm -> comm
		comm.addTestDataset("derby_test");				//TPP: comm->derby
		comm.addTestDataset("camel_test");				//TPP: comm->camel
		comm.addTestDataset("wicket_test"); 			//TPP: comm->wicket
		comm.addTestDataset("ambari_test"); 			//TPP: comm->ambari
		comm.addTestDataset("chromium_test"); 			//TPP: comm->chromium
		comm.addTestDataset("apache_test"); 			//TPP: comm->apache
		comm.addTestDataset("mozilla_test"); 			//TPP: comm->mozilla
		comm.addTestDataset("odcv_test"); 				//TPP: comm->odcv
		expParam.add(comm);*/
		
		return expParam;
	}

}
