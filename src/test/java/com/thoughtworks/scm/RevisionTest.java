package com.thoughtworks.scm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RevisionTest {

    private Revision revision;

    @Before
    public void setUp() throws Exception {
        revision = new Revision("r547", "http://achartengine.googlecode.com/svn/trunk");
    }

    @Test
    public void shouldCalculateTestToProdLineRatio() throws Exception {

        Ratio ratio = revision.testToProdLineRatio();

        assertThat(ratio, is(equalTo(new Ratio(0, 1))));
    }

    @Test
    public void shouldCalculateProdLineChanges() throws Exception {
        assertThat(revision.prodLineChanges(), is(54));
    }

    @Test
    public void shouldGetProdFiles() throws Exception {
        assertThat(revision.prodFiles(), is(new ArrayList()));

    }

    @Test
    public void shouldCalculateTestLineChanges() throws Exception {
        assertThat(revision.testLineChanges(), is(0));
    }

    @Test
    public void shouldMapToStat() throws Exception {

        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode node = mapper.createObjectNode();
        node.put("ts", 0);
        node.put("hash", "r547");
        node.put("testPercentage", 0);
        node.put("size", 34);
        node.put("author", "dandromereschi@gmail.com");
        assertThat(revision.toJson(), is(node));
    }
}