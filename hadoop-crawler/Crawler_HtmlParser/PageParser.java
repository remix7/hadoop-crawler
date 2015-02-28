package Crawler_HtmlParser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;
import org.htmlparser.visitors.ObjectFindingVisitor;

public class PageParser {
	//after some fix ,it can match chinese \u4e00-\u9fa5,just add it is ok
	private static final String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;\u4e00-\u9fa5]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static ArrayList<String> getLinks(String htmlString) throws ParserException{
		// this function is used to get the 
		if(htmlString==null){
			return null;
		}
		ObjectFindingVisitor visitor = new ObjectFindingVisitor(
				LinkTag.class);
		LinkTag linkTag;
		Parser parser = new Parser(htmlString);
	    parser.setEncoding(parser.getEncoding());
	    parser.visitAllNodesWith(visitor);
	    Node[] nodes = visitor.getTags();
	    ArrayList<String> stringArray= new ArrayList<String>();
	    // bea
	    for (int i = 0; i < nodes.length; i++) {
	    	linkTag=(LinkTag)nodes[i];
	    	if(isMatch(linkTag.getLink())){
	    		//System.out.println(linkTag.getLink());
	    		stringArray.add(linkTag.getLink().toString());
	    	}
	    }
	    if(stringArray.size()<=0){
	    	//System.out.println("we get no links at this page");
	    	stringArray=null;
	    }
	    return stringArray;
	}
	public static ArrayList getLinks2(String htmlString){
		try {
			return PageParser.getLinks(htmlString);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static boolean isMatch(String urlString){
		Pattern patt = Pattern.compile(regex);
        Matcher matcher = patt.matcher(urlString);
        return matcher.matches();
	}
}
