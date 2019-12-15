package MulticastRMIServers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MulticastIndexer  extends Thread{
    MulticastServer server;
    int quoteLength;

    MulticastIndexer (MulticastServer server, int quoteLength) {
        this.server = server;
        this.quoteLength = quoteLength;
    }

    @Override
    public void run(){
        HashMap <String, Object> request = new HashMap<>();

        while (true){
            while (server.indexationQueue.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            request = server.addRemoveRequest(null, true);

            recursiveIndexation((String) request.get("URL"), (int) request.get("Level"));
        }
    }

    /* ======================================== EXTRACT WEBSITE INFO ======================================== */

    private void recursiveIndexation(String url, int level) {
        HashMap<String, Object> siteInfo = extractWebsiteInfo(url);

        if (level > 1){
            for (String conn_url : (ArrayList<String>) siteInfo.get("Links")) {
                recursiveIndexation("", level - 1);
            }
        }

        siteInfo.put("URL", url);

        server.indexNewURL(siteInfo);
    }

    public HashMap <String, Object> extractWebsiteInfo(String ws){
        HashMap <String, Object> websiteInfo = new HashMap();

        ArrayList<String> titlesOut = new ArrayList<>();
        ArrayList<String> linksOut = new ArrayList<>();

        Document doc = null;
        Elements links;

        if (! ws.startsWith("http://") && ! ws.startsWith("https://"))
            ws = "http://".concat(ws);

        try {
            // Attempt to connect and get the document
            doc = Jsoup.connect(ws).get();  // Documentation: https://jsoup.org/

            // Title
            websiteInfo.put("DocTitle", doc.title());

        }catch (IOException e){
            System.out.println("Ups... Looks like there was a problem extracting data from the site");
        }

        links = doc.select("a[href]");

        for (Element link : links) {
            // Ignore bookmarks within the page
            if (link.attr("href").startsWith("#"))
                continue;

            // Shall we ignore local links? Otherwise we have to rebuild them for future parsing
            if (!link.attr("href").startsWith("http"))
                continue;

            titlesOut.add(link.text());
            linksOut.add(link.attr("href"));
        }

        // Get website text and count words
        String text = doc.text(); // We can use doc.body().text() if we only want to get text from <body></body>

        websiteInfo.put("Quote", "\"" + doc.text().substring(0, quoteLength) + "...\"");

        websiteInfo.put("Titles", titlesOut);
        websiteInfo.put("Links", linksOut);

        websiteInfo.put("Words", getWords(text));

        return websiteInfo;
    }

    ArrayList<String> getWords(String text){
        ArrayList<String> out = new ArrayList<>();
        String line;

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));

        while (true) {
            try {
                if ((line = reader.readLine()) == null)
                    break;

                String[] words = line.split("[ ,;:.?!“”(){}\\[\\]<>']+");

                for (String word : words) {
                    word = word.toLowerCase();

                    if ("".equals(word))
                        continue;

                    if (!out.contains(word))
                        out.add(word);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }
}
