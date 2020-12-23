# README #

This repository host the code base for the experiments reported in the article "An Improved Text Classification Modelling Approach to Identify Security Messages in Heterogeneous Projects" submitted to Software Quality Journal.

Details of code setup and experiments

# How to #

## Pre-requisites (data needed for the experiment): ##

- Extract data.zip

## A. Using the command line ##
### 1.	Download the fsecextplugin.zip  ###
- Unzip the file
- Edit the experiment.prop file in the folder “fsecextplugin” (i.e. Step 2. below)
- cd fsecextplugin
- run the command: java -jar fsecextplugin.jar -config experiment.prop

### 2.	Set up your configuration file ###
The very first step is to set up a configuration file. This is not a hassle as a default config file comes handy with the zipped files. Training a classification model is simple once the parameters for the algorithms are setup. The parameters in the “experiment.prop” file are described as shown below.

###	experiment.prop ###

#### set the path to the folder where all your data is located. (see the "Pre-requisites" section above) ####
DATA_PATH=data
#### does the file has a header? ####
HEADER=true 
#### specify csv column separator ####
SEPARATOR=;
#### specify ratio for splitting dataset into train and test ####
TRAIN_SIZE=0.90
#### specify how many times we should train. This concerns training. Best model is selected for validation stage ####
NUM_EXP=1
#### specify index of class label ####
CLASS_INDEX=1
#### specify the ratios (separated by comma) for sampling minority class - SBR : NSBR (SBR is always 1) => 1:0.5, 1:1, 1:1.5, 1:2 ####
CLASS_BALANCE_RATIOS=0,0.5,1,2
#### algorithms to use for training a model. LR-Logistic Regression,NB-Naive Bayes,KNN-K Nearest Neighbor,SVM-Support Vector Machine,RF-Random Forest
ALGORITHMS=NB,KNN,LR,SVM,RF
#### which features should be used (external or internal): fsec-tfidf, fsec-ext
#####	TFIDFHigh,		// fsec-tfidf
#####	Threat,			// fsec-ext (Features = Threat category)
#####	Control,		// fsec-ext (Features = Control category)
#####	TC,				// fsec-ext (Features = Threat + Control categories)
#####	CA,				// fsec-ext (Features = Control + Asset categories)
#####	TA,				// fsec-ext (Features = Threat + Asset categories)
#####	TCA,			// fsec-ext (Features = Threat + Control + Asset categories)
#####	TCAI			// fsec-ext (Features = Threat + Control + Asset + Implicit categories)
FEATURES=TFIDFHigh,Threat,Control,TC,CA,TA,TCA,TCAI
#### include security features in the training dataset {yes,no} {yes} {no}
INCLUDE_SEC_FEATURES=yes
#### name of the folder (separated by comma) containing the train and test csv files. Note: csv files must be named as folder_train.csv (e.g. apache_train.csv) and folder_test.csv (apache_test.csv) ####
TRAIN_FOLDER_NAMES=derby,wicket,ambari,camel
#### name of the folders (separated by comma) containing other projects' csv test files for validation. Note: csv files must be named as folder_test.csv (e.g. derby_test.csv) ####
VALIDATION_FOLDER_NAMES=derby,wicket,ambari,camel


## B. Usage from eclipse: ##

### 1. Clone/Check-out the folder "fsecext" into your local directory ###

### 2. Import fsecext maven project ###

- File -> import -> Exisiting maven project into workspace

- Navigate to the "fsecext" and select it

- Configure the experiment.prop file as above (it uses the same prop file as the cmd line) ####


### 3. Open the MainARFF.java ###
- In the main method: uncomment the following lines
- //String config = "./experiment.prop";
- //args = new String[2];
- //args[0] = "-config";
- //args[1] = config;

- right click and run

## C. Results ##
- Results are located in the algorithm  folder for each project. e.g. Random forest results will be located in "RF" folder

## D. Statistics ##
- classes for the statistics are located in "no.tosin.oyetoyan.experiment.statistics"
- open the StatisticalTests.java
- Generate the statistic data 	
## E. How to run tests ##
- Test scripts are located in a sub directory "analysis" and in each project's folder:
e.g. for ambari analysis/ambari/scripts/stats.r

## F. How to integrate the trained model in industrial repository environments ##
- Check the PluginModel.java located in the package no.tosin.oyetoyan.experiment
Use cases for integration
- Bug repositories
- Commit repositories
- Project document repositories
- etc.

