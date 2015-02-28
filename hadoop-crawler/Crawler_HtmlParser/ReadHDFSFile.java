package Crawler_HtmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
/*
 * this class is used to read info from HDFS files
 */
public class ReadHDFSFile {
	String test1="http://google.com";
	String test2="http://dongxi.douban.com/articles?dcs=anonymous-home-more-articles&amp;dcm=douban";
	String test3="http://e.weibo.com/doubandongxi";
	String test4="http://dongxi.douban.com/shows/食品/?dcs=anonymous-home-sidenav&amp;dcm=douban";
	public static Path[] getFilePath(String filePath) throws IOException{
		//this fucntion is to get the file path of folder
		// you should check if it is a folder before you try to use this functions
		Configuration conf = new Configuration();
	    conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/core-site.xml"));
	    conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/hdfs-site.xml"));
	    FileSystem fileSystem;
	    fileSystem = FileSystem.get(conf);
	    FileStatus[] status=fileSystem.listStatus(new Path(filePath));
	    Path[] listedPaths = FileUtil.stat2Paths(status);
	    fileSystem.close();
	    return listedPaths;
	}
	public static String getFileString(String filePath) throws IllegalArgumentException, IOException{
		// I think I should check if the file is a html,if not,delete it.
		Configuration conf = new Configuration();
	    conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/core-site.xml"));
	    conf.addResource(new Path("/opt/hadoop-2.3.0/etc/hadoop/hdfs-site.xml"));
	    FileSystem fileSystem;
	    fileSystem = FileSystem.get(conf);
		InputStream in=null;
		String prefix=filePath.substring(filePath.lastIndexOf(".")+1);
		String reuslt=null;
		if(prefix.equals("html")){
			in=fileSystem.open(new Path(filePath));
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			reuslt = writer.toString();
			in.close();
		}
		else{
			System.out.println("its not html file");
		}
		fileSystem.close();
		return reuslt;
	}
}
