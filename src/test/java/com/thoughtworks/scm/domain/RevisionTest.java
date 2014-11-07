package com.thoughtworks.scm.domain;

import com.thoughtworks.utils.ProcessRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.String;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


@RunWith(MockitoJUnitRunner.class)
public class RevisionTest {

    private static final String ROOT = "http://achartengine.googlecode.com/svn";
    private static final String URL = ROOT + "/trunk";

    public static final List<String> REVISION_OUTPUT = Arrays.asList(
            "------------------------------------------------------------------------",
            "r547 | dandromereschi@gmail.com | 2013-08-16 03:39:31 -0400 (Fri, 16 Aug 2013) | 1 line",
            "Changed paths:",
            "   M /trunk/achartengine/src/org/achartengine/chart/XYChart.java",
            "   M /trunk/achartengine/src/org/achartengine/renderer/DefaultRenderer.java",
            "",
            "Fix for issue #339."
    );
    private static final List<String> CHANGE_1_OUTPUT = Arrays.asList(
            "------------------------------------------------------------------------",
            "r547 | dandromereschi@gmail.com | 2013-08-16 03:39:31 -0400 (Fri, 16 Aug 2013) | 1 line",
            "",
            "Fix for issue #339.",
            "",
            "Index: XYChart.java",
            "===================================================================",
            "--- XYChart.java\t(revision 546)",
            "+++ XYChart.java\t(revision 547)",
            "@@ -454,8 +454,9 @@",
            "       transform(canvas, angle, false);",
            "     }",
            "     if (mRenderer.isShowAxes()) {",
            "-      paint.setColor(mRenderer.getAxesColor());",
            "+      paint.setColor(mRenderer.getXAxisColor());",
            "       canvas.drawLine(left, bottom, right, bottom, paint);",
            "+      paint.setColor(mRenderer.getYAxisColor());",
            "       boolean rightAxis = false;",
            "       for (int i = 0; i < maxScaleNumber && !rightAxis; i++) {",
            "         rightAxis = mRenderer.getYAxisAlign(i) == Align.RIGHT;",
            "",
            "------------------------------------------------------------------------"
    );
    private static final List<String> CHANGE_2_OUTPUT = Arrays.asList(
            "------------------------------------------------------------------------",
            "r547 | dandromereschi@gmail.com | 2013-08-16 03:39:31 -0400 (Fri, 16 Aug 2013) | 1 line",
            "",
            "Fix for issue #339.",
            "",
            "Index: DefaultRenderer.java",
            "===================================================================",
            "--- DefaultRenderer.java\t(revision 546)",
            "+++ DefaultRenderer.java\t(revision 547)",
            "@@ -51,8 +51,10 @@",
            "   private boolean mApplyBackgroundColor;",
            "   /** If the axes are visible. */",
            "   private boolean mShowAxes = true;",
            "-  /** The axes color. */",
            "-  private int mAxesColor = TEXT_COLOR;",
            "+  /** The Y axis color. */",
            "+  private int mYAxisColor = TEXT_COLOR;",
            "+  /** The X axis color. */",
            "+  private int mXAxisColor = TEXT_COLOR;",
            "   /** If the labels are visible. */",
            "   private boolean mShowLabels = true;",
            "   /** If the tick marks are visible. */",
            "@@ -253,7 +255,11 @@",
            "    * @return the axes color",
            "    */",
            "   public int getAxesColor() {",
            "-    return mAxesColor;",
            "+    if (mXAxisColor != TEXT_COLOR) {",
            "+      return mXAxisColor;",
            "+    } else {",
            "+      return mYAxisColor;",
            "+    }",
            "   }",
            "",
            "   /**",
            "@@ -262,7 +268,44 @@",
            "    * @param color the axes color",
            "    */",
            "   public void setAxesColor(int color) {",
            "-    mAxesColor = color;",
            "+    this.setXAxisColor(color);",
            "+    this.setYAxisColor(color);",
            "+  }",
            "+",
            "+  /**",
            "+   * Returns the color of the Y axis",
            "+   *",
            "+   * @return the Y axis color",
            "+   */",
            "+  public int getYAxisColor() {",
            "+    return mYAxisColor;",
            "+  }",
            "+",
            "+  /**",
            "+   * Sets the Y axis color.",
            "+   *",
            "+   * @param color the Y axis color",
            "+   */",
            "+  public void setYAxisColor(int color) {",
            "+    mYAxisColor = color;",
            "+  }",
            "+",
            "+  /**",
            "+   * Returns the color of the X axis",
            "+   *",
            "+   * @return the X axis color",
            "+   */",
            "+  public int getXAxisColor() {",
            "+    return mXAxisColor;",
            "+  }",
            "+",
            "+  /**",
            "+   * Sets the X axis color.",
            "+   *",
            "+   * @param color the X axis color",
            "+   */",
            "+  public void setXAxisColor(int color) {",
            "+    mXAxisColor = color;",
            "   }",
            "",
            "   /**",
            "",
            "------------------------------------------------------------------------");

    @Mock
    private ProcessRunner svn;
    private Revision revision;

    @Before
    public void setUp() throws Exception {
        given(svn.execute("log", "-v", "-l", "1", "-r", "r547", "--incremental", URL)).willReturn(REVISION_OUTPUT);
        given(svn.execute("log", ROOT + "/trunk/achartengine/src/org/achartengine/chart/XYChart.java@r547", "--diff", "-r", "r547", "--diff-cmd", "diff"))
                .willReturn(CHANGE_1_OUTPUT);
        given(svn.execute("log", ROOT + "/trunk/achartengine/src/org/achartengine/renderer/DefaultRenderer.java@r547", "--diff", "-r", "r547", "--diff-cmd", "diff"))
                .willReturn(CHANGE_2_OUTPUT);

        revision = new Revision(svn, "r547", URL, ROOT);
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
        assertThat(actual.get("testPercentage")).isEqualTo(0.0);
        assertThat(actual.get("hash")).isEqualTo("r547");
        assertThat(actual.get("size")).isEqualTo(50L);
    }
}
