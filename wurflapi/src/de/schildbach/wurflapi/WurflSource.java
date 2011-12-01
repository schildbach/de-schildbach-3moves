/*
 ***************************************************************************
 * Copyright 2003-2005 Luca Passani, passani at eunet.no                   *
 * Distributed under the Mozilla Public License                            *
 *   http://www.mozilla.org/NPL/MPL-1.1.txt                                *
 ***************************************************************************
 *   $Author: passani $
 *   $Header: /cvsroot/wurfl/tools/java/wurflapi-xom/antbuild/src/net/sourceforge/wurfl/wurflapi/WurflSource.java,v 1.1 2005/02/13 15:11:39 passani Exp $
 */

package de.schildbach.wurflapi;

import java.io.InputStream;

public interface WurflSource {


    public InputStream getWurflInputStream();

    public InputStream getWurflPatchInputStream();

}
