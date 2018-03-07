package project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.aliasi.lm.NGramProcessLM;

/**
 * @author Andrew
 *
 */
public class LanguageModel {
	
	/*
	 * Handles the main processing loop of the program
	 */
	public static void main(String[] args) {
		//Creates java lingpipe model form NGram Language Modelling
		NGramProcessLM model = createModel(3);
		//Searches Directory for each file
		final File file = new File("C:/Users/Andrew/workspace/TwoTimeNLM/resources/corpus/");
		for(final File child : file.listFiles()) {
			//Calls train method for each file
			model = train( model, file.toString() + "/" + child.getName());
		}
		likelyNGrams("an", model, 5);
		//System.out.println("Log2Estimate:" + (double)model.log2Estimate("alcohol"));
		//System.out.println("Prob:" + (double)model.prob("alcohol"));
	}
	
	/*
	 * int n - length of NGram
	 * 
	 * This method is for specifying any information during the creation of the model
	 * May become unneccessary if the only use is the constructor
	 */
	public static NGramProcessLM createModel(int n) {
		NGramProcessLM model = new NGramProcessLM(n);
		return model;
	}
	
	/*
	 * NGramProcessLM model - The model that requires training
	 * String file - The filename to train the model
	 */
	public static NGramProcessLM train(NGramProcessLM model, String filename) {
		File f = new File(filename);
		
		//Attempts to create a Buffered Reader to parse file
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			//For each line of the file train the model and output to console
			for(String line; (line = br.readLine()) != null;) {
				model.train(line);
				//System.out.println(line);
			}
			br.close();
			return model;
		} catch(IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * String input - The N - 1 Gram to assess
	 * NGramProcessLM  model - The model for analysis
	 * int topX - The amount of top results to return
	 * 
	 * Takes the N-1Gram has input and returns an array of the most likely n-grams
	 * 
	 * return - tuple (String,Percentage)
	 */
	public static AnalysisPair[] likelyNGrams(String input, NGramProcessLM model, int topX) {
		AnalysisPair[] aP = new AnalysisPair[94];
		if(input.length() != model.maxNGram() - 1) {
			System.out.println("Input string has the wrong amount of characters: Required - " + (model.maxNGram() - 1) + ", Entered - " + input.length());
			return null;
		}
		//Read all possible results for NGram extension (n-1 to n) 
		for(int i = 0; i<94; i++) {
			String s = input + (char)(i + 32);
			aP[i] = new AnalysisPair(s, model.prob(s));
		}
		//Sort in order of probability (Bubble Sort)
	    AnalysisPair temp;
	    for (int i = 0; i < 94; i++) {
	        for (int j = 1; j < (94 - i); j++) {

	            if (aP[j - 1].getProbability() < aP[j].getProbability()) {
	                temp = aP[j - 1];
	                aP[j - 1] = aP[j];
	                aP[j] = temp;
	            }

	        }
	    }
	    //Print results
	    AnalysisPair[] output = new AnalysisPair[topX];
	    for(int i = 0; i < topX; i++) {
	    	output[i] = new AnalysisPair(aP[i].getNgram(), aP[i].getProbability());
			System.out.println("Rank " + (i+1) + ": " + aP[i].toString());
	    }
	    //Return Sorted NGram information
		return output;
	}
	
	/*
	 * String filename - Name of File to save to
	 * NGramProcessLM model - Model to save
	 * TODO - Make path relative
	 */
	public static void saveToFile(String filename, NGramProcessLM model) {
		//Create File
		File f = new File("C:/Users/Andrew/workspace/TwoTimeNLM/resources/models/" + filename + ".txt");
		OutputStream out;
		try {
			//Establish Output Stream
			out = new FileOutputStream(f);
			model.writeTo(out);
			BufferedOutputStream bOutput = new BufferedOutputStream(out);
			//Dump model to Output Stream
			model.writeTo(bOutput);
			//Close Stream
			bOutput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	/*
	 * String filename - Name of File to load from
	 * TODO - Make path relative
	 */
	public static NGramProcessLM loadFromFile(String filename) {
		NGramProcessLM model = null;
		//Create File
		File f = new File("C:/Users/Andrew/workspace/TwoTimeNLM/resources/models/" + filename + ".txt");
		try {
			//Establish Input Stream
			InputStream input;
			input = new FileInputStream(f);
			BufferedInputStream bInput = new BufferedInputStream(input);
			//Read model from Input Stream
			model = NGramProcessLM.readFrom(bInput);
			//Close Stream
			bInput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return model;
	}
}
