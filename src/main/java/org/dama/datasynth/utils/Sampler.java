package org.dama.datasynth.utils;

import java.io.Serializable;
import java.util.Random;

public abstract class Sampler implements Serializable{
    public abstract String takeSample();
}


