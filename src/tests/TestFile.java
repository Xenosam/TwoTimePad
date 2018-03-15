package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.aliasi.lm.NGramProcessLM;

import project.AnalysisPair;
import project.LanguageModel;

public class TestFile {

	NGramProcessLM model, model4, laplace;

	/**
	 * Setup method for initialising test variables
	 * 
	 * @throws Exception
	 *             Runtime Exception
	 */
	@Before
	public void setUp() throws Exception {
		model = LanguageModel.train(LanguageModel.createModel(3),
				"C:/Users/Andrew/workspace/TwoTimeNLM/resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		model4 = LanguageModel.train(LanguageModel.createModel(4),
				"C:/Users/Andrew/workspace/TwoTimeNLM/resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
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
	 * given n-gram
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
		assertEquals("TEST3: Analysis Pair", "and, 0.007321452", aP.toString());
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
		assertEquals("TEST5: Saving/Loading Model to/from File", (int) (model2.prob("and") * 1000),
				(int) (model.prob("and") * 1000));
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
	// @Test
	public void testEight() {
		NGramProcessLM lm = LanguageModel.createModel(3);
		laplace = LanguageModel.smoothingLaplace(model, model.maxNGram());

		assertEquals("TEST8: Laplace Smoothing", true, laplace.prob("g,!") > lm.prob("g,!"));
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
		for (AnalysisPair p : output)
			System.out.println(p.toString());
		assertEquals("TEST10: QuickSort", true, output[4].getProbability() > output[0].getProbability());
	}

	/**
	 * Testing a simiplified setup for the decryption process the assumeptions
	 * include only taking the most prominent result for each cycle and a
	 * simplified ignorant walk
	 */
	@Test
	public void testEleven() {
		String f = "C:/Users/Andrew/workspace/TwoTimeNLM/resources/ciphertext/test11.txt";
		assertEquals("TEST11: Simple Decrypt", "", LanguageModel.simpleSolver(new File(f), model)[0]);
	}

	/*
	 * @Test public void test() { assertEquals("TEST:", 0, 0); }
	 */
}
