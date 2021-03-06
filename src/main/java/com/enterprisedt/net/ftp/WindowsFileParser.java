/**
 *
 *  edtFTPj
 * 
 *  Copyright (C) 2000-2004 Enterprise Distributed Technologies Ltd
 *
 *  www.enterprisedt.com
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *  Bug fixes, suggestions and comments should be should posted on 
 *  http://www.enterprisedt.com/forums/index.php
 *
 *  Change Log:
 *
 *    $Log: WindowsFileParser.java,v $
 *    Revision 1.14  2008-01-09 03:54:39  bruceb
 *    fixed bug re last modified date not being set
 *
 *    Revision 1.13  2007-12-18 07:52:53  bruceb
 *    trimStart() changes
 *
 *    Revision 1.12  2007-10-12 05:20:44  bruceb
 *    permit ignoring date parser errors
 *
 *    Revision 1.11  2006/10/11 08:58:16  hans
 *    Removed usage of deprecated FTPFile constructor
 *
 *    Revision 1.10  2005/06/03 11:26:25  bruceb
 *    comment change
 *
 *    Revision 1.9  2004/10/18 21:07:31  bruceb
 *    tweaked name finding
 *
 *    Revision 1.8  2004/10/18 15:58:33  bruceb
 *    setLocale
 *
 *    Revision 1.7  2004/09/02 11:02:31  bruceb
 *    rolled back
 *
 *
 */
package com.enterprisedt.net.ftp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *  Represents a remote Windows file parser
 *
 *  @author      Bruce Blackshaw
 *  @version     $Revision: 1.14 $
 */
public class WindowsFileParser extends FTPFileParser {

    /**
     *  Revision control id
     */
    public static String cvsId = "@(#)$Id: WindowsFileParser.java,v 1.14 2008-01-09 03:54:39 bruceb Exp $";
    
    /**
     * Date formatter
     */
    private SimpleDateFormat formatter;
    
    /**
     * Directory field
     */
    private final static String DIR = "<DIR>";
    
    /**
     * Number of expected fields
     */
    private final static int MIN_EXPECTED_FIELD_COUNT = 4;

    /**
     * Constructor
     */
     public WindowsFileParser() {
         setLocale(Locale.getDefault());
     }
     
    /**
     * Set the locale for date parsing of listings
     * 
     * @param locale    locale to set
     */
    public void setLocale(Locale locale) {
        formatter = new SimpleDateFormat("MM-dd-yy hh:mma", locale);
    }    


    /**
     * Parse server supplied string. Should be in
     * form 
     * 
     * 05-17-03  02:47PM                70776 ftp.jar
     * 08-28-03  10:08PM       <DIR>          EDT SSLTest
     * 
     * @param raw   raw string to parse
     */
    public FTPFile parse(String raw) throws ParseException {
        String[] fields = split(raw);
        
        if (fields.length < MIN_EXPECTED_FIELD_COUNT)
            return null;
            //throw new ParseException("Unexpected number of fields: " + fields.length, 0);
        
        // first two fields are date time
        Date lastModified = null;
        try {
            lastModified = formatter.parse(fields[0] + " " + fields[1]);
        }
        catch (ParseException ex) {
            if (!ignoreDateParseErrors) {
                throw ex;
            }
        }
        
        // dir flag
        boolean isDir = false;
        long size = 0L;
        if (fields[2].equalsIgnoreCase(DIR))
            isDir = true;
        else {
            try {
                size = Long.parseLong(fields[2]);
            }
            catch (NumberFormatException ex) {
                throw new ParseException("Failed to parse size: " + fields[2], 0);
            }
        }
        
        // we've got to find the starting point of the name. We
        // do this by finding the pos of all the date/time fields, then
        // the name - to ensure we don't get tricked up by a date or dir the
        // same as the filename, for example
        int pos = 0;
        boolean ok = true;
        for (int i = 0; i < 3; i++) {
            pos = raw.indexOf(fields[i], pos);
            if (pos < 0) {
                ok = false;
                break;
            }
            else { // move on the length of the field
                pos += fields[i].length();
            }
        }
        if (ok) {
            String name = trimStart(raw.substring(pos));
            return new FTPFile(raw, name, size, isDir, lastModified); 
        }
        else {
            throw new ParseException("Failed to retrieve name: " + raw, 0);  
        }
    }
  
}
