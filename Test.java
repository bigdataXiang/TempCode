package address_match;

import address_match.geotext.GeoQuery;
import com.google.gson.*;
import com.mongodb.BasicDBObject;
import net.sf.json.JSONObject;
import util.FileTool;
import util.Tool;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

/**
 * Created by bigdataxiang on 16-11-25.
 */
public class address {

    public static void getLngLat(String address,JSONObject jsonObject,BasicDBObject document) throws UnsupportedEncodingException {

        String request ="http://192.168.6.9:8080/p41?f=json";
        String parameters ="&within="+ java.net.URLEncoder.encode("北京市", "UTF-8")+"&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
        //"&within="+ java.net.URLEncoder.encode("", "UTF-8")+

        boolean batch = true;
        Gson gson = new Gson();
        if (batch)
            request = "http://192.168.6.9:8080/p4b?";
        StringBuffer sb = new StringBuffer();
        int count = 0;

        if(address.length()==0){
            address="未知";
        }
        count ++;
        sb.append(address).append("\n");


        JSONObject coor=new JSONObject();

        if (count == 1) {

            String urlParameters = sb.toString();
            byte[] postData;
            try {
                postData = (parameters + java.net.URLEncoder.encode(urlParameters,"UTF-8")).getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;

                URL url = new URL(request);
                //System.out.println(request + urlParameters);
                HttpURLConnection cox = (HttpURLConnection) url.openConnection();
                cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
                cox.setDoOutput(true);
                cox.setDoInput(true);
                cox.setInstanceFollowRedirects(false);
                cox.setRequestMethod("POST");
                // cox.setRequestProperty("Accept-Encoding", "gzip");
                cox.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                cox.setRequestProperty("charset", "utf-8");
                cox.setRequestProperty("Content-Length",
                        Integer.toString(postDataLength));
                cox.setUseCaches(false);

                try (DataOutputStream wr = new DataOutputStream(
                        cox.getOutputStream())) {

                    wr.write(postData);

                    InputStream is = cox.getInputStream();
                    if (is != null) {
                        byte[] header = new byte[2];
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bis.mark(2);
                        int result = bis.read(header);
                        bis.reset();
                        BufferedReader reader = null;
                        // 判断是否是GZIP格式
                        int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
                        if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {

                            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(bis), "utf-8"));
                        } else {

                            reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
                        }


                        JsonParser parser = new JsonParser();
                        String txt ="";
                        try {

                            txt = reader.readLine();
                            if (txt == null) {
                                System.out.println("txt为null！");
                            }
                            else {
                                int index1=txt .indexOf("chinesename");
                                String index3=",}";
                                if(index1!=-1&&index3!=null)
                                    txt =txt .replace(",}", "}");
                                //System.out.println(txt);
                                JsonElement el = parser.parse(txt);
                                JsonObject jsonObj = null;
                                if(el.isJsonObject())
                                {
                                    jsonObj = el.getAsJsonObject();
                                    GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
                                    //System.out.println(gq.getResult().size());
                                    String lnglat = "";
                                    String Admin="";
                                    if (gq != null && gq.getResult() != null && gq.getResult().size() > 0) {
                                        for (int m = 0; m < gq.getResult().size(); m ++) {
                                            if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null) {
                                                if(gq.getResult().get(m).getLocation().getRegion()!=null){
                                                    System.out.println("这批数据没有问题！");
                                                    try {
                                                        String province=gq.getResult().get(m).getLocation().getRegion().getProvince();
                                                        String city=gq.getResult().get(m).getLocation().getRegion().getCity();
                                                        String county=gq.getResult().get(m).getLocation().getRegion().getCounty();
                                                        String town=gq.getResult().get(m).getLocation().getRegion().getTown();

                                                        Admin=(province+","+city+","+county+","+town).replace("null","");

                                                    }catch (NullPointerException e){
                                                        System.out.println("admin这里出了问题？");
                                                    }
                                                }else{
                                                    Admin="暂无";
                                                }

                                                double longitude=gq.getResult().get(m).getLocation().getLng();
                                                double latitude=gq.getResult().get(m).getLocation().getLat();

                                                coor.put("region",Admin);
                                                coor.put("lng",longitude);
                                                coor.put("lat", latitude);

                                            }else {
                                                System.out.println("没有坐标信息");
                                            }
                                        }
                                    }
                                }
                            }

                        }catch (JsonSyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("JsonSyntaxException:"+e.getMessage());
                            System.out.println("存在JsonSyntaxException异常！");
                        }catch(NullPointerException e){
                            System.out.println("NullPointerException:"+e.getMessage());
                        }

                    }
                }

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(NullPointerException e){
                e.printStackTrace();
            }
            sb.setLength(0);

        }
        if(coor.containsKey("region")){
            document.put("region",coor.getString("region"));
            jsonObject.put("region",coor.getString("region"));
        }
        if(coor.containsKey("lng")){
            document.put("lng",coor.getDouble("lng"));
            jsonObject.put("lng",coor.getDouble("lng"));
        }
        if(coor.containsKey("lat")){
            document.put("lat",coor.getDouble("lat"));
            jsonObject.put("lat",coor.getDouble("lat"));
        }
    }

    public static void getLngLat(String address,BasicDBObject document) throws UnsupportedEncodingException {

        String request ="http://192.168.6.9:8080/p41?f=json";
        String parameters ="&within="+ java.net.URLEncoder.encode("北京市", "UTF-8")+"&key=327D6A095A8111E5BFE0B8CA3AF38727&queryStr=";
        //"&within="+ java.net.URLEncoder.encode("", "UTF-8")+

        boolean batch = true;
        Gson gson = new Gson();
        if (batch)
            request = "http://192.168.6.9:8080/p4b?";
        StringBuffer sb = new StringBuffer();
        int count = 0;

        if(address.length()==0){
            address="未知";
        }
        count ++;
        sb.append(address).append("\n");


        JSONObject coor=new JSONObject();

        if (count == 1) {

            String urlParameters = sb.toString();
            byte[] postData;
            try {
                postData = (parameters + java.net.URLEncoder.encode(urlParameters,"UTF-8")).getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;

                URL url = new URL(request);
                //System.out.println(request + urlParameters);
                HttpURLConnection cox = (HttpURLConnection) url.openConnection();
                cox.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; rv:11.0) like Gecko");
                cox.setDoOutput(true);
                cox.setDoInput(true);
                cox.setInstanceFollowRedirects(false);
                cox.setRequestMethod("POST");
                // cox.setRequestProperty("Accept-Encoding", "gzip");
                cox.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                cox.setRequestProperty("charset", "utf-8");
                cox.setRequestProperty("Content-Length",
                        Integer.toString(postDataLength));
                cox.setUseCaches(false);

                try (DataOutputStream wr = new DataOutputStream(
                        cox.getOutputStream())) {

                    wr.write(postData);

                    InputStream is = cox.getInputStream();
                    if (is != null) {
                        byte[] header = new byte[2];
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bis.mark(2);
                        int result = bis.read(header);
                        bis.reset();
                        BufferedReader reader = null;
                        // 判断是否是GZIP格式
                        int ss = (header[0] & 0xff) | ((header[1] & 0xff) << 8);
                        if (result != -1 && ss == GZIPInputStream.GZIP_MAGIC) {

                            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(bis), "utf-8"));
                        } else {

                            reader = new BufferedReader(new InputStreamReader(bis, "utf-8"));
                        }


                        JsonParser parser = new JsonParser();
                        String txt ="";
                        try {

                            txt = reader.readLine();
                            if (txt == null) {
                                System.out.println("txt为null！");
                            }
                            else {
                                int index1=txt .indexOf("chinesename");
                                String index3=",}";
                                if(index1!=-1&&index3!=null)
                                    txt =txt .replace(",}", "}");
                                //System.out.println(txt);
                                JsonElement el = parser.parse(txt);
                                JsonObject jsonObj = null;
                                if(el.isJsonObject())
                                {
                                    jsonObj = el.getAsJsonObject();
                                    GeoQuery gq = gson.fromJson(jsonObj, GeoQuery.class);
                                    //System.out.println(gq.getResult().size());
                                    String lnglat = "";
                                    String Admin="";
                                    if (gq != null && gq.getResult() != null && gq.getResult().size() > 0) {
                                        for (int m = 0; m < gq.getResult().size(); m ++) {
                                            if (gq.getResult().get(m) != null && gq.getResult().get(m).getLocation() != null) {
                                                if(gq.getResult().get(m).getLocation().getRegion()!=null){
                                                    //System.out.println("这批数据没有问题！");
                                                    try {
                                                        String province=gq.getResult().get(m).getLocation().getRegion().getProvince();
                                                        String city=gq.getResult().get(m).getLocation().getRegion().getCity();
                                                        String county=gq.getResult().get(m).getLocation().getRegion().getCounty();
                                                        String town=gq.getResult().get(m).getLocation().getRegion().getTown();

                                                        Admin=(province+","+city+","+county+","+town).replace("null","");

                                                    }catch (NullPointerException e){
                                                        System.out.println("admin这里出了问题？");
                                                    }
                                                }else{
                                                    Admin="暂无";
                                                }

                                                double longitude=gq.getResult().get(m).getLocation().getLng();
                                                double latitude=gq.getResult().get(m).getLocation().getLat();

                                                coor.put("region",Admin);
                                                coor.put("lng",longitude);
                                                coor.put("lat", latitude);

                                            }else {
                                                System.out.println("没有坐标信息");
                                            }
                                        }
                                    }
                                }
                            }

                        }catch (JsonSyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("JsonSyntaxException:"+e.getMessage());
                            System.out.println("存在JsonSyntaxException异常！");
                        }catch(NullPointerException e){
                            System.out.println("NullPointerException:"+e.getMessage());
                        }

                    }
                }

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }catch(NullPointerException e){
                e.printStackTrace();
            }
            sb.setLength(0);

        }
        if(coor.containsKey("region")){
            document.put("region",coor.getString("region"));
        }
        if(coor.containsKey("lng")){
            document.put("lng",coor.getDouble("lng"));
        }
        if(coor.containsKey("lat")){
            document.put("lat",coor.getDouble("lat"));
        }
    }

}
