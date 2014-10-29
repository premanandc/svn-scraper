package com.thoughtworks.scm;

import com.thoughtworks.utils.ProcessRunner;

import java.util.List;
import java.util.stream.Collectors;

public class SvnRepository {

    public final List<Revision> revisions;

    public SvnRepository(String url) {
        revisions = ProcessRunner.SVN
                .execute("log", "-q", url, "-l", "10")
                .parallelStream()
                .filter(l -> l.startsWith("r"))
                .map(l -> l.split("\\|")[0].trim())
                .map(r -> new Revision(r, url))
                .collect(Collectors.toList());
    }

    public void printSummary() {
        revisions.stream().forEach(System.out::println);
    }

    public static void main(String[] args) {
        new SvnRepository("http://achartengine.googlecode.com/svn/trunk")
                .printSummary();
    }
}
