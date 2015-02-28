package Crawler_HtmlParser;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

public class CreateFilePath {
	public static String creatFileOfHtmlFiles(String level,String htmlFolderPath,String HtmlPathFileFolderPath) 
			throws IOException{
		String filePathOfFileOfHtmlFiles=null;
		Configuration conf = new Configuration();
		conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/core-site.xml"));
		conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/hdfs-site.xml"));
		FileSystem fileSystem;
		fileSystem = FileSystem.get(conf);
		FileStatus[] status=fileSystem.listStatus(new Path(htmlFolderPath+level));
		Path[] listedPaths = FileUtil.stat2Paths(status);
		if(listedPaths.length==0){
			System.out.println("the file path is wrong or we have none files at this folder");
			return null;
		}
		// then we write the filepath of html to the path
		FSDataOutputStream out=fileSystem.append(new Path(HtmlPathFileFolderPath+level));
		for(int i=0;i<listedPaths.length;i++){
			out.write(listedPaths[i].toString().getBytes());
	    	out.write(System.getProperty("line.separator").getBytes());
		}
		fileSystem.close();
		//fisrt step, we read files from the corresponding path
		//second step,
		
		return filePathOfFileOfHtmlFiles+level;
	}
}
