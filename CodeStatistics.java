import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

public class CodeStatistics {

	private static HashMap<String, Integer> fileMap;
	private static HashMap<String, Integer> lineMap;
	private static HashMap<String, Integer> nonEmptyLineMap;
	private static HashMap<String, Long> sizeMap;
	// Add your own extension if you want
	private final static String[] allTypes = {
		".cc", ".c", ".cpp", ".h", ".java", ".xml", ".html", ".htm", ".php", ".py", ".asp",
		".js", ".css", ".jsp", ".pl", ".cs", ".sql"
	};

	public static void statistics(String path, String... ignorePaths) {
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
		HashSet<String> ignore=new HashSet<>();
		for (String s: ignorePaths) ignore.add(new File(s).getAbsolutePath());
		analyze(file, ignore);
		System.out.println("Code statistics at "+path+":");
		System.out.println();
		System.out.println(String.format("%-8s%-8s%-8s%-8s%-8s", "Type", "Files", "Lines", "Slocs","Size"));
		System.out.println("-----------------------------------------------");
		int a=0,b=0,c=0;long d=0;
		for (String type: allTypes) {
			String t=type.substring(1);
			if (!fileMap.containsKey(t)) continue;
			System.out.println(String.format("%-8s%-8d%-8d%-8d%-8s", t, fileMap.getOrDefault(t, 0),
				lineMap.getOrDefault(t, 0), nonEmptyLineMap.getOrDefault(t, 0),
					formatSize(sizeMap.getOrDefault(t, 0L))));
			a+=fileMap.getOrDefault(t, 0);
			b+=lineMap.getOrDefault(t, 0);
			c+=nonEmptyLineMap.getOrDefault(t, 0);
			d+=sizeMap.getOrDefault(t, 0L);
		}
		System.out.println("-----------------------------------------------");
		System.out.println(String.format("%-8s%-8d%-8d%-8d%-8s","Total",a,b,c,formatSize(d)));
	}

	private static String formatSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}
	
	public static void main(String[] args) {
		String path=".";
		if (args.length>0) {
			path=args[0];
		}
		statistics(path);
	}

	private static void analyze(File file, HashSet<String> ignore) {
		if (ignore.contains(file.getAbsolutePath())) return;
		if (file.isDirectory()) {
			File[] files=file.listFiles();
			for (File f: files)
				analyze(f, ignore);
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
			reader.close();

		}
		catch  (Exception i) {}
	}

	private static String check(File file) {
		for (String type: allTypes)  {
			if (file.getPath().endsWith(type))
				return type.substring(1);
		}
		return "";
	}
}

