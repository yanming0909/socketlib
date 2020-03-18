package com.patent.common;

import android.util.Log;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class StringUtil {


    /** * 产生随机字符串 * */
    public static String randomString(int length) {
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz").toCharArray();
        Random strGen = new Random();
        if (length < 1) {
            return null;
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[strGen.nextInt(35)];
        }
        return new String(randBuffer);
    }
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] bakeyword = new byte[s.length() / 2];
        for (int i = 0; i < bakeyword.length; i++) {
            try {
                bakeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            s = new String(bakeyword, "gbk");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    public static String getYUNYingData(String[] dataArr) {
        int index=getIndex(dataArr)-9;
        String[] aaa=new String[index];
        for(int i=0;i<index;i++) {
            if (i + 8 == dataArr.length - 1) {

            } else {
                aaa[i] = dataArr[i + 8];
            }
        }


        String str="";
        for(int i=0;i<aaa.length;i++) {
            str=str+aaa[i];
        }
        return str;
    }
    public static int getIndex(String[] dataArr) {
        String str = "";
        for(int i=0;i<dataArr.length;i++) {
            if(i!=0) {
                if((dataArr[i]+dataArr[i+1]).equals("55aa")) {
                    str=str+" "+dataArr[i];
                    return i;
                }
            }
        }
        return 0;
    }
    /**
     * 分隔id
     */
    public static String[] StringToArr(String str) {
        if(str==null)
            return null;


        int m=str.length()/2;
        if(m*2<str.length()){
            m++;
        }
        String[] strs=new String[m];
        int j=0;
        for(int i=0;i<str.length();i++){
            if(i%2==0){//每隔两个
                strs[j]=""+str.charAt(i);
            }else{
                strs[j]=strs[j]+str.charAt(i);//将字符加上两个空格
                j++;
            }
        }
        return strs;
    }
    /**
     * 方向转16进制
     */
    public static byte[] getBearingHex(float bearing) {
        byte[] by=new byte[1];
        if (bearing>2) bearing /=2;
        int d = (int) bearing;
        String str =Long.toHexString(d);

        //1个字节 就是  2个字符串

        if(str.length()==1)
            str="0"+str;

        String [] strArr =StringToArr(str);
        for(int i=0;i<by.length;i++){
            by[i]=(byte) StringToTenHex(strArr[i]);
        }

        return by;
    }


    /**
     * 当前车次
     */
    public static byte[] getTripsNum(int tripsNum) {
        byte[] by=new byte[4];
        String str =Integer.toHexString(tripsNum);

        if(str.length()==1)
            str="0000000"+str;

        if(str.length()==2)
            str="000000"+str;

        if(str.length()==3)
            str="00000"+str;

        if(str.length()==4)
            str="0000"+str;

        if(str.length()==5)
            str="000"+str;

        if(str.length()==6)
            str="00"+str;

        if(str.length()==7)
            str="0"+str;
        String [] strArr =StringToArr(str);
        for(int i=0;i<by.length;i++){
            by[i]=(byte) StringToTenHex(strArr[i]);
        }

        return by;
    }
    /**
     * 转16进制 2位
     */
    public static byte[] getTwoHex(float speed) {
        byte[] by=new byte[2];
        DecimalFormat df = new DecimalFormat("##0.0");
        double d =Double.valueOf(df.format(speed));
        long aa=(long)(d*10);
        String str =Long.toHexString(aa);

        //2个字节 就是  4个字符串

        if(str.length()==1)
            str="000"+str;

        if(str.length()==2)
            str="00"+str;

        if(str.length()==3)
            str="0"+str;

        String [] strArr =StringToArr(str);
        for(int i=0;i<by.length;i++){
            by[i]=(byte) StringToTenHex(strArr[i]);
        }

        return by;
    }

    /**
     * 经纬度转16进制
     */
    public static byte[] getLatitudeHex(double latlong) {
        byte[] by=new byte[4];
        DecimalFormat df = new DecimalFormat("##0.0000");
        double d =Double.valueOf(df.format(latlong));
        long aa=(long) (d*10000*60);
        String str =Long.toHexString(aa);
        //4个字节 就是  八个字符串

        if(str.length()==1)
            str="0000000"+str;

        if(str.length()==2)
            str="000000"+str;

        if(str.length()==3)
            str="00000"+str;

        if(str.length()==4)
            str="0000"+str;

        if(str.length()==5)
            str="000"+str;

        if(str.length()==6)
            str="00"+str;

        if(str.length()==7)
            str="0"+str;

        String [] strArr =StringToArr(str);

        for(int i=0;i<by.length;i++){
            by[i]=(byte) StringToTenHex(strArr[i]);
        }

        return by;
    }
    /**
     * 16进制 转 经纬度
     */
    public static String getSuchLatitude(String latlong) {
        DecimalFormat df = new DecimalFormat("##0.0000");
        long lat = Long.parseLong(latlong,16);
        double latD = (double) lat;
        double d = latD/10000d/60d;
        return df.format(d);
    }
    //把日期转为字符串
    public static String ConverToString(Date date)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(date);
    }


    /**
     * String转ASCII码
     * @param str
     * @return
     */
    public static String parseAscii(String str){
        StringBuilder sb=new StringBuilder();
        byte[] bs=str.getBytes();
        for(int i=0;i<bs.length;i++)
            sb.append(toHex(bs[i]));
        return sb.toString();
    }


    /**
     * 后面补零
     * @param parseAscii
     * @param sum
     * @return
     */
    public static StringBuilder addZeroFan(String parseAscii,int sum) {
        StringBuilder zero=new StringBuilder("");
        int zero_count =sum-parseAscii.length();
        zero.append(parseAscii);
        for(int i=0;i<zero_count;i++) {
            zero.append("0");
        }

        return zero;
    }

    /**
     * 前面补零
     * @param parseAscii
     * @param sum
     * @return
     */
    public static StringBuilder addZero(String parseAscii,int sum) {
        StringBuilder zero=new StringBuilder("");
        int zero_count =sum-parseAscii.length();
        for(int i=0;i<zero_count;i++) {
            zero.append("0");
        }
        zero.append(parseAscii);
        return zero;
    }


    public static String toHex(int n){
        StringBuilder sb=new StringBuilder();
        if(n/16==0){
            return toHexUtil(n);
        }else{
            String t=toHex(n/16);
            int nn=n%16;
            sb.append(t).append(toHexUtil(nn));
        }
        return sb.toString();
    }

    private static String toHexUtil(int n){
        String rt="";
        switch(n){
            case 10:rt+="A";break;
            case 11:rt+="B";break;
            case 12:rt+="C";break;
            case 13:rt+="D";break;
            case 14:rt+="E";break;
            case 15:rt+="F";break;
            default:
                rt+=n;
        }
        return rt;
    }




    /**
     * 获取需要设置的参数  最后返回对应设置的参数值
     * @param data
     * @param list
     */
    public static ArrayList<ArrayList<String>> getSetAnswer(String[] data, ArrayList<ArrayList<String>> list,int current) {
        list.get(0).add(data[current]+data[current+1]);
        list.get(1).add(getAnswerValue(data,current+2));

        int length=getAnswerLength(data,current+2);

        if((current+length+3+2)==data.length) {
            return list;
        }else {
            getSetAnswer(data, list,current+length+3);
        }
        return list;
    }

    /**
     * 设置值的长度
     * @param data
     * @param index
     * @return
     */
    private static int getAnswerLength(String[] data,int index) {
        return Integer.parseInt(data[index],16);
    }

    /**
     * 获取设置值的值
     * @param data
     * @param index
     * @return
     */
    private static String getAnswerValue(String[] data,int index) {


        String data_str = "";

        for(int j=0;j<getAnswerLength(data,index);j++){
            data_str=data_str+data[index+j+1];
        }
        System.out.println(data_str);
        return data_str;
    }




    /**
     * 获取需要查询的参数
     * @param data
     */
    public static String[] getSearchParmer(String[] data) {
        StringBuilder str=new StringBuilder();
        for(int i=13;i<data.length-2;i++) {
            str.append(data[i]);
        }

        char[] bankNoArray = str.toString().toCharArray();
        String bankNoString = "";
        for(int i=0;i<bankNoArray.length;i++){
            if(i%4==0 && i>0){
                bankNoString +=" ";
            }
            bankNoString+=bankNoArray[i];
        }
        String[] arr=bankNoString.split(" ");
        return arr;
    }


    /**
     * 字符串转换成十六进制字符串
     * @param  str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str)
    {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString().trim();
    }

    //16进制转10进制数据  可以直接放到new byte[]{}里面
    public static int StringToTenHex(String str16) {
        try {
            return Integer.parseInt(str16, 16);
        } catch (Exception e) {
            Log.i("", "");
        }
        return 0;
    }

    public static String getStrToASCII(String string) {
        String[] str=StringToArr(string);
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<str.length;i++) {
            stringBuilder.append(ASCIITOStr(str[i]));
        }

        return stringBuilder.toString();
    }


    public static String ASCIITOStr(String ch) {
        int aa=StringToTenHex(ch);
        char c=(char)aa;
        String s= Character.toString(c);
        return s;
    }

    /**
     * 数组元素查找
     * @param data
     * @param value
     * @return
     */
    public static ArrayList<Integer>  getIndexList(byte[] data, byte value,int maxSize){
        ArrayList<Integer> index = new ArrayList<>();
        //遍历数组，依次获取数组中的每个元素，和已知的数据进行比较
        for(int x=0; x<data.length; x++){
            if(data[x]==value){
            //如果相等，就返回当前的索引值
                if (index.size()<maxSize)
                index.add(x);
                else break;
            }
        }
        return index;
    }
}
