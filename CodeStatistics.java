import java.io.*;
import java.util.*;

public class CodeStatistics {

	private static HashMap<String, Integer> fileMap;
	private static HashMap<String, Integer> lineMap;
	private static HashMap<String, Integer> nonEmptyLineMap;
	// Add your own extension if you want
	private final static String[] allTypes = {
		".cc", ".c", ".cpp", ".h", ".java", ".xml", ".html", ".htm", ".php", ".py", ".asp",
		".js", ".css", ".jsp", ".pl", ".cs", ".sql"
	};

	public static void main(String[] args) {
		String path="";
		if (args.length==0) {
			path="E:\\java";
		} else {
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
		analyze(file);
		ArrayList<String> tps=new ArrayList<>();
		System.out.println("Code statistics at "+path+":");
		System.out.println();
		System.out.println("Type\tFiles\tLines\tNon-empty Lines");
		System.out.println("-----------------------------------------------");
		int a=0,b=0,c=0;
		for (String type: allTypes) {
			String t=type.substring(1);
			if (!fileMap.containsKey(t)) continue;
			System.out.println(String.format("%s\t%d\t%d\t%d", t, fileMap.getOrDefault(t, 0),
				lineMap.getOrDefault(t, 0), nonEmptyLineMap.getOrDefault(t, 0)));
			a+=fileMap.getOrDefault(t, 0);
			b+=lineMap.getOrDefault(t, 0);
			c+=nonEmptyLineMap.getOrDefault(t, 0);
		}
		System.out.println("-----------------------------------------------");
		System.out.println(String.format("Total\t%d\t%d\t%d",a,b,c));
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
		for (String type: allTypes)  {
			if (file.getPath().endsWith(type))
				return type.substring(1);
		}
		return "";
	}
}
