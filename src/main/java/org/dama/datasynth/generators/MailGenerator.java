package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;

/**
 * Created by quim on 4/20/16.
 */
public class MailGenerator extends Generator {
        private DFGenerator g;
        public void initialize(String file) {
            g = new DFGenerator();
            g.initialize(file, "0", "1", " ");
        }
        public String run(String name) {
            return name + "@" + g.run();
        }
}