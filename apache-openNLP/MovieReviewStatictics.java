import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.stemmer.PorterStemmer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MovieReviewStatictics
{
    private static final String DOCUMENTS_PATH = "movies/";
    private int _verbCount = 0;
    private int _nounCount = 0;
    private int _adjectiveCount = 0;
    private int _adverbCount = 0;
    private int _totalTokensCount = 0;

    private PrintStream _statisticsWriter;

    private SentenceModel _sentenceModel;
    private TokenizerModel _tokenizerModel;
    private DictionaryLemmatizer _lemmatizer;
    private PorterStemmer _stemmer;
    private POSModel _posModel;
    private TokenNameFinderModel _peopleModel;
    private TokenNameFinderModel _placesModel;
    private TokenNameFinderModel _organizationsModel;

    public static void main(String[] args)
    {
        MovieReviewStatictics statistics = new MovieReviewStatictics();
        statistics.run();
    }

    private void run()
    {
        try
        {
            initModelsStemmerLemmatizer();

            File dir = new File(DOCUMENTS_PATH);
            File[] reviews = dir.listFiles((d, name) -> name.endsWith(".txt"));

            _statisticsWriter = new PrintStream("statistics.txt", "UTF-8");

            Arrays.sort(reviews, Comparator.comparing(File::getName));
            for (File file : reviews)
            {
                System.out.println("Movie: " + file.getName().replace(".txt", ""));
                _statisticsWriter.println("Movie: " + file.getName().replace(".txt", ""));

                String text = new String(Files.readAllBytes(file.toPath()));
                processFile(text);

                _statisticsWriter.println();
            }

            overallStatistics();
            _statisticsWriter.close();

        } catch (IOException ex)
        {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initModelsStemmerLemmatizer()
    {
        try
        {
            _stemmer = new PorterStemmer();

            InputStream is = getClass().getResourceAsStream("/models/en-lemmatizer.dict");
            _lemmatizer = new DictionaryLemmatizer(is);

            _tokenizerModel = new TokenizerModel(new File("models/de-token.bin"));

            _posModel = new POSModel(new File("models/en-pos-maxent.bin"));

            _sentenceModel = new SentenceModel(new File("models/en-sent.bin"));

            _peopleModel = new TokenNameFinderModel(new File("models/en-ner-person.bin"));
            _placesModel = new TokenNameFinderModel(new File("models/en-ner-location.bin"));
            _organizationsModel = new TokenNameFinderModel(new File("models/en-ner-organization.bin"));

        } catch (IOException ex)
        {
            Logger.getLogger(MovieReviewStatictics.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFile(String text) throws IOException {
        // process the text to find the following statistics:
        // For each movie derive:
        //    - number of sentences
        int noSentences = 0;
        //    - number of tokens
        int noTokens = 0;
        //    - number of (unique) stemmed forms
        int noStemmed = 0;
        //    - number of (unique) words from a dictionary (lemmatization)
        int noWords = 0;
        //    -  people
        Span people[] = new Span[] { };
        //    - locations
        Span locations[] = new Span[] { };
        //    - organisations
        Span organisations[] = new Span[] { };

        // + compute the following overall (for all movies) POS tagging statistics:
        //    - percentage number of adverbs (class variable, private int _verbCount = 0)
        //    - percentage number of adjectives (class variable, private int _nounCount = 0)
        //    - percentage number of verbs (class variable, private int _adjectiveCount = 0)
        //    - percentage number of nouns (class variable, private int _adverbCount = 0)
        //    + update _totalTokensCount

        // ------------------------------------------------------------------

        // derive sentences (update noSentences variable)
        SentenceDetectorME sd = new SentenceDetectorME(_sentenceModel);
        noSentences = sd.sentDetect(text).length;

        // derive tokens and POS tags from text
        // (update noTokens and _totalTokensCount)
        TokenizerME tm = new TokenizerME(_tokenizerModel);
        String [] tokens = tm.tokenize(text);
        noTokens = tokens.length;
        _totalTokensCount += noTokens;
        POSTaggerME tagger = new POSTaggerME(_posModel);
        String [] tags = tagger.tag(tokens);

        // perform stemming (use derived tokens)
        // (update noStemmed)
        Set <String> stems = new HashSet <>();
        for (String token: tokens){
            stems.add(_stemmer.stem(token.toLowerCase().replaceAll("[^a-z0-9]", "")));
        }
        noStemmed = stems.size();

        // perform lemmatization (use derived tokens)
        // (remove "O" from results - non-dictionary forms, update noWords)
        String [] lemmatized = _lemmatizer.lemmatize(tokens, tags);
        List<String> lemmatized_new = new ArrayList<String>(Arrays.asList(lemmatized));
        lemmatized_new.remove("0");
        lemmatized = lemmatized_new.toArray(new String[0]);
        noWords = lemmatized.length;

        // derive people, locations, organisations (use tokens),
        // (update people, locations, organisations lists).
        NameFinderME peopleFinder = new NameFinderME(_peopleModel);
        //people = peopleFinder.find(tokens);

        NameFinderME placesFinder = new NameFinderME(_placesModel);
        //locations = placesFinder.find(tokens);

        NameFinderME organizationsFinder = new NameFinderME(_organizationsModel);
        //organisations = organizationsFinder.find(tokens);

        // update overall statistics - use tags and check first letters
        // verb, noun, adjective, adverb (czasownik, rzeczownik, przymiotnik, przysłówek)
        for (String tag: tags){
            if (tag.startsWith("V")){
                _verbCount++;
            } else if (tag.startsWith("N")){
                _nounCount++;
            } else if (tag.startsWith("J")){
                _adjectiveCount++;
            } else if (tag.startsWith("R")){
                _adverbCount++;
            }
        }

        // ------------------------------------------------------------------
        saveResults("Sentences", noSentences);
        saveResults("Tokens", noTokens);
        saveResults("Stemmed forms (unique)", noStemmed);
        saveResults("Words from a dictionary (unique)", noWords);

        saveNamedEntities("People", people, new String[] { });
        saveNamedEntities("Locations", locations, new String[] { });
        saveNamedEntities("Organizations", organisations, new String[] { });
    }


    private void saveResults(String feature, int count)
    {
        String s = feature + ": " + count;
        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void saveNamedEntities(String entityType, Span spans[], String tokens[])
    {
        StringBuilder s = new StringBuilder(entityType + ": ");
        for (int sp = 0; sp < spans.length; sp++)
        {
            for (int i = spans[sp].getStart(); i < spans[sp].getEnd(); i++)
            {
                s.append(tokens[i]);
                if (i < spans[sp].getEnd() - 1) s.append(" ");
            }
            if (sp < spans.length - 1) s.append(", ");
        }

        System.out.println("   " + s);
        _statisticsWriter.println(s);
    }

    private void overallStatistics()
    {
        _statisticsWriter.println("---------OVERALL STATISTICS----------");
        DecimalFormat f = new DecimalFormat("#0.00");

        if (_totalTokensCount == 0) _totalTokensCount = 1;
        String verbs = f.format(((double) _verbCount * 100) / _totalTokensCount);
        String nouns = f.format(((double) _nounCount * 100) / _totalTokensCount);
        String adjectives = f.format(((double) _adjectiveCount * 100) / _totalTokensCount);
        String adverbs = f.format(((double) _adverbCount * 100) / _totalTokensCount);

        _statisticsWriter.println("Verbs: " + verbs + "%");
        _statisticsWriter.println("Nouns: " + nouns + "%");
        _statisticsWriter.println("Adjectives: " + adjectives + "%");
        _statisticsWriter.println("Adverbs: " + adverbs + "%");

        System.out.println("---------OVERALL STATISTICS----------");
        System.out.println("Adverbs: " + adverbs + "%");
        System.out.println("Adjectives: " + adjectives + "%");
        System.out.println("Verbs: " + verbs + "%");
        System.out.println("Nouns: " + nouns + "%");
    }

}
