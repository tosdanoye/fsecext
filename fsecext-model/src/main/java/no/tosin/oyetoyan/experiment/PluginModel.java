/**
 * 
 */
package no.tosin.oyetoyan.experiment;

import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 * @author tdoy
 *
 */
public class PluginModel {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String[] secpredict = {"Yes", "No"};
		String txt1 = "JavaDocProvider should not require a ref to the JavaDoc lib";
		String txt2 = "javax.crypto.BadPaddingException: Given final block not properly padded;"
				+ "Using the attached pdf, When running the pdf through org.apache.pdfbox.pdmodel.encryption.SecurityHandler "
				+ "I get BadPaddingException. The exception occurs when it is processing nextObj = COSObject{3304,0} I see: "
				+ "nextCOSBase = COSDictionary{(COSName{Length}:COSInt{3504}) (COSName{Subtype}:COSName{XML}) (COSName{Type}:"
				+ "COSName{Metadata}) } The problem is that SecurityHandler.proceedDecryption() runs decryptObject(nextObj)  "
				+ "and then decrypt(base, objNum, genNum) and then decryptStream() However for this object decryptStream doesn't "
				+ "actually decrypt anything because the type is xml. So when decryptStream calls encryptData() encryptData() "
				+ "throws the bad packet exception. output.write(decryptCipher.doFinal())  throws the exception because the data "
				+ "buffer is all zeros. It has nothing in it. I recommend that encryption be skipped if the data buffer has all zeros.";

		String pathtomodel = "/experiments/adjdata/";			// specify the full path to the model and files here
		String dictFile = pathtomodel+"dictfile.txt";
		String modelfile = pathtomodel+"trmodel.model";
		String arfftxt = pathtomodel+"arfftxt";
		
		// pass the text you want to classify here - a new weka-compatible arff-file will be generated
		PluginUtil.writeUnlabeledInstance(txt2, arfftxt);
		Instances insts = PluginUtil.getDictionaryFilteredInstances(dictFile, arfftxt);
		
		//System.out.println(insts);
		//Classifier model = Utility.deserializeModel("./trmodel.model");
		Classifier model = PluginUtil.deserializeModel(modelfile);
		double clsindex = PluginUtil.classify(model, insts.firstInstance());
		System.out.println("Result = "+secpredict[(int) clsindex]);		
	}

}
