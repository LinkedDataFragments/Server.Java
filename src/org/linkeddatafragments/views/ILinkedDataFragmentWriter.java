package org.linkeddatafragments.views;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.linkeddatafragments.datasource.IDataSource;
import org.linkeddatafragments.fragments.ILinkedDataFragment;
import org.linkeddatafragments.fragments.ILinkedDataFragmentRequest;

/**
 *
 * @author mielvandersande
 */
public interface ILinkedDataFragmentWriter {
    public void writeNotFound(ServletOutputStream outputStream, HttpServletRequest request) throws Exception;
    public void writeError(ServletOutputStream outputStream, Exception ex) throws Exception;
    public void writeFragment(ServletOutputStream outputStream, IDataSource datasource, ILinkedDataFragment fragment,  ILinkedDataFragmentRequest ldfRequest) throws Exception;
}
