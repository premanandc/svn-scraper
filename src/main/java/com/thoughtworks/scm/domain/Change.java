package com.thoughtworks.scm.domain;

import com.thoughtworks.utils.ProcessRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.util.Arrays.copyOfRange;
import static java.util.Collections.emptyList;

public class Change {

    private static final Map<String, Type> ALL_TYPES = new HashMap<>();
    public static final String SVN_COLOR_CODE = "^\\[\\d+;\\d+m";
    public static final String ADDITION_PATTERN = "^\\+[^+].*";
    public static final String DELETION_PATTERN = "^-[^-].*";

    private final String file;
    private final long additions;
    private final long deletions;

    public static enum Type {
        ADD("A"), MODIFY("M"), DELETE("D") {
            @Override
            public List<String> changes(String revisionId, String root, String file) {
                return emptyList();
            }
        }, MOVE("R");

        Type(String code) {
            ALL_TYPES.put(code, this);
        }

        public List<String> changes(String revisionId, String root, String file) {
            return ProcessRunner.SVN.execute("log", String.format("%s%s@%s", root, file, revisionId), "--diff", "-r", revisionId, "--diff-cmd", "diff")
                    .parallelStream()
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        public static Type parse(String input) {
            return ALL_TYPES.get(input);
        }
    }

    public Change(String id, String root, String changeLine) {
        final String[] parts = changeLine.trim().split("\\s+");
        Type type = Type.parse(parts[0].trim());
        file = join("", copyOfRange(parts, 1, parts.length)).trim();
        final List<String> changes = type.changes(id, root, file);
        additions = changes.parallelStream().filter(l -> l.matches(ADDITION_PATTERN)).count();
        deletions = changes.parallelStream().filter(l -> l.matches(DELETION_PATTERN)).count();
    }

    public boolean isTest() {
        return file.endsWith("Tests?\\.\\w+");
    }

    public long lineChanges() {
        return additions + deletions;
    }

    @Override
    public String toString() {
        return file;
    }
}
