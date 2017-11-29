import java.io.File;
import java.util.Scanner;

import org.omg.CORBA.portable.OutputStream;

import com.aliasi.lm.NGramProcessLM;

public class LangModel {
	
	NGramProcessLM mLM;
	final File folder;
	String directory = "C:/Users/Andrew/Documents/NetBeansProjects/CppApplication_1/corpus/";
	
	public static void main(String args[]) {
		mLm = new NGramProcessLM(7);
		folder = new File(directory);
		for (final File f : folder.listFiles()) {	
			Scanner scanner = new Scanner(f).useDelimiter("\\Z");
			if(!scanner.hasNext()) continue;
			String read = scanner.next();
			mLM.train(read);
			scanner.close();
		}
		
		ByteArrayOutputStream ouput = new ByteArrayOutputStream();
		mLM.writeTo(output);
		try {
			OutputStream os = new FileOutputStream(outputCorpus);
			bytesOut.writeTo(os);
			os.close();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		System.out.println("\nDone");
	}
}