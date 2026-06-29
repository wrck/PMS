package com.dp.plat.security.xss.struts;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

public class MDispatcher extends Dispatcher {

    /**
     * Provide a logging instance.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MDispatcher.class);
    
    /**
     * Store state of StrutsConstants.DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP setting.
     */
    private boolean disableRequestAttributeValueStackLookup;
    
    private ServletContext servletContext;
    private Map<String, String> initParams;

    private String multipartSaveDir;
    
    /**
     * Store state of StrutsConstants.STRUTS_DEVMODE setting.
     */
    private boolean devMode;
    
    public MDispatcher(ServletContext servletContext, Map<String, String> initParams) {
        super(servletContext, initParams);
        this.servletContext = servletContext;
        this.initParams = initParams;
    }
    
    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, ServletContext servletContext)
            throws IOException {
         // don't wrap more than once
        if (request instanceof StrutsRequestWrapper) {
            return request;
        }

        String content_type = request.getContentType();
        if (content_type != null && content_type.contains("multipart/form-data")) {
            MultiPartRequest mpr = getMultiPartRequest();
            LocaleProvider provider = getContainer().getInstance(LocaleProvider.class);
            request = new MMultiPartRequestWrapper(mpr, request, getSaveDir(servletContext), provider);
        } else {
            request = new MStrutsRequestWrapper(request, disableRequestAttributeValueStackLookup);
        }

        return request;
    }

    private String getSaveDir(ServletContext servletContext) {
        String saveDir = multipartSaveDir.trim();

        if (saveDir.equals("")) {
            File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
            if (LOG.isInfoEnabled()) {
            LOG.info("Unable to find 'struts.multipart.saveDir' property setting. Defaulting to javax.servlet.context.tempdir");
            }

            if (tempdir != null) {
                saveDir = tempdir.toString();
                setMultipartSaveDir(saveDir);
            }
        } else {
            File multipartSaveDir = new File(saveDir);

            if (!multipartSaveDir.exists()) {
                if (!multipartSaveDir.mkdirs()) {
                    String logMessage;
                    try {
                        logMessage = "Could not find create multipart save directory '" + multipartSaveDir.getCanonicalPath() + "'.";
                    } catch (IOException e) {
                        logMessage = "Could not find create multipart save directory '" + multipartSaveDir.toString() + "'.";
                    }
                    if (devMode) {
                        LOG.error(logMessage);
                    } else {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn(logMessage);
                        }
                    }
                }
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("saveDir=" + saveDir);
        }

        return saveDir;
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_MULTIPART_SAVEDIR setting.
     * @param val New setting
     */
    @Override
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String val) {
        multipartSaveDir = val;
    }
    
    /**
     * Modify state of StrutsConstants.STRUTS_DEVMODE setting.
     * @param mode New setting
     */
    @Override
    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }
}