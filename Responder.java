import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author Juan Jimenez
 * @version 2024-4-25
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Populates the response map from tye text file where each entry consists of keywords
     * followed by a response separated by a blank line. The method reads the 'responses.txt'
     * file and organizes into the map.
     */

    private void fillResponseMap() {
        Charset charset = Charset.forName("UTF-8");
        Path path = Paths.get("responses.txt");
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            List<String> keys = new ArrayList<>();
            StringBuilder response = new StringBuilder();
            boolean readingResponse = false;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (readingResponse && !keys.isEmpty() && response.length() > 0) {
                        String responseText = response.toString().trim();
                        for (String key : keys) {
                            responseMap.put(key.trim().toLowerCase(), responseText); 
                        }
                        keys.clear();
                        response.setLength(0);
                        readingResponse = false;
                    }
                } else if (!readingResponse) {
                    keys = new ArrayList<>(Arrays.asList(line.split(",\\s*")));
                    readingResponse = true;
                } else {
                    response.append(line).append(System.lineSeparator());
                }
            }
            if (readingResponse && !keys.isEmpty() && response.length() > 0) {
                String responseText = response.toString().trim();
                for (String key : keys) {
                    responseMap.put(key.trim().toLowerCase(), responseText);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + path.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error reading file: " + path.toAbsolutePath());
        }
    }

    /**
     * Fills the list of default responses from the file. Thw method reads the 'default.txt'
     * file, with adding separated by blank lines to the list of default responses.
     */
    private void fillDefaultResponses() {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            StringBuilder response = new StringBuilder();
            String line;
            boolean LineWasBlank = true;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!LineWasBlank) {
                        defaultResponses.add(response.toString().trim());
                        response.setLength(0);
                    }
                    LineWasBlank = true;
                } else {
                    if (response.length() > 0) {
                        response.append(System.lineSeparator());
                    }
                    response.append(line);
                    LineWasBlank = false;
                }
            }
            if (!LineWasBlank && response.length() > 0) {
                defaultResponses.add(response.toString().trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
