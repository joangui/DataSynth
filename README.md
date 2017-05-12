# DataSynth
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/13e27d1053af4b2ab53414618b858fdc)](https://www.codacy.com/app/ArnauPrat/DataSynth?utm_source=github.com&utm_medium=referral&utm_content=DAMA-UPC/DataSynth&utm_campaign=badger)
[![Build Status](https://travis-ci.org/DAMA-UPC/DataSynth.svg?branch=dev)](https://travis-ci.org/DAMA-UPC/DataSynth)

DataSynth is a tool for the generation of user-driven datasets with arbitrary and complex schemas. Schemas are defined using the property graph model, where data is modeled as a graph with nodes representing the different entity types and edges represent the relations among them. In this model, both nodes and edges can have attached properties, in the form of key value pairs. Among many novel features, DataSynth has the following characteristics:

* Allows specifying multiple nodes and edge types, each type with its own set of properties.
* Allows full control of the way properties are generated, including the specification of correlations between those of the same entity and connected entities.
* Allows controlling structural properties of the underlying network of edges via the available structure generators.
* Allows scaling to terabytes of data, thanks to the use of BigData technologies such as Apache Spark.

The core idea of DataSynth, was first described in detail in the paper:

[Towards a property graph generator for benchmarking](https://arxiv.org/abs/1704.00630)

Arnau Prat-Pérez, Joan Guisado-Gámez, Xavier Fernández Salas, Petr Koupy, Siegfried Depner, Davide Basilio Bartolini

## Contributing

Feel free to contribute to the project by issuing pull requests, suggestions
etc. A Trello board with the current pending and in-dev tasks can be found here:

https://trello.com/b/AEZ99vTz/datasynth

## Citing this Work

```
@article{prat2017towards,
  title={Towards a property graph generator for benchmarking},
  author={Prat-P{\'e}rez, Arnau and Guisado-G{\'a}mez, Joan and Salas, Xavier Fern{\'a}ndez and Koupy, Petr and Depner, Siegfried and Bartolini, Davide Basilio},
  journal={arXiv preprint arXiv:1704.00630},
  year={2017}
}
```

