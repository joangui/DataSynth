package org.dama.datasynth.generators;

/**
 * Created by quim on 4/20/16.
 * Generates an email based in a dictionary of domains with a probability distribution function.
 */
public class MailGenerator extends Generator {
        private DFGenerator g;

    /**
     * Initializes the generator
     * @param file The file name with the dictionary of email domains
     */
    public void initialize(String file) {
        g = new DFGenerator();
        g.initialize(file, 0L, 1L, " ");
    }

    /**
     * Generates the email based on the given name
     * @param name
     * @return
     */
    public String run(String name) {
        return name + "@" + g.run();
        }
}