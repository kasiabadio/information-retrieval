import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Searcher
{

    public static void main(String args[]) throws IOException {
        // Load the previously generated index (DONE)
        IndexReader reader = getIndexReader();
        assert reader != null;

        // Construct index searcher (DONE)
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        // Standard analyzer
        // analysis based on basic grammar, removes stop words like "a", "an", converts to lowercase
        Analyzer analyzer = new StandardAnalyzer();

        // TODO your task is to construct several queries and seek for relevant documents

        // TERM QUERY
        // A Query that matches documents containing a term.
        // This may be combined with other terms with a BooleanQuery.
        // TODO seek for documents that contain word mammal
        String queryMammal = "MaMMal";
        TermQuery tq1;
        {
            // --------------------------------------
            // COMPLETE THE CODE HERE
            System.out.println("1) term query: mammal (CONTENT)");
            // as you may notice, this word is not normalized (but is should be normalized
            // in the same way as all documents were normalized when constructing the index.
            // For that reason you can use analyzer object (utf8TOString()!).

            // Then, build a Term object (seek in content - Constants.content) and TermQuery
            queryMammal = analyzer.normalize(Constants.content, queryMammal).utf8ToString();
            tq1 = new TermQuery(new Term(Constants.content, queryMammal));
            // Lastly, invoke printResultsForQuery.
            printResultsForQuery(indexSearcher, tq1);
            // --------------------------------------
        }

        // TODO Repeat the previous step for a word "bird".
        // Compare the results for "mammal" and "bird".
        String queryBird = "bird";
        TermQuery tq2;
        {
            // --------------------------------------
            System.out.println("2) term query bird (CONTENT)");
            queryBird = analyzer.normalize(Constants.content, queryBird).utf8ToString();
            tq2 = new TermQuery(new Term(Constants.content, queryBird));
            printResultsForQuery(indexSearcher, tq2);
            // --------------------------------------
        }

        //  now, we seek for documents that contain "mammal" or "bird".
        // Construct two clauses: BooleanClause (use BooleanClause.Occur to set a proper flag).
        // The first concerns tq1 ("mammal") and the second concerns ("bird").
        // To construct BooleanQuery, Use static methods of BooleanQuery.Builder().
        // Additionally, use setMinimumNumberShouldMatch() with a proper parameter
        // to generate "mammal" or "bird" rule.

        // Boolean query
        {
            // --------------------------------------
            System.out.println("3) boolean query (CONTENT): mammal or bird");
            BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
            booleanQuery.add(tq1, BooleanClause.Occur.SHOULD);
            booleanQuery.add(tq2, BooleanClause.Occur.SHOULD);
            booleanQuery.setMinimumNumberShouldMatch(1);
            //printResultsForQuery(indexSearcher, booleanQuery);
            // --------------------------------------
        }

        //  now, your task is to find all documents which size is smaller than 1000bytes.
        // For this reason, construct Range query.
        // Use IntPoint.newRangeQuery.
        {
            // --------------------------------------
            System.out.println("4) range query: file size in [0b, 1000b]");
            Query rangeQuery = IntPoint.newRangeQuery(Constants.filesize, 0, 1000);
            printResultsForQuery(indexSearcher, rangeQuery);
            // --------------------------------------
        }

        // let's find all documents which name starts with "ant".
        // For this reason, construct PrefixQuery.
        {
            // --------------------------------------
            System.out.println("5) Prefix query (FILENAME): ant");
            Query prefixQuery = new PrefixQuery(new Term(Constants.filename, "ant"));
            printResultsForQuery(indexSearcher, prefixQuery);
            // --------------------------------------
        }

        // let's build a wildcard query".
        // Construct a WildcardQuery object. Look for documents
        // which contain a term "eat?" "?" stand for any letter (* for a sequence of letters).
        {
            // --------------------------------------
            System.out.println("6) Wildcard query (CONTENT): eat?");
            Query wildcardQuery = new WildcardQuery(new Term(Constants.content, "eat?"));
            printResultsForQuery(indexSearcher, wildcardQuery);
            // --------------------------------------
        }

        // build a fuzzy query for a word "mamml" (use FuzzyQuerry).
        // Find all documents that contain words which are similar to "mamml".
        // Which documents have been found?
        {
            // --------------------------------------
            System.out.println("7) Fuzzy querry (CONTENT): mamml?");
            Query fuzzyQuery = new FuzzyQuery(new Term(Constants.content, "mamml"));
            printResultsForQuery(indexSearcher, fuzzyQuery);
            // --------------------------------------
        }

        // TODO now, use QueryParser to parse human-entered query strings
        // and generate query object.
        // - use AND, OR , NOT, (, ), + (must), and - (must not) to construct boolean queries
        // - use * and ? to contstruct wildcard queries
        // - use ~ to construct fuzzy (one word, similarity) or proximity (at least two words) queries
        // - use - to construct proximity queries
        // - use \ as an escape character for: + - && || ! ( ) { } [ ] ^ " ~ * ? : \
        // Consider following 5 examples of queries:
        String queryP1 = "MaMMal AND bat";
        String queryP2 = "ante*";
        String queryP3 = "brd~ ";
        String queryP4 = "(\"nocturnal life\"~10) OR bat";
        String queryP5 = "(\"nocturnal life\"~10) OR (\"are nocturnal\"~10)";
        // Select some query:
        String selectedQuery = queryP1;
        // Complete the code here, i.e., build query parser object, parse selected query
        // to query object, and find relevant documents. Analyze the outcomes.
        {
            // --------------------------------------
            System.out.println("8) query parser = " + selectedQuery);

            // --------------------------------------
        }


        try
        {
            reader.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void printResultsForQuery(IndexSearcher indexSearcher, Query q) throws IOException {

        // --------------------------------
        // - use indexSearcher to search for documents that
        // are relevant according to the query q
        // - Get TopDocs object (number of derived documents = Constant.top_docs)
        TopDocs topDocs = indexSearcher.search(q, Constants.top_docs);
        System.out.println("Total hits: " + topDocs.totalHits);
        // - Iterate over ScoreDocs (in in TopDocs) and print for each document (in separate lines):
        // a) score
        // b) filename
        // c) id
        // d) file size
        int i = 0;
        for (ScoreDoc scoreDoc: topDocs.scoreDocs){
            System.out.println("scoreDoc i: " + i);
            // You may use indexSearcher to get a Document object for some docId (ScoreDoc)
            // and use document.get(name of the field) to get the value of id, filename, etc.
            Document d = indexSearcher.doc(scoreDoc.doc);
            System.out.println(d.get("score")+": "+d.get("filename")+" (Id="+d.get("id")+") (Content="+d.get("content")+") (Size="+d.get("file size")+")");
            i += 1;
        }

        // --------------------------------
    }

    private static IndexReader getIndexReader()
    {
        try
        {
            Directory dir = FSDirectory.open(Paths.get(Constants.index_dir));
            return DirectoryReader.open(dir);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
