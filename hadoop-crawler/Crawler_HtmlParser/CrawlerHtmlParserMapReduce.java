package Crawler_HtmlParser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import onfire.configure.configure;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.ObjectFindingVisitor;


public class CrawlerHtmlParserMapReduce extends Configured implements Tool {
	private static String level = "1";
	private static String HtmlInfoFilePath ;
	private static String urlFilePath=configure.URLFILESPATH +"temp"+ configure.URLNAME;

	public static class MapClass extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private Text filePath = new Text();
		private final static IntWritable one = new IntWritable(1);
		private Text temp=new Text();
		private ArrayList linkList=null;
		
		// Map Method
		public void map(LongWritable key, Text filePathInfo, Context context)
				throws IOException, InterruptedException {
			StringTokenizer tokenizer = new StringTokenizer(
					filePathInfo.toString());
			while (tokenizer.hasMoreTokens()) {
				filePath.set(tokenizer.nextToken());
				if (filePath.toString() != null
						&& filePath.toString().length() > 0) {
					// if the url is null ,
					//System.out.println(filePath.toString());
					
					try {
						linkList = PageParser.getLinks(ReadHDFSFile
										.getFileString(filePath.toString()));
					} catch (ParserException | IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (linkList == null) {
						return;
					}
					if (linkList.size() <= 0) {
						return;
					}
					WriteURLToHDFS.writeURLToHDFS(linkList, urlFilePath);
					/*
					//for (int i = 0; i < linkList.size(); i++) {
						//System.out.println("test");
						context.write(new Text("test"), one);
						// we write the file to
						//context.write(new Text(linkList.get(i).toString()),one);
						//System.out.println(linkList.get(i));
					}
					*/
				}
			}
		}
		

		@Override
		protected void cleanup(Context context) {
			// this function is called when the mapreduce is finished
		}
	}

	public static class CrawlerHtmlParserReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		// Reduce Method
		public void reduce(Text url, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			System.out.println("ur"+url);
			//context.write(url, one);
		}
		/*
		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			//maybe I can put all the url to one at this place,or I can write a function to get all the 
		}
		*/
	}

	public void setLevel(String level) {
		this.level = level;
	}
	public void initPath(){
		HtmlInfoFilePath = configure.HTMLFILESINFOPATH
				+ level + configure.HTMLINFONAME;
		int intLevel=Integer.parseInt(this.level);
		intLevel++;
		/*
		urlFilePath=configure.URLFILESPATH + Integer.toString(intLevel)
				+ "/";
				*/
		System.out.println(urlFilePath);
	}
	public int run(String[] arg0) throws Exception {
		setLevel("1");
		initPath();
		configure.createFile(urlFilePath);
		Job job = new Job();
		job.setJarByClass(CrawlerHtmlParserMapReduce.class);

		FileInputFormat.addInputPath(job, new Path(HtmlInfoFilePath));
		//FileOutputFormat.setOutputPath(job, new Path(urlFilePath));
		
		
		job.setMapperClass(MapClass.class);
		//job.setCombinerClass(CrawlerHtmlParserReduce.class);
		job.setReducerClass(CrawlerHtmlParserReduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(NullOutputFormat.class);  

		//job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		//job.setOutputKeyClass(Text.class);
		//job.setOutputValueClass(IntWritable.class);
		
		//job.waitForCompletion(true);
		//return 0;
		return job.waitForCompletion(true) ? 0 : 1;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(),
				new CrawlerHtmlParserMapReduce(), args);
		//System.exit(res);
	}

}
