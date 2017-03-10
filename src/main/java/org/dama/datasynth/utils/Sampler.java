package org.dama.datasynth.utils;

import java.io.Serializable;

public abstract class Sampler implements Serializable{
    public abstract String takeSample(long seed);
}


