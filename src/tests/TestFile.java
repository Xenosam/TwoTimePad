package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.aliasi.lm.NGramProcessLM;

import project.LanguageModel;

public class TestFile {

	LanguageModel lm = new LanguageModel();
	NGramProcessLM model = new NGramProcessLM(7);
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	/*
	 * Creates Models and tests that NGram length is correct
	 */
	@Test
	public void testOne() {
		//assertEquals("String", $ExpectedOutcome, methodForTest());
	    assertEquals("TEST1: Create Model", 7, LanguageModel.createModel(7).maxNGram());
	}
	
	/*
	 * Recieves useful information from model
	 */
	@Test
	public void testTwo() {
		
		assertEquals("TEST1: Create Model", 3, (int)LanguageModel.createModel(7).log2ConditionalEstimate("and i a"));
	}
}
