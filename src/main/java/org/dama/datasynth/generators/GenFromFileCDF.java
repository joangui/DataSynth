package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.CDFSampler;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.Sampler;


/**
 * Created by quim on 4/19/16.
 */
public class GenFromFileCDF extends Generator {
        private Sampler s;
        public GenFromFileCDF(){}
        public void initialize(String file){
            CSVReader csv = new CSVReader(file);
            this.s = new CDFSampler(csv.toString());
        }
        public String run(){
            return s.takeSample();
        }
}
