package com.thoughtworks.scm.domain;

import com.thoughtworks.utils.ProcessRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ChangeTest {

    private static final List<String> OUTPUT = Arrays.asList(
            "------------------------------------------------------------------------",
            "r4 | prem.chandrasekaran | 2014-11-01 23:25:34 -0400 (Sat, 01 Nov 2014) | 2 lines",
            "",
            "Pushed functionality behind a web server",
            "",
            "",
            "Index: RevisionTest.java",
            "===================================================================",
            "--- RevisionTest.java\t(revision 0)",
            "+++ RevisionTest.java\t(revision 4)",
            "@@ -0,0 +1,55 @@",
            "+package com.thoughtworks.scm.domain;",
            "+",
            "+import org.junit.BeforeClass;",
            "+import org.junit.Test;",
            "+",
            "+import java.util.Map;",
            "+",
            "+import static org.assertj.core.api.Assertions.assertThat;",
            "+",
            "+",
            "+public class RevisionTest {",
            "+",
            "+    private static Revision revision;",
            "+",
            "+    @BeforeClass",
            "+    public static void setUp() throws Exception {",
            "+        revision = new Revision(\"r547\", \"http://achartengine.googlecode.com/svn/trunk\", \"http://achartengine.googlecode.com/svn\");",
            "+    }",
            "+",
            "+    @Test",
            "+    public void shouldCalculateTestToProdLineRatio() throws Exception {",
            "+",
            "+        Ratio ratio = revision.testToProdLineRatio();",
            "+",
            "+        assertThat(ratio).isEqualTo(new Ratio(0, 50));",
            "+    }",
            "+",
            "+    @Test",
            "+    public void shouldCalculateProdLineChanges() throws Exception {",
            "+        assertThat(revision.prodLineChanges()).isEqualTo(50L);",
            "+    }",
            "+",
            "+    @Test",
            "+    public void shouldGetProdFiles() throws Exception {",
            "+        assertThat(revision.prodFiles().size()).isEqualTo(2);",
            "+",
            "+    }",
            "+",
            "+    @Test",
            "+    public void shouldCalculateTestLineChanges() throws Exception {",
            "+        assertThat(revision.testLineChanges()).isEqualTo(0L);",
            "+    }",
            "+",
            "+    @Test",
            "+    public void shouldMapToStat() throws Exception {",
            "+",
            "+        final Map<String, ?> actual = revision.toStat();",
            "+",
            "+        assertThat(actual.get(\"ts\")).isNotEqualTo(0);",
            "+        assertThat(actual.get(\"author\")).isEqualTo(\"dandromereschi@gmail.com\");",
            "+        assertThat(actual.get(\"testPercentage\")).isEqualTo(0L);",
            "+        assertThat(actual.get(\"hash\")).isEqualTo(\"r547\");",
            "+        assertThat(actual.get(\"size\")).isEqualTo(50L);",
            "+    }",
            "+}",
            "\\ No newline at end of file",
            "",
            "------------------------------------------------------------------------");
    @Mock
    private ProcessRunner svn;

    @Before
    public void setUp() throws Exception {
        given(svn.execute("log", "https://github.com/premanandc/svn-scraper/trunk/src/test/java/com/thoughtworks/scm/domain/RevisionTest.java@r4", "--diff", "-r", "r4", "--diff-cmd", "diff"))
                .willReturn(OUTPUT);
    }

    @Test
    public void shouldRecognizeFilesEndingInTestAsTestChange() throws Exception {
        final Change change = new Change(svn, "r4", "https://github.com/premanandc/svn-scraper/", "A /trunk/src/test/java/com/thoughtworks/scm/domain/RevisionTest.java");
        assertThat(change.isTest()).isTrue();
    }

    @Test
    public void shouldRecognizeFilesNotEndingInTestAsProdChange() throws Exception {
        final Change change = new Change(svn, "r4", "https://github.com/premanandc/svn-scraper/", "A /trunk/src/main/java/com/thoughtworks/scm/domain/Revision.java");
        assertThat(change.isTest()).isFalse();
    }
}