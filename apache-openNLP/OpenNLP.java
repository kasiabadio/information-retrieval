import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OpenNLP {

    public static String LANG_DETECT_MODEL = "models/langdetect-183.bin";
    public static String TOKENIZER_MODEL = "models/en-token.bin";
    public static String SENTENCE_MODEL = "models/en-sent.bin";
    public static String POS_MODEL = "models/en-pos-maxent.bin";
    public static String CHUNKER_MODEL = "models/en-chunker.bin";
    public static String LEMMATIZER_DICT = "models/en-lemmatizer.dict";
    public static String NAME_MODEL = "models/en-ner-person.bin";
    public static String ENTITY_XYZ_MODEL = "models/en-ner-xyz.bin";

	public static void main(String[] args) throws IOException
    {
		OpenNLP openNLP = new OpenNLP();
		openNLP.run();
	}

	public void run() throws IOException
    {
		//languageDetection();
		//tokenization();
        //sentenceDetection();
		//posTagging();
		//lemmatization();
		//stemming();
		//chunking();
		nameFinding();
	}

	private void languageDetection() throws IOException
    {
		File modelFile = new File(LANG_DETECT_MODEL);
		LanguageDetectorModel model = new LanguageDetectorModel(modelFile);

		String text = "";
		text = "cats"; //plt

		text = "cats like milk"; //nob
		text = "Many cats like milk because in some ways it reminds them of their mother's milk."; //eng
		text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk."; //eng
		text = "The two things are not really related. Many cats like milk because in some ways it reminds them of their mother's milk. "
				+ "It is rich in fat and protein. They like the taste. They like the consistency . "
				+ "The issue as far as it being bad for them is the fact that cats often have difficulty digesting milk and so it may give them "
				+ "digestive upset like diarrhea, bloating and gas. After all, cow's milk is meant for baby calves, not cats. "
				+ "It is a fortunate quirk of nature that human digestive systems can also digest cow's milk. But humans and cats are not cows."; //eng
		text = "Many cats like milk because in some ways it reminds them of their mother's milk. Le lait n'est pas forc�ment mauvais pour les chats"; //eng
		text = "Many cats like milk because in some ways it reminds them of their mother's milk. Le lait n'est pas forc�ment mauvais pour les chats. " + "Der Normalfall ist allerdings der, dass Salonl�wen Milch weder brauchen noch gut verdauen k�nnen."; //eng

		LanguageDetector ld = new LanguageDetectorME(model);
//		Language[] predictedLanguages = ld.predictLanguages(text);
//		for (Language l: predictedLanguages){
//			System.out.println("Predicted language: "+ l.getLang());
//		}

		Language predictedLanguage = ld.predictLanguage(text);
		System.out.println("Predicted language: " + predictedLanguage.getLang());
	}

	private void tokenization() throws IOException
    {
		String text = "";

		text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
				+ "but there may have been instances of domestication as early as the Neolithic from around 9500 years ago (7500 BC).";
		/*text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
				+ "but there may have been instances of domestication as early as the Neolithic from around 9,500 years ago (7,500 BC).";
		text = "Since cats were venerated in ancient Egypt, they were commonly believed to have been domesticated there, "
		 + "but there may have been instances of domestication as early as the Neolithic from around 9 500 years ago ( 7 500 BC).";*/

		// EN
		System.out.println("-->EN");
		TokenizerModel model_en = new TokenizerModel(new File("models/en-token.bin"));
		TokenizerME tm_en = new TokenizerME(model_en);
		String [] tokenized_en = tm_en.tokenize(text); // tokenize input strings provided in the code
		for (String token: tokenized_en){
			System.out.println(token);
		}
		double [] probabilities_en = tm_en.getTokenProbabilities(); // obtain probability values
		for (double probability: probabilities_en){
			System.out.println(probability);
		}

		// DE
		System.out.println("\n-->DE");
		TokenizerModel model_de = new TokenizerModel(new File("models/de-token.bin"));
		TokenizerME tm_de = new TokenizerME(model_de);
		String [] tokenized_de = tm_de.tokenize(text); // tokenize input strings provided in the code
		for (String token: tokenized_de){
			System.out.println(token);
		}
		double [] probabilities_de = tm_de.getTokenProbabilities(); // obtain probability values
		for (double probability: probabilities_de){
			System.out.println(probability);
		}

		// Answer: There are differences in probabilities
	}

	private void sentenceDetection() throws IOException
    {
		String text = "";
		text = "Hi. How are you? Welcome to OpenNLP. "
				+ "We provide multiple built-in methods for Natural Language Processing.";
		//Result:
		//Hi. How are you?
		//Welcome to OpenNLP.
		//We provide multiple built-in methods for Natural Language Processing.

		text = "Hi. How are you?! Welcome to OpenNLP? "
				+ "We provide multiple built-in methods for Natural Language Processing.";

		//Result:
		//Hi. How are you?! Welcome to OpenNLP?
		//We provide multiple built-in methods for Natural Language Processing.

		text = "Hi. How are you? Welcome to OpenNLP.?? "
				+ "We provide multiple . built-in methods for Natural Language Processing.";

		//Result:
		//Hi. How are you?
		//Welcome to OpenNLP.??
		//We provide multiple .
		//built-in methods for Natural Language Processing.

		text = "The interrobang, also known as the interabang (often represented by ??! or !?), "
				+ "is a nonstandard punctuation mark used in various written languages. "
				+ "It is intended to combine the functions of the question mark (?.), or interrogative .point, "
				+ "and the exclamation mark (!), or exclamation point, known in the jargon of printers and programmers as a \"bang\". ";

		//Result:
		//The interrobang, also known as the interabang (often represented by ?! or !?), is a nonstandard punctuation mark used in various written languages.
		//It is intended to combine the functions of the question mark (?), or interrogative point, and the exclamation mark (!), or exclamation point, known in the jargon of printers and programmers as a "bang".


		SentenceModel model = new SentenceModel(new File("models/en-sent.bin"));
		SentenceDetectorME sd = new SentenceDetectorME(model);
		String [] sentences = sd.sentDetect(text); // determining sentences
		for (String sentence: sentences){
			System.out.println(sentence);
		}
		double [] probabilities = sd.getSentenceProbabilities(); // determining probabilities
		for (double probability: probabilities){
			System.out.println(probability);
		}

		// Answer 1: Yes, in third text - there is a dot
		// Answer 2: "Hi." gets concatenated with longer sentences
		// Answer 3: Dot after question mark didn't split text, same with additional question mark
	}

	private void posTagging() throws IOException {
		String[] sentence = new String[0];
		sentence = new String[] { "Cats", "like", "milk" };
		sentence = new String[]{"Cat", "is", "white", "like", "milk"};
		sentence = new String[] { "Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
				"built-in", "methods", "for", "Natural", "Language", "Processing" };
		sentence = new String[] { "She", "put", "the", "big", "knives", "on", "the", "table" };

		POSModel model = new POSModel(new File("models/en-pos-maxent.bin"));
		POSTaggerME tagger = new POSTaggerME(model);
		String [] tags = tagger.tag(sentence);

		for (String tag: tags){
			System.out.println(tag);
		}
	}

	private void lemmatization() throws IOException
    {
		String[] text = new String[0];
		text = new String[] { "Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
				"built-in", "methods", "for", "Natural", "Language",  "asd" };
		String[] tags = new String[0];
		tags = new String[] { "NNP", "WRB", "VBP", "PRP", "VB", "TO", "VB", "PRP", "VB", "JJ", "JJ", "NNS", "IN", "JJ",
				"NN", "VBG" };

		InputStream is = getClass().getResourceAsStream("/models/en-lemmatizer.dict");
		DictionaryLemmatizer dictionaryLemmatizer = new DictionaryLemmatizer(is); // returns base form of a word
		System.out.println("Dictionary lemmatizer: ");
		String [] lemmatized = dictionaryLemmatizer.lemmatize(text, tags); // returns an array of possible lemmas for each token in the sequence
		for (String lemma: lemmatized){
			System.out.println(lemma);
		}

		// Answer: Lemmatizer requires POS tags, because then the word is correctly lemmatized
		// When added "asd" word, which is not recognized, lemmatizer returned 0
	}

	private void stemming()
    {
		String[] sentence = new String[0];
		sentence = new String[] { "Hi", "How", "are", "you", "Welcome", "to", "OpenNLP", "We", "provide", "multiple",
				"built-in", "methods", "for", "Natural", "Language",  "asd" };

		PorterStemmer porterStemmer = new PorterStemmer(); // transforms a word into its root form
		System.out.println("Porter stemmer: ");
		for (String word: sentence){
			System.out.println(porterStemmer.stem(word));
		}

		// Answer: Lemmatizer changed "are" to "be" and porter stemmer cut it to "ar"
		// For "asd" word, stemmer didn't do anything

	}
	
	private void chunking() throws IOException
    {
		String[] sentence = new String[0];
		sentence = new String[] { "She", "put", "the", "big", "knives", "on", "the", "table"};

		String[] tags = new String[0];
		tags = new String[] { "PRP", "VBD", "DT", "JJ", "NNS", "IN", "DT", "NN" };

		ChunkerModel model = new ChunkerModel(new File("models/en-chunker.bin"));
		ChunkerME chunker = new ChunkerME(model);
		String [] chunks = chunker.chunk(sentence, tags); // chunker breaks the sentence into groups
		System.out.println("Chunks: ");
		for (String chunk: chunks){
			System.out.println(chunk);
		}

		// B - start of a chunk
		// I - continuation of a chunk

		// She (NP)
		// put (VP)
		// the (NP) big (NP) knives (NP)
		// on (PP)
		// the (NP) table (NP)

		// Answer: There are 5 chunks;
		// some of them are incorrect, for example "on" (IN), "the" (DT)
	}

	private void nameFinding() throws IOException
    {
		String text = "he idea of using computers to search for relevant pieces of information was popularized in the article "
				+ "As We May Think by Vannevar Bush in 1945. It would appear that Bush was inspired by patents "
				+ "for a 'statistical machine' - filed by Emanuel Goldberg in the 1920s and '30s - that searched for documents stored on film. "
				+ "The first description of a computer searching for information was described by Holmstrom in 1948, "
				+ "detailing an early mention of the Univac computer. Automated information retrieval systems were introduced in the 1950s: "
				+ "one even featured in the 1957 romantic comedy, Desk Set. In the 1960s, the first large information retrieval research group "
				+ "was formed by Gerard Salton at Cornell. By the 1970s several different retrieval techniques had been shown to perform "
				+ "well on small text corpora such as the Cranfield collection (several thousand documents). Large-scale retrieval systems, "
				+ "such as the Lockheed Dialog system, came into use early in the 1970s.";

		// detect named entities and numbers in texts
		TokenNameFinderModel model = new TokenNameFinderModel(new File("models/en-ner-xxx.bin"));
		NameFinderME nameFinder = new NameFinderME(model);
		Span[] spans = nameFinder.find(text.split("\\s+"));
		for (Span span: spans){
			System.out.println(span);
		}

		// Answer: model looks for dates
	}

}
