package com.thoughtworks.scm.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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

    public Revision(String id, String url, String root) {
        this.id = id;
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

    public boolean hasChanges() {
        return prodLineChanges() + testLineChanges() != 0;
    }

    public List<Change> prodFiles() {
        return changes.get(PROD_FILES);
    }

    public Ratio testToProdLineRatio() {
        return new Ratio(testLineChanges(), prodLineChanges());
    }

    public Map<String, Object> toStat() {
        final long prodLineChanges = prodLineChanges();
        final long testLineChanges = testLineChanges();
        Map<String, Object> map = new HashMap<>();
        map.put("ts", date.atZone(systemDefault()).toEpochSecond());
        map.put("hash", id);
        map.put("testLineChanges", testLineChanges);
        map.put("prodLineChanges", prodLineChanges);
//        map.put("testPercentage", testLineChanges / (testLineChanges + prodLineChanges));
        map.put("size", prodLineChanges + testLineChanges);
        map.put("author", author);
        return map;
    }
}
