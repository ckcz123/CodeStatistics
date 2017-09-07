import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class Main {

    public static final String DEFAULT_PATH = ".";

    private static HashMap<String, Integer> fileMap;
    private static HashMap<String, Integer> lineMap;
    private static HashMap<String, Integer> nonEmptyLineMap;
    private static HashMap<String, Long> sizeMap;

    // Add your own extensions if you want
    private final static String[] extensions = {
            ".cc", ".c", ".cpp", ".h", ".java", ".cs", ".php", ".py", ".xml", ".html", ".htm",
            ".asp", ".js", ".css", ".jsp", ".pl", ".sql", ".md"
    };

    public static void main(String[] args) {
        String path=DEFAULT_PATH;
        if (args.length>0) {
            path=args[0];
        }
        File file=new File(path);
        if (!file.exists()) {
            System.out.println(path+" doesn't exist!");
            return;
        }
        if (!file.isDirectory()) {
            System.out.println(path+" is not a directory!");
            return;
        }
        fileMap=new HashMap<>();
        lineMap=new HashMap<>();
        nonEmptyLineMap=new HashMap<>();
        sizeMap=new HashMap<>();
        analyze(file);
        System.out.println("Code statistics at "+file.getAbsolutePath()+":");
        System.out.println();
        System.out.println(String.format("%-8s%-8s%-10s%-10s%-10s", "Type","Files","Lines","NELines", "Size"));
        System.out.println("-----------------------------------------------");
        int a=0,b=0,c=0;
        long d=0;
        for (String type: extensions) {
            String t=type.substring(1);
            if (!fileMap.containsKey(t)) continue;
            System.out.println(String.format("%-8s%-8d%-10d%-10d%-10s", t, fileMap.getOrDefault(t, 0),
                    lineMap.getOrDefault(t, 0), nonEmptyLineMap.getOrDefault(t, 0),
                    formatFileSize(sizeMap.getOrDefault(t, 0L))));
            a+=fileMap.getOrDefault(t, 0);
            b+=lineMap.getOrDefault(t, 0);
            c+=nonEmptyLineMap.getOrDefault(t, 0);
            d+=sizeMap.getOrDefault(t, 0L);
        }
        System.out.println("-----------------------------------------------");
        System.out.println(String.format("%-8s%-8d%-10d%-10d%-10s","Total",a,b,c,formatFileSize(d)));
    }

    private static void analyze(File file) {
        if (file.isDirectory()) {
            File[] files=file.listFiles();
            for (File f: files)
                analyze(f);
            return;
        }
        try {
            String type=check(file);
            if ("".equals(type)) return;
            fileMap.put(type, fileMap.getOrDefault(type, 0)+1);
            sizeMap.put(type, sizeMap.getOrDefault(type, 0L)+file.length());

            BufferedReader reader=new BufferedReader(new FileReader(file));
            String line="";
            while ((line=reader.readLine())!=null) {
                line=line.trim();
                lineMap.put(type, lineMap.getOrDefault(type, 0)+1);
                if (!line.isEmpty())
                    nonEmptyLineMap.put(type, nonEmptyLineMap.getOrDefault(type, 0)+1);
            }

        }
        catch  (Exception ignore) {}
    }

    private static String check(File file) {
        for (String type: extensions)  {
            if (file.getPath().endsWith(type))
                return type.substring(1);
        }
        return "";
    }

    private static String formatFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (size < 1024) {
            fileSizeString = df.format((double) size) + " B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + " KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + " GB";
        }
        return fileSizeString;
    }
}
