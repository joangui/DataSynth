package org.dama.datasynth.lang.dependencygraph;

import java.util.List;

/**
 * Created by quim on 7/31/16.
 */
public interface ExecutableVertex {
    List<String> getInitParameters();
    List<String> getRunParameters();
    String getGenerator();
    String getId();
}
