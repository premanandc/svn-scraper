package com.thoughtworks.scm;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class RevisionTest {

    private static Revision revision;

    @BeforeClass
    public static void setUp() throws Exception {
        revision = new Revision("r547", "http://achartengine.googlecode.com/svn/trunk");
    }

    @Test
    public void shouldCalculateTestToProdLineRatio() throws Exception {

        Ratio ratio = revision.testToProdLineRatio();

        assertThat(ratio, is(equalTo(new Ratio(0, 50))));
    }

    @Test
    public void shouldCalculateProdLineChanges() throws Exception {
        assertThat(revision.prodLineChanges(), is(50L));
    }

    @Test
    public void shouldGetProdFiles() throws Exception {
        assertThat(revision.prodFiles().size(), is(2));

    }

    @Test
    public void shouldCalculateTestLineChanges() throws Exception {
        assertThat(revision.testLineChanges(), is(0L));
    }

    @Test
    public void shouldMapToStat() throws Exception {

        final JsonNode actual = revision.toJson();

        assertThat(actual.get("ts").asLong(), is(not(0)));
        assertThat(actual.get("author").asText(), is("dandromereschi@gmail.com"));
        assertThat(actual.get("testPercentage").asDouble(), is(0.0));
        assertThat(actual.get("hash").asText(), is("r547"));
        assertThat(actual.get("size").asInt(), is(50));
    }
}