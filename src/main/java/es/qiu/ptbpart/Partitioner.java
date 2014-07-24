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

    public static void partition(Integer numPartition, String fileOrDirName, String outputName, String extension, PrintStyle printStyle) throws IOException {
        File f = new File(fileOrDirName);
        PennTreeReader ptReader = null;
        if(f.isDirectory()) {
            ptReader = loadPTBCorpusFromDir(fileOrDirName, extension);
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
            if (printStyle == PrintStyle.penn) {
                currentTree.pennPrint(printWriterList.get(count % numPartition));
            } else if (printStyle == PrintStyle.flat) {
                PrintWriter printWriter = printWriterList.get(count % numPartition);
                printWriter.print(currentTree.toString());
                printWriter.println();
            }
            count++;
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

    private static PennTreeReader loadPTBCorpusFromDir(String dirName, String extension){
        PennTreeReader ptReader = null;
        File dir = new File(dirName);
        assert(dir.isDirectory());
        transverse(dir, extension);
        return new PennTreeReader(new InputStreamReader(dirStream));
    }

    private static void transverse(File node, String extension){
        if(node.isFile() && node.getName().endsWith(extension)){
            System.out.println("accessing " + node.getName());
            try {
                dirStream = new SequenceInputStream(dirStream, new FileInputStream(node));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if(node.isDirectory()){
            String[] subNodes = node.list();
            for (String fileName : subNodes){
                transverse(new File(node, fileName), extension);
            }
        }
    }





    public static void main(String args[]){
        ArgumentParser parser = ArgumentParsers.newArgumentParser("PtPartition").description("partition Penn TreeBank style corpus into several parts");
        parser.addArgument("num").metavar("N").type(Integer.class).help("Number of partitions desired");
        parser.addArgument("path").metavar("P").help("The path to the file or directory which contains the corpus");
        parser.addArgument("--ext").metavar("E").setDefault(".mrg").help("only file with certain extension will be processed");
        parser.addArgument("--outname").metavar("O").setDefault("partition").help("the prefix used for names of output files");
        parser.addArgument("--format").metavar("F").choices("penn", "flat").setDefault("penn").help("print trees in certain format. Can be either one-line bracketed or pprint s-expression");
        try {
            Namespace res = parser.parseArgs(args);
            partition((Integer) res.get("num"), (String) res.get("path"), (String) res.get("outname"), (String) res.get("ext"), PrintStyle.valueOf((String) res.get("format")));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArgumentParserException e) {
            e.printStackTrace();
        }

    }

}
