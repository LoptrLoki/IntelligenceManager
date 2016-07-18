package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Util;

import service.motion.Motion;
import util.RecordTrans;
import database.DatabaseHelper;
import database.SearchType;
import entity.Record;

/**
 * 该类已单例化，请调用静态方法getInstance来获取实例。
 * 该类用于暂存数据，以完成数据从计算部分到UI部分的传递。
 */
public class DataManager {
	
	//关键词
	static private String keyword = null;
	static public String getKeyword(){
		if(keyword == null){
			keyword = "";
		}
		return keyword;
	}
	static public void setKeyword(String arg){
		if(arg != null){
			if(arg != keyword){
				keyword = arg;
				reset();
			}
		}
	}
	static public boolean haveKeyword(){
		return keyword != null;
	}
	
	//各类别记录
	static private List<Record> recordsAll = null;
	static public List<Record> getRecordsAll(){
		System.out.print("getRecordAll called\n");
		if(recordsAll == null){
			recordsAll = DatabaseHelper.search(getKeyword(), SearchType.ALL);
			System.out.print("RecordsAll get! List size:" + recordsAll.size() +"\n");
		}
		return recordsAll;
	}
	
	static private List<Record> recordsGov = null;
	static public List<Record> getRecordsGov(){
		System.out.print("getRecordGov called\n");
		if(recordsGov == null){
			recordsGov = DatabaseHelper.search(getKeyword(), SearchType.GOV);
			System.out.print("RecordsGov get!\n");
		}
		return recordsGov;
	}
	
	static private List<Record> recordsMedia = null;
	static public List<Record> getRecordsMedia(){
		System.out.print("getRecordMedia called\n");
		if(recordsMedia == null){
			recordsMedia = DatabaseHelper.search(getKeyword(), SearchType.MEDIA);
			System.out.print("RecordsMedia get!\n");
		}
		return recordsMedia;
	}
	
	static private List<Record> recordsPublic = null;
	static public List<Record> getRecordsPublic(){
		System.out.print("getRecordPublic called\n");
		if(recordsPublic == null){
			recordsPublic = DatabaseHelper.search(getKeyword(), SearchType.PUBLIC);
			System.out.print("RecordsPublic get!\n");
		}
		return recordsPublic;
	}
	
	static private List<Record> recordsGovMedia = null;
	static public List<Record> getRecordsGovMedia(){
		System.out.print("getRecordGovMedia called\n");
		if(recordsGovMedia == null){
			recordsGovMedia = DatabaseHelper.search(getKeyword(), SearchType.GOVMEDIA);
			System.out.print("RecordsGovMedia get!\n");
			
		}
		return recordsGovMedia;
	}
	
	static private List<Record> recordsHottestYear = null;
	static public List<Record> getRecordsHottestYear(){
		if(recordsHottestYear == null){
			recordsHottestYear = DatabaseHelper.search(getKeyword(), getHottestYear());
		}
		return recordsHottestYear;
	}
	
	static private String hottestYear = null;
	static public String getHottestYear(){
		if(hottestYear == null){
			Map<String, Integer> count = getYearRecordNums();
			int max = 0;
			for(int i=2016;i>2000;i--){
				int tmp = 0;
				if(count.get(Integer.toString(i))!=null) tmp = count.get(Integer.toString(i));
				if(tmp>max){
					max = tmp;
					hottestYear = Integer.toString(i);
				}
			}
		}
		return hottestYear;
	}
	
	//各类别舆论评分，顺序为全网、政府、媒体、公众
	static private float[] opinionIndexes = null;
	static public float[] getOpinionIndex(){
		System.out.print("getOpinionIndex called\n");
		if(opinionIndexes == null){
			float sum = 0;
			for (Record record : getRecordsAll()) {
				sum += Float.valueOf(record.getOther());
			}
			float indexAll = sum/getRecordsAll().size();
			sum = 0;
			for (Record record : getRecordsGov()) {
				sum += Float.valueOf(record.getOther());
			}
			float indexGov = sum/getRecordsGov().size();
			sum = 0;
			for (Record record : getRecordsMedia()) {
				sum += Float.valueOf(record.getOther());
			}
			float indexMedia = sum/getRecordsMedia().size();
			sum = 0;
			for (Record record : getRecordsPublic()) {
				sum += Float.valueOf(record.getOther());
			}
			float indexPublic = sum/getRecordsPublic().size();
			
			float[] tmp = {indexAll, indexGov, indexMedia, indexPublic};
			opinionIndexes = tmp;
			System.out.print("OpinionIndex:");
			System.out.print(tmp[0] +" "+ tmp[1] +" "+ tmp[2] +" "+ tmp[3] + "\n");
		}
		return opinionIndexes;
	}
	
	//各类别关键词，顺序为全网、政府、媒体、公众
	static private List<List<String>> keywords = null;
	static public List<List<String>> getKeywords(){
		System.out.print("getKeywords called\n");
		if(keywords == null){
			keywords = new ArrayList<List<String>>();
			keywords.add(service.keyword.Keyword.getKeyword(getRecordsAll(), properties.Configure.KEYWORD_SIZE_WHOLEWEB));
			keywords.add(service.keyword.Keyword.getKeyword(getRecordsGov(), properties.Configure.KEYWORD_SIZE_NORMAL));
			keywords.add(service.keyword.Keyword.getKeyword(getRecordsMedia(), properties.Configure.KEYWORD_SIZE_NORMAL));
			keywords.add(service.keyword.Keyword.getKeyword(getRecordsPublic(), properties.Configure.KEYWORD_SIZE_NORMAL));
			List<String> tmp = service.keyword.Keyword.getKeyword(getRecordsHottestYear(), properties.Configure.KEYWORD_SIZE_NORMAL);
			tmp.add(0, getHottestYear());
			keywords.add(tmp);
		}
		System.out.print("Hottest year keywords:");
		System.out.print(keywords.get(4));
		System.out.print("\n");
		return keywords;
	}

	//各类别记录数量，顺序为全网、政府、媒体、公众
	static private int[] recordNums = null;
	static public int[] getRecordNum(){
		System.out.print("getRecordNum called\n");
		if(recordNums == null){
			recordNums = new int[4];
			recordNums[0] = getRecordsAll().size();
			recordNums[1] = getRecordsGov().size();
			recordNums[2] = getRecordsMedia().size();
			recordNums[3] = getRecordsPublic().size();
		}
		return recordNums;
	}
	
	//各年份记录数量
	static private HashMap<String, Integer> yearRecordNums = null;
	static public HashMap<String, Integer> getYearRecordNums(){
		System.out.print("getYearRecordNums called\n");
		if(yearRecordNums == null){
			yearRecordNums = DatabaseHelper.count(getKeyword());
		}
		return yearRecordNums;
	}
	
	//舆情分数分布
	static private int[] opinionIndexDistribution = null;
	static public int[] getOpinionIndexDistribution(){
		System.out.print("getOpinionIndexDistribution called\n");
		if(opinionIndexDistribution == null){
			opinionIndexDistribution = new int[11];
			for(int i=0;i<11;i++) opinionIndexDistribution[i] = 0;
			for (Record record : getRecordsAll()) {
				opinionIndexDistribution[Math.round(Float.valueOf(record.getOther())*10)]++;
			}
			System.out.print("OpinionIndexDistribution: ");
			System.out.print(opinionIndexDistribution[0] +" "+ 
					opinionIndexDistribution[1] +" "+ 
					opinionIndexDistribution[2] +" "+ 
					opinionIndexDistribution[3] +" "+ 
					opinionIndexDistribution[4] +" "+ 
					opinionIndexDistribution[5] +" "+ 
					opinionIndexDistribution[6] +" "+ 
					opinionIndexDistribution[7] +" "+ 
					opinionIndexDistribution[8] +" "+ 
					opinionIndexDistribution[9] +" "+ 
					opinionIndexDistribution[10]);
			System.out.print("\n");
		}
		return opinionIndexDistribution;
	}
	
	static private void reset(){
		recordsAll = null;
		recordsGov = null;
		recordsMedia = null;
		recordsPublic = null;
		recordsGovMedia = null;
		recordsHottestYear = null;
		hottestYear = null;
		opinionIndexes = null;
		keywords = null;
		recordNums = null;
		yearRecordNums = null;
		opinionIndexDistribution = null;
	}
}
