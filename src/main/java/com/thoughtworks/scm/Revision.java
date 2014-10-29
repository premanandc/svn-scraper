package com.thoughtworks.scm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.thoughtworks.utils.ProcessRunner.SVN;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.partitioningBy;

public class Revision {

    public static final boolean TEST_LINES = true;
    public static final boolean PROD_LINES = false;
    public static final DateTimeFormatter DATE_TIME_FORMAT = ofPattern("yyyy-MM-dd HH:mm:ss Z (eee, dd MMM yyyy)");
    public final Map<Boolean, List<Change>> changes;
    public final String id;
    private final String author;
    private final LocalDateTime date;

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

    public long testLineChanges() {
        return testFiles().parallelStream().mapToLong(Change::changes).sum();
    }

    private List<Change> testFiles() {
        return changes.get(TEST_LINES);
    }

    public long prodLineChanges() {
        return prodFiles().parallelStream().mapToLong(Change::changes).sum();
    }

    private List<Change> prodFiles() {
        return changes.get(PROD_LINES);
    }
}
