package Crawler_Download;

import java.util.concurrent.Callable;





   public class CrawlerThread implements Callable {
	    private byte []PageBody;

        CrawlerThread( String url,String outPutPath) {
        	PageBody=DownloadPage.downloadPage(url,outPutPath);
        }

        public Object call() throws Exception {
            return PageBody;
        }
    }

/*
public class CrawlerThread implements Runnable {
	private Thread crawlerThread;
	//private static String hdfsPath="hdfs://ubuntu:9000/Crawler/HtmlFiles/";
	private String Url;
	private String outPutPath;
	private byte []PageBody;
	CrawlerThread(byte[] pageBody, String url,String tempOutPutPath){
		crawlerThread =new Thread(this);
		Url=url;
		outPutPath=tempOutPutPath;
		crawlerThread.start();
		pageBody=PageBody;
	}
	//this is the entry point for the new thread
	public void run(){
		PageBody=DownloadPage.downloadPage(Url,outPutPath);
	}
	
}
*/