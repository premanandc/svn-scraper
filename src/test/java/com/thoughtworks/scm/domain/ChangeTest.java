package com.thoughtworks.scm.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeTest {

    @Test
    public void shouldRecognizeFilesEndingInTestAsTestChange() throws Exception {
        final Change change = new Change("r4", "https://github.com/premanandc/svn-scraper/", "A /trunk/src/test/java/com/thoughtworks/scm/domain/RevisionTest.java");
        assertThat(change.isTest()).isTrue();

    }

    @Test
    public void shouldRecognizeFilesNotEndingInTestAsProdChange() throws Exception {
        final Change change = new Change("r4", "https://github.com/premanandc/svn-scraper/", "A /trunk/src/main/java/com/thoughtworks/scm/domain/Revision.java");
        assertThat(change.isTest()).isFalse();

    }
}