package com.ontology.secure;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.ontology.model.RequestBean;
import com.ontology.model.ResponseBean;
import com.ontology.utils.Constant;
import com.ontology.utils.HelperUtil;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SecureHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> implements ISecureHttpMessageConverter {

    @Autowired
    RSAUtil utilRSA;

    @Autowired
    AESUtil utilAES;

    private static final Logger logger = LoggerFactory.getLogger(SecureHttpMessageConverter.class);

    public static final Charset DEFAULT_CHARSET;

    public static String SubType = "ontid.manage.api.v1+json";

    protected ObjectMapper objectMapper;
    @Nullable
    private Boolean prettyPrint;
    @Nullable
    private PrettyPrinter ssePrettyPrinter;

    @Nullable
    private String jsonPrefix;


    public SecureHttpMessageConverter() {
//        vnd.custodian.inner.api.v1+json
        //NOTE:  @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = {"application/vnd.custodian.api.v1+json"})

        this((new Jackson2ObjectMapperBuilder()
                        .indentOutput(true)
                        .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE))
                        .build(),
                new MediaType("application", SecureHttpMessageConverter.SubType));
    }


    protected SecureHttpMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.setDefaultCharset(DEFAULT_CHARSET);
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
        this.ssePrettyPrinter = prettyPrinter;
    }

    protected SecureHttpMessageConverter(ObjectMapper objectMapper, MediaType... supportedMediaTypes) {
        this(objectMapper);
        this.setSupportedMediaTypes(Arrays.asList(supportedMediaTypes));
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "ObjectMapper must not be null");
        this.objectMapper = objectMapper;
        this.configurePrettyPrint();
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.configurePrettyPrint();
    }

    private void configurePrettyPrint() {
        if (this.prettyPrint != null) {
            this.objectMapper.configure(SerializationFeature.INDENT_OUTPUT, this.prettyPrint);
        }

    }

    public boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
        return this.canRead(clazz, (Class) null, mediaType);
    }

    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        if (!this.canRead(mediaType)) {
            return false;
        } else {
            JavaType javaType = this.getJavaType(type, contextClass);
            AtomicReference<Throwable> causeRef = new AtomicReference();
            if (this.objectMapper.canDeserialize(javaType, causeRef)) {
                return true;
            } else {
                this.logWarningIfNecessary(javaType, (Throwable) causeRef.get());
                return false;
            }
        }
    }

    public boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
        if (!this.canWrite(mediaType)) {
            return false;
        } else {
            AtomicReference<Throwable> causeRef = new AtomicReference();
            if (this.objectMapper.canSerialize(clazz, causeRef)) {
                return true;
            } else {
                this.logWarningIfNecessary(clazz, (Throwable) causeRef.get());
                return false;
            }
        }
    }

    protected void logWarningIfNecessary(Type type, @Nullable Throwable cause) {
        if (cause != null) {
            boolean debugLevel = cause instanceof JsonMappingException && cause.getMessage().startsWith("Cannot find");
            if (debugLevel) {
                if (!this.logger.isDebugEnabled()) {
                    return;
                }
            } else if (!this.logger.isWarnEnabled()) {
                return;
            }

            String msg = "Failed to evaluate Jackson " + (type instanceof JavaType ? "de" : "") + "serialization for type [" + type + "]";
            if (debugLevel) {
                this.logger.debug(msg, cause);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, cause);
            } else {
                this.logger.warn(msg + ": " + cause);
            }

        }
    }

    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(clazz, (Class) null);
        return this.readJavaType(javaType, inputMessage);
    }

    public Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(type, contextClass);
        return this.readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        try {
            return ReadConvert(javaType, inputMessage);
        } catch (InvalidDefinitionException var4) {
            throw new HttpMessageConversionException("Type definition error: " + var4.getType(), var4);
        } catch (JsonProcessingException var5) {
//            throw new HttpMessageNotReadableException("JSON parse error: " + var5.getOriginalMessage(), var5, inputMessage);
            throw new HttpMessageNotReadableException("JSON parse error: " + var5.getOriginalMessage(), var5);
        } catch (Exception e) {
            throw new HttpMessageConversionException("Read convert error: " + e.getMessage(), e);
        }
    }

    /**
     * 当前端post加密数据过来，此处解密。get请求不会经过这里。
     *
     * @param javaType
     * @param inputMessage
     * @return
     * @throws Exception
     */
    @Override
    public Object ReadConvert(JavaType javaType, HttpInputMessage inputMessage) throws Exception {

        RequestBean requestBean = this.objectMapper.readValue(inputMessage.getBody(), RequestBean.class);
        String enJson = (String) requestBean.getData();
        List<String> secureKeys = inputMessage.getHeaders().get(Constant.HTTP_HEADER_SECURE);

        if (enJson != null && secureKeys != null && secureKeys.size() > 0 && HelperUtil.isNotEmptyAndNotNull(secureKeys.get(0))) {
            String enKey = secureKeys.get(0);
            String deKey = utilRSA.decryptByPrivateKey(enKey);
            String deJson = utilAES.decryptData(deKey, enJson);
            return this.objectMapper.readValue(deJson, javaType);
        } else {
            return this.objectMapper.readValue(inputMessage.getBody(), javaType);
        }
    }

    protected void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        MediaType contentType = outputMessage.getHeaders().getContentType();
        JsonEncoding encoding = this.getJsonEncoding(contentType);
        JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);

        try {
            this.writePrefix(generator, object);
            Object value = object;
            Class<?> serializationView = null;
            FilterProvider filters = null;
            JavaType javaType = null;
            if (object instanceof MappingJacksonValue) {
                MappingJacksonValue container = (MappingJacksonValue) object;
                value = container.getValue();
                serializationView = container.getSerializationView();
                filters = container.getFilters();
            }

            if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
                javaType = this.getJavaType(type, (Class) null);
            }

            ObjectWriter objectWriter = serializationView != null ? this.objectMapper.writerWithView(serializationView) : this.objectMapper.writer();
            if (filters != null) {
                objectWriter = objectWriter.with(filters);
            }

            if (javaType != null && javaType.isContainerType()) {
                objectWriter = objectWriter.forType(javaType);
            }

            SerializationConfig config = objectWriter.getConfig();
            if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) && config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
                objectWriter = objectWriter.with(this.ssePrettyPrinter);
            }

            value = writeConvert(objectWriter, outputMessage, value);
            objectWriter.writeValue(generator, value);
            this.writeSuffix(generator, object);
            generator.flush();
        } catch (InvalidDefinitionException var13) {
            throw new HttpMessageConversionException("Type definition error: " + var13.getType(), var13);
        } catch (JsonProcessingException var14) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + var14.getOriginalMessage(), var14);
        } catch (Exception e) {
            throw new HttpMessageConversionException("Could not RSAUtil.decryptByPrivateKey: " + e.getMessage(), e);
        }
    }

    /**
     * 处理返回的数据，当前端post数据过来，java服务回返回ResponseBean，此处可以加密
     *
     * @param outputMessage
     * @param value
     * @return
     * @throws Exception
     */
    @Override
    public Object writeConvert(ObjectWriter ow, HttpOutputMessage outputMessage, Object value) throws Exception {
        if (value instanceof ResponseBean) {

//            final ClientHttpResponse responseCopy = new BufferingClientHttpResponseWrapper(response);

            Object result = ((ResponseBean) value).getResult();
            String json = ow.writeValueAsString(result);

            List<String> secureKeys = outputMessage.getHeaders().get(Constant.HTTP_HEADER_SECURE);

            if (result != null && secureKeys != null && secureKeys.size() > 0 && HelperUtil.isNotEmptyAndNotNull(secureKeys.get(0))) {

                String enKey = secureKeys.get(0);
                String deKey = utilRSA.decryptByPrivateKey(enKey);
                String enJson = utilAES.encryptData(deKey, json);
                ((ResponseBean) value).setResult(enJson);
            }
        }
        return value;
    }

    /**
     * Specify a custom prefix to use for this view's JSON output.
     * Default is none.
     *
     * @see #setPrefixJson
     */
    public void setJsonPrefix(String jsonPrefix) {
        this.jsonPrefix = jsonPrefix;
    }

    /**
     * Indicate whether the JSON output by this view should be prefixed with ")]}', ". Default is false.
     * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
     * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
     * This prefix should be stripped before parsing the string as JSON.
     *
     * @see #setJsonPrefix
     */
    public void setPrefixJson(boolean prefixJson) {
        this.jsonPrefix = (prefixJson ? ")]}', " : null);
    }


    protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
        if (this.jsonPrefix != null) {
            generator.writeRaw(this.jsonPrefix);
        }
    }

    protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
    }

    protected JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        return typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
    }

    protected JsonEncoding getJsonEncoding(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            Charset charset = contentType.getCharset();
            JsonEncoding[] var3 = JsonEncoding.values();
            int var4 = var3.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                JsonEncoding encoding = var3[var5];
                if (charset.name().equals(encoding.getJavaName())) {
                    return encoding;
                }
            }
        }

        return JsonEncoding.UTF8;
    }

    @Nullable
    protected MediaType getDefaultContentType(Object object) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue) object).getValue();
        }

        return super.getDefaultContentType(object);
    }

    protected Long getContentLength(Object object, @Nullable MediaType contentType) throws IOException {
        if (object instanceof MappingJacksonValue) {
            object = ((MappingJacksonValue) object).getValue();
        }

        return super.getContentLength(object, contentType);
    }

    static {
        DEFAULT_CHARSET = StandardCharsets.UTF_8;
    }
}