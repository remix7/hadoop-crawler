package Crawler_Download;

import onfire.configure.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CrawlerDownloadMapReduce extends Configured implements Tool {
	private static String level = "1";
	private static String HtmlFilePath = configure.HTMLFILESPATH + level + "/";
	private static String HtmlInfoFilePath = configure.HTMLFILESINFOPATH
			+ level + configure.HTMLINFONAME;
	private static String urlFilePath = configure.URLFILESPATH + level
			+ configure.URLNAME;

	public static class MapClass extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private Text reduceInputUrl = new Text();
		private final static IntWritable one = new IntWritable(1);

		// Map Method
		public void map(LongWritable key, Text mapInputUrl, Context context)
				throws IOException, InterruptedException {
			StringTokenizer tokenizer = new StringTokenizer(
					mapInputUrl.toString());
			while (tokenizer.hasMoreTokens()) {
				reduceInputUrl.set(tokenizer.nextToken());
				if (reduceInputUrl.toString() != null
						&& reduceInputUrl.toString().length() > 0) {
					// if the url is null ,then we do nothing
					context.write(reduceInputUrl, one);
				}
			}
		}

		@Override
		protected void cleanup(Context context) {
			// this function is called when the mapreduce is finished
		}
	}

	public static class Reduce extends
			Reducer<Text, IntWritable, NullWritable, NullWritable> {
		private ExecutorService threadPool = Executors.newFixedThreadPool(100);
		private ArrayList pathList = new ArrayList();

		// Reduce Method
		public void reduce(Text url, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			byte[] pageBody = null;
			System.out.println(url);
			// new CrawlerThread(url.toString(),HtmlFilePath);
			// new CrawlerThread(pageBody,url.toString(),HtmlFilePath);
			try {
				pageBody = (byte[])threadPool.submit(
						new CrawlerThread(url.toString(), HtmlFilePath)).get();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (pageBody != null) {
				System.out.println("not null");
				String temp = DownloadPage.saveToHdfs(pageBody, url.toString(),
						HtmlFilePath, HtmlInfoFilePath);
				pathList.add(temp);
			} else {
				System.out.println("pagebody is null");
			}
			// threadPool.execute(new
			// CrawlerThread(pageBody,url.toString(),HtmlFilePath));
			// threadPool.execute(new
			// CrawlerThread(url.toString(),HtmlFilePath));
			// context.write(url, NullWritable.get());
		}

		@Override
		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			threadPool.shutdown();
			// then we write the string to the file
			Configuration conf = new Configuration();
			conf.addResource(new Path(
					"/opt/hadoop-2.3.0/etc/hadoop/core-site.xml"));
			conf.addResource(new Path(
					"/opt/hadoop-2.3.0/etc/hadoop/hdfs-site.xml"));
			FileSystem fileSystem = FileSystem.get(conf);
			if (fileSystem.exists(new Path(HtmlInfoFilePath))) {
				System.out.println("File " + HtmlInfoFilePath
						+ " already exists");
				return;
			}
			FSDataOutputStream out = fileSystem.create(new Path(
					HtmlInfoFilePath));
			for (int i = 0; i < pathList.size(); i++) {
				out.write(pathList.get(i).toString().getBytes());
				out.write(System.getProperty("line.separator").getBytes());
			}
			out.close();
			fileSystem.close();
			// context.write(new Text("Max"), new IntWritable(max));
		}
	}
	public void setLevel(String level){
		this.level=level;
	}
	public void initPath(){
		HtmlFilePath = configure.HTMLFILESPATH + level + "/";
		HtmlInfoFilePath = configure.HTMLFILESINFOPATH
				+ level + configure.HTMLINFONAME;
		urlFilePath = configure.URLFILESPATH + level
				+ configure.URLNAME;
	}
	@Override  
	    public int run(String[] arg0) throws Exception {  
	    	//HtmlFilePath=configure.HTMLFILESPATH+level+"/";
			//String urlFilePath=configure.URLFILESPATH+level+configure.URLNAME;
	    	setLevel("1");
	    	initPath();
	        Job job = new Job();  
	        job.setJarByClass(CrawlerDownloadMapReduce.class);  
	          
	        FileInputFormat.addInputPath(job, new Path(urlFilePath));  
	       // FileOutputFormat.setOutputPath(job, new Path(HTMLInfoFilePath));  
	          
	        job.setMapperClass(MapClass.class);  
	        job.setReducerClass(Reduce.class);  
	        job.setInputFormatClass(TextInputFormat.class);  
	        job.setOutputFormatClass(NullOutputFormat.class);  
	        
	        job.setMapOutputKeyClass(Text.class);
	        job.setMapOutputValueClass(IntWritable.class);
	        //job.setOutputKeyClass(NullWritable.class);  
	        //job.setOutputValueClass(NullWritable.class);  
	        //job.waitForCompletion(true);  
	        return job.waitForCompletion(true) ? 0 : 1;  
	    }	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(),
				new CrawlerDownloadMapReduce(), args);
		// System.exit(res);
	}

}

/*
 * import org.apache.hadoop.fs.Path; import org.apache.hadoop.io.*; import
 * org.apache.hadoop.mapred.FileInputFormat; import
 * org.apache.hadoop.mapred.FileOutputFormat; import
 * org.apache.hadoop.mapred.JobClient; import org.apache.hadoop.mapred.JobConf;
 * import org.apache.hadoop.mapred.MapReduceBase; import
 * org.apache.hadoop.mapred.Mapper; import
 * org.apache.hadoop.mapred.OutputCollector; import
 * org.apache.hadoop.mapred.Reducer; import org.apache.hadoop.mapred.Reporter;
 * import org.apache.hadoop.mapred.TextInputFormat; import
 * org.apache.hadoop.mapred.TextOutputFormat;
 * 
 * 
 * public class CrawlerDownloadMapReduce { /* in map part,as the stringTokenizer
 * set as the space and changline as defualt to divide the string .At this
 * part,we divide the string by the changeline"\n" so we don't need to change
 * the code here and the ouput is ok for us
 */
/*
 * private static String HtmlFilePath; private static String
 * recordFilePathOfHtmlFilePath; private static String level="1"; //private
 * static String hdfsPath="hdfs://ubuntu:9000/Crawler/HtmlFiles/"; public static
 * class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text,
 * IntWritable> { private final static IntWritable one = new IntWritable(1);
 * private Text reduceInputUrl = new Text(); public void map(LongWritable key,
 * Text mapInputUrl, OutputCollector<Text, IntWritable> output, Reporter
 * reporter) throws IOException { String url = mapInputUrl.toString();
 * StringTokenizer tokenizer = new StringTokenizer(url); while
 * (tokenizer.hasMoreTokens()) { reduceInputUrl.set(tokenizer.nextToken());
 * if(reduceInputUrl.toString() != null && reduceInputUrl.toString().length() >
 * 0){ //if the url is null ,then we do nothing output.collect(reduceInputUrl,
 * one); } } } } /* the difficult part is the reducer part,we need to output
 * multi html files and we need to crawler the file by multi-threads
 */
/*
 * public static class Reduce extends MapReduceBase implements
 * Reducer<Text,IntWritable,Text,Text>{ private final static IntWritable one =
 * new IntWritable(1); private final static NullWritable nw=NullWritable.get();
 * private final static Text test=new Text("test"); public void reduce(Text url,
 * Iterator<IntWritable> values, OutputCollector<Text, Text> output, Reporter
 * reporter) throws IOException{ //ExecutorService threadPool =
 * Executors.newFixedThreadPool(100); //int
 * status=DownloadPage.getStatus(url.toString()); //if (status >= 200 && status
 * < 300) { System.out.println(url); int sum=0; while (values.hasNext()) { sum
 * += values.next().get(); } output.collect(url, test); //new
 * CrawlerThread(url.toString(),HtmlFilePath); //}
 * //if(reduceInputUrl.toString() != null && reduceInputUrl.toString().length()
 * > 0){
 * 
 * //output.collect(url, NullWritable.get()); } } public static void
 * main(String[] args)throws Exception{
 * HtmlFilePath=configure.HTMLFILESPATH+level+"/"; String
 * urlFilePath=configure.URLFILESPATH+level+configure.URLNAME; String
 * HTMLInfoFilePath=configure.HTMLFILESINFOPATH+level; //
 * +configure.HTMLINFONAME; //String filenamePath=args[0]; //String
 * outPut=args[1]; //outPutPath=args[2]; JobConf conf = new
 * JobConf(CrawlerDownloadMapReduce.class);
 * //conf.setOutputKeyClass(Text.class); conf.setMapOutputKeyClass(Text.class);
 * conf.setMapOutputValueClass(IntWritable.class);
 * //conf.setOutputKeyClass(Text.class); //conf.setOutputValueClass(Text.class);
 * //conf.setOutputValueClass(IntWritable.class);
 * 
 * conf.setJobName("test"); conf.setMapperClass(Map.class);
 * conf.setCombinerClass(Reduce.class); conf.setReducerClass(Reduce.class);
 * 
 * conf.setInputFormat(TextInputFormat.class);
 * conf.setOutputFormat(TextOutputFormat.class);
 * 
 * FileInputFormat.setInputPaths(conf, new Path(urlFilePath));
 * FileOutputFormat.setOutputPath(conf, new Path(HTMLInfoFilePath));
 * JobClient.runJob(conf); } }
 */
