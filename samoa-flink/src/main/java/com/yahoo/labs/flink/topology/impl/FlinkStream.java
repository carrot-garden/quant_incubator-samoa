package com.yahoo.labs.flink.topology.impl;

/*
 * #%L
 * SAMOA
 * %%
 * Copyright (C) 2014 - 2015 Apache Software Foundation
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import com.yahoo.labs.flink.com.yahoo.labs.flink.helpers.Utils;
import com.yahoo.labs.samoa.core.ContentEvent;
import com.yahoo.labs.samoa.topology.AbstractStream;
import org.apache.flink.streaming.api.datastream.DataStream;

import java.io.Serializable;


/**
 * A stream for SAMOA based on Apache Flink's DataStream
 */
public class FlinkStream extends AbstractStream implements FlinkComponent, Serializable {

	private static int outputCounter = 0;
	private FlinkComponent procItem;
	private transient DataStream<SamoaType> dataStream;
	private int sourcePiId;
	private String flinkStreamId;

	public FlinkStream(FlinkComponent sourcePi) {
		this.procItem = sourcePi;
		this.sourcePiId = sourcePi.getComponentId();
		setStreamId("stream-" + Integer.toString(outputCounter));
		flinkStreamId = "stream-" + Integer.toString(outputCounter);
		outputCounter++;
	}

	@Override
	public void initialise() {
		if (procItem instanceof FlinkProcessingItem) {
			dataStream = procItem.getOutStream().filter(Utils.getFilter(getStreamId()))
			.setParallelism(((FlinkProcessingItem) procItem).getParallelism());
		} else
			dataStream = procItem.getOutStream();
	}

	@Override
	public boolean canBeInitialised() {
		return procItem.isInitialised();
	}

	@Override
	public boolean isInitialised() {
		return dataStream != null;
	}

	@Override
	public DataStream getOutStream() {
		return dataStream;
	}

	@Override
	public void put(ContentEvent event) {
		((FlinkProcessingItem) procItem).putToStream(event, this);
	}

	@Override
	public int getComponentId() {
		return -1; //dummy number shows that it comes from a Stream
	}

	public int getSourcePiId() {
		return sourcePiId;
	}

	@Override
	public String getStreamId() {
		return flinkStreamId;
	}
}
