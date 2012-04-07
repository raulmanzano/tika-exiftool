/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.embed.exiftool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tika.embed.ExternalEmbedder;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.exiftool.ExiftoolIptcMetadataExtractor;
import org.apache.tika.parser.exiftool.ExiftoolTikaMapper;
import org.apache.tika.parser.external.ExternalParser;

/**
 * Convenience class to programmatically create an {@link ExternalEmbedder} which uses ExifTool.
 *
 * @author rgauss
 * @see <a href="http://www.sno.phy.queensu.ca/~phil/exiftool/">ExifTool</a>
 *
 */
public class ExiftoolExternalEmbedder extends ExternalEmbedder {

	private static final long serialVersionUID = 6037513204935762760L;

	private static final String COMMAND_APPEND_OPERATOR = "+=";
	
	private final String runtimeExiftoolExecutable;

	/**
	 * Default constructor
	 */
	public ExiftoolExternalEmbedder() {
		super();
		this.runtimeExiftoolExecutable = null;
		init();
	}
	
	public ExiftoolExternalEmbedder(String runtimeExiftoolExecutable) {
		super();
		this.runtimeExiftoolExecutable = runtimeExiftoolExecutable;
		init();
	}

	/**
	 * Programmatically sets up the metadata to command line arguments map, sets the executable, and append operator.
	 */
	public void init() {
		// Convert the exiftool metadata names into command line arguments
		Map<String, String[]> metadataCommandArguments = new HashMap<String, String[]>();
		for (Object tikaMetadata : ExiftoolTikaMapper.getTikaToExiftoolMetadataMap().keySet()) {
			List<Property> exiftoolMetadataNames = ExiftoolTikaMapper.getTikaToExiftoolMetadataMap().get(tikaMetadata);
			String[] exiftoolCommandArguments = new String[exiftoolMetadataNames.size()];
			for (int i = 0; i < exiftoolMetadataNames.size(); i++) {
				exiftoolCommandArguments[i] = "-" + exiftoolMetadataNames.get(i).getName();
			}
			if (tikaMetadata instanceof Property) {
				metadataCommandArguments.put(((Property) tikaMetadata).getName(), exiftoolCommandArguments);
			} else {
				metadataCommandArguments.put((String) tikaMetadata, exiftoolCommandArguments);
			}

		}
		setMetadataCommandArguments(metadataCommandArguments);
		
		setExiftoolExecutable((new ExiftoolIptcMetadataExtractor(null, null, runtimeExiftoolExecutable)).getExiftoolExecutable());
		setCommandAppendOperator(COMMAND_APPEND_OPERATOR);
	}

	/**
	 * Sets the path to the executable exiftool.
	 *
	 * @param exiftoolExecutable
	 */
	public void setExiftoolExecutable(String exiftoolExecutable) {
		String[] cmd = new String[] { exiftoolExecutable, "-v0",
    			"-o", ExternalParser.OUTPUT_FILE_TOKEN,
    			ExternalEmbedder.METADATA_COMMAND_ARGUMENTS_TOKEN,
    			ExternalParser.INPUT_FILE_TOKEN };
    	setCommand(cmd);
	}

}
