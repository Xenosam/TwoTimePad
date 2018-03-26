package project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.lm.TrieCharSeqCounter;

/**
 * @author Andrew
 *
 */
public class LanguageModel {

	public static Scanner ui = new Scanner(System.in);

	/**
	 * Main method for controlling execution flow
	 * 
	 * @param args
	 *            command line arguements fed to the program
	 */
	public static void main(String[] args) {
		NGramProcessLM model = null;
		TrieCharSeqCounter counter = null;
		boolean fail = false;
		boolean newItem = false;
		boolean TCSC = false;
		String s = "";
		// Create new ciphertext
		System.out.println("Create new Ciphertext? <Y/N>");
		s = ui.next();
		if (s.equals("Y") || s.equals("y")) {
			String a, b;
			System.out.println("Enter String 1: <string>");
			ui.nextLine();
			a = ui.nextLine();
			System.out.println("Enter String 2: <string>");
			b = ui.nextLine();
			System.out.println("Enter Filename: <string>");
			s = ui.nextLine();
			try {
				fail = createCiphertext(a.toCharArray(), b.toCharArray(), s);
			} catch (IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else if (s.equals("N") || s.equals("n")) {
			System.out.println("continue");
		} else {
			System.out.println("Invalid Input");
			fail = true;
		}
		// Create Model
		System.out.println("Use Exisiting Model? <Y/N>");
		s = ui.next();
		int n = 0;
		int i;
		// Choose Load or New Model
		if (s.equals("Y") || s.equals("y")) {
			System.out.println("Enter Filename: <string>");
			s = String.valueOf(ui.next());
			File f = new File("./resources/models/" + s + ".txt");
			if (f.exists()) {
				model = loadFromFile(s);
				n = model.maxNGram();
				counter = loadCounter(s);
			} else {
				System.out.println("File Does Not Exist");
				fail = true;
			}
		} else if (s.equals("N") || s.equals("n")) {
			newItem = true;
			System.out.println("Creating New Model...");
			System.out.println("Enter n value: <int>");
			n = Integer.valueOf(ui.next());
			model = createModel(n);
			counter = new TrieCharSeqCounter(n);
			// Train the Model
			final File file = new File("./resources/corpus/");
			for (final File child : file.listFiles()) {
				// Calls train method for each file
				System.out.println("Training: " + child.getName());
				model = train(model, file.toString() + "/" + child.getName());
				counter = trainTCSC(counter, n, file.toString() + "/" + child.getName());
			}

		} else {
			System.out.println("Invalid Input");
			fail = true;
		}
		if (!fail && newItem) {
			// Choose Language Smoothing
			System.out.println("Use Smoothing? <Y/N>");
			s = ui.next();
			if (s.equals("Y") || s.equals("y")) {
				System.out.println(
						"Select Method: <1/2/3>\n1: Laplace \n2: Good-Turing \n3: Witten-Bell \n4: Witten-Bell(LingPipe)");
				i = Integer.valueOf(ui.next());
				if (i == 1) {
					TCSC = true;
					counter = smoothingLaplace(counter, n);
				} else if (i == 2) {
					TCSC = true;
					counter = smoothingGoodTuring(counter, n);
				} else if (i == 3) {
					TCSC = true;
					counter = smoothingWittenBell(counter, n);
				} else if (i == 4) {
					TCSC = false;
					System.out.println("CONTINUE");
				} else {
					System.out.println("Invalid Input");
					fail = true;
				}
			} else if (s.equals("N") || s.equals("n")) {
				TCSC = true;
			} else {
				System.out.println("Invalid Input");
				fail = true;
			}
		}
		// Ask to Save model
		if (!fail && newItem) {
			System.out.println("Save Model To Resources? <Y/N>");
			s = ui.next();
			if (s.equals("Y") || s.equals("y")) {
				System.out.println("Enter Filename: <string>");
				s = ui.next();
				File f = new File("./resources/models/" + s + ".txt");
				if (f.exists()) {
					System.out.println("File Exists, Delete and Recreate? <Y/N>");
					s = ui.next();
					if (s.equals("Y") || s.equals("y")) {
						f.delete();
						saveToFile((s), model);
						saveCounter(counter, s);
					} else if (s.equals("N") || s.equals("n")) {
						System.out.println("Continuing");
					} else {
						System.out.println("Invalid Input");
						fail = true;
					}
				} else {
					saveToFile((s), model);
					saveCounter(counter, s);
				}
			} else if (s.equals("N") || s.equals("n")) {
				System.out.println("Continuing");
			} else {
				System.out.println("Invalid Input");
				fail = true;
			}
		}
		if (!fail && !TCSC) {
			// Automated Execution
			System.out.println("Simple or Complex Solver? <S/C>");
			s = ui.next();
			if (s.equals("C") || s.equals("c")) {
				System.out.println("Enter Ciphertext Filename: <string>");
				s = "./resources/ciphertext/" + ui.next() + ".txt";
				if (new File(s).exists()) {
					int x;
					File f = new File(s);
					System.out.println("Enter The Amount Of Results To Keep Each Pass: <int>");
					x = Integer.valueOf(ui.next());
					String[] output = solver(f, model, x, true);
					for (int j = 0; j < (output.length / 2); j++) {
						System.out.println("i: " + j);
						System.out.println("A: " + output[j].substring(4));
						System.out.println("B: " + output[j + (output.length / 2)].substring(4));
					}
				} else {
					System.out.println("File Does Not Exist");
					fail = true;
				}
			} else if (s.equals("S") || s.equals("s")) {
				System.out.println("Enter Ciphertext Message: <string>");
				s = ui.next();
				String[] output = simpleSolver(s.toCharArray(), model);
				System.out.println("A: " + output[0] + "\nB: " + output[1]);
			} else {
				System.out.println("Invalid Input");
				fail = true;
			}
		}
		if (!fail && TCSC) {
			// Implemented Execution
			System.out.println("Enter Ciphertext Filename: <string>");
			s = "./resources/ciphertext/" + ui.next() + ".txt";
			if (new File(s).exists()) {
				int x;
				File f = new File(s);
				System.out.println("Enter The Amount Of Results To Keep Each Pass: <int>");
				x = Integer.valueOf(ui.next());
				System.out.println("Enter previous models? <Y/N>");
				s = ui.next();
				if (s.equals("Y") || s.equals("y")) {
					String[] output = TCSCSolver(f, counter, n, x, true);
					for (int j = 0; j < (output.length / 2); j++) {
						System.out.println("i: " + j);
						System.out.println("A: " + output[j].substring(4));
						System.out.println("B: " + output[j + (output.length / 2)].substring(4));
					}
				} else if (s.equals("N") || s.equals("n")) {
					String[] output = TCSCSolver(f, counter, n, x, false);
					for (int j = 0; j < (output.length / 2); j++) {
						System.out.println("i: " + j);
						System.out.println("A: " + output[j]);
						System.out.println("B: " + output[j + (output.length / 2)]);
					}
				} else {
					System.out.println("Invalid Input");
					fail = true;
				}

			} else {
				System.out.println("File Does Not Exist");
				fail = true;
			}
		}
		ui.close();
	}

	/**
	 * Paths the hidden markov model with a TrieCharSeqCounter
	 * 
	 * @param message
	 *            the ciphertext file
	 * @param counter
	 *            the TrieCharSeqCounter to use as the model
	 * @param n
	 *            the size of the ngrams
	 * @param x
	 *            the amount of results to keep each pass
	 * @param in
	 *            a boolean trigger for whether the user supplies additional
	 *            models for the 0 - n-1 phase
	 * @return the top x combinations for string decryptions
	 */
	public static String[] TCSCSolver(File message, TrieCharSeqCounter counter, int n, int x, boolean in) {
		AnalysisPair[] workQueue = new AnalysisPair[x];
		AnalysisPair[] input = new AnalysisPair[x * x];
		int index = 0;
		int loop = 0;
		char c;
		int currentChar;
		char[] cXOR;
		try {
			BufferedReader br = new BufferedReader(new FileReader(message));
			while ((currentChar = br.read()) != -1) {
				// UPDATE/CLEAR VARIABLES
				c = (char) currentChar;
				cXOR = XORHandler(c);
				index = 0;
				input = new AnalysisPair[x * x];

				System.out.println("C: " + c + ", (" + currentChar + ")");

				if (loop == 0) {
					// First Character
					AnalysisPair[] temp = new AnalysisPair[256];
					TrieCharSeqCounter tempCounter = null;
					// Create Counter
					if (n != 1) {
						tempCounter = new TrieCharSeqCounter(1);
						if (in == false) {
							tempCounter = trainTCSC(tempCounter, loop + 1,
									"./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
						} else {
							System.out.println("Enter filename for n:" + (loop + 1) + " Model");
							tempCounter = loadCounter(ui.next());
						}
					} else {
						tempCounter = counter;
					}
					// Extend
					for (int i = 0; i < 256; i++) {
						char a = (char) i;
						char b = cXOR[i];
						double ap = (double) tempCounter.count("" + a) / (double) counter.totalSequenceCount();
						double bp = (double) tempCounter.count("" + b) / (double) counter.totalSequenceCount();
						System.out.println("A: " + a + ", P: " + ap);
						System.out.println("B: " + b + ", P: " + bp);
						temp[i] = new AnalysisPair("" + a, "" + b, Math.log(ap + bp), "" + a, "" + b);
					}
					// Sort
					temp = quickSort(0, 255, temp);
					// Filter
					for (int i = 0; i < x; i++) {
						AnalysisPair curr = temp[255 - i];
						System.out.println("A: " + curr.getNGram());
						System.out.println("B: " + curr.getNGram2());
						workQueue[i] = new AnalysisPair(curr.getNGram(), curr.getNGram2(), curr.getProbability());
						workQueue[i].addData(curr.getNGram().charAt(0), curr.getNGram2().charAt(0));
					}
					// End
					loop++;
					continue;
				} else if (loop < n - 1) {
					// Character less than limit
					// Train
					TrieCharSeqCounter tempCounter = new TrieCharSeqCounter(loop + 1);
					if (in == false) {
						tempCounter = trainTCSC(tempCounter, loop + 1,
								"./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
					} else {
						System.out.println("Enter filename for n:" + (loop + 1) + " Model");
						tempCounter = loadCounter(ui.next());
					}
					for (AnalysisPair aP : workQueue) {
						if (aP == null) {
							// Skip if null
							continue;
						}
						AnalysisPair[] temp = new AnalysisPair[256];
						// Extend
						for (int i = 0; i < 256; i++) {
							char a = (char) i;
							char b = cXOR[i];
							String s = aP.getNGram() + a;
							String t = aP.getNGram2() + b;
							double ap = (double) tempCounter.count(s) / (double) counter.totalSequenceCount();
							double bp = (double) tempCounter.count(t) / (double) counter.totalSequenceCount();
							System.out.println("A: " + s + ", P: " + ap);
							System.out.println("B: " + t + ", P: " + bp);
							temp[i] = new AnalysisPair(s, t, Math.log(ap + bp), aP.getData1(), aP.getData2());
							temp[i].addData(a, b);
						}
						// Sort
						temp = quickSort(0, 255, temp);
						// Filter
						for (int i = 0; i < x; i++) {
							AnalysisPair curr = temp[255 - i];
							System.out.println("A: " + curr.getNGram());
							System.out.println("B: " + curr.getNGram2());
							input[index] = curr;
							index++;
						}

					}
					// Sort
					input = quickSort(0, input.length - 1, input);
					// Filter
					for (int i = 0; i < x; i++) {
						workQueue[i] = input[(input.length - 1) - x];
					}
					// END
					loop++;
				} else {
					for (AnalysisPair aP : workQueue) {
						if (aP == null) {
							continue;
						}
						// Character at or beyond limit
						String q = aP.getNGram();
						String r = aP.getNGram2();
						if (loop != n - 1 && n != 1) {
							// Trim
							q = new String(stringTrim(q.toCharArray())).substring(0, n - 2);
							r = new String(stringTrim(r.toCharArray())).substring(0, n - 2);
						}
						// Extend
						AnalysisPair[] temp = new AnalysisPair[256];
						for (int i = 0; i < 256; i++) {
							char a = (char) i;
							char b = cXOR[i];
							String s = q + a;
							String t = r + b;
							double ap = (double) counter.count(s) / (double) counter.totalSequenceCount();
							double bp = (double) counter.count(t) / (double) counter.totalSequenceCount();
							System.out.println("A: " + s + ", P: " + ap);
							System.out.println("B: " + t + ", P: " + bp);
							temp[i] = new AnalysisPair(s, t, Math.log(ap + bp), aP.getData1(), aP.getData2());
							temp[i].addData(a, b);
							// / (double) counter.totalSequenceCount()
							// -Math.log(ap * bp)

						}
						// Sort
						temp = quickSort(0, 255, temp);
						// Filter
						for (int i = 0; i < x; i++) {
							AnalysisPair curr = temp[255 - i];
							System.out.println("A: " + curr.getNGram());
							System.out.println("B: " + curr.getNGram2());
							input[index] = curr;
							index++;
						}
					}
					// Sort
					input = quickSort(0, input.length - 1, input);
					// Filter
					for (int i = 0; i < x; i++) {
						workQueue[i] = input[(input.length - 1) - x];
					}
					// END
					loop++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] output = new String[x * 2];
		for (int i = 0; i < x; i++) {
			output[i] = workQueue[i].getData1();
		}
		for (int i = 0; i < x; i++) {
			output[i + x] = workQueue[i].getData2();
		}
		return output;
	}

	/**
	 * Method for turning to character arrays into an XOR'd ciphertext
	 * 
	 * @param a
	 *            String for our "Plaintext a"
	 * @param b
	 *            String for our "Plaintext b"
	 * @throws IOException
	 *             Error for File IO
	 */
	public static boolean createCiphertext(char[] a, char[] b, String filename) throws IOException {
		if (a.length != b.length) {
			System.out.println("Strings must be the same length");
			System.out.println(new String(a) + "\nLength: " + a.length);
			System.out.println(new String(b) + "\nLength: " + b.length);
			return true;
		}
		FileWriter fw = new FileWriter("./resources/ciphertext/" + filename + ".txt");
		for (int i = 0; i < a.length; i++) {
			fw.append((char) ((int) a[i] ^ (int) b[i]));
		}
		fw.close();
		return false;
	}

	/**
	 * The complex solver method for using a given NGramProcessLM model to
	 * assess the shortest path through the hidden markov model in the
	 * ciphertext
	 * 
	 * @param message
	 *            the file containing the ciphertext
	 * @param model
	 *            the model to use for the ngram probabilities
	 * @param x
	 *            the amount of results to keep and extend each cycle
	 * @param in
	 *            a boolean trigger for whether the user supplies additional
	 *            models for the 0 - n-1 phase
	 * @return the top x combinations for string decyrption
	 */
	public static String[] solver(File message, NGramProcessLM model, int x, boolean in) {
		AnalysisPair[] workQueue = new AnalysisPair[x];
		AnalysisPair[] input = new AnalysisPair[x * x];
		int index = 0;
		int loop = 0;
		char c;
		int currentChar;
		char[] cXOR;
		try {
			BufferedReader br = new BufferedReader(new FileReader(message));
			while ((currentChar = br.read()) != -1) {
				// UPDATE/CLEAR VARIABLES
				c = (char) currentChar;
				cXOR = XORHandler(c);
				index = 0;
				input = new AnalysisPair[x * x];

				// System.out.println("C: " + c + ", (" + currentChar + ")");

				if (loop == 0) {
					// First Character
					AnalysisPair[] temp = new AnalysisPair[256];
					// Create Counter
					NGramProcessLM tempModel = createModel(1);
					if (model.maxNGram() != 1) {
						if (in == false) {
							tempModel = train(tempModel,
									"./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
						} else {
							System.out.println("Enter filename for n:" + (loop + 1) + " Model");
							tempModel = loadFromFile(ui.next());
						}
						tempModel = train(tempModel, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
					} else {
						tempModel = model;
					}
					// Extend
					for (int i = 0; i < 256; i++) {
						char a = (char) i;
						char b = cXOR[i];
						double ap = tempModel.prob("" + a);
						double bp = tempModel.prob("" + b);
						// System.out.println("A: " + a + ", P: " + ap);
						// System.out.println("B: " + b + ", P: " + bp);
						temp[i] = new AnalysisPair("" + a, "" + b, Math.log(ap * bp), "" + a, "" + b);
					}
					// Sort
					temp = quickSort(0, 255, temp);
					// Filter
					for (int i = 0; i < x; i++) {
						AnalysisPair curr = temp[255 - i];
						// System.out.println("A: " + curr.getNGram());
						// System.out.println("B: " + curr.getNGram2());
						workQueue[i] = new AnalysisPair(curr.getNGram(), curr.getNGram2(), curr.getProbability());
						workQueue[i].addData(curr.getNGram().charAt(0), curr.getNGram2().charAt(0));
					}
					// End
					loop++;
					continue;
				} else if (loop < model.maxNGram() - 1) {
					// Character less than limit
					// Train
					NGramProcessLM tempModel = createModel(loop + 1);
					if (in == false) {
						tempModel = train(tempModel, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
					} else {
						System.out.println("Enter filename for n:" + (loop + 1) + " Model");
						tempModel = loadFromFile(ui.next());
					}
					for (AnalysisPair aP : workQueue) {
						if (aP == null) {
							// Skip if null
							continue;
						}
						AnalysisPair[] temp = new AnalysisPair[256];
						// Extend
						for (int i = 0; i < 256; i++) {
							char a = (char) i;
							char b = cXOR[i];
							String s = aP.getNGram() + a;
							String t = aP.getNGram2() + b;
							double ap = tempModel.prob(s);
							double bp = tempModel.prob(t);
							System.out.println("A: " + s + ", P: " + ap);
							System.out.println("B: " + t + ", P: " + bp);
							temp[i] = new AnalysisPair(s, t, Math.log(ap * bp), aP.getData1(), aP.getData2());
							temp[i].addData(a, b);
						}
						// Sort
						temp = quickSort(0, 255, temp);
						// Filter
						for (int i = 0; i < x; i++) {
							AnalysisPair curr = temp[255 - i];
							System.out.println("A: " + curr.getNGram());
							System.out.println("B: " + curr.getNGram2());
							input[index] = curr;
							index++;
						}

					}
					// Sort
					input = quickSort(0, input.length - 1, input);
					// Filter
					for (int i = 0; i < x; i++) {
						workQueue[i] = input[(input.length - 1) - x];
					}
					// END
					loop++;
				} else {
					for (AnalysisPair aP : workQueue) {
						if (aP == null) {
							continue;
						}
						// Character at or beyond limit
						String q = aP.getNGram();
						String r = aP.getNGram2();
						if (loop != model.maxNGram() - 1 && model.maxNGram() != 1) {
							// Trim
							q = new String(stringTrim(q.toCharArray())).substring(0, model.maxNGram() - 2);
							r = new String(stringTrim(r.toCharArray())).substring(0, model.maxNGram() - 2);
						}
						// Extend
						AnalysisPair[] temp = new AnalysisPair[256];
						for (int i = 0; i < 256; i++) {
							char a = (char) i;
							char b = cXOR[i];
							String s = q + a;
							String t = r + b;
							double ap = model.prob(s);
							double bp = model.prob(t);
							System.out.println("A: " + s + ", P: " + ap);
							System.out.println("B: " + t + ", P: " + bp);
							temp[i] = new AnalysisPair(s, t, Math.log(ap * bp), aP.getData1(), aP.getData2());
							temp[i].addData(a, b);
						}
						// Sort
						temp = quickSort(0, 255, temp);
						// Filter
						for (int i = 0; i < x; i++) {
							AnalysisPair curr = temp[255 - i];
							System.out.println("A: " + curr.getNGram());
							System.out.println("B: " + curr.getNGram2());
							input[index] = curr;
							index++;
						}
					}
					// Sort
					input = quickSort(0, input.length - 1, input);
					// Filter
					for (int i = 0; i < x; i++) {
						workQueue[i] = input[(input.length - 1) - x];
					}
					// END
					loop++;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] output = new String[x * 2];
		for (int i = 0; i < x; i++) {
			output[i] = workQueue[i].getData1();
		}
		for (int i = 0; i < x; i++) {
			output[i + x] = workQueue[i].getData2();
		}
		return output;

	}

	/**
	 * Moves each character back an index, dropping the character that was at
	 * index 0 and clears the character at index length -1
	 * 
	 * @param strA
	 *            the string to be trimmed
	 * @return the trimmed string
	 */
	public static char[] stringTrim(char[] strA) {
		for (int j = 1; j < strA.length; j++) {
			// Remove least significant character
			strA[j - 1] = strA[j];
		}
		strA[strA.length - 1] = 0;
		return strA;
	}

	/**
	 * Solution to the problem that assumes the simplest state in that the
	 * ciphertext is very short and that our value for the amount of items kept
	 * each cycle is 1
	 * 
	 * @param message
	 *            ciphertext string
	 * @param model
	 *            model for ngram probabilities
	 */
	public static String[] simpleSolver(char[] message, NGramProcessLM model) {
		// Strings for current NGram
		char[] strA = new char[model.maxNGram()];
		char[] strB = new char[model.maxNGram()];
		// Strings for building plaintext outputs
		String strSA = "";
		String strSB = "";
		// Variables for reading in each character from file
		char c;
		char[] cXOR;
		// Array for sorting/scoring and filtering
		AnalysisPair[] score = new AnalysisPair[256];
		// Output
		String[] output = new String[2];
		for (int i = 0; i < message.length; i++) {
			// Update c
			c = message[i];
			// Create XOR map
			cXOR = XORHandler(c);
			// Code until ngram size is reached
			if (i <= model.maxNGram() - 2) {
				if (i == 0) {
					// File Load: First read character is ASCII - 02
					strA[i] = (char) 2;
					strB[i] = (char) 2;
					// Update output string
					strSA += strA[i];
					strSB += strB[i];

				} else {
					NGramProcessLM temp = createModel(i);
					// EXPAND
					for (int k = 0; k < 256; k++) {
						strA[i] = (char) k;
						strB[i] = cXOR[k];
						String s = (new String(strA.toString()) + new String(strB));
						double p = Math.log(temp.prob(new String(strA)) * temp.prob(new String(strB)));
						score[k] = new AnalysisPair(s, p);
					}
					// SORT
					score = quickSort(0, score.length - 1, score);
					// FILTER
					// Rest current ngram
					strA[i] = score[255].getNGram().toCharArray()[i];
					strB[i] = score[255].getNGram().toCharArray()[i * 2 + 1];
					// Update output string
					strSA += strA[i];
					strSB += strB[i];
				}
			} else {
				if (i != model.maxNGram() - 1) {
					strA = stringTrim(strA);
					strB = stringTrim(strB);
				}
				// EXPAND
				for (int j = 0; j < 256; j++) {
					strA[model.maxNGram() - 1] = (char) j;
					strB[model.maxNGram() - 1] = cXOR[j];
					// SCORE Strings: score[i] -> aPA[i] + aPB[i]
					String s = new String(strA) + new String(strB);
					double p = Math.log(model.prob(new String(strA)) * model.prob(new String(strB)));
					score[j] = new AnalysisPair(s, p);
				}
				// SORT
				score = quickSort(0, score.length - 1, score);
				// FILTER
				// Rest current ngram
				strA[model.maxNGram() - 1] = score[255].getNGram().toCharArray()[model.maxNGram() - 1];
				strB[model.maxNGram() - 1] = score[255].getNGram().toCharArray()[model.maxNGram() * 2 - 1];
				// Update output string
				strSA += strA[model.maxNGram() - 1];
				strSB += strB[model.maxNGram() - 1];
			}
		}

		output[0] = strSA;
		output[1] = strSB;

		return output;
	}

	/**
	 * Performs a recursive QuickSort over a given array of doubles
	 * 
	 * @param low
	 *            the lowest index of the division (initially should be 0)
	 * @param high
	 *            the highest index of the division (initially should be length
	 *            - 1)
	 * @param output
	 *            the array to be sorted
	 * @return the sorted array
	 */
	public static AnalysisPair[] quickSort(int low, int high, AnalysisPair[] output) {
		// Establish Low, High and Middle(The Pivot)
		int i = low;
		int j = high;
		// Take Value from the middle
		AnalysisPair pivot = output[low + (high - low) / 2];

		// Sort Loop
		while (i <= j) {
			// Increase low pointer
			while (output[i].getProbability() < pivot.getProbability()) {
				i++;
			}

			// Decrease high pointer
			while (output[j].getProbability() > pivot.getProbability()) {
				j--;
			}

			// Swap the positions of the high and low
			if (i <= j) {
				AnalysisPair temp = output[i];
				output[i] = output[j];
				output[j] = temp;
				i++;
				j--;
			}
		}

		// Recursion
		if (low < j) {
			// Create new division from low to j
			quickSort(low, j, output);
		}
		if (i < high) {
			// Create new division from i to high
			quickSort(i, high, output);
		}

		// Return
		return output;
	}

	/**
	 * Returns a character array with every possible combination of characters
	 * the XOR to the character given as the input
	 * 
	 * @param input
	 *            the XOR character to be split
	 * @return a character array of each possible character combination
	 */
	public static char[] XORHandler(char input) {
		// char[x][0] = ptA, char[x][1] = ptB
		char[] output = new char[256];
		char ptB;
		// attempt all 256 possible for output = i ^ ptB
		for (int i = 0; i < 256; i++) {
			// c = a ^ b, a = c ^ b, b = c ^ a
			ptB = (char) (input ^ i);
			// ptA = i
			output[i] = ptB;
		}
		return output;
	}

	/**
	 * Method for creation and initialization of new NGramProcessLM models
	 * 
	 * @param n
	 *            The length of the n-grams created for the model
	 * @return An initialized NGramProcessLM model
	 */
	public static NGramProcessLM createModel(int n) {
		NGramProcessLM model = new NGramProcessLM(n);
		return model;
	}

	/**
	 * Creates a model using the AnalysisPair class based off of a preexistant
	 * NGramProcessLM model
	 * 
	 * @param model
	 *            The NGramProcessLM model to take the information from
	 * @param n
	 *            The length of the NGram
	 * @return An array of String,Double pairs that represents each ngram and
	 *         its percentage chance of appearance
	 */
	public static AnalysisPair[] createAPModel(NGramProcessLM model, int n) {
		// Will require a lot of memory when n > 4
		if (n > 3) {
			System.out.println("This may require allocating more memory");
		}
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		// Create neccessary amount of space
		AnalysisPair[] aP = new AnalysisPair[(int) Math.pow(256, n)];
		int j = n - 1;
		for (int i = 0; i < aP.length; i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Adds result to array and checks probability
			aP[i] = new AnalysisPair(tempS, model.prob(tempS));
			// Moves back to least significant character
			j = n - 1;

		}
		return aP;
	}

	/**
	 * Method for training a given model by reading each line of the given file
	 * and constructing probabilities for each possible ngram
	 * 
	 * @param model
	 *            The model to be trained
	 * @param filename
	 *            The filename of the file to be read
	 * @return A trained NGramProcessLM model
	 */
	public static NGramProcessLM train(NGramProcessLM model, String filename) {
		File f = new File(filename);

		// Attempts to create a Buffered Reader to parse file
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			// For each line of the file train the model and output to console
			for (String line; (line = br.readLine()) != null;) {
				model.train(line);
			}
			br.close();
			return model;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method for training a TrieCharSeqCounter for the counts of ngrams
	 * appearing in our model for the given value of n
	 * 
	 * @param tcsc
	 *            the TrieCharSeqCounter object we're training
	 * @param n
	 *            the size of the ngram
	 * @param filename
	 *            the file we are training from
	 * @return the trained TrieCharSeqCounter object
	 */
	public static TrieCharSeqCounter trainTCSC(TrieCharSeqCounter tcsc, int n, String filename) {
		File f = new File(filename);
		int loop = 0;
		int c;
		char currentchar;
		char[] ngram = new char[n];
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			// Read file character by character
			while ((c = br.read()) != -1) {
				currentchar = (char) c;
				if (loop < n - 1) {
					// Fill ngram until almost full
					ngram[loop] = currentchar;
				} else if (loop == n - 1) {
					// Fill final spot (No Trim) then count
					ngram[loop] = currentchar;
					tcsc.incrementSubstrings(new String(ngram));
				} else {
					// Trim then Fill then count
					stringTrim(ngram);
					ngram[n - 1] = currentchar;
					tcsc.incrementSubstrings(new String(ngram));
				}
				// Count Loops
				loop++;
			}
			br.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		// Return
		return tcsc;
	}

	/**
	 * Method for returning the topX most likely NGrams for a given N-1Gram for
	 * a given model. Currently the model looks at letters between 32 and 126
	 * for testing
	 * 
	 * @param input
	 *            The N-1Gram to be expanded
	 * @param model
	 *            The model for the expected probabilities
	 * @param topX
	 *            The amount of results to be returned
	 * @return An array of pairs of length topX that contains a given string and
	 *         its associated probability
	 */
	public static AnalysisPair[] likelyNGrams(String input, NGramProcessLM model, int topX) {
		AnalysisPair[] aP = new AnalysisPair[256];
		if (input.length() != model.maxNGram() - 1) {
			System.out.println("Input string has the wrong amount of characters: Required - " + (model.maxNGram() - 1)
					+ ", Entered - " + input.length());
			return null;
		}
		// Read all possible results for NGram extension (n-1 to n)
		for (int i = 0; i < 256; i++) {
			String s = input + (char) (i);
			aP[i] = new AnalysisPair(s, model.prob(s));
		}
		// Sort in order of probability (Bubble Sort)
		AnalysisPair temp;
		for (int i = 0; i < 256; i++) {
			for (int j = 1; j < (256 - i); j++) {

				if (aP[j - 1].getProbability() < aP[j].getProbability()) {
					temp = aP[j - 1];
					aP[j - 1] = aP[j];
					aP[j] = temp;
				}

			}
		}
		// Print results
		AnalysisPair[] output = new AnalysisPair[topX];
		for (int i = 0; i < topX; i++) {
			output[i] = new AnalysisPair(aP[i].getNGram(), aP[i].getProbability());
		}
		// Return Sorted NGram information
		return output;
	}

	/**
	 * Adds a single appearance to every item in the model
	 * 
	 * @param prob
	 *            The double to be smoothed
	 * @param count
	 *            The total amount of results from the model
	 * @return The smoothed double
	 */
	public static TrieCharSeqCounter smoothingLaplace(TrieCharSeqCounter counter, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < Math.pow(256, n); i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Trains for an extra result
			counter.incrementSubstrings(tempS);
			// Moves back to least significant character
			j = n - 1;
		}
		// Returns the smoothed model
		return counter;
	}

	/**
	 * TODO: JAVADOC
	 * 
	 * @param model
	 * @param n
	 * @return
	 */
	public static TrieCharSeqCounter smoothingGoodTuring(TrieCharSeqCounter counter, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		int val = (int) Math.pow(256, n);
		AnalysisPair[] aP = new AnalysisPair[val];
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < val; i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Trains for an extra result
			aP[i] = new AnalysisPair(tempS, counter.count(tempS));
			// Moves back to least significant character
			j = n - 1;
		}
		// Returns the smoothed model
		return counter;
	}

	/**
	 * TODO: JAVADOC
	 * 
	 * @param counter
	 * @param n
	 * @return
	 */
	public static TrieCharSeqCounter smoothingWittenBell(TrieCharSeqCounter counter, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		int count = 0;
		String tempS;
		int val = (int) Math.pow(256, n);
		long total = counter.totalSequenceCount();
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < val; i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Moves back to least significant character
			j = n - 1;
			// Check for smooth
			if (counter.count(tempS) > 0) {
				counter.incrementSubstrings(tempS, 3);
			} else {
				counter.incrementSubstrings(tempS, 7);
			}
		}
		// Console Information
		double offset = (double) count / (double) total;
		double negOffset = 1 - offset;
		System.out.println("TOTALT: " + total);
		System.out.println("COUNT: " + count);
		System.out.println("OFFSET: " + (double) offset);
		System.out.println("NEG OFFSET: " + (double) negOffset);
		// Returns the smoothed model
		return counter;
	}

	/**
	 * This method is for saving an NGramProcessLM model to the disc
	 * 
	 * @param filename
	 *            The name of the file to be created
	 * @param model
	 *            The model being exported
	 */
	public static void saveToFile(String filename, NGramProcessLM model) {
		// Create File
		File f = new File("./resources/models/" + filename + ".txt");
		System.out.println("SAVING: " + f.getAbsolutePath());
		OutputStream out;
		try {
			// Establish Output Stream
			out = new FileOutputStream(f);
			model.writeTo(out);
			BufferedOutputStream bOutput = new BufferedOutputStream(out);
			// Dump model to Output Stream
			model.writeTo(bOutput);
			// Close Stream
			bOutput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Method for loading an NGramProcessLM model from the disc
	 * 
	 * @param filename
	 *            The file to be loaded
	 * @return The model containing the data from the loaded file
	 */
	public static NGramProcessLM loadFromFile(String filename) {
		NGramProcessLM model = null;
		// Create File
		File f = new File("./resources/models/" + filename + ".txt");
		System.out.println("LOADING: " + f.getName());
		try {
			// Establish Input Stream
			InputStream input;
			input = new FileInputStream(f);
			BufferedInputStream bInput = new BufferedInputStream(input);
			// Read model from Input Stream
			model = NGramProcessLM.readFrom(bInput);
			// Close Stream
			bInput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * This method is for saving a TrieCharSeqCounter object to the disc
	 * 
	 * @param filename
	 *            The name of the file to be created
	 * @param model
	 *            The counter being exported
	 */
	public static void saveCounter(TrieCharSeqCounter tcsc, String filename) {
		// Create File
		File f = new File("./resources/counters/" + filename + ".txt");
		System.out.println("SAVING: " + f.getAbsolutePath());
		OutputStream out;
		try {
			// Establish Output Stream
			out = new FileOutputStream(f);
			tcsc.writeTo(out);
			BufferedOutputStream bOutput = new BufferedOutputStream(out);
			// Dump model to Output Stream
			tcsc.writeTo(bOutput);
			// Close Stream
			bOutput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Method for loading an TrieCharSeqCounter object from the disc
	 * 
	 * @param filename
	 *            The file to be loaded
	 * @return The counter containing the data from the loaded file
	 */
	public static TrieCharSeqCounter loadCounter(String filename) {
		TrieCharSeqCounter tcsc = null;
		// Create File
		File f = new File("./resources/counters/" + filename + ".txt");
		System.out.println("LOADING: " + f.getName());
		try {
			// Establish Input Stream
			InputStream input;
			input = new FileInputStream(f);
			BufferedInputStream bInput = new BufferedInputStream(input);
			// Read model from Input Stream
			tcsc = TrieCharSeqCounter.readFrom(bInput);
			// Close Stream
			bInput.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return tcsc;
	}

	/**
	 * Smoothing method for adding a predetermined integer to each count,
	 * potentially (and usually) a more aggressive form of LaPlace smoothing
	 * 
	 * @param counter the TrieCharSeqCounter to smooth 
	 * @param x the amount to add to each value
	 * @param n the length of the ngram
	 * @return the smoothed counter
	 */
	public static TrieCharSeqCounter smoothingAddX(TrieCharSeqCounter counter, int x, int n) {
		// Follows the same process as the AP model production
		char[] cSeq = new char[n];
		int temp;
		String tempS;
		// Fill string with ASCII(0) characters
		for (int i = 0; i < n; i++) {
			cSeq[i] = Character.valueOf((char) 0);
		}
		int j = n - 1;
		for (int i = 0; i < Math.pow(256, n); i++) {
			// Moves onto the next significant character if it's 256
			while (Integer.valueOf((int) cSeq[j]) == 256) {
				cSeq[j] = Character.valueOf((char) 0);
				j--;
			}
			// Steps the character
			temp = Integer.valueOf((int) cSeq[j]);
			cSeq[j] = Character.valueOf((char) (temp + 1));
			tempS = String.valueOf(cSeq);
			// Trains for an extra result
			counter.incrementSubstrings(tempS, x);
			// Moves back to least significant character
			j = n - 1;
		}
		// Returns the smoothed model
		return counter;
	}
}