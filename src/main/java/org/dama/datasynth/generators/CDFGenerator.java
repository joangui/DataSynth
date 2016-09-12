package org.dama.datasynth.generators;

import org.dama.datasynth.runtime.Generator;
import org.dama.datasynth.utils.CDFSampler;
import org.dama.datasynth.utils.CSVReader;
import org.dama.datasynth.utils.MurmurHash;
import org.dama.datasynth.utils.Sampler;


/**
 * Created by quim on 4/19/16.
 */
public class CDFGenerator extends Generator {
        private Sampler s;
        public CDFGenerator(){}
        public void initialize(String file, Long x, Long y, String sep ){
            CSVReader csv = new CSVReader(file, sep);
            this.s = new CDFSampler(csv.fetchSubMatrix(x.intValue(), y.intValue()));
        }
        public String run(Long id){
            return s.takeSample(MurmurHash.hash64(id.toString()));
        }
}
