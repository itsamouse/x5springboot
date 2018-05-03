package net.jingbo.x5springboot.api.controller;

import com.alibaba.fastjson.JSONObject;
import net.jingbo.x5springboot.baas.action.ActionException;
import net.jingbo.x5springboot.baas.action.Engine;
import net.jingbo.x5springboot.baas.data.DataUtils;
import net.jingbo.x5springboot.baas.data.sql.SQLException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018-4-26.
 */
@Controller
public class BaasController {

    private final Logger logger= LoggerFactory.getLogger(getClass());

    private static final long serialVersionUID = -5873620616781916663L;
    private static final String METHOD_POST = "POST";
    private static final String METHOD_GET = "GET";
    private static final String MULTIPART_CONTENT_TYPE = "multipart/form-data";
    private static final String JSON_CONTENT_TYPE = "application/json";

    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    @PostMapping("/ecptest/ecp_test_service/*")
    public void service(ServletRequest request, ServletResponse response) throws ServletException {
        HttpServletRequest reg = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        execService(reg, resp);
    }

    private static String getRequestContentType(HttpServletRequest request) {
        return request.getContentType();
    }

    private static boolean isRequestMultipart(String type) {
        return null != type && -1 < type.indexOf(MULTIPART_CONTENT_TYPE);
    }

    private static boolean isJson(String type) {
        return null!=type && -1<type.indexOf(JSON_CONTENT_TYPE);
    }

    private static boolean isJson(HttpServletRequest request) {
        return isJson(getRequestContentType(request));
    }

    private static boolean isRequestMultipart(HttpServletRequest request) {
        return isRequestMultipart(getRequestContentType(request));
    }

    private void execService(HttpServletRequest reg, HttpServletResponse resp) throws ServletException {
        String URI = reg.getRequestURI();
        String contextPath = reg.getContextPath();

        logger.info(String.format("打印入参url : %s %s ",contextPath,URI.toString()));

        if(URI.startsWith(contextPath)) URI = URI.substring(contextPath.length()+1);
        try {
            JSONObject params = (JSONObject) getParams(reg);
            params.put(REQUEST, reg);
            params.put(RESPONSE, resp);
            JSONObject ret = Engine.execAction(URI, params);
            if(null!=ret){
                DataUtils.writeJsonToResponse(resp, ret);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("执行Action："+URI+"失败，"+e.getMessage(), e);
        } catch (ActionException e) {
            e.printStackTrace();
            throw new ServletException("执行Action："+URI+"失败，"+e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("执行Action："+URI+"失败，"+e.getMessage(), e);
        }
    }

    private String getBufferPath() {
        return System.getProperty("java.io.tmpdir");
    }

    private JSONObject getParams(ServletInputStream inputStream) throws Exception  {
        final int BUFFER_SIZE = 8 * 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bLen = 0;
        while ((bLen = inputStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bLen);
        }
        String bodyData = new String(baos.toByteArray(), "UTF-8");
        JSONObject jo = JSONObject.parseObject(bodyData);
        return jo;
    }

    //目前暂时没有支持Multipart请求
    @SuppressWarnings("unused")
    private JSONObject getParamsByMultipart(HttpServletRequest request) throws Exception  {
        JSONObject params = new JSONObject();
        File tempPathFile = new File(getBufferPath());
        if (!tempPathFile.exists()) {
            tempPathFile.mkdirs();
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(10 * 1024 * 1024);
        factory.setRepository(tempPathFile);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(-1); // 设置最大文件尺寸，-1为不限制
        upload.setHeaderEncoding("UTF-8");
        List<?> items = upload.parseRequest(request);
        Iterator<?> iterator = items.iterator();
        while (iterator.hasNext()) {
            FileItem fi = (FileItem) iterator.next();
            if (fi.isFormField()) {
                String fieldName = fi.getFieldName();
                params.put(fieldName, fi.getString());
            } else {
                String fileName = fi.getName();
                if (fileName != null) {
                    String fieldName = fi.getFieldName();
                    params.put(fieldName, fi.getInputStream());
                }
            }
        }
        return params;
    }

    private JSONObject getParams(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        if(isRequestMultipart(request) || (!METHOD_GET.equalsIgnoreCase(method)&&!isJson(request))){
            JSONObject params = new JSONObject();
            return params;
        }else if(METHOD_POST.equalsIgnoreCase(method)&&isJson(request)){
            return getParams(request.getInputStream());
        } else{
            JSONObject params = new JSONObject();
            for (Object k : request.getParameterMap().keySet()) {
                String key = (String)k;
                params.put(key, request.getParameter(key));
            }
            return params;
        }
    }


}
