package controllers;

import au.com.bytecode.opencsv.CSVReader;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.annotation.Transactional;
import com.avaje.ebean.text.csv.CsvReader;
import com.google.gson.Gson;
import com.ning.http.multipart.MultipartRequestEntity;
import constants.Constants;
import models.Food;
import models.Shop;
import models.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;
import play.data.FileUpload;
import play.mvc.Http;
import play.server.ServletWrapper;
import utils.Pagination;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: lala
 * Date: 3/15/14
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class MasterItem extends Basic  {


    final static Logger logger = LoggerFactory.getLogger(MasterItem.class);


    public static void show(Long id) {
        renderJSON(Food.view(id));
    }

    public static void update() {

        Food food = new Gson().fromJson(new InputStreamReader(request.body),Food.class);
        if (food.id != null && food.id > 0) {
            System.out.println(new Gson().toJson(food));
            food.modifiedBy = session.get(Constants.CURRENT_USERNAME);
            food.modifiedDate = new Date();

            Ebean.update(food);
        } else {
            Ebean.save(food);
        }
        Map data = new HashMap();
        data.put("messages", "Store Item Successfully.");
        renderJSON(data);
    }
    public static void uploadImg(File img) {

        DiskFileItemFactory factory = new DiskFileItemFactory();


// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            FileUpload fileUpload = (FileUpload)((List) Http.Request.current().args.get("__UPLOADS")).get(0);

            File destPath =new File(Play.applicationPath.getPath()+"/public/upload/"+fileUpload.getFileName());
            logger.info("Upload file "+destPath.getAbsolutePath());

            BufferedImage bimg =ImageIO.read(fileUpload.asFile());

            if(bimg.getWidth()!=bimg.getHeight() && Math.abs(bimg.getWidth()-bimg.getHeight())>50)
                throw  new IllegalArgumentException("File width and height are not equal : "+bimg.getWidth()+"/"+bimg.getHeight());
            FileUtils.copyFile(fileUpload.asFile(),destPath);
            Map data = new HashMap();
            data.put("success", "Upload file Successfully.");
            data.put("path",fileUpload.getFileName());
            renderJSON(data);
        } catch (Exception e) {
            logger.error(e.getMessage());
            Map data = new HashMap();
            data.put("error",e.getMessage());
            renderJSON(data);
        }
    }
    public static void upload() {

        Map<String,String> response= new HashMap<>();
        int size = 0;
        try {
        String attachment = request.params.get("attachment");
        attachment=attachment.substring(attachment.indexOf(",")+1);
        byte[] bytes =  Base64.decodeBase64(attachment);
        new ByteArrayInputStream(bytes);
        InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        logger.info(attachment+"");

        CSVReader csv = new CSVReader(reader);


        List<String[]> list = csv.readAll();
        if(list.size()>500) throw new RuntimeException("Records size <= 500");
        if(list.size()<=1) throw new RuntimeException("No Record");
        if(list.size()!=0) {
            List foodList  =new ArrayList();
            Map<Long,Shop> shopMap = new HashMap<>();
            int insertCount = 0;
            for(int i=1;i<list.size();i++) {
                String columns[]= list.get(i);
                if(columns.length!=11) throw new RuntimeException("Invalid CSV Columns");

                Food food = new Food();
                food.sn=columns[0];
                food.barCode=columns[1];
                food.name=columns[2];
                food.nameZh=columns[3];
                if(NumberUtils.isNumber(columns[4]))
                   food.costPrice=Float.valueOf(columns[4]);
                if(NumberUtils.isNumber(columns[5]))
                    food.retailPrice=Float.valueOf(columns[5]);

                food.picture=columns[6];
                food.status=true;
                food.createBy=session.get(Constants.CURRENT_USERNAME);
                food.createDate=new Date();
                //food.flag=true;


                if(NumberUtils.isNumber(columns[8]))
                {
                    food.position=Integer.valueOf(columns[8]);
                }
                if(NumberUtils.isNumber(columns[9]))
                {
                    if(Integer.valueOf(columns[9])==1)
                        food.flag=true;
                    else
                        food.flag=false;
                }
                food.type=columns[10];
                if(NumberUtils.isNumber(columns[7]))
                {
                    Shop shop = null;
                    if(!shopMap.containsKey(Long.valueOf(columns[7]))) {
                        shop = Shop.view(Long.valueOf(columns[7]));
                        shopMap.put(Long.valueOf(columns[7]),shop);
                    }
                    shop = shopMap.get(Long.valueOf(columns[7]));
                    if(shop!=null) {
                        food.shop= new Shop();
                        food.shop.id=Long.valueOf(columns[7]);
                        foodList.add(food);
                        insertCount++;
                    }
                }


            }
            size = foodList.size();
            Food.bulkStore(foodList);
        }
        } catch (Exception e) {

            response.put("error",e.getMessage());
            renderJSON(response);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        response.put("success","true");
        response.put("size",size+"");
        renderJSON(response);
    }


    public static void search() {
        int currentPage = 1;
        if(request.params.get("iDisplayStart")!="0") {
            currentPage = (Integer.parseInt(request.params.get("iDisplayStart"))/Integer.parseInt(request.params.get("iDisplayLength")))+1;
        }
        Pagination pagination = new Pagination();
        pagination.currentPage = currentPage;

        pagination.pageSize = Integer.parseInt(request.params.get("iDisplayLength"));
        Map<String,String> searchs = new HashMap<String, String>();
        String outlet =  request.params.get("sSearch_0");
        if(StringUtils.isEmpty(outlet) || "undefined".equalsIgnoreCase(outlet) || "ALL".equalsIgnoreCase(outlet))
            outlet="%";
        if(session.get(Constants.CURRENT_USERTYPE).equals("OPERATOR"))
            outlet=session.get("shopName");
        searchs.put("shopName",outlet);
        renderJSON(Food.search(searchs, pagination));
    }
}
