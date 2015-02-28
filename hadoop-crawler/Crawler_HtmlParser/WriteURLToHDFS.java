package Crawler_HtmlParser;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class WriteURLToHDFS {
	public static void writeURLToHDFS(ArrayList stringList,String path) throws IOException{
		if(stringList==null){
			return;
		}
		if(stringList.size()<=0){
			return;
		}
		Configuration conf = new Configuration();
	    conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/core-site.xml"));
	    conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/hdfs-site.xml"));
	    FileSystem fileSystem;
	    fileSystem = FileSystem.get(conf);
	    FSDataOutputStream out=fileSystem.append(new Path(path));
	    for(int i=0;i<stringList.size();i++){
	    	out.write(stringList.get(i).toString().getBytes());
	    	out.write(System.getProperty("line.separator").getBytes());
	    }
	    out.close();
	    fileSystem.close();
	}
}
