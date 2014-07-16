package es.qiu.ptbpart;

import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wqiu on 15/07/14.
 */
public class Partitioner {
    //create an empty InputStream
    private static InputStream dirStream = new ByteArrayInputStream(new String("").getBytes());

    public static void partition(Integer numPartition, String fileOrDirName, String outputName) throws IOException {
        File f = new File(fileOrDirName);
        PennTreeReader ptReader = null;
        if(f.isDirectory()) {
            ptReader = loadPTBCorpusFromDir(fileOrDirName);
        }else{
            ptReader = loadPTBCorpusFromFile(fileOrDirName);
        }

        List<PrintWriter> printWriterList = new ArrayList<PrintWriter>();
        for (int i = 0; i < numPartition; i++){
            String outputFileName = outputName + "_" + (i+1);
            printWriterList.add(new PrintWriter(outputFileName));
        }
        int count = 0;
        Tree currentTree = ptReader.readTree();
        while(currentTree != null ){
            currentTree.pennPrint(printWriterList.get(count % numPartition));
            count ++;
            currentTree = ptReader.readTree();
        }
        for (PrintWriter pw: printWriterList){
            pw.close();
        }
        ptReader.close();
    }


    private static PennTreeReader loadPTBCorpusFromFile(String fileName){
        PennTreeReader ptReader =  null;
        try {
            ptReader =  new PennTreeReader(new BufferedReader(new FileReader(new File(fileName))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ptReader;
    }

    private static PennTreeReader loadPTBCorpusFromDir(String dirName){
        PennTreeReader ptReader = null;
        File dir = new File(dirName);
        assert(dir.isDirectory());
        transverse(dir);
        return new PennTreeReader(new InputStreamReader(dirStream));
    }

    private static void transverse(File node){
        System.out.println("accessing " + node.getName());
        if(node.isFile()){
            try {
                dirStream = new SequenceInputStream(dirStream, new FileInputStream(node));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(node.isDirectory()){
            String[] subNodes = node.list();
            for (String fileName : subNodes){
                transverse(new File(node, fileName));
            }
        }
    }





    public static void main(String args[]){
        ArgumentParser parser = ArgumentParsers.newArgumentParser("PtPartition").description("partition Penn TreeBank style corpus into several parts");
        parser.addArgument("num").metavar("N").type(Integer.class).help("Number of partitions desired");
        parser.addArgument("path").metavar("P").help("The path to the file or directory which contains the corpus");
        parser.addArgument("--outname").metavar("O").setDefault("partition").help("the prefix used for names of output files");
        try {
            Namespace res = parser.parseArgs(args);
            //partition(10, "/Users/wqiu/Projects/eval/data/penn-treebank/original/CD/combined/wsj/00/wsj_0002.mrg");
            partition((Integer)res.get("num"), (String)res.get("path"), (String)res.get("outname"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArgumentParserException e) {
            e.printStackTrace();
        }

    }

}
