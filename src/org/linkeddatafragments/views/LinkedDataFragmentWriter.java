package org.linkeddatafragments.views;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.fragments.LinkedDataFragment;
import org.linkeddatafragments.fragments.LinkedDataFragmentRequest;

/**
 *
 * @author mielvandersande
 */
public interface LinkedDataFragmentWriter {
    public void writeNotFound(ServletOutputStream outputStream, HttpServletRequest request) throws Exception;
    public void writeError(ServletOutputStream outputStream, Exception ex) throws Exception;
    public void writeFragment(ServletOutputStream outputStream, IDataSource datasource, LinkedDataFragment fragment,  LinkedDataFragmentRequest ldfRequest) throws Exception;
}
