package Crawler_Download;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class WriteHTMLInfoToHDFS {
	public static void writeHTMLInfoToHDFS(String filePath,String HTMLInfo ) throws IOException{
		Configuration conf = new Configuration();
		conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/core-site.xml"));
		conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/hdfs-site.xml"));
		FileSystem fileSystem;
		fileSystem = FileSystem.get(conf);
		FSDataOutputStream out=fileSystem.append(new Path(filePath));
		out.write(HTMLInfo.getBytes());
    	out.write(System.getProperty("line.separator").getBytes());
    	fileSystem.close();
	}
}
