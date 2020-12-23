/**
 * 
 */
package no.tosin.oyetoyan.experiment;

/**
 * @author tdoy
 * @deprecated Replaced with configuration file "experiment.prop"
 */
public enum Selector {

	TFIDFHigh,		// fsec-tfidf
	Threat,			// fsec-ext (Features = Threat category)
	Control,		// fsec-ext (Features = Control category)
	TC,				// fsec-ext (Features = Threat + Control categories)
	CA,				// fsec-ext (Features = Control + Asset categories)
	TA,				// fsec-ext (Features = Threat + Asset categories)
	TCA,			// fsec-ext (Features = Threat + Control + Asset categories)
	TCAI			// fsec-ext (Features = Threat + Control + Asset + Implicit categories)
}
