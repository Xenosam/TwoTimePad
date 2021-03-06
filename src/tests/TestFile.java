package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.aliasi.lm.NGramProcessLM;
import com.aliasi.lm.TrieCharSeqCounter;

import project.AnalysisPair;
import project.LanguageModel;

public class TestFile {

	NGramProcessLM model, model4, gt, wb;

	/**
	 * Setup method for initialising test variables
	 * 
	 * @throws Exception
	 *             Runtime Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = LanguageModel.train(LanguageModel.createModel(3),
				"./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		// model.setLambdaFactor(0);
		model4 = LanguageModel.train(LanguageModel.createModel(4),
				"./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		// model4.setLambdaFactor(0);
	}

	/**
	 * Creates Models and tests that NGram length is correct
	 */
	@Test
	public void testOne() {
		// assertEquals("String", $ExpectedOutcome, methodForTest());
		assertEquals("TEST1: Create Model", 3, model.maxNGram());
	}

	/**
	 * Recieves useful information from model and grabs the probability for a
	 * given n-gramw
	 */
	@Test
	public void testTwo() throws IOException {
		assertEquals("TEST2: Access Model", 7, (int) Integer.valueOf((int) (model.prob("and") * 1000)));
	}

	/**
	 * 
	 */
	@Test
	public void testThree() {
		AnalysisPair aP = new AnalysisPair("and", 0.007321452);
		assertEquals("TEST3: Analysis Pair",
				"NGRAM A: and\nNGRAM B: null\nDATA A: null\nDATA B: null\nPROB: 0.007321452", aP.toString());
	}

	/**
	 * Grab the most likely N-Grams given an N-1Gram
	 */
	@Test
	public void testFour() {
		AnalysisPair aP = new AnalysisPair("and", 0.007442417374451658);
		assertEquals("TEST4: Testing most likely NGrams", aP.toString(),
				LanguageModel.likelyNGrams("an", model, 1)[0].toString());
	}

	/**
	 * Test Save/Load functionality of the program
	 */
	@Test
	public void testFive() {
		LanguageModel.saveToFile("testfile", model);
		NGramProcessLM model2 = LanguageModel.loadFromFile("testfile");
		int i = (int) model2.prob("and") * 1000;
		int j = (int) model.prob("and") * 1000;
		File f = new File("./resources/models/testfile.txt");
		f.delete();
		assertEquals("TEST5: Saving/Loading Model to/from File", i, j);
	}

	/**
	 * Creating a seperate model using the AnalysisPair class for more helpful
	 * analysis
	 */
	// @Test
	public void testSix() {
		AnalysisPair[] aP = LanguageModel.createAPModel(model, 3);
		assertEquals("TEST6: AnalysisPair model", (int) (model.prob("and") * 1000),
				(int) (aP[6435122].getProbability() * 1000));
	}

	/**
	 * Same as testSix() but for models where ngrams are length 4 this test may
	 * fail when a small amount of memory is alloted to JVM
	 */
	// @Test
	public void testSeven() {
		AnalysisPair[] aP = LanguageModel.createAPModel(model, 4);
		int correctindex = 0;
		for (int i = 0; i < aP.length; i++) {
			if (aP[i].getNGram().equals("and")) {
				correctindex = i;
				break;
			}
		}
		System.out.println(correctindex);
		assertEquals("TEST7: AnalysisPair model for 4grams", (int) (model.prob("and ") * 1000),
				(int) (aP[correctindex].getProbability() * 1000));
	}

	/**
	 * Language Smoothing Test (Laplace)
	 * 
	 */
	@Test
	public void testEight() {
		int n = 3;
		String s = "g,!";
		TrieCharSeqCounter lm = new TrieCharSeqCounter(n);
		lm = LanguageModel.trainTCSC(lm, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		TrieCharSeqCounter laplace = new TrieCharSeqCounter(n);
		laplace = LanguageModel.smoothingLaplace(laplace, n);
		laplace = LanguageModel.trainTCSC(laplace, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		System.out.println("LAPLACE: " + laplace.count(s) + ", BASE: " + lm.count(s));
		assertEquals("TEST8: Laplace Smoothing", true, laplace.count(s) > lm.count(s));
	}

	/**
	 * Handle splitting an XOR input
	 */
	@Test
	public void testNine() {
		assertEquals("TEST9: XOR Handler", '_', LanguageModel.XORHandler('u')[42]);
	}

	/**
	 * Testing the QuickSort Method for score sorting
	 */
	@Test
	public void testTen() {
		AnalysisPair[] testArr = new AnalysisPair[5];
		testArr[0] = new AnalysisPair("000", 0.5);
		testArr[1] = new AnalysisPair("001", 0.4);
		testArr[2] = new AnalysisPair("002", 0.3);
		testArr[3] = new AnalysisPair("003", 0.2);
		testArr[4] = new AnalysisPair("004", 0.1);
		AnalysisPair[] output = LanguageModel.quickSort(0, 4, testArr);
		assertEquals("TEST10: QuickSort", true, output[4].getProbability() > output[0].getProbability());
	}

	/**
	 * Test the char array string timming
	 */
	@Test
	public void testEleven() {
		char[] out = { 'e', 'l', 'l', 'o', 0 };
		assertEquals("TEST11: CharTrim", new String(out), new String(LanguageModel.stringTrim("Hello".toCharArray())));
	}

	/**
	 * Testing a simiplified setup for the decryption process the assumeptions
	 * include only taking the most prominent result for each cycle and a
	 * simplified ignorant walk
	 */
	@Test
	public void testTwelve() {
		char[] f = createXOR(((char) 2 + "secret data").toCharArray(), ((char) 2 + "hidden info").toCharArray());
		for (int i = 0; i < f.length; i++) {
			System.out.println("i: " + i);
			System.out.println("char: " + f[i]);
			System.out.println("int val: " + Integer.valueOf((int) f[i]));
		}
		char[] c = { 2, 'C', 'h', 'a', 'd', 'o', 'w', ' ', 'y', 'g', 's', 'e' };
		assertEquals("TEST12: Simple Decrypt", new String(c), LanguageModel.simpleSolver(f, model)[0]);
	}

	/**
	 * Testing the final more complex solver solution for retaining an amount of
	 * best guesses and extending from there
	 * 
	 * @throws IOException
	 *             Exception for the IO complexities
	 */
	@Test
	public void testThirteen() throws IOException {
		FileWriter fw = new FileWriter("./newfile.txt");
		fw.append(new String(createXOR(((char) 2 + "this is the test!").toCharArray(),
				((char) 2 + "I am also to test").toCharArray())));
		File f = new File("./newfile.txt");
		fw.close();
		int x = 3;
		String[] output = LanguageModel.solver(f, model, x, false);
		for (int i = 0; i < 3; i++) {
			System.out.println("i: " + i);
			System.out.println("A: " + output[i].substring(4));
			System.out.println("B: " + output[i + 3].substring(4));
		}
		String o = output[0].substring(3, 20);
		assertEquals("TEST13: Complex Decrypt", "t as also to you ", o);
		f.delete();
	}

	/**
	 * Testing the language smoothing for a Good-Turing Solution - Retired Due to Time Constraints
	 * 
	 * @Test public void testFourteen() { int n = 3; String s = "g,!";
	 *       TrieCharSeqCounter lm = new TrieCharSeqCounter(n); lm =
	 *       LanguageModel.trainTCSC(lm, n, "./resources/corpus/A Tale of Two
	 *       Cities - Charles Dickens.txt"); TrieCharSeqCounter gt = new
	 *       TrieCharSeqCounter(n); gt = LanguageModel.trainTCSC(gt, n,
	 *       "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
	 *       gt = LanguageModel.smoothingGoodTuring(gt, n);
	 *       System.out.println("GT: " + gt.count(s) + ", BASE: " +
	 *       lm.count(s)); assertEquals("TEST14: Good Turing Smoothing", true,
	 *       gt.count(s) > lm.count(s)); }
	 */

	/**
	 * Testing the language smoothing for a Witten-Bell Solution
	 */
	@Test
	public void testFifteen() {
		int n = 3;
		int x = 3;
		String s = "g,!";
		TrieCharSeqCounter lm = new TrieCharSeqCounter(n);
		lm = LanguageModel.trainTCSC(lm, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		TrieCharSeqCounter wb = new TrieCharSeqCounter(n);
		wb = LanguageModel.trainTCSC(wb, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		wb = LanguageModel.smoothingWittenBell(wb, n, x);
		System.out.println("WB: " + wb.count(s) + ", BASE: " + lm.count(s));
		assertEquals("TEST15: Witten Bell Smoothing", true, wb.count(s) > lm.count(s));
	}

	/**
	 * TrieCharSeqCounter for smoothing
	 */
	@Test
	public void testSixteen() {
		TrieCharSeqCounter tcsc = new TrieCharSeqCounter(3);
		tcsc = LanguageModel.trainTCSC(tcsc, 3, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		System.out.println("TOP 10 NGRAMS:");
		System.out.println(tcsc.topNGrams(3, 10));
		assertEquals("TEST16: Fixing Smoothing", 7261884, tcsc.totalSequenceCount());
	}

	/**
	 * TrieCharSeqCounter Load/Save
	 */
	@Test
	public void testSeventeen() {
		TrieCharSeqCounter tcsc = new TrieCharSeqCounter(3);
		tcsc = LanguageModel.trainTCSC(tcsc, 3, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		LanguageModel.saveCounter(tcsc, "testcounter");
		TrieCharSeqCounter temp = LanguageModel.loadCounter("testcounter");
		boolean flag = (temp.totalSequenceCount() == tcsc.totalSequenceCount());
		File f = new File("./resources/counters/testcounter.txt");
		f.delete();
		assertEquals("TEST17: Test Save/Load", true, flag);
	}

	/**
	 * @throws IOException
	 * 
	 */
	@Test
	public void testEighteen() throws IOException {
		int n = 3;
		int x = 3;
		FileWriter fw = new FileWriter("./resources/ciphertext/newfile.txt");
		TrieCharSeqCounter tcsc = new TrieCharSeqCounter(n);
		tcsc = LanguageModel.trainTCSC(tcsc, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		fw.append(new String(createXOR(("this is the test!").toCharArray(), ("I am also to test").toCharArray())));
		File f = new File("./resources/ciphertext/newfile.txt");
		fw.close();
		String[] output = LanguageModel.TCSCSolver(f, tcsc, n, x, false);
		f.delete();
		for (int i = 0; i < 3; i++) {
			System.out.println("i: " + i);
			System.out.println("A: " + output[i].substring(4));
			System.out.println("B: " + output[i + 3].substring(4));
		}
		String o = output[3].substring(3, 20);
		assertEquals("TEST18: TCSC Decrypt", "l a t ander ander", o);
	}

	/**
	 * Test for agressive additive smooth
	 */
	@Test
	public void testNineteen() {
		int n = 3;
		int x = 3;
		String s = "g,!";
		TrieCharSeqCounter lm = new TrieCharSeqCounter(n);
		lm = LanguageModel.trainTCSC(lm, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		TrieCharSeqCounter ax = new TrieCharSeqCounter(n);
		ax = LanguageModel.trainTCSC(ax, n, "./resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		ax = LanguageModel.smoothingAddX(ax, x, n);
		System.out.println("AX: " + ax.count(s) + ", BASE: " + lm.count(s));
		assertEquals("TEST19: ADD X Smoothing", true, ax.count(s) > lm.count(s));
	}

	/*
	 * @Test public void test() { assertEquals("TEST:", 0, 0); }
	 */

	/**
	 * TODO: JAVADOC
	 * 
	 * @param a
	 * @param b
	 * @throws IOException
	 */
	public void createXORFile(String a, String b) throws IOException {
		FileWriter fw = new FileWriter("./newfile.txt");
		fw.append(new String(createXOR(a.toCharArray(), b.toCharArray())));
		fw.close();
	}

	/**
	 * TODO: JAVADOC
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static char[] createXOR(char[] a, char[] b) {
		char[] output = new char[a.length];
		for (int i = 0; i < a.length; i++) {
			output[i] = (char) (a[i] ^ b[i]);
		}
		return output;
	}
}
