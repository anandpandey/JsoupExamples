import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;

/**
 * @author pandey
 *
 */
public class JsoupTestAmazonUrlToImage {

	private static final String TOY_STORY_URL = "http://www.thetoystore.com";
	private static final String IMAGE_FOLDER = "output/images_amazon/";

	public JsoupTestAmazonUrlToImage() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {

		Path imageFolderPath = Paths.get(IMAGE_FOLDER);

		boolean pathExists = Files.exists(imageFolderPath, LinkOption.NOFOLLOW_LINKS);

		if (!pathExists) {
			Files.createDirectory(imageFolderPath);
		}

		// Reader
		BufferedReader br = new BufferedReader(new FileReader("config/amazonkeywordsUrl4img.txt"));

		try {
			String line = br.readLine();

			while (line != null) {

				try {
					fetchImageUrlAndCreateImage(line);
					line = br.readLine();
				} catch (IOException e) {
					System.out.println(line);
					e.printStackTrace();
					line = br.readLine();
				}

			}
		} finally {
			br.close();
		}

	}

	private static void fetchImageUrlAndCreateImage(String keywordUrl) throws IOException {

		String[] keywordUrlArr = keywordUrl.split(",");

		String url1 = keywordUrlArr[1];

		// System.out.println(url1);

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		// get the HTML document
		Document doc = Jsoup.connect(url1).get();

//		System.out.println(doc);
//		System.exit(1);

		String docString = doc.toString();
		int value = docString.indexOf("ImageBlockATF");
		int value2 = docString.indexOf("A.trigger('P.AboveTheFold')");

		String requiredValue = docString.substring(value, value2);
//		System.out.println(requiredValue);


		int index = requiredValue.indexOf("hiRes");
		int index1 = requiredValue.indexOf(",\"thumb");

		int j = 1;
		String imageValue = requiredValue.substring(index, index1);
		imageValue = imageValue.substring(7).replaceAll("\"","");

		boolean highResNotFound = false;
		if (imageValue.equals("null")) {
			highResNotFound = true;
			downloadLargeImage(doc, keywordUrlArr);
		} else {
			if (!"".equals(imageValue)) {
				try {
					// Open a URL Stream
					getImages(imageValue, keywordUrlArr[0], j);
				} catch (Exception e) {
					e.printStackTrace();
				}
//
			}
		}
//		System.out.println(imageValue.substring(7));

		while (!highResNotFound && index >= 0 &&
				(index < requiredValue.length()
						&& index1 < requiredValue.length())) {
//			System.out.println(index);
			j++;

			index = requiredValue.indexOf("hiRes", index + 1);
			index1 = requiredValue.indexOf(",\"thumb", index1 + 1);

			// avoiding array index out of bound
			if (index == -1 || index1 == 1) {
				break;
			}

			imageValue = requiredValue.substring(index, index1);
			imageValue = imageValue.substring(7).replaceAll("\"", "");


			if (imageValue.equals("null")) {
				downloadLargeImage(doc, keywordUrlArr);
			} else {
				if (!"".equals(imageValue)) {
					try {
						// Open a URL Stream
						getImages(imageValue, keywordUrlArr[0], j);
					} catch (Exception e) {
						e.printStackTrace();
					}
//
				}
			}
//			System.out.println(imageValue.substring(7));
		}

//		System.exit(1);
	}
//		if (newsHeadlines != null && newsHeadlines.get(0) != null) {
//			Element element = newsHeadlines.get(0);

//			System.out.println(element.attr("href"));
			

//		}

//	}

	private static void downloadLargeImage(Document doc, String[] keywordUrlArr) {
		Elements newsHeadlines = doc.getElementsByTag("span");

		int i = 0;
		for (Element spanElement : newsHeadlines) {
//			System.out.println(spanElement);
			if (spanElement.attr("data-action").equals("thumb-action")) {
				Elements imageElements = spanElement.getElementsByTag("img");
				for (Element element : imageElements) {


					String imageSrc = element.attr("src").replace("._SS40_.", ".");

					System.out.println();
					if (imageSrc != null && !"".equals(imageSrc)) {
						try {
							// Open a URL Stream
							i++;
							getImages(imageSrc, keywordUrlArr[0], i);
						} catch (Exception e) {
							e.printStackTrace();
						}
//
					}
				}

			}
		}
	}

	private static void getImages(String src, String keyword, int count) throws IOException {

		System.out.println(src);

		String name = keyword + "_" + count+".jpg";
		// System.out.println(name);

		// to allow access to https links
//		System.setProperty("java.protocol.handler.pkgs",
//				"com.sun.net.ssl.internal.www.protocol");
//		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		// Open a URL Stream
		URL url = new URL(src);

		InputStream in = url.openStream();

		OutputStream out = new BufferedOutputStream(new FileOutputStream(IMAGE_FOLDER + name));
		for (int b; (b = in.read()) != -1;) {
			out.write(b);
		}
		out.close();
		in.close();

	}

}
