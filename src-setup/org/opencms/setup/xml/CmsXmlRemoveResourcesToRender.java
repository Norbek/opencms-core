/*
 * File   : $Source: /alkacon/cvs/opencms/src-setup/org/opencms/setup/xml/CmsXmlRemoveResourcesToRender.java,v $
 * Date   : $Date: 2009/08/20 11:30:48 $
 * Version: $Revision: 1.4 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2009 Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.setup.xml;

import org.opencms.configuration.CmsConfigurationManager;
import org.opencms.configuration.CmsImportExportConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Removes a wrong resources to render in the static export configuration, from 6.2.3 to 7.0.x.<p>
 * 
 * @author Michael Moossen
 * 
 * @version $Revision: 1.4 $ 
 * 
 * @since 6.9.2
 */
public class CmsXmlRemoveResourcesToRender extends A_CmsSetupXmlUpdate {

    /** List of xpaths to update. */
    private List m_xpaths;

    /**
     * @see org.opencms.setup.xml.I_CmsSetupXmlUpdate#getName()
     */
    public String getName() {

        return "Remove wrong resources to render";
    }

    /**
     * @see org.opencms.setup.xml.I_CmsSetupXmlUpdate#getXmlFilename()
     */
    public String getXmlFilename() {

        return CmsImportExportConfiguration.DEFAULT_XML_FILE_NAME;
    }

    /**
     * @see org.opencms.setup.xml.A_CmsSetupXmlUpdate#getCommonPath()
     */
    @Override
    protected String getCommonPath() {

        // /opencms/importexport/staticexport/rendersettings/resourcestorender/
        StringBuffer xp = new StringBuffer(256);
        xp.append("/").append(CmsConfigurationManager.N_ROOT);
        xp.append("/").append(CmsImportExportConfiguration.N_IMPORTEXPORT);
        xp.append("/").append(CmsImportExportConfiguration.N_STATICEXPORT);
        xp.append("/").append(CmsImportExportConfiguration.N_STATICEXPORT_RENDERSETTINGS);
        xp.append("/").append(CmsImportExportConfiguration.N_STATICEXPORT_RESOURCESTORENDER);
        return xp.toString();
    }

    /**
     * @see org.opencms.setup.xml.A_CmsSetupXmlUpdate#getXPathsToRemove()
     */
    @Override
    protected List getXPathsToRemove() {

        if (m_xpaths == null) {
            // "/opencms/importexport/staticexport/rendersettings/resourcestorende/regex[text()='...']";
            StringBuffer xp = new StringBuffer(256);
            xp.append("/").append(CmsConfigurationManager.N_ROOT);
            xp.append("/").append(CmsImportExportConfiguration.N_IMPORTEXPORT);
            xp.append("/").append(CmsImportExportConfiguration.N_STATICEXPORT);
            xp.append("/").append(CmsImportExportConfiguration.N_STATICEXPORT_RENDERSETTINGS);
            xp.append("/").append(CmsImportExportConfiguration.N_STATICEXPORT_RESOURCESTORENDER);
            xp.append("/").append(CmsImportExportConfiguration.N_REGEX);
            xp.append("[text()='");
            m_xpaths = new ArrayList();
            m_xpaths.add(xp.toString() + "/system/workplace/commons/styles/.*']");
        }
        return m_xpaths;
    }
}