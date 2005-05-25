/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/workplace/threads/CmsModuleDeleteThread.java,v $
 * Date   : $Date: 2005/05/25 09:01:57 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.workplace.threads;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProject;
import org.opencms.main.CmsException;
import org.opencms.main.CmsLog;
import org.opencms.main.I_CmsConstants;
import org.opencms.main.OpenCms;
import org.opencms.report.A_CmsReportThread;
import org.opencms.report.I_CmsReport;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;

/**
 * Deletes a module.<p>
 *
 * @author Alexander Kandzior (a.kandzior@alkacon.com)
 * 
 * @version $Revision: 1.2 $
 * @since 5.1.10
 */
public class CmsModuleDeleteThread extends A_CmsReportThread {
    
    /** The log object for this class. */
    private static final Log LOG = CmsLog.getLog(CmsModuleDeleteThread.class);  
    
    private List m_moduleNames;
    
    private boolean m_replaceMode;

    /**
     * Creates the module delete thread.<p>
     * 
     * @param cms the current cms context
     * @param moduleNames the name of the module
     * @param replaceMode the replace mode
     * @param old flag for report mode
     */
    public CmsModuleDeleteThread(CmsObject cms, List moduleNames, boolean replaceMode, boolean old) {

        super(cms, "OpenCms: Module deletion of " + moduleNames);
        m_moduleNames = moduleNames;
        m_replaceMode = replaceMode;
        if (old) {
            initOldHtmlReport(cms.getRequestContext().getLocale());
        } else {
            initHtmlReport(cms.getRequestContext().getLocale());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(Messages.get().key(Messages.LOG_DELETE_THREAD_CONSTRUCTED_0));     
        }         
        
    }

    /**
     * @see org.opencms.report.A_CmsReportThread#getReportUpdate()
     */
    public String getReportUpdate() {

        return getReport().getReportUpdate();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(Messages.get().key(Messages.LOG_DELETE_THREAD_STARTED_0));
            }    
            Iterator j = m_moduleNames.iterator();
            while (j.hasNext()) {
                String moduleName = (String)j.next();
                
                moduleName = moduleName.replace('\\', '/');
                CmsProject project = null;
    
                // create a Project to delete the module.
                project = getCms().createProject(
                    "DeleteModule",
                    "A System generated project to delete the module " + moduleName,
                    OpenCms.getDefaultUsers().getGroupAdministrators(),
                    OpenCms.getDefaultUsers().getGroupAdministrators(),
                    I_CmsConstants.C_PROJECT_TYPE_TEMPORARY);
                getCms().getRequestContext().setCurrentProject(project);
    
                getReport().print(getReport().key("report.delete_module_begin"), I_CmsReport.C_FORMAT_HEADLINE);
                getReport().println(" <i>" + moduleName + "</i>", I_CmsReport.C_FORMAT_HEADLINE);
    
                // copy the resources to the project
                List projectFiles = OpenCms.getModuleManager().getModule(moduleName).getResources();
                for (int i = 0; i < projectFiles.size(); i++) {
                    try {
                        getCms().copyResourceToProject((String)projectFiles.get(i));
                    } catch (CmsException e) {
                        // may happen if the resource has already been deleted
                        LOG.error(Messages.get().key(Messages.LOG_MOVE_RESOURCE_FAILED_1, projectFiles.get(i)), e);
                        getReport().println(e);
                    }
                }
                // now delete the module
                OpenCms.getModuleManager().deleteModule(getCms(), moduleName, m_replaceMode, getReport());
    
                getReport().println(getReport().key("report.publish_project_begin"), I_CmsReport.C_FORMAT_HEADLINE);
                // now unlock and publish the project
                getCms().unlockProject(project.getId());
                getCms().publishProject(getReport());
    
                getReport().println(getReport().key("report.publish_project_end"), I_CmsReport.C_FORMAT_HEADLINE);
                getReport().println(getReport().key("report.delete_module_end"), I_CmsReport.C_FORMAT_HEADLINE);
    
                if (LOG.isDebugEnabled()) {
                    LOG.debug(Messages.get().key(Messages.LOG_DELETE_THREAD_FINISHED_0));
                }  
            }
        } catch (Exception e) {
            getReport().println(e);
            LOG.error(Messages.get().key(Messages.LOG_MODULE_DELETE_FAILED_1, m_moduleNames), e);
        }

    }
}