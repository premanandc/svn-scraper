package com.thoughtworks.scm.domain;

import com.thoughtworks.utils.ProcessRunner;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

public class SvnRepository {

    public final List<Revision> revisions;

    public SvnRepository(ProcessRunner svn, String url, final int maxRevisions) {
        String root = svn.execute("info", url).parallelStream()
                .filter(l -> l.startsWith("Repository Root"))
                .findFirst()
                .map(l -> l.split("\\s")[2].trim()).get();
        revisions = svn
                .execute("log", "-q", url, "-l", valueOf(maxRevisions))
                .parallelStream()
                .filter(l -> l.startsWith("r"))
                .map(l -> l.split("\\|")[0].trim())
                .map(r -> new Revision(svn, r, url, root))
                .collect(Collectors.toList());
    }
}
