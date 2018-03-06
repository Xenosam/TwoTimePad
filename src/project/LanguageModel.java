package project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		NGramProcessLM model = createModel(7);
		try {
			//Searches Directory for each file
			final File file = new File("C:/Users/Andrew/workspace/TwoTimeNLM/corpus/");
			for(final File child : file.listFiles()) {
				//Calls train method for each file
				train(model, file.toString() + "/" + child.getName());
			}
		} catch (IOException e) {
			//IOException Handle
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
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
	public static void train(NGramProcessLM model, String file) throws IOException {
		File f = new File(file);
		
		//Attempts to create a Buffered Reader to parse file
		try{
			BufferedReader br = new BufferedReader(new FileReader(f));
			//For each line of the file train the model and output to console
			for(String line; (line = br.readLine()) != null;) {
				model.train(line);
				System.out.println(line);
			}
		} catch(FileNotFoundException e) {
			//FileNotFoundException Handler
			System.out.println("ERROR: " + e.getMessage());
		}
	}
	
}
