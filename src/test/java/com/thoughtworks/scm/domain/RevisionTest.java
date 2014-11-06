package com.thoughtworks.scm.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class RevisionTest {

    private static Revision revision;

    @BeforeClass
    public static void setUp() throws Exception {
        revision = new Revision("r547", "http://achartengine.googlecode.com/svn/trunk", "http://achartengine.googlecode.com/svn");
    }

    @Test
    public void shouldCalculateTestToProdLineRatio() throws Exception {

        Ratio ratio = revision.testToProdLineRatio();

        assertThat(ratio).isEqualTo(new Ratio(0, 50));
    }

    @Test
    public void shouldCalculateProdLineChanges() throws Exception {
        assertThat(revision.prodLineChanges()).isEqualTo(50L);
    }

    @Test
    public void shouldGetProdFiles() throws Exception {
        assertThat(revision.prodFiles().size()).isEqualTo(2);

    }

    @Test
    public void shouldCalculateTestLineChanges() throws Exception {
        assertThat(revision.testLineChanges()).isEqualTo(0L);
    }

    @Test
    public void shouldMapToStat() throws Exception {

        final Map<String, ?> actual = revision.toStat();

        assertThat(actual.get("ts")).isNotEqualTo(0);
        assertThat(actual.get("author")).isEqualTo("dandromereschi@gmail.com");
        assertThat(actual.get("testLineChanges")).isEqualTo(0L);
        assertThat(actual.get("prodLineChanges")).isEqualTo(50L);
//        assertThat(actual.get("testPercentage")).isEqualTo(0L);
        assertThat(actual.get("hash")).isEqualTo("r547");
        assertThat(actual.get("size")).isEqualTo(50L);
    }
}