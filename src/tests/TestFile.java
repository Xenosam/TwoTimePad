package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.aliasi.lm.NGramProcessLM;

import project.AnalysisPair;
import project.LanguageModel;

public class TestFile {

	NGramProcessLM model, laplace;
<<<<<<< HEAD

	/**
	 * Setup method for initialising test variables
	 * 
	 * @throws Exception
	 *             Runtime Exception
=======
	
	/**
	 * Setup method for initialising test variables
	 * 
	 * @throws Exception Runtime Exception
>>>>>>> master
	 */
	@Before
	public void setUp() throws Exception {
		model = LanguageModel.train(LanguageModel.createModel(3),
				"C:/Users/Andrew/workspace/TwoTimeNLM/resources/corpus/A Tale of Two Cities - Charles Dickens.txt");
		laplace = LanguageModel.smoothingLaplace(model);
	}

	/**
	 * Creates Models and tests that NGram length is correct
	 */
	@Test
	public void testOne() {
		// assertEquals("String", $ExpectedOutcome, methodForTest());
		assertEquals("TEST1: Create Model", 3, model.maxNGram());
<<<<<<< HEAD
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
	@Test
	public void testSix() {
		AnalysisPair[] aP = LanguageModel.createAPModel(model, 3);
		int correctindex = 0;
		for (int i = 0; i < aP.length; i++) {
			if (aP[i].getNgram().equals("and")) {
				correctindex = i;
				break;
			}
		}

		assertEquals("TEST6: AnalysisPair model", (int) (model.prob("and") * 1000),
				(int) (aP[correctindex].getProbability() * 1000));
=======
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
>>>>>>> master
	}

	/**
	 * Language Smoothing Test (Laplace)
	 */
	@Test
<<<<<<< HEAD
	public void testSeven() {
		assertEquals("TEST7: Laplace Smoothing", (int) (0 * 1000),
				(int) LanguageModel.smoothingLaplace(model).prob("and") * 1000);
=======
	public void testSix() {
		assertEquals("TEST6: Laplace Smoothing", (int) (0 * 1000), (int) (laplace.prob("and") * 1000));
>>>>>>> master
	}

	/**
	 * 
	 */
	@Test
	public void test() {
		assertEquals("TEST:", 0, 0);
	}
}
