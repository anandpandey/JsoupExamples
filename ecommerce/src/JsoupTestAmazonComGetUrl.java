import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

/**
 * @author pandey
 *
 */
public class JsoupTestAmazonComGetUrl {

	private static final String AMAZON_STORY_URL = "https://www.amazon.com/s/ref=nb_sb_noss?url=search-alias%3Daps&field-keywords=";

	private static final String AMAZON_WEBSITE = "http://www.amazon.co.uk";

	/**
	 *
	 */
	public JsoupTestAmazonComGetUrl() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {

		// Reader
		BufferedReader br = new BufferedReader(new FileReader("config/keywords_amazoncom.txt"));

		// Writer
		File file = new File("output/amazoncomurlbatch7.csv");
		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter out = new PrintWriter(bw);

		try {
			String line = br.readLine();

			while (line != null) {

				try {
					out.println(fetchAndPrintData(line));
					out.flush();
					line = br.readLine();
				} catch (IOException e) {
					System.out.println(line);
					e.printStackTrace();
				}

			}
		} finally {

			out.close();
			br.close();
		}

	}

	private static String fetchAndPrintData(String url1) throws IOException {

		StringBuilder sb = new StringBuilder();
		sb.append(url1);
		
		sb.append(",");
		
		url1 = AMAZON_STORY_URL.concat(url1);

//		try {
//			Thread.sleep(500);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		Document doc = Jsoup.connect(url1).get();


		Elements newsHeadlines = doc.getElementsByTag("a");

		for (Element element : newsHeadlines) {

			if (element.attr("class").contains("a-link-normal a-text-normal")) {
				System.out.println(element.attr("href"));
				sb.append(element.attr("href"));
				break;
			}

		}

		return sb.toString();
	}

}
