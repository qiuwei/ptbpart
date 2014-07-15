package es.qiu.ptbpart;

import edu.stanford.nlp.trees.PennTreeReader;

import java.io.File;
import java.io.FileReader;

/**
 * Created by wqiu on 15/07/14.
 */
public class Partitioner {
    static int numPartition = 0;

    public Partitioner(){}

    /**
     * load the
     * @param fileOrDirName
     */
    public void loadPTB(String fileOrDirName){

    }

    /**
     * load Penn Treebank from a single file
     * @param fileName the path of the file which contains ptb format
     */
    private static void loadPTBFromSingleFile(String fileName){
        PennTreeReader ptReader = PennTreeReader(new FileReader(new File(fileName)))
    }


    /**
     * load Penn Treebank
     * @param dirName the path of directory which contains files which are in ptb format
     */
    private static void loadPTBFromDir(String dirName){

    }

    public static void main(String args[]){

    }

}
