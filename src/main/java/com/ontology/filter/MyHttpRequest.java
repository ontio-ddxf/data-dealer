package com.ontology.filter;

import com.ontology.utils.Helper;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.DelegatingServletInputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.util.*;


public class MyHttpRequest extends HttpServletRequestWrapper {

    private static final Logger logger = LoggerFactory.getLogger(MyHttpRequest.class);

    private byte[] bytes;
    private String body;
    private Map<String, List<String>> map;
    private int readMap = 0;
    private String queryString;

    /**
     * @param request {@link HttpServletRequest} object.
     * @throws IOException
     */
    public MyHttpRequest(HttpServletRequest request) throws IOException {
        super(request);
        bytes = IOUtils.toByteArray(request.getInputStream());
        queryString = request.getQueryString();

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return new DelegatingServletInputStream(byteArrayInputStream);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getParameter(String name) {
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        try {
            Map<String, List<String>> nameVals = doParameter();
            List<String> list = nameVals.get(name);
            if (list != null && list.size() > 0) {
                return list.toArray(new String[]{});
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    public String getBody() throws UnsupportedEncodingException {
        return new String(bytes, getCharacterEncoding());
    }

    //这里获取所有参数值的map
    public Map<String, List<String>> doParameter() throws UnsupportedEncodingException {
        if (readMap == 0) {
            //这里把post里的参数和地址栏参数结合到一起,然后解析
            if (Helper.isEmptyOrNull(queryString)) {
                body = new String(bytes, getCharacterEncoding());
            } else {
                body = new String(bytes, getCharacterEncoding()) + "&" + queryString;
            }
            String[] nameVals = body.split("&");
            map = new HashMap<String, List<String>>();
            for (String nameVal : nameVals) {
                if (!"".equals(nameVal)) {
                    String name = nameVal.split("=")[0];
                    String val = nameVal.split("=")[1];
                    if (map.containsKey(name)) {
                        List<String> vals = map.get(name);
                        vals.add(val);
                        map.put(name, vals);
                    } else {
                        List<String> vals = new ArrayList<String>();
                        vals.add(val);
                        map.put(name, vals);
                    }
                }
            }
            readMap = 1;
        }
        return map;

    }
}
