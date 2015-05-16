
package com.hq.dateparser;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * @author qiaohua
 * @date   2015年5月16日 下午
 * @see
 */
public class Dateparser {

    private static final Map<Pattern,DateFormat> DATA_PATTERNS_FORMAT_MAP;
    static {
        DATA_PATTERNS_FORMAT_MAP = new LinkedHashMap<Pattern, DateFormat>();

        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{4} [0-9]{1,2} [0-9]{1,2}$"),new SimpleDateFormat("yyyy MM dd",Locale.US));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{4} [0-9]{1,2} [0-9]{1,2} [0-9]{1,2}:[0-9]{1,2} (PM|AM)$"),new SimpleDateFormat("yyyy MM dd hh:mm a",Locale.US));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{4} [0-9]{1,2} [0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}$"),new SimpleDateFormat("yyyy MM dd HH:mm"));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{4} [0-9]{1,2} [0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}$"),new SimpleDateFormat("yyyy MM dd HH:mm:ss"));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{1,2} [0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}$"),new SimpleDateFormat("MM dd HH:mm"));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}日$"),new SimpleDateFormat("yyyy年MM月dd日"));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{1,2} [0-9]{1,2} [0-9]{4}$"),new SimpleDateFormat("MM dd yyyy"));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[0-9]{1,2}:[0-9]{1,2} [0-9]{1,2} [0-9]{1,2} [0-9]{4}$"),new SimpleDateFormat("HH:mm MM dd yyyy"));
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[a-z|A-Z]{3} [0-9]{1,2} [0-9]{1,2} [0-9]{1,2}:[0-9]{1,2}$"),new SimpleDateFormat("MMM dd yy HH:mm",Locale.US));
        DateFormatSymbols chineseMonthDfs = new DateFormatSymbols();
        chineseMonthDfs.setMonths(new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"});
        chineseMonthDfs.setShortMonths(new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"});
        DATA_PATTERNS_FORMAT_MAP.put(Pattern.compile("^[\\u4E00-\\u9FA5]{2,3} [0-9]{1,2} [0-9]{4}$"),new SimpleDateFormat("MMM dd yyyy",chineseMonthDfs));
    }

    /**
     *英语中对顺序的提示，如1st，2nd，3rd，4th，等
     */
    private static final Pattern ORDER_POS_PATTERN = Pattern.compile("([0-9])(st|nd|rd|th)");

    /**
     * 处理时间间隔类型的时间，例如：1小时前
     */
    private static final Map<Pattern, Integer> DELTA_DATE_PATTERNS_FORMAT_MAP = new LinkedHashMap<Pattern, Integer>();
    static {
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*秒前$"), Calendar.SECOND);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*秒钟前$"), Calendar.SECOND);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*秒鐘前$"), Calendar.SECOND);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*分前$"), Calendar.MINUTE);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*分钟前$"), Calendar.MINUTE);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*分鐘前$"), Calendar.MINUTE);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*小时前$"), Calendar.HOUR);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*小時前$"), Calendar.HOUR);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*时前$"), Calendar.HOUR);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*時前$"), Calendar.HOUR);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*个小时前$"), Calendar.HOUR);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*個小時前$"), Calendar.HOUR);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*天前$"), Calendar.DATE);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*日前$"), Calendar.DATE);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*月前$"), Calendar.MONTH);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*个月前$"), Calendar.MONTH);
    	DELTA_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile("^\\d+\\s*個月前$"), Calendar.MONTH);
    }
    /**
     * 例如：昨天 前天
     */
    private static final Map<Pattern, Object[]> DAY_DATE_PATTERNS_FORMAT_MAP = new LinkedHashMap<Pattern, Object[]>();
    static {
    	DAY_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile(".*昨天.*"), new Object[]{1,"天"});
    	DAY_DATE_PATTERNS_FORMAT_MAP.put(Pattern.compile(".*前天.*"), new Object[]{2,"天"});
    }
    /**
     * 解析帖子的发布时间,提供的功能摄像是替换掉日期中的特殊符号，把日期整理成2014 01 02这样的格式统一解析，避免生成不同的date format对象，时间和上午下午部分暂未做处理
     * 其实可以把这部分信息写在配置文件中，但由于日期的格式不会很多，写在配置文件中，实现复杂还多了维护的成本，暂时写在代码中，后续如果格式太多，导致代码臃肿，可以考虑
     * 转移到配置文件中
     * 格式如下
     * <ul>
     *     <li>http://www.chineseinla.com : 2014/12/26&nbsp;  11:27 pm</li>
     *     <li>http://www.kijiji.com.tw  : 2015.01.04</li>
     * </ul>
     * @param dateStr
     * @return
     */
    private static Date parseDate(String dateStr){
        String tempDateStr = dateStr.trim();
        Matcher matcher = ORDER_POS_PATTERN.matcher(tempDateStr);
        while (matcher.find()){
            tempDateStr = tempDateStr.replaceAll(matcher.group(),  matcher.group(1));
        }
        tempDateStr = tempDateStr.replaceAll("\\."," ");
        tempDateStr = tempDateStr.replaceAll(","," ");
        tempDateStr = tempDateStr.replaceAll("/"," ");
        tempDateStr = tempDateStr.replaceAll("-"," ");
        tempDateStr = tempDateStr.replaceAll("&nbsp;"," ");
        tempDateStr = tempDateStr.replaceAll("\\s+"," ");
        tempDateStr = tempDateStr.toUpperCase();

        Date resultDate = null;
        for (Map.Entry<Pattern,DateFormat> entry : DATA_PATTERNS_FORMAT_MAP.entrySet() ){
            Matcher tempMatcher = entry.getKey().matcher(tempDateStr);
            if(tempMatcher.find()){
                try {
                    resultDate = entry.getValue().parse(tempDateStr);
                    resultDate = noYearCorrection(resultDate);
                    return resultDate;
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        if (resultDate == null) {
        	tempDateStr = convertDate(tempDateStr);
        	resultDate = parseDeltaDate(tempDateStr);
        }
        return resultDate;
    }
    
    private static String convertDate(String tempDateStr) {
        for (Map.Entry<Pattern,Object[]> entry : DAY_DATE_PATTERNS_FORMAT_MAP.entrySet() ){
            Matcher tempMatcher = entry.getKey().matcher(tempDateStr);
            if(tempMatcher.find()){
                try {
                	Object[] values = entry.getValue();
                	int num = (Integer)values[0];
                	String unit = (String)values[1];
                    return num+unit+"前";
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
		return tempDateStr;
	}

	/**
     * 处理时间间隔类型的时间字符串，例如：1 小时前等
     * 
     * @param dateStr
     * @return
     * @author Zhang Ning
     * @date   2015年2月10日 下午2:52:49
     * @see   
     * @since  1.0.0
     */
    private static Date parseDeltaDate(String dateStr) {
    	Date resultDate = null;
        for (Map.Entry<Pattern, Integer> entry : DELTA_DATE_PATTERNS_FORMAT_MAP.entrySet() ){
            Matcher tempMatcher = entry.getKey().matcher(dateStr);
            if(tempMatcher.find()){
                try {
                	int deltaUnit = entry.getValue();
                	int delta = 0;
                	Matcher matcher = Pattern.compile("\\d+").matcher(dateStr);
                	if (matcher.find()) {
                		String number = matcher.group();
                        delta = Integer.valueOf(number);
                	}
                	Calendar date = Calendar.getInstance();
                	date.add(deltaUnit, 0 - delta);
                	resultDate = date.getTime();
                    return resultDate;
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return resultDate;
	}

	/**
     * 11-11 11:11这种格式的时间，没有年，parse时会默认为1970年，这个方法把1970年改成当前年
     * 如果改成当前年后时间大于当前时间，则减一年，改成去年
     * 例如当前是2015-01-21 18:22
     * 收到的时间是：11-11 11:11，则先转成2015-11-11 11:11，然后发现现在还没到11月11日11:11，则返回2014-11-11 11:11
     * 如果收到的时间是1-2 11：11，则返回2015-1-2 11:11
     * 
     * @param noYearDate
     * @return
     * @author Zhang Ning
     * @date   2015年1月21日 下午5:49:10
     * @see   
     * @since  1.0.0
     */
    private static Date noYearCorrection(Date noYearDate) {
    	if (noYearDate == null) {
    		return null;
    	}
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(noYearDate);
    	if (calendar.get(Calendar.YEAR) == 1970) {
    		Calendar now = Calendar.getInstance();
    		calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
    		if (calendar.after(Calendar.getInstance())) {
    			calendar.add(Calendar.YEAR, -1);
    		}
    	}
		return calendar.getTime();
	}

	public static void main(String args[]){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        /*System.out.println(df.format(parseDate("2014/12/26&nbsp;  11:27 pm")));
        System.out.println(df.format(parseDate(" 2013/07/05&nbsp;  12:31 pm  ")));
        System.out.println(df.format(parseDate(" 2014/11/17  2:03 pm")));
        System.out.println(df.format(parseDate("2015.01.04")));
        System.out.println(df.format(parseDate("2015 01 04")));
        System.out.println(df.format(parseDate("2015-01-04")));
        System.out.println(df.format(parseDate("2015-1-8 2:10")));
        System.out.println(df.format(parseDate("2015-1-18 20:10")));
        System.out.println(df.format(parseDate("2015-11-18 20:10")));
        System.out.println(df.format(parseDate("2013-11-30 13:48:06")));
        System.out.println(df.format(parseDate("2015年02月03日")));
        System.out.println(df.format(parseDate("1秒前")));
        System.out.println(df.format(parseDate("2 秒前")));
        System.out.println(df.format(parseDate("1 秒钟前")));
        System.out.println(df.format(parseDate("1 分前")));
        System.out.println(df.format(parseDate("1 分钟前")));
        System.out.println(df.format(parseDate("1 小时前")));
        System.out.println(df.format(parseDate("1 个小时前")));
        System.out.println(df.format(parseDate("1 时前")));
        System.out.println(df.format(parseDate("1 小時前")));
        System.out.println(df.format(parseDate("1 個小時前")));
        System.out.println(df.format(parseDate("1 時前")));
        System.out.println(df.format(parseDate("1 天前")));
        System.out.println(df.format(parseDate("1 日前")));
        System.out.println(df.format(parseDate("1 月前")));
        System.out.println(df.format(parseDate("1 个月前")));
        System.out.println(df.format(parseDate("1 個月前")));
        System.out.println(df.format(parseDate("02-10-2015")));*/
        
//        System.out.println(df.format(parseDate("昨天 12:32")));
//        System.out.println(df.format(parseDate("1 天前")));
//        System.out.println(df.format(parseDate("Apr 1st 15 14:01")));
        System.out.println(df.format(parseDate("十月 14, 2014")));
        System.out.println(df.format(parseDate("2014-10-29 21:56:57")));
//        System.out.println(df.format(parseDate("9:35 03/01/2015")));
        /*Matcher tempMatcher =Pattern.compile(".*昨天.*").matcher("大厦大厦昨天");
        if(tempMatcher.find()){
        	System.out.println("true");
        }else{
        	System.out.println("false");
        }*/
    }
}
