package hello.parser;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by John Silver on 13.21.2016 21:15
 */
public class CsvToJson {

    private static final String CSV_DLIMITER = ";";

    private static final String OSCAR_NODE_ID = "competition_oscar";
    private static final String CANNES_NODE_ID = "competition_cannes";
    private static final String BERLIN_NODE_ID = "competition_berlin";
    private static final String VENICE_NODE_ID = "competition_venice";
    private static final String COMPETITION_SIZE = "10.1";

    private static List<String> oscarKeys = new ArrayList<>();
    private static List<String> cannesKeys = new ArrayList<>();
    private static List<String> berlinKeys = new ArrayList<>();
    private static List<String> veniceKeys = new ArrayList<>();

    public static String parse(URI path) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("  \"nodes\": [");

        sb.append(getCompetitionNode(OSCAR_NODE_ID, "Oscar", "#8ac141"));
        sb.append(getCompetitionNode(CANNES_NODE_ID, "Cannes Festival", "#af2e53"));
        sb.append(getCompetitionNode(BERLIN_NODE_ID, "Berlin Festival", "#174792"));
        sb.append(getCompetitionNode(VENICE_NODE_ID, "Venice Festival", "#ff008f"));

        List<String> lines = Files.readAllLines(Paths.get(path), Charset.defaultCharset());
        for (int i = 0; i < lines.size(); i++) {
            if (i == 0) continue;

            String line = (String)lines.toArray()[i];

            String[] values = line.split(CSV_DLIMITER, -1);
            String title = values[0];
            String mark = values[1];
            String oscar = values[2];
            String cannes = values[3];
            String berlin = values[4];
            String venice = values[5];

            String key = "m" + i;

            if ("y".equals(oscar))
                oscarKeys.add(key);

            if ("y".equals(cannes))
                cannesKeys.add(key);

            if ("y".equals(berlin))
                berlinKeys.add(key);

            if ("y".equals(venice))
                veniceKeys.add(key);

            sb.append(getMovie(title, mark, key));
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("  ],");

        sb.append("  \"edges\": [");
        printEdges(sb, oscarKeys, OSCAR_NODE_ID);
        printEdges(sb, cannesKeys, CANNES_NODE_ID);
        printEdges(sb, berlinKeys, BERLIN_NODE_ID);
        printEdges(sb, veniceKeys, VENICE_NODE_ID);
        sb.deleteCharAt(sb.length()-1);
        sb.append("  ]");
        sb.append("}");

        return sb.toString();
    }

    private static String getCompetitionNode(String id, String name, String color) {
        return String.format("    {\"id\":\"%s\", \"label\":\"%s\", \"size\":%s, \"color\":\"%s\"},", id, name, COMPETITION_SIZE, color);
    }

    private static void printEdges(StringBuilder sb, List<String> keys, String competitionNodeId) {
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            sb.append(getEdge(i, key, competitionNodeId));
        }
    }

    private static String getEdge(int i, String key, String competitionNodeId) {
        return String.format("    {\"id\":\"%s%s\", \"source\":\"%1$2s\", \"target\":\"%s\"},", competitionNodeId, i, key);
    }

    private static String getMovie(String title, String mark, String key) {
        return String.format(
                "    {\"id\":\"%s\", \"label\":\"%s\", \"size\":%s},",
                key,
                title,
                mark
        );
    }
}
