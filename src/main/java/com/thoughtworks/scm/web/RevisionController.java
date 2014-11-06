package com.thoughtworks.scm.web;

import com.thoughtworks.scm.domain.Revision;
import com.thoughtworks.scm.domain.SvnRepository;
import com.thoughtworks.utils.ProcessRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/revisions")
public class RevisionController {


    private final ProcessRunner svn;

    @Autowired
    public RevisionController(ProcessRunner svn) {
        this.svn = svn;
    }

    @RequestMapping(method = GET)
    public List<Map<String, Object>> revisions(@RequestParam String url, @RequestParam(defaultValue = "10") int maxRevisions) {
        return new SvnRepository(svn, url, maxRevisions).revisions
                .stream()
                .filter(Revision::hasChanges)
                .map(Revision::toStat)
                .collect(toList());
    }
}
