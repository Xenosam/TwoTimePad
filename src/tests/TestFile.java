package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.aliasi.lm.NGramProcessLM;

import project.AnalysisPair;
import project.LanguageModel;

public class TestFile {

	NGramProcessLM model;
	
	@Before
	public void setUp() throws Exception {
		model = LanguageModel.train(LanguageModel.createModel(3), "C:/Users/Andrew/workspace/TwoTimeNLM/corpus/A Tale of Two Cities - Charles Dickens.txt");
	}
	
	/*
	 * Creates Models and tests that NGram length is correct
	 */
	@Test
	public void testOne() {
		//assertEquals("String", $ExpectedOutcome, methodForTest());
	    assertEquals("TEST1: Create Model", 3, model.maxNGram());
	}
	
	/*
	 * Recieves useful information from model and grabs the probability for a given n-gram
	 */
	@Test
	public void testTwo() throws IOException {
		assertEquals("TEST2: Access Model", 7, (int)Integer.valueOf((int)(model.prob("and")*1000)));
	}
	
	/*
	 * 
	 */
	@Test
	public void testThree() {
		AnalysisPair aP = new AnalysisPair("and", 0.007321452);
		assertEquals("TEST3: Analysis Pair", "and, 0.007321452", aP.toString());
	}
	
	/*
	 * Grab the most likely N-Grams given an N-1Gram
	 */
	@Test
	public void testFour() {
		AnalysisPair aP = new AnalysisPair("and", 0.007442417374451658);
		assertEquals("TEST4: Testing most likely NGrams", aP.toString(), LanguageModel.likelyNGrams("an", model, 1)[0].toString());
	}
	
	/*
	 * 
	 */
	@Test
	public void test() {
		assertEquals("TEST:",0,0);
	}
}
