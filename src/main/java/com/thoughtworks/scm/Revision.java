package com.thoughtworks.scm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.utils.ProcessRunner.SVN;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.partitioningBy;

public class Revision {

    public static final boolean TEST_FILES = true;
    public static final boolean PROD_FILES = false;
    public static final DateTimeFormatter DATE_TIME_FORMAT = ofPattern("yyyy-MM-dd HH:mm:ss Z (eee, dd MMM yyyy)");
    public final Map<Boolean, List<Change>> changes;
    public final String id;
    public final String author;
    public final LocalDateTime date;
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public Revision(String id, String url) {
        this.id = id;
        String root = SVN.execute("info", url).parallelStream()
                .filter(l -> l.startsWith("Repository Root"))
                .findFirst()
                .map(l -> l.split("\\s")[2].trim()).get();
        final List<String> output = SVN.execute("log", "-v", "-l", "1", "-r", id, "--incremental", url);

        String[] revisionLine = output.parallelStream()
                .filter(l -> l.startsWith(id))
                .findFirst()
                .get().split("\\|");

        author = revisionLine[1].trim();
        date = LocalDateTime.parse(revisionLine[2].trim(), DATE_TIME_FORMAT);
                changes = output.parallelStream()
                        .filter(l -> l.matches("^\\s+[AMRD]\\s+/.*"))
                        .map(l -> new Change(id, root, l))
                        .collect(partitioningBy(Change::isTest));
    }

    public long changes() {
        return testFiles().size() + prodFiles().size();
    }

    @Override
    public String toString() {
        return String.format("On %s author '%s' made committed %s. Changes: %d files, prod lines: %d, test lines: %d", date.format(ISO_LOCAL_DATE_TIME), author, id, changes(), prodLineChanges(), testLineChanges());
    }

    private List<Change> testFiles() {
        return changes.get(TEST_FILES);
    }

    public long testLineChanges() {
        return testFiles().parallelStream().mapToLong(Change::lineChanges).sum();
    }


    public long prodLineChanges() {
        return prodFiles().parallelStream().mapToLong(Change::lineChanges).sum();
    }

    public List<Change> prodFiles() {
        return changes.get(PROD_FILES);
    }

    public Ratio testToProdLineRatio() {
        return new Ratio(testLineChanges(), prodLineChanges());
    }

    public JsonNode toJson() {
        return MAPPER.createObjectNode()
                .put("ts", date.atZone(systemDefault()).toEpochSecond())
                .put("hash", id)
                .put("testPercentage", testLineChanges() / prodLineChanges())
                .put("size", prodLineChanges() + testLineChanges())
                .put("author", author);
    }
}
