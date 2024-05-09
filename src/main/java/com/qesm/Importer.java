package com.qesm;

import org.jgrapht.nio.dot.DOTImporter;

public interface Importer<V, E> {

    default public void  importDotFile(String filePath){
        DOTImporter<V,E> importer = new DOTImporter<V, E>();
    } 
}
