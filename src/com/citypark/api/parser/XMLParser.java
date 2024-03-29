package com.citypark.api.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import com.citypark.R;
import com.citypark.api.task.LoginTask;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * This file is part of BikeRoute.
 * 
 * Copyright (C) 2011  Jonathan Gray
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * @author jono@nanosheep.net
 * @version Jan 21, 2011
 */

public class XMLParser {
	// names of the XML tags
	protected static final String MARKERS = "markers";
	protected static final String MARKER = "marker";
	
	protected URL feedUrl;
	
	protected XMLParser() {
		
	}
	
	protected XMLParser(final String feedUrl) {
		try {
			final String encodedUrl;
			if(feedUrl!=null){
				encodedUrl = feedUrl.replaceAll(" ", "+");
			}else
			{
				encodedUrl = feedUrl;
			}
			this.feedUrl = new URL(encodedUrl);
		} catch (MalformedURLException e) {
			Log.e(e.getMessage(), "XML parser - " + feedUrl);
		}
	}

	protected InputStream getInputStream() throws IOException {
		try {
			HttpUriRequest request = new HttpGet(feedUrl.toString());
			request.addHeader("Accept-Encoding", "gzip");
			//request.addHeader("encoding", "utf-8");
	        final HttpResponse response = new DefaultHttpClient().execute(request);
	        Header ce = response.getFirstHeader("Content-Encoding");
	        String contentEncoding = null;
	        
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() == 500){ //TODO currently return 500, after server fix replace with 401
	        	//re login
	        	LoginTask.login(null);
	        	return null;
	        }
	        
	        HttpEntity entity = response.getEntity();
	        InputStream instream = null;
	      
	        if (entity != null)
	        	instream = entity.getContent();
	        
	        if (ce != null) {
	        	 contentEncoding = ce.getValue();
	        	 if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
				    instream = new GZIPInputStream(instream);
				}
			}
	        
			return instream;
		} catch (IOException e) {
			Log.e(e.getMessage(), "XML parser - " + feedUrl);
			throw e;
		}
	}
}
