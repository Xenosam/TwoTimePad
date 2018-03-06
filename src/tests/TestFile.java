package tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.aliasi.lm.NGramProcessLM;

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
		System.out.println(model.prob("and"));
		assertEquals("TEST2: Assess Information", 7, (int)Integer.valueOf((int)(model.prob("and")*1000)));
	}
}
